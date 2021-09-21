package com.yokesen.parafdigitalyokesen.view.ui.profile.child_profile.security;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.yokesen.parafdigitalyokesen.R;

public class ConfirmPasscode extends AppCompatActivity {
    TextView tvConfirmPasscode;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_passcode);
        initComponent();
        initToolbar();
    }
    public void initComponent(){
        tvConfirmPasscode = findViewById(R.id.tvCreatePasscode);
        tvConfirmPasscode.setText("Confirm Passcode");
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
    public void back(){
        this.finish();
    }
}
