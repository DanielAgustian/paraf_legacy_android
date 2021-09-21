package com.yokesen.parafdigitalyokesen.view.ui.profile.child_profile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.yokesen.parafdigitalyokesen.R;
import com.yokesen.parafdigitalyokesen.view.ui.profile.child_profile.security.CreatePasscodeActivity;

public class SecurityPrivacyActivity extends AppCompatActivity implements View.OnClickListener{
    Switch switchFaceId;
    Switch switchPasscode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security_privacy);
        initSharedPref();
        initToolbar();
        initComponent();
    }

    private void initSharedPref() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
    }

    public void initToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarSecurity);
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

    public void initComponent(){
        RelativeLayout rlPasscode = findViewById(R.id.rlChangePasscode);
        rlPasscode.setOnClickListener(this);
        switchFaceId = findViewById(R.id.switchFaceID);
        switchFaceId.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    dialogFaceID();
                }
            }
        });

        switchPasscode = findViewById(R.id.switchPassCode);
        switchPasscode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    dialogCreatePasscode();
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.rlChangePasscode:
                dialogCreatePasscode();
                break;
        }
    }

    private void dialogCreatePasscode(){
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_confirmation);
        dialog.setCancelable(true);
        dialog.show();
        TextView tvTitle = dialog.findViewById(R.id.tvTitleDialog);
        tvTitle.setText("Create Passcode");
        TextView tvData = dialog.findViewById(R.id.tvTitleData);
        tvData.setText("You don't have any passcode yet");
        Button btnCancel = dialog.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();

            }
        });
        Button btnContinue = dialog.findViewById(R.id.btnContinue);
        btnContinue.setText("Create Now");
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                gotoChild(CreatePasscodeActivity.class);
            }
        });
    }
    private void dialogFaceID(){
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_confirmation);
        //dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        TextView tvTitle = dialog.findViewById(R.id.tvTitleDialog);
        tvTitle.setText("Do you want to allow “Paraf” to use Face ID?");
        TextView tvData = dialog.findViewById(R.id.tvTitleData);
        tvData.setText("Paraf can use Face ID to verify your identitiy\n" +
                "and to secure your data.");
        Button btnCancel = dialog.findViewById(R.id.btnCancel);
        btnCancel.setText("Don't Allow");
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                switchFaceId.setChecked(false);
            }
        });
        Button btnContinue = dialog.findViewById(R.id.btnContinue);
        btnContinue.setText("Allow");
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();

            }
        });
    }
    private void gotoChild(Class child){
        Intent intent = new Intent(this, child);
        startActivity(intent);
    }

}