package com.example.parafdigitalyokesen.view.ui.profile.child_profile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.example.parafdigitalyokesen.R;
import com.example.parafdigitalyokesen.view.ui.profile.child_profile.child_help.ContactUsActivity;
import com.example.parafdigitalyokesen.view.ui.profile.child_profile.child_help.FAQActivity;

public class HelpActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        initToolbar();
        initComponent();
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.rlContactSupport:
                gotoChildHelp(ContactUsActivity.class);
                break;
            case R.id.rlFaq:
                gotoChildHelp(FAQActivity.class);
                break;
        }
    }
    public void initComponent(){
        RelativeLayout rlContactUs = findViewById(R.id.rlContactSupport);
        rlContactUs.setOnClickListener(this);
        RelativeLayout rlFaq = findViewById(R.id.rlFaq);
        rlFaq.setOnClickListener(this);
    }

    public void initToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarHelp);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
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

    void gotoChildHelp(Class childHelp){
        Intent intent = new Intent(this, childHelp);
        startActivity(intent);
    }

}