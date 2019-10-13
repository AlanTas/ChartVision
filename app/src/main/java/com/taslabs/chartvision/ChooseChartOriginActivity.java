package com.taslabs.chartvision;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.taslabs.chartvision.interfaces.IUser;
import com.taslabs.chartvision.userManager.User;
import com.taslabs.chartvision.userManager.UserManager;

public class ChooseChartOriginActivity extends AppCompatActivity {
    Button btnBarChart, btnQR, btnNFC, btnLOCAL, btnRemove;
    UserManager userManager;
    IUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_chart_origin);
        BarChart barChart = (BarChart) findViewById(R.id.barchart);

        userManager = UserManager.getInstance(this.getApplicationContext());
        Bundle extras = getIntent().getExtras();
        String username = extras.getString("user");
        user = userManager.getUser(username);

        System.out.println("USUÁRIO ATUAL:" +user.getName());


        btnBarChart = findViewById(R.id.btnBarChart);
        btnBarChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent I = new Intent(ChooseChartOriginActivity.this, BarChartActivity.class);
                I.putExtra("url", "https://alantas.dev/jsons/frutas.json");
                startActivity(I);
            }
        });

        btnQR = findViewById(R.id.btnQR);
        btnQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent I = new Intent(ChooseChartOriginActivity.this, QRScanActivity.class);
                startActivity(I);
            }
        });

        btnNFC = findViewById(R.id.btnNFC);
        btnNFC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent I = new Intent(ChooseChartOriginActivity.this, NFCActivity.class);
                startActivity(I);
            }
        });

        btnLOCAL = findViewById(R.id.btnLocal);
        btnLOCAL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent I = new Intent(ChooseChartOriginActivity.this, LocalFileActivity.class);
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

    }
}
