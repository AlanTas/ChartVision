package com.taslabs.chartvision;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.github.mikephil.charting.charts.BarChart;
import com.taslabs.chartvision.interfaces.IUser;
import com.taslabs.chartvision.userManager.User;
import com.taslabs.chartvision.userManager.UserManager;

public class ChooseChartOriginActivity extends AppCompatActivity {
    Button btnBarChart, btnQR, btnNFC, btnLOCAL, btnRemove, btnConfig;
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

        System.out.println("USUÁRIO ATUAL:" +user.getName());


//        btnBarChart = findViewById(R.id.btnBarChart);
//        btnBarChart.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent I = new Intent(ChooseChartOriginActivity.this, BarChartActivity.class);
//                I.putExtra("url", "https://alantas.dev/jsons/frutas.json");
//                startActivity(I);
//            }
//        });

        layoutUser = findViewById(R.id.constraintUserBox);
        layoutUser.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        btnConfig = findViewById(R.id.btnConfigs);

        btnQR = findViewById(R.id.btnQR);
        btnQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent I = new Intent(ChooseChartOriginActivity.this, QRScanActivity.class);
                I.putExtra("user", user.getName());
                startActivity(I);
            }
        });

        btnNFC = findViewById(R.id.btnNFC);
        btnNFC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent I = new Intent(ChooseChartOriginActivity.this, NFCActivity.class);
                I.putExtra("user", user.getName());
                startActivity(I);
            }
        });

        btnLOCAL = findViewById(R.id.btnLocal);
        btnLOCAL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent I = new Intent(ChooseChartOriginActivity.this, LocalFileActivity.class);
                I.putExtra("user", user.getName());
                startActivity(I);
            }
        });

        btnRemove = findViewById(R.id.btnRemove);
        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                userManager.RemoveUser(user);
                finish();
            }
        });

        setButtonColors();


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
            btnBarChart.setBackgroundColor(getResources().getColor(R.color.colorForthUser));
            btnLOCAL.setBackgroundColor(getResources().getColor(R.color.colorForthUser));
            btnNFC.setBackgroundColor(getResources().getColor(R.color.colorForthUser));
            btnQR.setBackgroundColor(getResources().getColor(R.color.colorForthUser));
            btnConfig.setBackgroundColor(getResources().getColor(R.color.colorForthUser));
        }
    }
}
