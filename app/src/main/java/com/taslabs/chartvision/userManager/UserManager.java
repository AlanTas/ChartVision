package com.taslabs.chartvision.userManager;

import android.content.Context;
import android.content.SharedPreferences;

import com.taslabs.chartvision.enums.FontSize;
import com.taslabs.chartvision.interfaces.IUser;
import com.taslabs.chartvision.interfaces.IUserManager;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;


public class UserManager implements IUserManager {


    private static  UserManager userManagerInstance;
    private final Context mContext;
    private final SharedPreferences mSharedPref;
    private List<IUser> mUsers;

    public static final String KEY_USERS = "usuarios";



    private UserManager(Context c) {
        mContext = c;
        mSharedPref = c.getSharedPreferences("pref",Context.MODE_PRIVATE);
        String[] users = mSharedPref.getString(KEY_USERS,"").split(",");
        for (String user:
             users) {
            mUsers.add(getUser(user));
        }
    }

    public UserManager getInstance(Context c) {
        if (userManagerInstance==null)
            userManagerInstance = new UserManager(c);
        return userManagerInstance;
    }

    private IUser getUser(String name) {
       User user = new User();
       user.setName(name);
       user.setAudioEnabled(Boolean.parseBoolean(mSharedPref.getString(user.KEY_AUDIO,"false")));
       user.setFontSize(FontSize.valueOf(mSharedPref.getString(user.KEY_FONT_SIZE,FontSize.Medium.toString())));
       user.setVibrationEnabled(Boolean.parseBoolean(mSharedPref.getString(user.KEY_VIBRATION,"false")));
       return user;
    }


    @Override
    public void SaveUser(IUser user) {
        if (!user.validadeUser()) return;
        String users = mSharedPref.getString(KEY_USERS,"");
       if (mUsers.size() > 1) {
          users += ",";
       }
        users += user.getName();
        final SharedPreferences.Editor sharedPrefEditor = mSharedPref.edit();
        sharedPrefEditor.putString(KEY_USERS,users);
        HashMap<String,String> values = user.getUserData();

        for (Map.Entry<String, String> pair : values.entrySet()) {
            sharedPrefEditor.putString(user.getName() + pair.getKey(), pair.getValue());
        }
        mUsers.add(user);
        sharedPrefEditor.apply();
    }

    @Override
    public List<IUser> getUsers() {
      return mUsers;
    }
}
