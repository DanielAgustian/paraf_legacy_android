package com.example.parafdigitalyokesen.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.parafdigitalyokesen.R;
import com.example.parafdigitalyokesen.view.add_sign.ResultSignature;

import java.util.Timer;
import java.util.TimerTask;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        Timer obRunTime = new Timer();
        obRunTime.schedule(new TimerTask() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                gotoMainActivity();
            }
        },2000);
    }

    private void gotoMainActivity() {

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}