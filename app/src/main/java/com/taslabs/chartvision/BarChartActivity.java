package com.taslabs.chartvision;
import android.content.Context;
import android.graphics.Rect;
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
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.taslabs.chartvision.JsonParser;

public class BarChartActivity extends AppCompatActivity {
    TextToSpeech textToSpeech;
    BarChart chart;
    BarChartObj chartData;
    TextView titulo;
    TextView xlabel;
    TextView ylabel;
    ConstraintLayout constraintLayout;
    String url = null;

    // in View Bounds variables
    Rect outRect = new Rect();
    int[] location = new int[2];
    // in View Bounds variables

    // Control for TTS repeats
    boolean lastTitulo = false;
    boolean lastY = false;
    boolean lastX = false;
    boolean first = true;
    Highlight lastHighlight;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar_chart);
        chart = findViewById(R.id.barchart);

        titulo = findViewById(R.id.title);
        xlabel = findViewById(R.id.xlabel);
        ylabel = findViewById(R.id.ylabel);
        constraintLayout = findViewById(R.id.layout);

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
                }
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


//        jsonParser.loadJSONFromAsset(this);
//        chartData = jsonParser.getBarChartObj();
//        starListeners();
//        stpChart();

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

        chart.getAxisLeft().setDrawGridLines(false);
        chart.getAxisRight().setDrawGridLines(false);
        chart.getXAxis().setDrawGridLines(false);
        chart.getDescription().setText("Description of my chart");

        XAxis xAxis = chart.getXAxis();

        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        chart.setScaleEnabled(false); // Disable all zooming

        YAxis rightYAxis = chart.getAxisRight();
        rightYAxis.setEnabled(false);

        chart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry entry, Highlight h) {

                System.out.println("RESETOU");
                lastTitulo = false;
                lastX = false;
                lastY = false;


                Long entryX = (long) entry.getY();
                Toast.makeText(getApplicationContext(), String.valueOf(entryX), Toast.LENGTH_LONG).show();

                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                // Vibrate for 500 milliseconds
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    v.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    //deprecated in API 26
                    v.vibrate(100);
                }

                tts(String.valueOf(entryX));
            }

            @Override
            public void onNothingSelected() {

            }
        });
//        chart.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                System.out.println("CHAMOU CHAMOU CHAMOU");
//            }
//        });
        chart.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int x = (int)event.getRawX();
                int y = (int)event.getRawY();
                System.out.println("X - " + x);
                System.out.println("Y - " + y);
                int leftBound = v.getLeft();
                int upperBound = v.getTop();
                int downerBound = v.getBottom();
                if(event.getAction() == MotionEvent.ACTION_MOVE){

                    // Se for arrastado para o Titulo
                    if(y < upperBound && !lastTitulo) {
                        lastTitulo = true;
                        lastX = false;
                        lastY = false;
                        tts(titulo.getText().toString());

                        Toast.makeText(getApplicationContext(), "Titulo", Toast.LENGTH_LONG).show();
                    }

                    // Se for arrastado para o LabelX
                    if(y > downerBound && !lastX){
                        lastX = true;
                        lastY = false;
                        lastTitulo = false;
                        tts(xlabel.getText().toString());
                        //Here goes code to execute on onTouch ViewA
                    }

                    // Se for arrastado para o labelY
                    if((x < leftBound) && !lastY){
                        lastY = true;
                        lastX = false;
                        lastTitulo = false;
                        tts(ylabel.getText().toString());
                        //Here goes code to execute on onTouch ViewA
                    }

                }
                // Further touch is not handled
                return false;
            }
        });

    }

    public void tts(String text){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        }
        else {
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    public void startListeners(){

        titulo.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                int x = (int)event.getRawX();
                int y = (int)event.getRawY();

                int leftBound = chart.getLeft();
                int upperBound = chart.getTop();
                int downerBound = chart.getBottom();

                System.out.println("X Y da outra: " + event.getRawX() + " " + event.getRawY());
                if(event.getAction() == MotionEvent.ACTION_MOVE) {
                    if(isViewInBounds(chart, x, y)) {

                        MotionEvent newTouch = MotionEvent.obtain(event.getDownTime(), event.getEventTime(), MotionEvent.ACTION_DOWN,
                                event.getRawX(), event.getRawY(), event.getMetaState());
                        Highlight highlight = chart.getHighlightByTouchPoint(event.getRawX() - chart.getLeft(), event.getRawY() - chart.getTop());

                        if(!first) {
                            if (!highlight.equalTo(lastHighlight)) {
                                lastHighlight = highlight;
                                chart.highlightValue(highlight, true);
                            }
                        }
                        lastHighlight = highlight;
                        first = false;
                    }

                    // Se for arrastado para o Titulo
                    if(y < upperBound && !lastTitulo) {
                        lastTitulo = true;
                        lastX = false;
                        lastY = false;
                        tts(titulo.getText().toString());

                        Toast.makeText(getApplicationContext(), "Titulo", Toast.LENGTH_LONG).show();
                    }

                    // Se for arrastado para o LabelX
                    if(y > downerBound && !lastX){
                        lastX = true;
                        lastY = false;
                        lastTitulo = false;
                        tts(xlabel.getText().toString());
                        //Here goes code to execute on onTouch ViewA
                    }

                    // Se for arrastado para o labelY
                    if((x < leftBound) && !lastY){
                        lastY = true;
                        lastX = false;
                        lastTitulo = false;
                        tts(ylabel.getText().toString());
                        //Here goes code to execute on onTouch ViewA
                    }

                }

                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    lastTitulo = true;
                    lastX = false;
                    lastY = false;
                    tts(titulo.getText().toString());
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
                int downerBound = chart.getBottom();

                System.out.println("X Y da outra: " + event.getRawX() + " " + event.getRawY());
                if(event.getAction() == MotionEvent.ACTION_MOVE) {
                    if(isViewInBounds(chart, x, y)) {

                        MotionEvent newTouch = MotionEvent.obtain(event.getDownTime(), event.getEventTime(), MotionEvent.ACTION_DOWN,
                                event.getRawX(), event.getRawY(), event.getMetaState());
                        Highlight highlight = chart.getHighlightByTouchPoint(event.getRawX() - chart.getLeft(), event.getRawY() - chart.getTop());

                        if(!first) {
                            if (!highlight.equalTo(lastHighlight)) {
                                lastHighlight = highlight;
                                chart.highlightValue(highlight, true);
                            }
                        }
                        lastHighlight = highlight;
                        first = false;
                    }

                    // Se for arrastado para o Titulo
                    if(y < upperBound && !lastTitulo) {
                        lastTitulo = true;
                        lastX = false;
                        lastY = false;
                        tts(titulo.getText().toString());

                        Toast.makeText(getApplicationContext(), "Titulo", Toast.LENGTH_LONG).show();
                    }

                    // Se for arrastado para o LabelX
                    if(y > downerBound && !lastX){
                        lastX = true;
                        lastY = false;
                        lastTitulo = false;
                        tts(xlabel.getText().toString());
                        //Here goes code to execute on onTouch ViewA
                    }

                    // Se for arrastado para o labelY
                    if((x < leftBound) && !lastY){
                        lastY = true;
                        lastX = false;
                        lastTitulo = false;
                        tts(ylabel.getText().toString());
                        //Here goes code to execute on onTouch ViewA
                    }

                }

                if(event.getAction() == MotionEvent.ACTION_DOWN && x < leftBound) {
                    lastY = true;
                    lastX = false;
                    lastTitulo = false;
                    tts(ylabel.getText().toString());
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

                System.out.println("X Y da outra: " + event.getRawX() + " " + event.getRawY());
                if(event.getAction() == MotionEvent.ACTION_MOVE) {
                    if(isViewInBounds(chart, x, y)) {

                        MotionEvent newTouch = MotionEvent.obtain(event.getDownTime(), event.getEventTime(), MotionEvent.ACTION_DOWN,
                                event.getRawX(), event.getRawY(), event.getMetaState());
                        Highlight highlight = chart.getHighlightByTouchPoint(event.getRawX() - chart.getLeft(), event.getRawY() - chart.getTop());

                        if(!first) {
                            if (!highlight.equalTo(lastHighlight)) {
                                lastHighlight = highlight;
                                chart.highlightValue(highlight, true);
                            }
                        }
                        lastHighlight = highlight;
                        first = false;
                    }

                    // Se for arrastado para o Titulo
                    if(y < upperBound && !lastTitulo) {
                        lastTitulo = true;
                        lastX = false;
                        lastY = false;
                        tts(titulo.getText().toString());

                        Toast.makeText(getApplicationContext(), "Titulo", Toast.LENGTH_LONG).show();
                    }

                    // Se for arrastado para o LabelX
                    if(y > downerBound && !lastX){
                        lastX = true;
                        lastY = false;
                        lastTitulo = false;
                        tts(xlabel.getText().toString());
                        //Here goes code to execute on onTouch ViewA
                    }

                    // Se for arrastado para o labelY
                    if((x < leftBound) && !lastY){
                        lastY = true;
                        lastX = false;
                        lastTitulo = false;
                        tts(ylabel.getText().toString());
                        //Here goes code to execute on onTouch ViewA
                    }

                }

                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    lastX = true;
                    lastY = false;
                    lastTitulo = false;
                    tts(xlabel.getText().toString());
                    System.out.println("FOI DO X LABEL");
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

                System.out.println("X Y da outra: " + event.getRawX() + " " + event.getRawY());
                if(event.getAction() == MotionEvent.ACTION_MOVE) {
                    if(isViewInBounds(chart, x, y)) {

                        MotionEvent newTouch = MotionEvent.obtain(event.getDownTime(), event.getEventTime(), MotionEvent.ACTION_DOWN,
                                event.getRawX(), event.getRawY(), event.getMetaState());
                        Highlight highlight = chart.getHighlightByTouchPoint(event.getRawX() - chart.getLeft(), event.getRawY() - chart.getTop());

                        if(!first) {
                            if (!highlight.equalTo(lastHighlight)) {
                                lastHighlight = highlight;
                                chart.highlightValue(highlight, true);
                            }
                        }
                        lastHighlight = highlight;
                        first = false;
                    }

                    // Se for arrastado para o Titulo
                    if(y < upperBound && !lastTitulo) {
                        lastTitulo = true;
                        lastX = false;
                        lastY = false;
                        tts(titulo.getText().toString());

                        Toast.makeText(getApplicationContext(), "Titulo", Toast.LENGTH_LONG).show();
                    }

                    // Se for arrastado para o LabelX
                    if(y > downerBound && !lastX){
                        lastX = true;
                        lastY = false;
                        lastTitulo = false;
                        tts(xlabel.getText().toString());
                        //Here goes code to execute on onTouch ViewA
                    }

                    // Se for arrastado para o labelY
                    if((x < leftBound) && !lastY){
                        lastY = true;
                        lastX = false;
                        lastTitulo = false;
                        tts(ylabel.getText().toString());
                        //Here goes code to execute on onTouch ViewA
                    }

                }

                if(event.getAction() == MotionEvent.ACTION_DOWN) {

                    // Se for arrastado para o Titulo
                    if(y < upperBound) {
                        lastTitulo = true;
                        lastX = false;
                        lastY = false;
                        tts(titulo.getText().toString());

                        Toast.makeText(getApplicationContext(), "Titulo", Toast.LENGTH_LONG).show();
                    }

                    // Se for arrastado para o LabelX
                    if(y > downerBound){
                        lastX = true;
                        lastY = false;
                        lastTitulo = false;
                        tts(xlabel.getText().toString());
                        //Here goes code to execute on onTouch ViewA
                    }

                    // Se for arrastado para o labelY
                    if((x < leftBound)){
                        lastY = true;
                        lastX = false;
                        lastTitulo = false;
                        tts(ylabel.getText().toString());
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


}