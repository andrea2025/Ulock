package com.example.user.bluetooth_communication.remote;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.example.user.bluetooth_communication.ui.Utils.Constants;

public class SharedPref {

    private static SharedPreferences mSharedPref;
    private static final String IS_LOGIN = "IsLoggedIn";
    public static final String KEY_TOKEN = Constants.TOKEN;


    public SharedPref(Context context)
    {
        if(mSharedPref == null)
            mSharedPref = context.getSharedPreferences(context.getPackageName(), Activity.MODE_PRIVATE);
    }


    public static String getString(String key, String defValue) {
        return mSharedPref.getString(key, defValue);
    }

    public static void putString(String key, String value) {
        SharedPreferences.Editor prefsEditor = mSharedPref.edit();
        prefsEditor.putString(key, value);
        prefsEditor.apply();
    }

    public void createLoginSession(String name) {
        SharedPreferences.Editor prefsEditor = mSharedPref.edit();
        // Storing login value as TRUE
        prefsEditor.putBoolean(IS_LOGIN, true);

        // Storing name in pref
        prefsEditor.putString(KEY_TOKEN, name);

        // commit changes
        prefsEditor.commit();
    }

    public String getUser(){
        return mSharedPref.getString(Constants.TOKEN,null);
    }

    public boolean isLoggedIn() {
        return mSharedPref.getBoolean(IS_LOGIN, false);
    }


    //// Clear Preference ////
    public static void clearPreference(Context context) {
        mSharedPref.edit().clear().apply();
    }

    //// Remove ////
    public static void removePreference(String Key){
        mSharedPref.edit().remove(Key).apply();
    }

}
