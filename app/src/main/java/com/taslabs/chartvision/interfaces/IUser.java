package com.taslabs.chartvision.interfaces;

import com.taslabs.chartvision.enums.FontSize;

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

     void SetVibrationEnabled(boolean enabled);

     boolean isVibrationEnabled();




}
