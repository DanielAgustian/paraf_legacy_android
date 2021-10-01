package com.yokesen.parafdigitalyokesen.Repository;

import android.content.Context;
import android.content.SharedPreferences;

import com.yokesen.parafdigitalyokesen.constant.Variables;

public class PreferencesRepo {
    Context context;
    Variables var = new Variables();
    public PreferencesRepo(Context context) {
        this.context = context;
    }


    final private String NAME_TOKEN = "token";
    final private String PASSCODE = "passcode";
    final private String ALLOW_PASSCODE = "allow_passcode";
    final private String ALLOW_BIOMETRIC = "allow_biometric";
    final private String PROFILE_NAME = "name_profile";
    final private String PROFILE_EMAIL = "email_profile";
    public String getToken(){
        SharedPreferences pref = context.getSharedPreferences("LOGIN", context.MODE_PRIVATE);
        return (pref.getString(NAME_TOKEN, ""));
    }
    public void setToken(String token){
        SharedPreferences sharedPref = context.getSharedPreferences("LOGIN", context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("token", "Bearer "+ token);
        editor.apply();

    }
    public void deleteToken(){
        SharedPreferences pref = context.getSharedPreferences("LOGIN", context.MODE_PRIVATE);
        pref.edit().remove(NAME_TOKEN).apply();
    }

    //----------------------- Allow Biometric Pattern ---------------------------------------//
    public void setBiometric(){
        SharedPreferences sharedPref = context.getSharedPreferences("LOGIN", context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(ALLOW_BIOMETRIC, 1);
        editor.apply();
    }
    public int getBiometric(){
        SharedPreferences pref = context.getSharedPreferences("LOGIN", context.MODE_PRIVATE);
        return (pref.getInt(ALLOW_BIOMETRIC, 0));
    }

    public void deleteAllowBiometric(){
        SharedPreferences pref = context.getSharedPreferences("LOGIN", context.MODE_PRIVATE);
        pref.edit().remove(ALLOW_BIOMETRIC).apply();
    }


    //---------------------- ALLOW PASSCODE ----------------------------------------//
    public void setAllowPasscode(int allow){
        SharedPreferences sharedPref = context.getSharedPreferences("LOGIN", context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(ALLOW_PASSCODE, allow);
        editor.apply();
    }
    public int getAllowPasscode(){
        SharedPreferences pref = context.getSharedPreferences("LOGIN", context.MODE_PRIVATE);
        return (pref.getInt(ALLOW_PASSCODE, 0));
    }

    public void deleteAllowPasscode(){
        SharedPreferences pref = context.getSharedPreferences("LOGIN", context.MODE_PRIVATE);
        pref.edit().remove(ALLOW_PASSCODE).apply();
    }

    //----------------------- PASSCODE VALUE -----------------------------------------//
    public void setPasscode(String passcode){
        SharedPreferences sharedPref = context.getSharedPreferences("LOGIN", context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(PASSCODE, passcode);
        editor.apply();
    }
    public String getPasscode(){
        SharedPreferences pref = context.getSharedPreferences("LOGIN", context.MODE_PRIVATE);
        return (pref.getString(PASSCODE, ""));
    }
    public void deletePasscode(){
        SharedPreferences pref = context.getSharedPreferences("LOGIN", context.MODE_PRIVATE);
        pref.edit().remove(PASSCODE).apply();
    }

    // -------------------------- Profile VALUE --------------------------------------//

    public void setProfile(String email, String name){
        SharedPreferences sharedPref = context.getSharedPreferences("PROFILE", context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(PROFILE_EMAIL, email);
        editor.putString(PROFILE_NAME, name);
        editor.apply();
    }

    public String getEmailProfile(){
        SharedPreferences pref = context.getSharedPreferences("PROFILE", context.MODE_PRIVATE);
        return (pref.getString(PROFILE_EMAIL, ""));
    }
    public String getNameProfile(){
        SharedPreferences pref = context.getSharedPreferences("PROFILE", context.MODE_PRIVATE);
        return (pref.getString(PROFILE_NAME, ""));
    }

    public void deleteProfile(){
        SharedPreferences pref = context.getSharedPreferences("PROFILE", context.MODE_PRIVATE);
        pref.edit().remove(PROFILE_NAME).apply();
        pref.edit().remove(PROFILE_EMAIL).apply();
    }


    //-------------------------------- Log Out ----------------------------//
    public void logOut(){
        deleteAllowPasscode();
        deleteAllowBiometric();
        deletePasscode();
        deleteProfile();
        deleteToken();
    }
}
