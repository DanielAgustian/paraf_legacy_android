package com.example.parafdigitalyokesen.Repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import static android.content.Context.MODE_PRIVATE;

public class PreferencesRepo {
    Context context;

    public PreferencesRepo(Context context) {
        this.context = context;
    }


    final private String NAME_TOKEN = "token";
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
}
