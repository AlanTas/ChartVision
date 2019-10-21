package com.taslabs.chartvision;
import com.taslabs.chartvision.BarChartObj;

import android.content.Context;
import android.os.AsyncTask;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.HttpsURLConnection;

public class JsonParser {

    public JSONObject obj;
    public BarChartObj barChartObj;
    public GroupedBarChartObj groupedBarChartObj;



    public String loadJSONFromAsset(Context context) {
        String json = null;
        try {
            InputStream is = context.getAssets().open("fatmen.json");

            int size = is.available();

            byte[] buffer = new byte[size];

            is.read(buffer);

            is.close();

            json = new String(buffer, "UTF-8");

        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }

        try {
            obj = new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        getDataFromJson(obj);

        return json;
    }

    public void loadJSONFromJsonString(String json) {
        JSONObject encoding = null;

        try {
            obj = new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            encoding = obj.getJSONObject("encoding");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (encoding.has("column")){
            getGroupedDataFromJson(obj);
        }
        else {
            getDataFromJson(obj);
        }

    }


    public String loadJSONFromURL(String url) {
        JSONObject encoding = null;
        try {
            MyTask task = new MyTask();
            String objeto =  task.execute(url).get();

            try {
                obj = new JSONObject(objeto);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                encoding = obj.getJSONObject("encoding");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (encoding.has("column")){
                getGroupedDataFromJson(obj);
            }
            else {
                getDataFromJson(obj);
            }


        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return null;
    }

    public BarChartObj getDataFromJson(JSONObject json){

        String TitleS = null;
        String xLabelS = null;
        String yLabelS = null;
        String[] labelsS = null;
        double[] valuesS = null;


        // Get Title

        try {
            TitleS = String.valueOf(json.get("title"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Get Axis Labels

        try {
            JSONObject encoding = json.getJSONObject("encoding");
            xLabelS = String.valueOf(encoding.getJSONObject("x").get("field"));
            yLabelS = String.valueOf(encoding.getJSONObject("y").get("field"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Ger Labels and Values
        try {
            JSONArray values = json.getJSONObject("data").getJSONArray("values");
            valuesS = new double[values.length()];
            labelsS = new String[values.length()];

            for (int i = 0; i < values.length(); i++){

                JSONObject each = values.getJSONObject(i);
                labelsS[i] = String.valueOf(each.get(xLabelS));
                valuesS[i] = each.getDouble(yLabelS);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        barChartObj = new BarChartObj(TitleS, xLabelS, yLabelS, labelsS, valuesS);
        barChartObj.printChart();

        return barChartObj;
    }

    public GroupedBarChartObj getGroupedDataFromJson(JSONObject json){

        String TitleS = null;
        String xLabelS = null;
        String yLabelS = null;
        List<String> groups = new ArrayList<String>();
        List<String> series = new ArrayList<String>();
        List<List> values = new ArrayList<>();



        // Get Title

        try {
            TitleS = String.valueOf(json.get("title"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Get Axis Labels
        String fieldGroups = "";
        String fieldSeries = "";
        String fieldValues = "";

        try {
            JSONObject encoding = json.getJSONObject("encoding");
            xLabelS = String.valueOf(encoding.getJSONObject("x").getJSONObject("axis").get("title"));
            yLabelS = String.valueOf(encoding.getJSONObject("y").getJSONObject("axis").get("title"));
            fieldGroups = String.valueOf(encoding.getJSONObject("column").get("field"));
            fieldSeries = String.valueOf(encoding.getJSONObject("x").get("field"));
            fieldValues = String.valueOf(encoding.getJSONObject("y").get("field"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        System.out.println("Titulo: " + TitleS);
        System.out.println("X Label: " + xLabelS);
        System.out.println("Y Label: " + yLabelS);
        System.out.println("Field Groups: " + fieldGroups);
        System.out.println("Field Series: " + fieldSeries);
        System.out.println("Field Values: " + fieldValues);





        try {
            // Get Groups
            JSONArray valuesKey = json.getJSONObject("data").getJSONArray("values");

            for (int i = 0; i < valuesKey.length(); i++){
                JSONObject each = valuesKey.getJSONObject(i);
                String currentGroup = each.getString(fieldGroups);
                String currentSerie = each.getString(fieldSeries);
                if (!groups.contains(currentGroup)) {
                    groups.add(currentGroup);
                }

                if (!series.contains(currentSerie)) {
                    series.add(currentSerie);
                }
            }

            System.out.println("SIZE GROUPS: " +groups.size());
            System.out.println("SIZE SERIES: " +series.size());

            // Get Values
            for(int i = 0; i < series.size(); i++){
                String serieAtual = series.get(i);

                List<Float> currentSerie = new ArrayList<>();

                for(int j = 0; j < groups.size(); j++){
                    String grupoAtual = groups.get(j);
                    System.out.println(serieAtual + " " + grupoAtual);

                    for (int k = 0; k < valuesKey.length(); k++){
                        JSONObject each = valuesKey.getJSONObject(k);

                        if(each.getString(fieldSeries).equals(serieAtual) && each.getString(fieldGroups).equals(grupoAtual)) {
                            System.out.println(each.toString());
                            currentSerie.add(Float.valueOf(each.getString(fieldValues)));
                        }


                    }
                }
                values.add(currentSerie);
            }

            System.out.println("Values: " + values.toString());

            System.out.println("GROUPS: ");
            for( int i = 0 ; i < groups.size(); i++){
                System.out.println(groups.get(i));
            }

            System.out.println("SERIES: ");
            for( int i = 0 ; i < series.size(); i++){
                System.out.println(series.get(i));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


        groupedBarChartObj = new GroupedBarChartObj(TitleS, xLabelS, yLabelS, series, groups, values);

        return groupedBarChartObj;
    }

    public BarChartObj getBarChartObj() {
        return barChartObj;
    }

    public GroupedBarChartObj getGroupedBarChartObj() {
        return groupedBarChartObj;
    }

    public boolean isGrouped(String url){
        JSONObject encoding = null;
        boolean isGrouped = false;

        try {
            MyTask task = new MyTask();
            String objeto =  task.execute(url).get();

            try {
                obj = new JSONObject(objeto);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                encoding = obj.getJSONObject("encoding");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (encoding.has("column")){
                isGrouped = true;
            }

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

       return isGrouped;
    }
}

class MyTask extends AsyncTask<String, Void, String> {

    @Override
    protected String doInBackground(String... params) {

        HttpsURLConnection con = null;
        URLConnection conFile = null;

        try {
            if(params[0].contains("https")) {
                URL u = new URL(params[0]);
                con = (HttpsURLConnection) u.openConnection();

                con.connect();


                BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line + "\n");
                }
                br.close();
                return sb.toString();
            }

            else {

                File file = new File(params[0]);


                System.out.println("URL:" +  params[0]);
                System.out.println("PATH:" + file.getCanonicalPath());

                if(file.exists()) {
                    System.out.println("EXISTE");
                }
                URL u = new URL(params[0]);
                conFile = u.openConnection();

                conFile.connect();

                BufferedReader br = new BufferedReader(new InputStreamReader(conFile.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line + "\n");
                }
                br.close();
                return sb.toString();
            }



        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (con != null) {
                try {
                    con.disconnect();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        return null;

    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

    }
}
