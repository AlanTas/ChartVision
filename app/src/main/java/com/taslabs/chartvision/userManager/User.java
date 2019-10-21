package com.taslabs.chartvision.userManager;

import com.taslabs.chartvision.enums.FontSize;
import com.taslabs.chartvision.interfaces.IUser;

import java.util.HashMap;

public class User implements IUser {


    private String mName;
    private FontSize mFontSize;
    private boolean mAudioEnabled;
    private boolean mVibrationEnabled;
    private boolean mHighContrastEnabled;
    private boolean mShake2LeaveEnabled;
    private boolean mReadSeriesEnabled;

    public final String KEY_NAME = "user_name";
    public final String KEY_FONT_SIZE = "user_font_size";
    public final String KEY_AUDIO = "user_audio";
    public final String KEY_VIBRATION = "user_vibration";
    public final String KEY_CONTRAST = "user_contrast";
    public final String KEY_SHAKE2LEAVE = "user_shake";
    public final String KEY_READSERIES = "user_read_series";


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
    public boolean isHighContrastEnabled() {
        return mHighContrastEnabled;
    }

    @Override
    public boolean isShake2LeaveEnabled() {
        return mShake2LeaveEnabled;
    }

    @Override
    public void setVibrationEnabled(boolean enabled) {
        mVibrationEnabled = enabled;
    }

    @Override
    public void setHighContrastEnabled(boolean enabled) {
        mHighContrastEnabled = enabled;
    }

    @Override
    public void setShake2LeaveEnabled(boolean enabled) {
        mShake2LeaveEnabled = enabled;
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
    public void setReadSeriesEnabled(boolean enabled){
        mReadSeriesEnabled = enabled;
    }

    @Override
    public boolean isReadSeriesEnabled() {
        return mReadSeriesEnabled;
    }


    @Override
    public HashMap<String, String> getUserData() {
        HashMap<String,String> dados = new HashMap<>();
        dados.put(KEY_FONT_SIZE,getFontSize().toString());
        dados.put(KEY_AUDIO, String.valueOf(isAudioEnabled()));
        dados.put(KEY_VIBRATION, String.valueOf(isVibrationEnabled()));
        dados.put(KEY_CONTRAST, String.valueOf(isHighContrastEnabled()));
        dados.put(KEY_SHAKE2LEAVE, String.valueOf(isShake2LeaveEnabled()));
        dados.put(KEY_READSERIES, String.valueOf(isReadSeriesEnabled()));
        return dados;
    }
}
