package com.taslabs.chartvision.userManager;

import android.content.Context;
import android.content.SharedPreferences;

import com.taslabs.chartvision.interfaces.IUser;
import com.taslabs.chartvision.interfaces.IUserManager;

import java.util.List;


public class UserManager implements IUserManager {


    private static  UserManager userManagerInstance;
    private final Context mContext;
    private final SharedPreferences mSharedPref;
    private List<IUser> mUsers;


    private UserManager(Context c) {
        mContext = c;
        mSharedPref = c.getSharedPreferences("pref",Context.MODE_PRIVATE);
        
    }

    public UserManager getInstance(Context c) {
        if (userManagerInstance==null)
            userManagerInstance = new UserManager(c);
        return userManagerInstance;
    }


    @Override
    public void SaveUser(IUser user) {

    }

    @Override
    public void getUsers(List<IUser> users) {

    }
}
