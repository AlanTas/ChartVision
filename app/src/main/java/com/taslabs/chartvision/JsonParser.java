package com.taslabs.chartvision;
import com.taslabs.chartvision.BarChartObj;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class JsonParser {

    public JSONObject obj;
    public BarChartObj barChartObj;

    public String loadJSONFromAsset(Context context) {
        String json = null;
        try {
            InputStream is = context.getAssets().open("f atmen.json");

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
        //barChartObj.printChart();

        return barChartObj;
    }

    public BarChartObj getBarChartObj() {
        return barChartObj;
    }
}
