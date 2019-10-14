package com.taslabs.chartvision;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.taslabs.chartvision.interfaces.IUser;
import com.taslabs.chartvision.userManager.UserManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UserSelectionActivity extends AppCompatActivity {

    UserManager userManager;
    public List<IUser> users;


    TextView txtInicial1, txtInicial2, txtInicial3, txtInicial4, txtName1, txtName2, txtName3, txtName4;
    ImageView on1st, on2nd, on3rd, on4th;

    List<TextView> usuario1 = new ArrayList<>();
    List<TextView> usuario2 = new ArrayList<>();
    List<TextView> usuario3 = new ArrayList<>();
    List<TextView> usuario4 = new ArrayList<>();
    List<List> usuarios = new ArrayList<>();
    List<Boolean> fieldsFilled = new ArrayList<>();
    Vibrator v;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_selection);


        findViewById(R.id.contraintUserSelection).setBackgroundColor(getResources().getColor(R.color.colorBkg));


        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        getSquares();


    }

    private void getUsers() {
        userManager = UserManager.getInstance(this.getApplicationContext());
        userManager.update();
        users = new ArrayList<>();
        users = userManager.getUsers();
    }

    @Override
    protected void onResume(){
        super.onResume();

        getUsers();

        for(int i = 0; i < fieldsFilled.size(); i++){
            fieldsFilled.set(i, false);
        }

        for(int i = 0; i < users.size(); i++){
            fieldsFilled.set(i, true);
        }


        setupUserSquares();
        setupListeners();
        setupAddButton();
        setupAddListener();


    }

    private void setupAddListener() {
    }

    private void setupAddButton() {

        final ConstraintLayout layoutUser1 = findViewById(R.id.User1);
        final ConstraintLayout layoutUser2 = findViewById(R.id.User2);
        final ConstraintLayout layoutUser3 = findViewById(R.id.User3);
        final ConstraintLayout layoutUser4 = findViewById(R.id.User4);

        int tamanho = users.size();

        on1st.setVisibility(View.INVISIBLE);
        on2nd.setVisibility(View.INVISIBLE);
        on3rd.setVisibility(View.INVISIBLE);
        on4th.setVisibility(View.INVISIBLE);

        if (tamanho == 4){

        }

        else if(tamanho == 3){
            on4th.setVisibility(View.VISIBLE);
            txtName4.setVisibility(View.VISIBLE);
            txtName4.setText("Adicionar Usuário");

            layoutUser4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                        layoutUser4.setOnClickListener(null);
                        vibrate(50);
                        Intent I = new Intent(UserSelectionActivity.this, UserSetupActivity.class);
                        startActivity(I);

                }
            });

        }

        else if(tamanho == 2){
            on3rd.setVisibility(View.VISIBLE);
            txtName3.setVisibility(View.VISIBLE);
            txtName3.setText("Adicionar Usuário");

            layoutUser3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    layoutUser3.setOnClickListener(null);
                    vibrate(50);
                    Intent I = new Intent(UserSelectionActivity.this, UserSetupActivity.class);
                    startActivity(I);

                }
            });
        }

        else if(tamanho == 1){
            on2nd.setVisibility(View.VISIBLE);
            txtName2.setVisibility(View.VISIBLE);
            txtName2.setText("Adicionar Usuário");

            layoutUser2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    layoutUser2.setOnClickListener(null);
                    vibrate(50);
                    Intent I = new Intent(UserSelectionActivity.this, UserSetupActivity.class);
                    startActivity(I);

                }
            });
        }

        else if(tamanho == 0){
            on1st.setVisibility(View.VISIBLE);
            txtName1.setVisibility(View.VISIBLE);
            txtName1.setText("Adicionar Usuário");

            layoutUser1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    layoutUser1.setOnClickListener(null);
                    vibrate(50);
                    Intent I = new Intent(UserSelectionActivity.this, UserSetupActivity.class);
                    startActivity(I);

                }
            });
        }


    }

    public void getSquares(){
        txtInicial1 = findViewById(R.id.txtInicial1);
        txtInicial2 = findViewById(R.id.txtInicial2);
        txtInicial3 = findViewById(R.id.txtInicial3);
        txtInicial4 = findViewById(R.id.txtInicial4);
        txtName1 = findViewById(R.id.textUserName1);
        txtName2 = findViewById(R.id.textUserName2);
        txtName3 = findViewById(R.id.textUserName3);
        txtName4 = findViewById(R.id.textUserName4);

        txtInicial1.setTextColor(Color.parseColor("#000000"));
        txtInicial2.setTextColor(Color.parseColor("#000000"));
        txtInicial3.setTextColor(Color.parseColor("#000000"));
        txtInicial4.setTextColor(Color.parseColor("#000000"));

        txtName1.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        txtName2.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        txtName3.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        txtName4.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));

        txtName1.setTextColor(Color.parseColor("#FFFFFF"));
        txtName2.setTextColor(Color.parseColor("#FFFFFF"));
        txtName3.setTextColor(Color.parseColor("#FFFFFF"));
        txtName4.setTextColor(Color.parseColor("#FFFFFF"));



        usuario1.add(txtInicial1);
        usuario1.add(txtName1);
        usuario2.add(txtInicial2);
        usuario2.add(txtName2);
        usuario3.add(txtInicial3);
        usuario3.add(txtName3);
        usuario4.add(txtInicial4);
        usuario4.add(txtName4);

        usuarios.add(usuario1);
        usuarios.add(usuario2);
        usuarios.add(usuario3);
        usuarios.add(usuario4);

        fieldsFilled.add(false);
        fieldsFilled.add(false);
        fieldsFilled.add(false);
        fieldsFilled.add(false);

        on1st = findViewById(R.id.addOnFirst);
        on2nd = findViewById(R.id.addOnSecond);
        on3rd = findViewById(R.id.addOnThird);
        on4th = findViewById(R.id.addOnFourth);


    }

    public void setupUserSquares(){


        txtInicial1.setBackgroundColor(getResources().getColor(R.color.colorFirstUser));
        txtInicial2.setBackgroundColor(getResources().getColor(R.color.colorSecondUser));
        txtInicial3.setBackgroundColor(getResources().getColor(R.color.colorThirdUser));
        txtInicial4.setBackgroundColor(getResources().getColor(R.color.colorForthUser));

        for(int i = 0; i < users.size(); i++){

            fieldsFilled.set(i, true);
            List lista = usuarios.get(i);
            TextView txtSigla = (TextView) lista.get(0);
            TextView txtFull = (TextView) lista.get(1);

            txtFull.setVisibility(View.VISIBLE);
            txtSigla.setVisibility(View.VISIBLE);

            txtFull.setText(users.get(i).getName());
            txtSigla.setText(users.get(i).getName().substring(0, 1));

        }


        for(int i = 0; i < usuarios.size(); i++){
            System.out.println("SIZE: " + usuarios.size());

            if(!fieldsFilled.get(i)){
                System.out.println("NAO EXISTE O: " + (i+1));
                List lista = usuarios.get(i);
                TextView txtSigla = (TextView) lista.get(0);
                TextView txtFull = (TextView) lista.get(1);

                txtFull.setVisibility(View.INVISIBLE);
                txtSigla.setVisibility(View.INVISIBLE);
            }

        }

    }

    @SuppressLint("ClickableViewAccessibility")
    public void setupListeners(){

        final ConstraintLayout layoutUser1 = findViewById(R.id.User1);
        final ConstraintLayout layoutUser2 = findViewById(R.id.User2);
        final ConstraintLayout layoutUser3 = findViewById(R.id.User3);
        final ConstraintLayout layoutUser4 = findViewById(R.id.User4);

        layoutUser1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(fieldsFilled.get(0)) {
                    layoutUser1.setOnClickListener(null);
                    vibrate(50);
                    Intent I = new Intent(UserSelectionActivity.this, ChooseChartOriginActivity.class);
                    I.putExtra("user", users.get(0).getName());
                    I.putExtra("color", "1");
                    startActivity(I);
                }

            }
        });

        layoutUser2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fieldsFilled.get(1)) {
                    layoutUser2.setOnClickListener(null);
                    vibrate(50);
                    Intent I = new Intent(UserSelectionActivity.this, ChooseChartOriginActivity.class);
                    I.putExtra("user", users.get(1).getName());
                    I.putExtra("color", "2");
                    startActivity(I);
                }

            }
        });

        layoutUser3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fieldsFilled.get(2)) {
                    layoutUser3.setOnClickListener(null);
                    vibrate(50);
                    Intent I = new Intent(UserSelectionActivity.this, ChooseChartOriginActivity.class);
                    I.putExtra("user", users.get(2).getName());
                    I.putExtra("color", "3");
                    startActivity(I);
                }

            }
        });

        layoutUser4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fieldsFilled.get(3)) {
                    layoutUser4.setOnClickListener(null);
                    vibrate(50);
                    Intent I = new Intent(UserSelectionActivity.this, ChooseChartOriginActivity.class);
                    I.putExtra("user", users.get(3).getName());
                    I.putExtra("color", "4");
                    startActivity(I);
                }

            }
        });

    }

    private void vibrate(int duration){


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                //deprecated in API 26
                v.vibrate(duration);
            }
    }
}
