package com.taslabs.chartvision;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.highlight.ChartHighlighter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.taslabs.chartvision.JsonParser;

public class BarChartActivity extends AppCompatActivity {
    // global variables
    protected static Entry entra;
    protected static int index;
    protected static Highlight higha;

    TextToSpeech textToSpeech;
    BarChart chart;
    BarChartObj chartData;
    TextView titulo;
    TextView xlabel;
    TextView ylabel;
    ConstraintLayout constraintLayout;
    String url = null;
    Vibrator v;

    // in View Bounds variables
    Rect outRect = new Rect();
    int[] location = new int[2];
    // in View Bounds variables

    // Control for TTS repeats
    boolean lastTitulo = false;
    boolean lastY = false;
    boolean lastX = false;
    boolean first = true;
    boolean isInside = true;
    Highlight lastHighlight;

    // Flags pro TTS
    public static final int FLAG_TITULO = 0;
    public static final int FLAG_XLABEL = 1;
    public static final int FLAG_YLABEL = 2;
    public static final int FLAG_CHARTVALUE = 3;

    //SHAKE VARIABLES
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private ShakeHelper mShakeHelper;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar_chart);
        chart = findViewById(R.id.barchart);

        titulo = findViewById(R.id.title);
        xlabel = findViewById(R.id.xlabel);
        ylabel = findViewById(R.id.ylabel);
        constraintLayout = findViewById(R.id.layout);

        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int res = textToSpeech.setLanguage(new Locale("pt", "BR"));
                    if (res == TextToSpeech.LANG_MISSING_DATA || res == TextToSpeech.LANG_NOT_SUPPORTED) {
                        res = textToSpeech.setLanguage(Locale.getDefault());
                        if (res == TextToSpeech.LANG_MISSING_DATA || res == TextToSpeech.LANG_NOT_SUPPORTED) {
                            res = textToSpeech.setLanguage(Locale.US);
                            if (res == TextToSpeech.LANG_MISSING_DATA || res == TextToSpeech.LANG_NOT_SUPPORTED) {
                            }
                        }
                    }

                    tts("Por favor, desligue o recurso Tólque Béque. Para sair da visualização do gráfico, sacuda seu aparelho");
                }
            }
        });

        // ShakeDetector initialization
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        mShakeHelper = new ShakeHelper();

        mShakeHelper.setOnShakeListener(new ShakeHelper.OnShakeListener() {

            @Override
            public void onShake(int count) {
                /*
                 * The following method, "handleShakeEvent(count):" is a stub //
                 * method you would use to setup whatever you want done once the
                 * device has been shook.
                 */

                textToSpeech.stop();
                tts("Saindo. Por favor, re-ligue o recurso Tólque Béque");
                finish();
            }
        });

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            url = extras.getString("url");
        }

        JsonParser jsonParser = new JsonParser();
        jsonParser.loadJSONFromURL(url);
        chartData = jsonParser.getBarChartObj();
        startListeners();
        stpChart();
        hideSystemUI();


    }


    public void stpChart(){

        titulo.setText(chartData.getTitle());
        xlabel.setText(chartData.getxLabel());
        ylabel.setText(chartData.getyLabel());

        // Get values from object
        List<BarEntry> entries = new ArrayList<>();
        int lenght = chartData.getLabels().length;

        for(int i = 0; i < lenght; i++){
            entries.add(new BarEntry((float)i, (float)chartData.getValues()[i]));

        }

        BarDataSet set = new BarDataSet(entries, "BarDataSet");
        BarData data = new BarData(set);
        chart.setData(data);


        //data.setBarWidth(0.9f); // set custom bar width

        chart.setFitBars(true); // make the x-axis fit exactly all bars
        chart.invalidate(); // refresh
        chart.setDoubleTapToZoomEnabled(false);
        chart.setPinchZoom(false);
        String[] labels = chartData.getLabels();
        chart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        chart.getXAxis().setLabelCount(lenght);

        chart.getAxisLeft().setDrawGridLines(false);
        chart.getXAxis().setDrawGridLines(false);
        chart.getLegend().setEnabled(false); //Desabilita a legenda

        chart.getAxisLeft().setDrawGridLines(false);
        chart.getAxisRight().setDrawGridLines(false);
        chart.getXAxis().setDrawGridLines(false);
        chart.getDescription().setEnabled(false);

        XAxis xAxis = chart.getXAxis();

        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        chart.setScaleEnabled(false); // Disable all zooming

        YAxis rightYAxis = chart.getAxisRight();
        rightYAxis.setEnabled(false);

    }

    public void tts(String text){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        }
        else {
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    private void vibrate(int duration){

        // Vibrate for 500 milliseconds
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            v.vibrate(duration);
        }


    }

    @SuppressLint("ClickableViewAccessibility")
    public void startListeners(){


        chart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry entry, Highlight h) {
                entra = entry;
                higha = h;


                if (isInside) {
                    lastTitulo = false;
                    lastX = false;
                    lastY = false;


                    Long entryY = (long) entry.getY();
                    int entryX = (int) entry.getX();
                    Toast.makeText(getApplicationContext(), String.valueOf(entryY), Toast.LENGTH_LONG).show();
                    fitInTemplate(String.valueOf(entryY), FLAG_CHARTVALUE, entryX);
                }

                else{
                    chart.highlightValue(0f, 0f, -1);
                }
            }

            @Override
            public void onNothingSelected() {

            }
        });

        chart.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int x = (int)event.getRawX();
                int y = (int)event.getRawY();
                int leftBound = v.getLeft();
                int upperBound = v.getTop();
                int downerBound = v.getBottom() + (v.getBottom()/12);
                if(event.getAction() == MotionEvent.ACTION_MOVE){

                    // Se for arrastado para o Titulo
                    if(y < upperBound && !lastTitulo) {
                        isInside = false;
                        lastTitulo = true;
                        lastX = false;
                        lastY = false;
                        fitInTemplate(titulo.getText().toString(), FLAG_TITULO, -1);
                        chart.highlightValues(null);

                    }

                    // Se for arrastado para o LabelX
                    if(y > downerBound && !lastX){
                        isInside = false;
                        lastX = true;
                        lastY = false;
                        lastTitulo = false;
                        fitInTemplate(xlabel.getText().toString(), FLAG_XLABEL, -1);
                        chart.highlightValues(null);
                        //Here goes code to execute on onTouch ViewA
                    }

                    // Se for arrastado para o labelY
                    if((x < leftBound) && !lastY){
                        isInside = false;
                        lastY = true;
                        lastX = false;
                        lastTitulo = false;
                        fitInTemplate(ylabel.getText().toString(), FLAG_YLABEL, -1);
                        chart.highlightValues(null);
                        //Here goes code to execute on onTouch ViewA
                    }

                    if (isViewInBounds(chart, x, y)){
                        isInside = true;
                    }


                }
                if(event.getAction() == MotionEvent.ACTION_DOWN) {

                isInside = true;
                }
                // Further touch is not handled
                return false;
            }
        });


        titulo.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                int x = (int)event.getRawX();
                int y = (int)event.getRawY();

                int leftBound = chart.getLeft();
                int upperBound = chart.getTop();
                int downerBound = chart.getBottom() + (chart.getBottom()/12);


                if(event.getAction() == MotionEvent.ACTION_MOVE) {
                    if(isViewInBounds(chart, x, y)) {
                        isInside = true;
                        MotionEvent newTouch = MotionEvent.obtain(event.getDownTime(), event.getEventTime(), MotionEvent.ACTION_DOWN,
                                event.getRawX(), event.getRawY(), event.getMetaState());
                        Highlight highlight = chart.getHighlightByTouchPoint(event.getRawX() - chart.getLeft(), event.getRawY() - chart.getTop());

                            if (!highlight.equalTo(lastHighlight)) {
                                lastHighlight = highlight;
                                chart.highlightValue(highlight, true);
                            }

                        lastHighlight = highlight;
                        first = false;
                        lastTitulo = false;
                        lastY = false;
                        lastX = false;
                    }

                    // Se for arrastado para o Titulo
                    if(y < upperBound && !lastTitulo) {
                        lastHighlight = null;
                        isInside = false;
                        chart.highlightValues(null);
                        lastTitulo = true;
                        lastX = false;
                        lastY = false;
                        fitInTemplate(titulo.getText().toString(), FLAG_TITULO, -1);

                        Toast.makeText(getApplicationContext(), "Titulo", Toast.LENGTH_LONG).show();
                    }

                    // Se for arrastado para o LabelX
                    if(y > downerBound && !lastX){
                        isInside = false;
                        chart.highlightValues(null);
                        lastX = true;
                        lastY = false;
                        lastTitulo = false;
                        fitInTemplate(xlabel.getText().toString(), FLAG_XLABEL, -1);
                        //Here goes code to execute on onTouch ViewA
                    }

                    // Se for arrastado para o labelY
                    if((x < leftBound) && !lastY){
                        isInside = false;
                        chart.highlightValues(null);
                        lastY = true;
                        lastX = false;
                        lastTitulo = false;
                        fitInTemplate(ylabel.getText().toString(), FLAG_YLABEL, -1);
                        //Here goes code to execute on onTouch ViewA
                    }

                }

                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    isInside = false;
                    lastHighlight = null;
                    chart.highlightValues(null);
                    lastTitulo = true;
                    lastX = false;
                    lastY = false;
                    fitInTemplate(titulo.getText().toString(), FLAG_TITULO, -1);
                }

                return true;
            }
        });

        ylabel.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                int x = (int)event.getRawX();
                int y = (int)event.getRawY();

                int leftBound = chart.getLeft();
                int upperBound = chart.getTop();
                int downerBound = chart.getBottom() + (chart.getBottom()/12);


                if(event.getAction() == MotionEvent.ACTION_MOVE) {
                    if(isViewInBounds(chart, x, y)) {
                        isInside = true;

                        MotionEvent newTouch = MotionEvent.obtain(event.getDownTime(), event.getEventTime(), MotionEvent.ACTION_DOWN,
                                event.getRawX(), event.getRawY(), event.getMetaState());
                        Highlight highlight = chart.getHighlightByTouchPoint(event.getRawX() - chart.getLeft(), event.getRawY() - chart.getTop());

                            if (!highlight.equalTo(lastHighlight)) {
                                lastHighlight = highlight;
                                chart.highlightValue(highlight, true);
                            }
                        lastHighlight = highlight;
                        first = false;
                        lastTitulo = false;
                        lastY = false;
                        lastX = false;
                    }

                    // Se for arrastado para o Titulo
                    if(y < upperBound && !lastTitulo) {
                        isInside = false;
                        chart.highlightValues(null);
                        lastTitulo = true;
                        lastX = false;
                        lastY = false;
                        fitInTemplate(titulo.getText().toString(), FLAG_TITULO, -1);

                        Toast.makeText(getApplicationContext(), "Titulo", Toast.LENGTH_LONG).show();
                    }

                    // Se for arrastado para o LabelX
                    if(y > downerBound && !lastX){
                        isInside = false;
                        chart.highlightValues(null);
                        lastX = true;
                        lastY = false;
                        lastTitulo = false;
                        fitInTemplate(xlabel.getText().toString(), FLAG_XLABEL, -1);
                        //Here goes code to execute on onTouch ViewA
                    }

                    // Se for arrastado para o labelY
                    if((x < leftBound) && !lastY){
                        isInside = false;
                        chart.highlightValues(null);
                        lastHighlight = null;
                        lastY = true;
                        lastX = false;
                        lastTitulo = false;
                        fitInTemplate(ylabel.getText().toString(), FLAG_YLABEL, -1);
                        //Here goes code to execute on onTouch ViewA
                    }

                }

                if(event.getAction() == MotionEvent.ACTION_DOWN ) {
                    if(x < leftBound) {
                        isInside = false;
                        chart.highlightValues(null);
                        lastHighlight = null;
                        lastY = true;
                        lastX = false;
                        lastTitulo = false;
                        fitInTemplate(ylabel.getText().toString(), FLAG_YLABEL, -1);
                    }

                    if(isViewInBounds(chart, x, y)){

                        if(isViewInBounds(chart, x, y)) {
                            isInside = true;

                            MotionEvent newTouch = MotionEvent.obtain(event.getDownTime(), event.getEventTime(), MotionEvent.ACTION_DOWN,
                                    event.getRawX(), event.getRawY(), event.getMetaState());
                            Highlight highlight = chart.getHighlightByTouchPoint(event.getRawX() - chart.getLeft(), event.getRawY() - chart.getTop());

                            if (!highlight.equalTo(lastHighlight)) {
                                lastHighlight = highlight;
                                chart.highlightValue(highlight, true);
                            }
                            lastHighlight = highlight;
                            first = false;
                            lastTitulo = false;
                            lastY = false;
                            lastX = false;
                        }

                    }
                }

                return true;
            }
        });

        xlabel.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                int x = (int)event.getRawX();
                int y = (int)event.getRawY();

                int leftBound = chart.getLeft();
                int upperBound = chart.getTop();
                int downerBound = chart.getBottom() + (chart.getBottom()/12);


                if(event.getAction() == MotionEvent.ACTION_MOVE) {
                    if(isViewInBounds(chart, x, y)) {
                        isInside = true;

                        MotionEvent newTouch = MotionEvent.obtain(event.getDownTime(), event.getEventTime(), MotionEvent.ACTION_DOWN,
                                event.getRawX(), event.getRawY(), event.getMetaState());
                        Highlight highlight = chart.getHighlightByTouchPoint(event.getRawX() - chart.getLeft(), event.getRawY() - chart.getTop());

                            if (!highlight.equalTo(lastHighlight)) {
                                lastHighlight = highlight;
                                chart.highlightValue(highlight, true);
                            }

                        lastHighlight = highlight;
                        first = false;
                        lastTitulo = false;
                        lastY = false;
                        lastX = false;
                    }

                    // Se for arrastado para o Titulo
                    if(y < upperBound && !lastTitulo) {
                        isInside = false;
                        chart.highlightValues(null);
                        lastTitulo = true;
                        lastX = false;
                        lastY = false;
                        fitInTemplate(titulo.getText().toString(), FLAG_TITULO, -1);

                        Toast.makeText(getApplicationContext(), "Titulo", Toast.LENGTH_LONG).show();
                    }

                    // Se for arrastado para o LabelX
                    if(y > downerBound && !lastX){
                        isInside = false;
                        chart.highlightValues(null);
                        lastHighlight = null;
                        lastX = true;
                        lastY = false;
                        lastTitulo = false;
                        fitInTemplate(xlabel.getText().toString(), FLAG_XLABEL, -1);
                        //Here goes code to execute on onTouch ViewA
                    }

                    // Se for arrastado para o labelY
                    if((x < leftBound) && !lastY){
                        isInside = false;
                        chart.highlightValues(null);
                        lastY = true;
                        lastX = false;
                        lastTitulo = false;
                        fitInTemplate(ylabel.getText().toString(), FLAG_YLABEL, -1);
                        //Here goes code to execute on onTouch ViewA
                    }

                }

                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    if((x < leftBound) && !lastY) {


                    }
                    isInside = false;
                    lastHighlight = null;
                    chart.highlightValues(null);
                    lastX = true;
                    lastY = false;
                    lastTitulo = false;
                    fitInTemplate(xlabel.getText().toString(), FLAG_XLABEL, -1);

                    //Here goes code to execute on onTouch ViewA
                }

                return true;
            }
        });


        constraintLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {


                int x = (int)event.getRawX();
                int y = (int)event.getRawY();

                int leftBound = chart.getLeft();
                int upperBound = chart.getTop();
                int downerBound = chart.getBottom() + (chart.getBottom()/12);


                if(event.getAction() == MotionEvent.ACTION_MOVE) {
                    if(isViewInBounds(chart, x, y)) {
                        isInside = true;

                        MotionEvent newTouch = MotionEvent.obtain(event.getDownTime(), event.getEventTime(), MotionEvent.ACTION_DOWN,
                                event.getRawX(), event.getRawY(), event.getMetaState());
                        Highlight highlight = chart.getHighlightByTouchPoint(event.getRawX() - chart.getLeft(), event.getRawY() - chart.getTop());


                            if (!highlight.equalTo(lastHighlight)) {
                                lastHighlight = highlight;
                                chart.highlightValue(highlight, true);
                            }

                        lastHighlight = highlight;
                        first = false;
                        lastTitulo = false;
                        lastY = false;
                        lastX = false;
                    }

                    // Se for arrastado para o Titulo
                    if(y < upperBound && !lastTitulo) {
                        isInside = false;
                        chart.highlightValues(null);
                        lastHighlight = null;
                        lastTitulo = true;
                        lastX = false;
                        lastY = false;
                        fitInTemplate(titulo.getText().toString(), FLAG_TITULO, -1);

                        Toast.makeText(getApplicationContext(), "Titulo", Toast.LENGTH_LONG).show();
                    }

                    // Se for arrastado para o LabelX
                    if(y > downerBound && !lastX){
                        isInside = false;
                        chart.highlightValues(null);
                        lastHighlight = null;
                        lastX = true;
                        lastY = false;
                        lastTitulo = false;
                        fitInTemplate(xlabel.getText().toString(), FLAG_XLABEL, -1);
                        //Here goes code to execute on onTouch ViewA
                    }

                    // Se for arrastado para o labelY
                    if((x < leftBound) && !lastY){
                        isInside = false;
                        chart.highlightValues(null);
                        lastHighlight = null;
                        lastY = true;
                        lastX = false;
                        lastTitulo = false;
                        fitInTemplate(ylabel.getText().toString(), FLAG_YLABEL, -1);
                        //Here goes code to execute on onTouch ViewA
                    }

                }

                if(event.getAction() == MotionEvent.ACTION_DOWN) {



                    // Se for arrastado para o Titulo
                    if(y < upperBound) {
                        isInside = false;
                        lastHighlight = null;
                        chart.highlightValues(null);
                        lastTitulo = true;
                        lastX = false;
                        lastY = false;
                        fitInTemplate(titulo.getText().toString(), FLAG_TITULO, -1);

                        Toast.makeText(getApplicationContext(), "Titulo", Toast.LENGTH_LONG).show();
                    }

                    // Se for arrastado para o LabelX
                    if(y > downerBound){
                        lastHighlight = null;
                        isInside = false;
                        chart.highlightValues(null);
                        lastX = true;
                        lastY = false;
                        lastTitulo = false;
                        fitInTemplate(xlabel.getText().toString(), FLAG_XLABEL, -1);
                        //Here goes code to execute on onTouch ViewA
                    }

                    // Se for arrastado para o labelY
                    if((x < leftBound)){
                        lastHighlight = null;
                        isInside = false;
                        chart.highlightValues(null);
                        lastY = true;
                        lastX = false;
                        lastTitulo = false;
                        fitInTemplate(ylabel.getText().toString(), FLAG_YLABEL, -1);
                        //Here goes code to execute on onTouch ViewA
                    }
                }

                return true;
            }
        });

    }

    private boolean isViewInBounds(View view, int x, int y){
        view.getDrawingRect(outRect);
        view.getLocationOnScreen(location);
        outRect.offset(location[0], location[1]);
        return outRect.contains(x, y);
    }

    private void fitInTemplate(String value, int flag, int index){
        String tituloStr = "O título é denominado. ";
        String legendayStr =  "A legenda do eixo y é denominada. ";
        String legendaxStr =  "A legenda do eixo x é denominada. ";

        switch(flag){
            case FLAG_TITULO:
                tts(tituloStr + value);
                vibrate(200);
                break;
            case FLAG_XLABEL:
                tts(legendaxStr + value);
                vibrate(200);
                break;
            case FLAG_YLABEL:
                tts(legendayStr + value);
                vibrate(200);
                break;
            case FLAG_CHARTVALUE:
                tts("A" + (index + 1) + "ª barra é denominada. " + chartData.getLabels()[index] + " e apresenta valor. " + value);
                vibrate(100);
                //Do this and this:
                break;
            default: //For all other cases, do this
                break;
        }
    }

    private void hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(mShakeHelper, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
    }


    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(mShakeHelper);
    }

}