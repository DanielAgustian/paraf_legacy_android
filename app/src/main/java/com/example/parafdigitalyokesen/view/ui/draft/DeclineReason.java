package com.example.parafdigitalyokesen.view.ui.draft;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.parafdigitalyokesen.R;

public class DeclineReason extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decline_reason);
        initToolbar();
        initComponent();
    }

    public void initComponent(){
        Button btnSaveReason = findViewById(R.id.btnSaveReason);
        btnSaveReason.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getToResult();
            }
        });
    }

    public void initToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarDeclineReason);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationIcon(R.drawable.ic_back_gray);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Respond Signature", "Back Clicked");
                back();
            }
        });

    }
    public void back(){
        this.finish();
    }
    public void getToResult(){
        Intent intent = new Intent(this, ResultAfterRespond.class);
        intent.putExtra("Result", "0");
        startActivity(intent);
    }


}