package com.yokesen.parafdigitalyokesen.view.ui.profile.child_profile.security;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.yokesen.parafdigitalyokesen.R;

public class CreatePasscodeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_passcode);
        initToolbar();
        initComponent();
    }
    public void initToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarCreatePasscode);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Security & Privacy");
        toolbar.setNavigationIcon(R.drawable.ic_back_gray);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("mainActivity", "Back Clicked");
                back();
            }
        });
    }
    public void initComponent(){
        Button btnSave = findViewById(R.id.buttonPickPascode);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoConfirmPasscode();
            }
        });
    }
    void gotoConfirmPasscode(){
        Intent intent = new Intent(this, ConfirmPasscode.class);
        startActivity(intent);
    }

    public void back(){
        this.finish();
    }
}