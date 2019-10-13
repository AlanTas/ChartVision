package com.taslabs.chartvision.interfaces;

import com.taslabs.chartvision.enums.FontSize;

import java.util.HashMap;

public interface IUser {

    //String Name
    //int FontSize
    //boolean audio
    //boolean vibration


     void setName(String name);

     String getName();

     void setFontSize(FontSize size);

     FontSize getFontSize();

     void setAudioEnabled(boolean enabled);

     boolean isAudioEnabled();

     void setVibrationEnabled(boolean enabled);

     boolean isVibrationEnabled();

     void setHighContrastEnabled(boolean enabled);

     boolean isHighContrastEnabled();

     void setShake2LeaveEnabled(boolean enabled);

     boolean isShake2LeaveEnabled();

     boolean validadeUser();

     HashMap<String,String> getUserData();


}
