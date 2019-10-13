package com.taslabs.chartvision;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.taslabs.chartvision.interfaces.IUser;
import com.taslabs.chartvision.userManager.UserManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UserSelectionActivity extends AppCompatActivity {

    UserManager userManager;
    public List<IUser> users;
    TextView userNames;
    Button addUsers;


    TextView txtInicial1, txtInicial2, txtInicial3, txtInicial4, txtName1, txtName2, txtName3, txtName4;

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

        userNames = findViewById(R.id.txtUsers);
        addUsers = findViewById(R.id.btnAddUser);


        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        getSquares();




        addUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addUsers.setEnabled(false);

                Intent i = new Intent(UserSelectionActivity.this, UserSetupActivity.class);
                startActivity(i);

            }
        });
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
        addUsers.setEnabled(true);

        String userString = "" ;
        for(int i = 0; i < users.size();i++){
            userString += " " + users.get(i).getName().toString();

        }

        userNames.setText(userString);

        for(int i = 0; i < fieldsFilled.size(); i++){
            fieldsFilled.set(i, false);
        }

        for(int i = 0; i < users.size(); i++){
            fieldsFilled.set(i, true);
        }


        setupUserSquares();
        setupListeners();


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
    }

    public void setupUserSquares(){


        txtInicial1.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        txtInicial2.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        txtInicial3.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        txtInicial4.setBackgroundColor(getResources().getColor(R.color.colorAccent));

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
