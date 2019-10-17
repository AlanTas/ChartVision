package com.taslabs.chartvision;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.graphics.fonts.Font;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.taslabs.chartvision.enums.FontSize;
import com.taslabs.chartvision.interfaces.IUser;
import com.taslabs.chartvision.userManager.User;
import com.taslabs.chartvision.userManager.UserManager;

import java.util.List;

public class UserSetupActivity extends AppCompatActivity {

    UserManager userManager;
    Switch swcContrast, swcVibra, swcTTS, swcShake;
    RadioButton radioP, radioM, radioG;
    EditText txtNome;
    Button btnSave, btnRemove;
    User newUser;
    IUser oldUser;
    ConstraintLayout constraintLayoutAddUser;
    TextView topText;
    Boolean type = false;
    String color = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_setup);
        userManager = UserManager.getInstance(this.getApplicationContext());

        Bundle extras = getIntent().getExtras();
        type = extras.getBoolean("type"); //false = new user, true = editUser


        if (extras.containsKey("user")) {
             oldUser = userManager.getUser(extras.getString("user"));
        }

        color = extras.getString("color");


        swcContrast = findViewById(R.id.swcContr);
        swcVibra = findViewById(R.id.swcVibr);
        swcTTS = findViewById(R.id.swcTTS);
        swcShake = findViewById(R.id.swcShake);
        txtNome = findViewById(R.id.txtNome);
        btnSave = findViewById(R.id.btnSave);
        radioP = findViewById(R.id.radioP);
        radioM = findViewById(R.id.radioM);
        radioG = findViewById(R.id.radioG);
        btnRemove = findViewById(R.id.btnRemoveUser);
        topText = findViewById(R.id.topText);

        constraintLayoutAddUser =  findViewById(R.id.constraintAddUser);
        constraintLayoutAddUser.setBackgroundColor(getResources().getColor(R.color.colorBkg));

        if(type){
            isOldUser();
        }

        else{
            isNewUser();
        }




    }

    public FontSize getFontSize(){

        FontSize tempFont = FontSize.Small;

        if(radioM.isChecked()){

           tempFont =  FontSize.Medium;

        }

        else if(radioG.isChecked()){

            tempFont =  FontSize.Large;
        }
        return tempFont;
    }

    public void isNewUser(){

        swcTTS.setChecked(true);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(txtNome.getText().toString().trim().length() != 0 && !txtNome.getText().toString().contains(",")) {
                    newUser = new User();
                    newUser.setName(txtNome.getText().toString());
                    newUser.setFontSize(getFontSize());
                    newUser.setAudioEnabled(swcTTS.isChecked());
                    newUser.setVibrationEnabled(swcVibra.isChecked());
                    newUser.setHighContrastEnabled(swcContrast.isChecked());
                    newUser.setShake2LeaveEnabled(swcShake.isChecked());
                    boolean success = userManager.SaveUser(newUser);
                    if(success) {
                        finish();
                    }
                }

                else {
                    Toast.makeText(getApplicationContext(), "O campo 'Nome' não pode ficar vazio", Toast.LENGTH_LONG).show();
                }

                if(txtNome.getText().toString().contains(",")){
                    Toast.makeText(getApplicationContext(), "O campo 'Nome' não pode conter vírgulas", Toast.LENGTH_LONG).show();
                }

            }
        });

    }

    public void isOldUser(){

        btnRemove.setVisibility(View.VISIBLE);
        topText.setText("EDITAR USUÁRIO");
        txtNome.setText(oldUser.getName());

        if(oldUser.isHighContrastEnabled()){
            swcContrast.setChecked(true);
        }

        if(oldUser.isVibrationEnabled()){
            swcVibra.setChecked(true);
        }

        if(oldUser.isAudioEnabled()){
            swcTTS.setChecked(true);
        }

        if(oldUser.isShake2LeaveEnabled()){
            swcShake.setChecked(true);
        }

        if(oldUser.getFontSize() == FontSize.Small){
            radioP.setChecked(true);
        }

        else if(oldUser.getFontSize() == FontSize.Medium){
            radioM.setChecked(true);
        }

        else if(oldUser.getFontSize() == FontSize.Large){
            radioG.setChecked(true);
        }

        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userManager.RemoveUser(oldUser);
                finish();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(txtNome.getText().toString().trim().length() != 0  && !txtNome.getText().toString().contains(",")) {
                    newUser = new User();
                    newUser.setName(txtNome.getText().toString());
                    newUser.setFontSize(getFontSize());
                    newUser.setAudioEnabled(swcTTS.isChecked());
                    newUser.setVibrationEnabled(swcVibra.isChecked());
                    newUser.setHighContrastEnabled(swcContrast.isChecked());
                    newUser.setShake2LeaveEnabled(swcShake.isChecked());
                    boolean success = userManager.EditUser(oldUser, newUser);
                    if(success) {
                        Intent I = new Intent(UserSetupActivity.this, ChooseChartOriginActivity.class);
                        I.putExtra("user", oldUser.getName());
                        I.putExtra("color", color);
                        startActivity(I);
                        finish();
                        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);

                    }
                }

                else {
                    Toast.makeText(getApplicationContext(), "O campo 'Nome' não pode ficar vazio", Toast.LENGTH_LONG).show();
                }

                if(txtNome.getText().toString().contains(",")){
                    Toast.makeText(getApplicationContext(), "O campo 'Nome' não pode conter vírgulas", Toast.LENGTH_LONG).show();
                }

            }
        });

    }

    @Override
    public void onBackPressed() {
        if(type){
            Intent I = new Intent(UserSetupActivity.this, ChooseChartOriginActivity.class);
            I.putExtra("user", oldUser.getName());
            I.putExtra("color", color);
            startActivity(I);
            finish();
            overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
        }

        else {
            super.onBackPressed();
        }
    }
}
