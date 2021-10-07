package com.yokesen.parafdigitalyokesen.view.ui.profile.child_profile.child_help;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.yokesen.parafdigitalyokesen.R;
import com.yokesen.parafdigitalyokesen.Repository.PreferencesRepo;
import com.yokesen.parafdigitalyokesen.util.UtilWidget;
import com.yokesen.parafdigitalyokesen.view.ui.profile.child_profile.security.PasscodeView;

import java.util.Calendar;

public class ContactSupportActivity extends AppCompatActivity {
    PreferencesRepo preferencesRepo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_support);
        preferencesRepo = new PreferencesRepo(this);
        initComponent();
        initToolbar();
    }

    long milisStart = 0;
    @Override
    protected void onPause() {
        super.onPause();
        milisStart = Calendar.getInstance().getTimeInMillis();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        String passcode = preferencesRepo.getPasscode();
        int isActive = preferencesRepo.getAllowPasscode();
        long intervetion = 30 * 60 * 1000;
        long milisNow = Calendar.getInstance().getTimeInMillis();
        long milisSelisih = milisNow - milisStart;

        if(isActive == 1 && passcode!= null && !passcode.equals("")){

            if(intervetion < milisSelisih && milisSelisih!= milisNow){
                Intent intent = new Intent(this, PasscodeView.class);
                startActivity(intent);
            }
        }

        int isBiometricActive = preferencesRepo.getBiometric();
        if(isBiometricActive == 1){
            if(intervetion < milisSelisih && milisSelisih!= milisNow){
                UtilWidget uw = new UtilWidget(this);
                uw.biometricPrompt();
            }
        }

    }

    void initComponent(){
        TextView tvCS = findViewById(R.id.tvTerms);
        tvCS.setText(contactText());
    }
    public void initToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarCS);
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
    private String contactText(){
        return "Our priorities is to help you experiencing the best of our product and services. Should you have any questions, facing problems during your journey with Teken or in case you need to get any information about us, feel free to reach us through:";
    }
    public void back(){
        this.finish();
    }
}