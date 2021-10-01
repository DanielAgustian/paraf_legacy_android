package com.yokesen.parafdigitalyokesen.view.ui.profile.child_profile.child_help;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.yokesen.parafdigitalyokesen.R;
import com.yokesen.parafdigitalyokesen.Repository.PreferencesRepo;
import com.yokesen.parafdigitalyokesen.view.ui.profile.child_profile.security.PasscodeView;

import java.util.Calendar;

public class ContactActivity extends AppCompatActivity {
    Switch switchGoogle, switchPhone;
    PreferencesRepo preferencesRepo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
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

        if(isActive == 1 && passcode!= null && passcode.equals("")){
            long intervetion = 30 * 60 * 1000;
            long milisNow = Calendar.getInstance().getTimeInMillis();
            long milisSelisih = milisNow - milisStart;
            if(intervetion < milisSelisih && milisSelisih!= milisNow){
                Intent intent = new Intent(this, PasscodeView.class);
                startActivity(intent);
            }
        }

        //biometricPrompt();
    }



    public void initToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarContact);
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
    public void initComponent(){
        switchGoogle = findViewById(R.id.switchGoogle);
        switchPhone = findViewById(R.id.switchPhoneContact);
        switchGoogle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    dialogGoogleSwitch();
                }
            }
        });
        switchPhone.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    dialogPhoneSwitch();
                }
            }
        });
    }
    private void dialogPhoneSwitch(){
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_confirmation);
        //dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        TextView tvTitle = dialog.findViewById(R.id.tvTitleDialog);
        tvTitle.setText("“Paraf” Would Like to Access Your Contact");
        TextView tvData = dialog.findViewById(R.id.tvTitleData);
        tvData.setText("Add recipients from your address book by typing a name or email id. Makes it fast and easy");
        Button btnCancel = dialog.findViewById(R.id.btnCancel);
        btnCancel.setText("Don't Allow");
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                switchPhone.setChecked(false);
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
    private void dialogGoogleSwitch(){
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_confirmation);
        //dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        TextView tvTitle = dialog.findViewById(R.id.tvTitleDialog);
        tvTitle.setText("“Paraf” Wants to Use “google.com” to Sign in");
        TextView tvData = dialog.findViewById(R.id.tvTitleData);
        tvData.setText("This allows the app and website to share information about you");
        Button btnCancel = dialog.findViewById(R.id.btnCancel);
        btnCancel.setText("Don't Allow");
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                switchGoogle.setChecked(false);
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
    public void back(){
        this.finish();
    }
}