package com.taslabs.chartvision;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;

public class LocalFileActivity extends AppCompatActivity {
    Button btnFile;
    final int PICKFILE_RESULT_CODE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_file);

        btnFile = findViewById(R.id.button);

        btnFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) throws ActivityNotFoundException {

                Intent fileintent = new Intent(Intent.ACTION_GET_CONTENT);
                fileintent.setType("application/octet-stream");
                startActivityForResult(fileintent, PICKFILE_RESULT_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == PICKFILE_RESULT_CODE)
        {
                Uri uri = intent.getData();

                String type = intent.getType();
                System.out.println("Pick completed: "+ uri + " "+type);
                if (uri != null)
                {
                    try {
                        InputStream inputStream = getContentResolver().openInputStream(uri);

                        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
                        StringBuilder sb = new StringBuilder();
                        String line;

                        while ((line = br.readLine()) != null) {
                            sb.append(line + "\n");
                        }
                        br.close();
//                        System.out.println("OUTPUT");
//                        System.out.println(sb.toString());

                        String intentData = sb.toString();

                        JSONObject encoding = null;
                        JSONObject obj = null;
                        try {
                            obj = new JSONObject(intentData);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        try {
                            encoding = obj.getJSONObject("encoding");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        System.out.println("AQUIAQUIAQUIAQUIAQUIAQUIAQUI");

                        if (encoding.has("column")){
                            Intent i = new Intent(LocalFileActivity.this, GroupedBarChartActivity.class);
                            i.putExtra("json", intentData);
                            startActivity(i);
                        }
                        else {
                            Intent i = new Intent(LocalFileActivity.this, BarChartActivity.class);
                            i.putExtra("json", intentData);
                            startActivity(i);
                        }



                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
        }
    }
}
