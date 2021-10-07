package com.yokesen.parafdigitalyokesen.view.ui.profile.child_profile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.yokesen.parafdigitalyokesen.R;
import com.yokesen.parafdigitalyokesen.Repository.APIClient;
import com.yokesen.parafdigitalyokesen.Repository.APIInterface;
import com.yokesen.parafdigitalyokesen.Repository.PreferencesRepo;
import com.yokesen.parafdigitalyokesen.model.GetNotifSettingsModel;
import com.yokesen.parafdigitalyokesen.model.GetPasscodeModel;
import com.yokesen.parafdigitalyokesen.model.NotifSettingModel;
import com.yokesen.parafdigitalyokesen.model.SimpleResponse;
import com.yokesen.parafdigitalyokesen.util.Util;
import com.yokesen.parafdigitalyokesen.util.UtilWidget;
import com.yokesen.parafdigitalyokesen.view.ui.profile.child_profile.security.PasscodeView;

import java.util.Calendar;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class NotificationActivity extends AppCompatActivity {
    APIInterface apiInterface;
    String token;
    PreferencesRepo preferencesRepo;
    Util util = new Util();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        initToolbar();
        initData();
    }
    public void initToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarNotif);
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

    private void initData() {
        apiInterface = APIClient.getClient().create(APIInterface.class);
        preferencesRepo = new PreferencesRepo(this);
        token = preferencesRepo.getToken();
        Observable<GetNotifSettingsModel> callPasscode = apiInterface.GetNotifSettings(token);
        callPasscode.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(this::onSuccessGetData, this::onFailedGetData);
    }



    private void onSuccessGetData(GetNotifSettingsModel getPasscodeModel) {
        if(getPasscodeModel!=null){
            initComponent(getPasscodeModel.getData());
        }
    }


    private void initComponent(NotifSettingModel model){
        Switch switchNotif = findViewById(R.id.switchNotif);
        Switch switchNewRequest  =findViewById(R.id.switchNewRequestSign);
        Switch switchAcc= findViewById(R.id.switchSignReqAcc);
        Switch switchRej = findViewById(R.id.switchSignRequestRej);

        switchNotif.setChecked(util.intToBool(model.getNotification()));
        switchNotif.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                putNotif();
            }
        });

        switchNewRequest.setChecked(util.intToBool(model.getRequest()));
        switchNewRequest.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                putNewRequest();
            }
        });
        switchAcc.setChecked(util.intToBool(model.getAccepted()));
        switchAcc.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                putAccRequest();
            }
        });
        switchRej.setChecked(util.intToBool(model.getRejected()));
        switchRej.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                putRejRequest();
            }
        });
    }

    private void putNotif() {
        Observable<SimpleResponse> callPasscode = apiInterface.ToggleNotifSetting(token);
        callPasscode.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(this::onSuccessToggle, this::onFailedToggleNotif);
    }

    private void putNewRequest() {
        Observable<SimpleResponse> callPasscode = apiInterface.ToggleRequestSetting(token);
        callPasscode.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(this::onSuccessToggle, this::onFailedToggleRequest);
    }

    private void putAccRequest() {
        Observable<SimpleResponse> callPasscode = apiInterface.ToggleAcceptedSetting(token);
        callPasscode.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(this::onSuccessToggle, this::onFailedToggleAcc);
    }

    private void putRejRequest() {
        Observable<SimpleResponse> callPasscode = apiInterface.ToggleRejectedSetting(token);
        callPasscode.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(this::onSuccessToggle, this::onFailedToggleRej);
    }

    private void onSuccessToggle(SimpleResponse simpleResponse) {
        util.toastMisc(this, "SUCCESS!");
    }




    public void back(){
        this.finish();
    }


    private void onFailedGetData(Throwable throwable) {
        util.toastError(this, "API GET NOtif Setting", throwable);
    }

    private void onFailedToggleNotif(Throwable throwable) {
        util.toastError(this, "API Toggle NOTIF", throwable);
    }
    private void onFailedToggleRequest(Throwable throwable) {
        util.toastError(this, "API Toggle Request", throwable);
    }

    private void onFailedToggleAcc(Throwable throwable) {
        util.toastError(this, "API Toggle Accepted", throwable);
    }
    private void onFailedToggleRej(Throwable throwable) {
        util.toastError(this, "API Toggle Rejected", throwable);
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
}