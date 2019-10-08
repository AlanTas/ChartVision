package com.taslabs.chartvision.userManager;

import com.taslabs.chartvision.enums.FontSize;
import com.taslabs.chartvision.interfaces.IUser;

import java.util.HashMap;

public class User implements IUser {


    private String mName;
    private FontSize mFontSize;
    private boolean mAudioEnabled;
    private boolean mVibrationEnabled;

    public final String KEY_NAME = "user_name";
    public final String KEY_FONT_SIZE = "user_font_size";
    public final String KEY_AUDIO = "user_audio";
    public final String KEY_VIBRATION = "user_vibration";


    @Override
    public void setName(String name) {
        mName = name;
    }

    @Override
    public String getName() {
        return mName;
    }

    @Override
    public void setFontSize(FontSize size) {
       mFontSize = size;
    }

    @Override
    public FontSize getFontSize() {
        return mFontSize;
    }

    @Override
    public void setAudioEnabled(boolean enabled) {
       mAudioEnabled = enabled;

    }

    @Override
    public boolean isAudioEnabled() {
        return mAudioEnabled;
    }

    @Override
    public void setVibrationEnabled(boolean enabled) {
        mVibrationEnabled = enabled;
    }

    @Override
    public boolean isVibrationEnabled() {
        return mVibrationEnabled;
    }

    @Override
    public boolean validadeUser() {
        return true;
    }

    @Override
    public HashMap<String, String> getUserData() {
        HashMap<String,String> dados = new HashMap<>();
        dados.put(KEY_FONT_SIZE,getFontSize().toString());
        dados.put(KEY_AUDIO, String.valueOf(isAudioEnabled()));
        dados.put(KEY_VIBRATION, String.valueOf(isVibrationEnabled()));
        return dados;
    }
}
