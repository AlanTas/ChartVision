package com.taslabs.chartvision.userManager;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.taslabs.chartvision.enums.FontSize;
import com.taslabs.chartvision.interfaces.IUser;
import com.taslabs.chartvision.interfaces.IUserManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;


public class UserManager implements IUserManager {


    private static UserManager userManagerInstance;
    private final Context mContext;
    private final SharedPreferences mSharedPref;
    private List<IUser> mUsers = new ArrayList<>();
    private final int userLimit = 4;

    public static final String KEY_USERS = "usuarios";



    private UserManager(Context c) {
        mContext = c;
        mSharedPref = c.getSharedPreferences("pref",Context.MODE_PRIVATE);
        System.out.println("RAW STRING" + mSharedPref.getString(KEY_USERS,""));
        String[] users = mSharedPref.getString(KEY_USERS,"").split(",");
            for (String user : users) {
                if(!user.isEmpty())
                    mUsers.add(getUser(user));
            }

    }

    public static synchronized UserManager getInstance(Context c) {
        if (userManagerInstance == null)
            userManagerInstance = new UserManager(c);
        return userManagerInstance;
    }

    public IUser getUser(String name) {
       User user = new User();
       user.setName(name);
       user.setAudioEnabled(Boolean.parseBoolean(mSharedPref.getString(name + user.KEY_AUDIO,"true")));
       user.setFontSize(FontSize.valueOf(mSharedPref.getString(name + user.KEY_FONT_SIZE,FontSize.Small.toString())));
       user.setVibrationEnabled(Boolean.parseBoolean(mSharedPref.getString(name + user.KEY_VIBRATION,"true")));
       user.setHighContrastEnabled(Boolean.parseBoolean(mSharedPref.getString(name + user.KEY_CONTRAST,"false")));
       user.setShake2LeaveEnabled(Boolean.parseBoolean(mSharedPref.getString(name + user.KEY_SHAKE2LEAVE,"true")));
       return user;
    }

    @Override
    public boolean SaveUser(IUser user) {

        String userString = "" ;
        for(int i = 0; i < mUsers.size();i++){
            userString += " " + mUsers.get(i).getName();

        }

        if (!user.validadeUser()|| mUsers.size()==userLimit) {
            return false;
        }

        if (userString.contains(user.getName())){
            Toast.makeText(mContext, "Usuário com esse nome já existe", Toast.LENGTH_LONG).show();
            return false;
        }

        String users = mSharedPref.getString(KEY_USERS,"");
       if (mUsers.size() >= 1) {
          users += ",";
       }
        users += user.getName();
        System.out.println("NEW USERS STRING:" + users);
        final SharedPreferences.Editor sharedPrefEditor = mSharedPref.edit();
        sharedPrefEditor.putString(KEY_USERS,users);
        HashMap<String,String> values = user.getUserData();

        for (Map.Entry<String, String> pair : values.entrySet()) {
            sharedPrefEditor.putString(user.getName() + pair.getKey(), pair.getValue());
        }

        mUsers.add(user);
        sharedPrefEditor.apply();
        return true;
    }


    @Override
    public void RemoveUser(IUser user) {

        String users = mSharedPref.getString(KEY_USERS,"");
        String subStrToRmv = "";


        if(mUsers.size() > 1) {
            if (!mUsers.get(0).getName().equals(user.getName())) {
                subStrToRmv += ",";
            }

            subStrToRmv += user.getName();

            if (mUsers.get(0).getName().equals(user.getName())) {
                subStrToRmv += ",";
            }
        }

        else{
            subStrToRmv += user.getName();
        }


        System.out.println("SUBSTRING TO REMOVE:" + subStrToRmv);
        System.out.println("ANTES DE REMOVER:" + users);
        users = users.replace(subStrToRmv, "");
        System.out.println("DEPOIS DE REMOVER:" + users);
        final SharedPreferences.Editor sharedPrefEditor = mSharedPref.edit();
        sharedPrefEditor.putString(KEY_USERS,users);

        HashMap<String,String> values = user.getUserData();

        for (Map.Entry<String, String> pair : values.entrySet()) {
            sharedPrefEditor.remove(user.getName() + pair.getKey());
        }

        mUsers.remove(user);
        sharedPrefEditor.apply();
    }

    @Override
    public boolean EditUser(IUser oldUser, IUser newUser) {

        String users = mSharedPref.getString(KEY_USERS,"");


        if (!newUser.validadeUser()|| users.contains(newUser.getName())){
            if(oldUser.getName().equals(newUser.getName())){

            }
            else {
                Toast.makeText(mContext, "Usuário com esse nome já existe", Toast.LENGTH_LONG).show();
                return false;
            }
        }

        users = users.replace(oldUser.getName(), newUser.getName());

        final SharedPreferences.Editor sharedPrefEditor = mSharedPref.edit();
        sharedPrefEditor.putString(KEY_USERS,users);

        HashMap<String,String> values = oldUser.getUserData();

        for (Map.Entry<String, String> pair : values.entrySet()) {
            sharedPrefEditor.remove(oldUser.getName() + pair.getKey());
        }

        values = newUser.getUserData();

        for (Map.Entry<String, String> pair : values.entrySet()) {
            sharedPrefEditor.putString(newUser.getName() + pair.getKey(), pair.getValue());
        }

        mUsers.remove(oldUser);
        mUsers.add(newUser);
        sharedPrefEditor.apply();
        return true;
    }

    @Override
    public List<IUser> getUsers() {
      return mUsers;
    }

    public void update(){
        mUsers = new ArrayList<>();
        String[] users = mSharedPref.getString(KEY_USERS,"").split(",");
        for (String user : users) {
            if(!user.isEmpty())
                mUsers.add(getUser(user));
        }

    }

    public void printWholeSharedPrefs(){

    Map<String,?> keys = mSharedPref.getAll();
    for(Map.Entry<String,?> entry : keys.entrySet()){
        System.out.println( entry.getKey() + ": " +
                entry.getValue().toString());
    }
    }
}
