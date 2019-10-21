package com.taslabs.chartvision;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
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

import androidx.annotation.ColorInt;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.Chart;
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

import com.github.mikephil.charting.utils.Utils;
import com.taslabs.chartvision.JsonParser;
import com.taslabs.chartvision.enums.FontSize;
import com.taslabs.chartvision.interfaces.IUser;
import com.taslabs.chartvision.userManager.UserManager;

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
    String json = null;
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

    //USER PREFS VARIABLES
    public boolean ttsEnabled = true;
    public boolean highContrastEnabled = false;
    public boolean hapticEnabled = true;
    public boolean vibrateToLeave = true;
    public FontSize fontSize = FontSize.Small;

    UserManager userManager;
    IUser user;



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

        userManager = UserManager.getInstance(this.getApplicationContext());

        Bundle extras = getIntent().getExtras();
        String username = extras.getString("user");
        user = userManager.getUser(username);

        setUserPrefs();

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

                    String totalRead = "";

                    if(true){
                        totalRead += "Por favor, desligue o recurso Tólque Béque. ";
                    }

                    if(vibrateToLeave){
                        totalRead += "Para sair da visualização do gráfico, sacuda seu aparelho. ";
                    }

                    totalRead += "Este é um gráfico de barras verticais";

                    tts(totalRead, true);
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
                if(vibrateToLeave) {
                    /*
                     * The following method, "handleShakeEvent(count):" is a stub //
                     * method you would use to setup whatever you want done once the
                     * device has been shook.
                     */
                    if(ttsEnabled) {
                        tts("Saindo. Por favor, re-ligue o Tólque Béque. Este é um gráfico de barras verticais.", true);
                    }

                    //textToSpeech.stop();
                    finish();
                }
            }
        });

        JsonParser jsonParser = new JsonParser();
        extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.containsKey("url")) {
                url = extras.getString("url");
                jsonParser.loadJSONFromURL(url);
            }

            else if(extras.containsKey("json")){
                json = extras.getString("json");
                jsonParser.loadJSONFromJsonString(json);
            }
        }



        chartData = jsonParser.getBarChartObj();
        startListeners();
        stpChart(fontSize, highContrastEnabled);

    }

    private void setUserPrefs() {

        System.out.println("TTS: " + Boolean.toString(user.isAudioEnabled()));
        System.out.println("Vibration: " + Boolean.toString(user.isVibrationEnabled()));
        System.out.println("HAPTIC: " + Boolean.toString(user.isShake2LeaveEnabled()));
        System.out.println("CONTRAST: " + Boolean.toString(user.isHighContrastEnabled()));
        System.out.println("FONT SIZE: " + user.getFontSize().toString());

        ttsEnabled = user.isAudioEnabled();
        vibrateToLeave = user.isShake2LeaveEnabled();
        highContrastEnabled = user.isHighContrastEnabled();
        hapticEnabled = user.isVibrationEnabled();
        fontSize = user.getFontSize();


    }


    public void stpChart(FontSize fontSize, boolean highContrastEnabled){

        XAxis xAxis = chart.getXAxis();

        @ColorInt int colorBackground = Color.parseColor("#000000");
        @ColorInt int colorFont = Color.parseColor("#ffff00");
        @ColorInt int colorBars = Color.parseColor("#66BB6A");
        @ColorInt int colorBarsHigh = Color.parseColor("#C8E6C9");

        // Carregando dados do gráfico
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
        if(this.highContrastEnabled){
            set.setColor(colorBars);
            set.setHighLightColor(colorBarsHigh);
        }
        BarData data = new BarData(set);
        chart.setData(data);

        // Configurações gerais do gráfico
        chart.setFitBars(true); // make the x-axis fit exactly all bars
        chart.invalidate(); // refresh
        chart.setDoubleTapToZoomEnabled(false);
        chart.setPinchZoom(false);
        chart.getAxisLeft().setDrawGridLines(false);
        chart.getLegend().setEnabled(false); //Desabilita a legenda
        chart.setScaleEnabled(false); // Disable all zooming
        chart.getXAxis().setDrawGridLines(false);
        chart.getDescription().setEnabled(false);
        chart.getAxisLeft().setDrawGridLines(false);
        chart.getAxisRight().setDrawGridLines(false);
        chart.getAxisRight().setEnabled(false);
        String[] labels = chartData.getLabels();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setLabelCount(lenght);
        xAxis.setDrawGridLines(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        chart.getAxisLeft().setAxisMinimum(0f);



        // Configurações especificas


        if(this.fontSize == FontSize.Small){
            float left = 0f;
            float top = 0f;
            float right = 0f;
            float bottom = 0f;

            chart.getXAxis().setTextSize(10f);
            chart.getAxisLeft().setTextSize(10f);
            chart.getBarData().setValueTextSize(10f);


            chart.setExtraOffsets(left, top, right, bottom);

        }

        else if(this.fontSize == FontSize.Medium) {
            float left = 0f;
            float top = 0f;
            float right = 0f;
            float bottom = 4f;

            chart.getXAxis().setTextSize(15f);
            chart.getAxisLeft().setTextSize(15f);
            chart.getBarData().setValueTextSize(15f);


            chart.setExtraOffsets(left, top, right, bottom);
        }

        else if(this.fontSize == FontSize.Large){
            float left = 0f;
            float top = 0f;
            float right = 0f;
            float bottom = 7f;

            chart.getXAxis().setTextSize(20f);
            chart.getAxisLeft().setTextSize(20f);
            chart.getBarData().setValueTextSize(20f);


            chart.setExtraOffsets(left, top, right, bottom);
        }

        if(this.highContrastEnabled){

            chart.setBackgroundColor(colorBackground);
            chart.getXAxis().setTextColor(colorFont);
            chart.getAxisLeft().setTextColor(colorFont);
            chart.getBarData().setValueTextColor(colorFont);

            chart.getXAxis().setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            chart.getAxisLeft().setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            chart.getBarData().setValueTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            titulo.setTextColor(colorFont);
            ylabel.setTextColor(colorFont);
            xlabel.setTextColor(colorFont);

            titulo.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            ylabel.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            xlabel.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));

            constraintLayout.setBackgroundColor(colorBackground);

        }



    }

    public void tts(String text, boolean override){
        if(ttsEnabled || override) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
            } else {
                textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
            }
        }
    }

    private void vibrate(int duration){
        if(hapticEnabled) {
            // Vibrate for 500 milliseconds
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                //deprecated in API 26
                v.vibrate(duration);
            }
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
                    //Toast.makeText(getApplicationContext(), String.valueOf(entryY), Toast.LENGTH_LONG).show();
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

                        //Toast.makeText(getApplicationContext(), "Titulo", Toast.LENGTH_LONG).show();
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

                        //Toast.makeText(getApplicationContext(), "Titulo", Toast.LENGTH_LONG).show();
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

                        //Toast.makeText(getApplicationContext(), "Titulo", Toast.LENGTH_LONG).show();
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

                        //Toast.makeText(getApplicationContext(), "Titulo", Toast.LENGTH_LONG).show();
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

                        //Toast.makeText(getApplicationContext(), "Titulo", Toast.LENGTH_LONG).show();
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
        String tituloStr = "O título é. ";
        String legendayStr =  "A legenda do eixo y é. ";
        String legendaxStr =  "A legenda do eixo x é. ";

        switch(flag){
            case FLAG_TITULO:
                tts(tituloStr + value, false);
                vibrate(200);
                break;
            case FLAG_XLABEL:
                tts(legendaxStr + value, false);
                vibrate(200);
                break;
            case FLAG_YLABEL:
                tts(legendayStr + value, false);
                vibrate(200);
                break;
            case FLAG_CHARTVALUE:
                tts("A" + (index + 1) + "ª barra é. " + chartData.getLabels()[index] + " e apresenta valor. " + chartData.getValues()[index], false);
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
        hideSystemUI();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        hideSystemUI();
    }


    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(mShakeHelper);
        //textToSpeech.shutdown();
    }

    @Override
    public void onBackPressed() {
        if(ttsEnabled) {
            tts("Saindo. Por favor, re-ligue o Tólque Béque", true);
        }

        super.onBackPressed();

    }


}