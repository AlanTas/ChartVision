package com.taslabs.chartvision;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
    EditText txtNome;
    Button btnSave;
    User newUser;
    ConstraintLayout constraintLayoutAddUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_setup);
        userManager = UserManager.getInstance(this.getApplicationContext());

        swcContrast = findViewById(R.id.swcContr);
        swcVibra = findViewById(R.id.swcVibr);
        swcTTS = findViewById(R.id.swcTTS);
        swcShake = findViewById(R.id.swcShake);
        txtNome = findViewById(R.id.txtNome);
        btnSave = findViewById(R.id.btnSave);

        constraintLayoutAddUser =  findViewById(R.id.constraintAddUser);
        constraintLayoutAddUser.setBackgroundColor(getResources().getColor(R.color.colorBkg));

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(txtNome.getText().toString().trim().length() != 0) {
                    newUser = new User();
                    newUser.setName(txtNome.getText().toString());
                    newUser.setFontSize(FontSize.Medium);
                    newUser.setAudioEnabled(swcTTS.isChecked());
                    newUser.setVibrationEnabled(swcVibra.isChecked());
                    newUser.setHighContrastEnabled(swcContrast.isChecked());
                    newUser.setShake2LeaveEnabled(swcShake.isChecked());
                    userManager.SaveUser(newUser);
                    finish();
                }

                else {
                    Toast.makeText(getApplicationContext(), "O campo 'Nome' não pode ficar vazio", Toast.LENGTH_LONG).show();
                }

            }
        });




    }
}
