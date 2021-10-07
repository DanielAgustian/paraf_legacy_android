package com.yokesen.parafdigitalyokesen.view.ui.profile.child_profile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.yokesen.parafdigitalyokesen.R;
import com.yokesen.parafdigitalyokesen.Repository.PreferencesRepo;
import com.yokesen.parafdigitalyokesen.util.UtilWidget;
import com.yokesen.parafdigitalyokesen.view.ui.profile.child_profile.child_help.ContactSupportActivity;
import com.yokesen.parafdigitalyokesen.view.ui.profile.child_profile.child_help.DisclaimerActivity;
import com.yokesen.parafdigitalyokesen.view.ui.profile.child_profile.child_help.FAQActivity;
import com.yokesen.parafdigitalyokesen.view.ui.profile.child_profile.child_help.PrivacyPolicyActivity;
import com.yokesen.parafdigitalyokesen.view.ui.profile.child_profile.child_help.TermConditionActivity;
import com.yokesen.parafdigitalyokesen.view.ui.profile.child_profile.security.PasscodeView;

import java.util.Calendar;

public class HelpActivity extends AppCompatActivity implements View.OnClickListener{
    PreferencesRepo preferencesRepo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        preferencesRepo = new PreferencesRepo(this);
        initToolbar();
        initComponent();
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


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.rlContactSupport:
                gotoChildHelp(ContactSupportActivity.class);
                break;
            case R.id.rlFaq:
                gotoChildHelp(FAQActivity.class);
                break;
            case R.id.rlTerms:
                gotoChildHelp(TermConditionActivity.class);
                break;
            case R.id.rlPrivacyPolicy:
                gotoChildHelp(PrivacyPolicyActivity.class);
                break;
            case R.id.rlDisclaimer:
                gotoChildHelp(DisclaimerActivity.class);
                break;
            default:
                break;
        }
    }
    public void initComponent(){
        RelativeLayout rlContactUs = findViewById(R.id.rlContactSupport);
        rlContactUs.setOnClickListener(this);
        RelativeLayout rlFaq = findViewById(R.id.rlFaq);
        rlFaq.setOnClickListener(this);
        RelativeLayout rlTerms = findViewById(R.id.rlTerms);
        rlTerms.setOnClickListener(this);
        RelativeLayout rlPrivacyPolicy = findViewById(R.id.rlPrivacyPolicy);
        rlPrivacyPolicy.setOnClickListener(this);
        RelativeLayout rlDisclaimer = findViewById(R.id.rlDisclaimer);
        rlDisclaimer.setOnClickListener(this);
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