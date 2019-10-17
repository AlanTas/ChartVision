package com.taslabs.chartvision;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.github.mikephil.charting.charts.BarChart;
import com.taslabs.chartvision.interfaces.IUser;
import com.taslabs.chartvision.userManager.UserManager;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ChooseChartOriginActivity extends AppCompatActivity {
    final int PICKFILE_RESULT_CODE = 0;
    Button btnQR, btnNFC, btnLOCAL, btnConfig, btnDEMOGROUPED;
    TextView siglaNome;
    UserManager userManager;
    IUser user;
    ConstraintLayout chooseConstraint;
    String color = null;
    ConstraintLayout layoutUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_chart_origin);
        BarChart barChart = (BarChart) findViewById(R.id.barchart);


        userManager = UserManager.getInstance(this.getApplicationContext());
        Bundle extras = getIntent().getExtras();
        String username = extras.getString("user");
        color = extras.getString("color");
        user = userManager.getUser(username);
        chooseConstraint = findViewById(R.id.constraintOrigin);
        chooseConstraint.setBackgroundColor(getResources().getColor(R.color.colorBkg));

        System.out.println("USUÁRIO ATUAL:" + user.getName());


        layoutUser = findViewById(R.id.constraintUserBox);
        layoutUser.setBackgroundColor(getResources().getColor(R.color.colorPrimary));


        btnConfig = findViewById(R.id.btnConfigs);
        btnConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnConfig.setEnabled(false);
                Intent I = new Intent(ChooseChartOriginActivity.this, UserSetupActivity.class);
                I.putExtra("type", true);
                I.putExtra("color", color);
                I.putExtra("user", user.getName());
                startActivity(I);
                finish();
            }
        });


        btnQR = findViewById(R.id.btnQR);
        btnQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnQR.setEnabled(false);
                Intent I = new Intent(ChooseChartOriginActivity.this, QRScanActivity.class);
                I.putExtra("user", user.getName());
                startActivity(I);
            }
        });

        btnDEMOGROUPED = findViewById(R.id.btnDEMOGROUPED);
        btnDEMOGROUPED.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnDEMOGROUPED.setEnabled(false);
                Intent I = new Intent(ChooseChartOriginActivity.this, GroupedBarChartActivity.class);
                I.putExtra("user", user.getName());
                startActivity(I);
            }
        });

        btnNFC = findViewById(R.id.btnNFC);
        btnNFC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnNFC.setEnabled(false);
                Intent I = new Intent(ChooseChartOriginActivity.this, NFCActivity.class);
                I.putExtra("user", user.getName());
                startActivity(I);
            }
        });


        btnLOCAL = findViewById(R.id.btnLocal);
        btnLOCAL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnLOCAL.setEnabled(false);

                Intent fileintent = new Intent(Intent.ACTION_GET_CONTENT);
                fileintent.setType("application/octet-stream");
                startActivityForResult(fileintent, PICKFILE_RESULT_CODE);

            }
        });

        setButtonColors();


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
                    System.out.println("OUTPUT");
                    System.out.println(sb.toString());

                    String intentData = sb.toString();
                    Intent i = new Intent(ChooseChartOriginActivity.this, BarChartActivity.class);
                    i.putExtra("user", user.getName());
                    i.putExtra("json", intentData);
                    startActivity(i);


                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void setButtonColors() {

        if (color.equals("1")){
            System.out.println("ENTROU AQUI");
            btnLOCAL.setBackgroundColor(getResources().getColor(R.color.colorFirstUser));
            btnNFC.setBackgroundColor(getResources().getColor(R.color.colorFirstUser));
            btnQR.setBackgroundColor(getResources().getColor(R.color.colorFirstUser));
            btnConfig.setBackgroundColor(getResources().getColor(R.color.colorFirstUser));
        }

        else if (color.equals("2")){
            btnLOCAL.setBackgroundColor(getResources().getColor(R.color.colorSecondUser));
            btnNFC.setBackgroundColor(getResources().getColor(R.color.colorSecondUser));
            btnQR.setBackgroundColor(getResources().getColor(R.color.colorSecondUser));
            btnConfig.setBackgroundColor(getResources().getColor(R.color.colorSecondUser));

        }

        else if (color.equals("3")){
            btnLOCAL.setBackgroundColor(getResources().getColor(R.color.colorThirdUser));
            btnNFC.setBackgroundColor(getResources().getColor(R.color.colorThirdUser));
            btnQR.setBackgroundColor(getResources().getColor(R.color.colorThirdUser));
            btnConfig.setBackgroundColor(getResources().getColor(R.color.colorThirdUser));
        }

        else if (color.equals("4")){
            btnLOCAL.setBackgroundColor(getResources().getColor(R.color.colorForthUser));
            btnNFC.setBackgroundColor(getResources().getColor(R.color.colorForthUser));
            btnQR.setBackgroundColor(getResources().getColor(R.color.colorForthUser));
            btnConfig.setBackgroundColor(getResources().getColor(R.color.colorForthUser));
        }
    }

    @Override

    public void onResume(){
        super.onResume();
        btnConfig.setEnabled(true);
        btnQR.setEnabled(true);
        btnNFC.setEnabled(true);
        btnLOCAL.setEnabled(true);
    }
}
