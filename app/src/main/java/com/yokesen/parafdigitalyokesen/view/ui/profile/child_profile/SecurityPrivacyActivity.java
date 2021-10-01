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
import com.yokesen.parafdigitalyokesen.Repository.APIClient;
import com.yokesen.parafdigitalyokesen.Repository.APIInterface;
import com.yokesen.parafdigitalyokesen.Repository.PreferencesRepo;
import com.yokesen.parafdigitalyokesen.constant.refresh;
import com.yokesen.parafdigitalyokesen.model.GetPasscodeModel;
import com.yokesen.parafdigitalyokesen.model.PasscodeModel;
import com.yokesen.parafdigitalyokesen.model.SimpleResponse;
import com.yokesen.parafdigitalyokesen.util.Util;
import com.yokesen.parafdigitalyokesen.util.UtilWidget;
import com.yokesen.parafdigitalyokesen.view.ui.profile.child_profile.security.CreatePasscodeActivity;
import com.yokesen.parafdigitalyokesen.view.ui.profile.child_profile.security.PasscodeView;
import com.yokesen.parafdigitalyokesen.viewModel.SecurityState;
import com.yokesen.parafdigitalyokesen.viewModel.SignCollabState;

import java.util.Calendar;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class SecurityPrivacyActivity extends AppCompatActivity implements View.OnClickListener{
    Switch switchFaceId;
    Switch switchPasscode;
    APIInterface apiInterface;
    String token;
    PreferencesRepo preferencesRepo;
    Util util = new Util();
    boolean fromDialog = false;
    DisposableObserver observer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security_privacy);
        initData();

        initToolbar();
        observer();
        //initComponent();
    }

    private void observer() {
        observer = SecurityState.getSubject().subscribeWith(new DisposableObserver<String>() {
            @Override
            public void onNext(@io.reactivex.annotations.NonNull String refresh) {
                if(refresh.equals("refresh")){
                    initData();
                }
            }

            @Override
            public void onError(@io.reactivex.annotations.NonNull Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    private void initData() {
        apiInterface = APIClient.getClient().create(APIInterface.class);
        preferencesRepo = new PreferencesRepo(this);
        token = preferencesRepo.getToken();
        Observable<GetPasscodeModel> callPasscode = apiInterface.GetPasscode(token);
        callPasscode.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(this::onSuccessGetData, this::onFailedGetData);
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

    //-------------------------------- GET DATA --------------------------------//
    private void onFailedGetData(Throwable throwable) {
        util.toastError(this, "API GET PASSCODE", throwable);
    }

    private void onSuccessGetData(GetPasscodeModel getPasscodeModel) {
        if(getPasscodeModel!=null){
            PasscodeModel model= getPasscodeModel.getData();
            int isActive = model.getIsActive();
            String passcode = model.getPasscode();

            preferencesRepo.setPasscode(passcode);
            if(isActive == 0){
                initComponent(false, passcode);
                preferencesRepo.setAllowPasscode(0);
            }else{
                initComponent(true, passcode);
                preferencesRepo.setAllowPasscode(1);
            }
        }
    }


    public void initComponent(boolean checkPasscode, String passcode){
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
        switchPasscode.setChecked(checkPasscode);

        switchPasscode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if(b && passcode==null){
                    dialogCreatePasscode();
                }else{
                    //fromDialog
                    if(!fromDialog){
                        toggleSwitch();
                    }else{
                        fromDialog = false;
                    }

                }
            }
        });
    }
     //--------------------------------------------------------------------------//



    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.rlChangePasscode:
                dialogChangePasscode();
                break;
        }
    }

    private void dialogChangePasscode(){
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_confirmation);
        dialog.setCancelable(false);
        dialog.show();
        TextView tvTitle = dialog.findViewById(R.id.tvTitleDialog);
        tvTitle.setText("Change Passcode");
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


    private void dialogCreatePasscode(){
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_confirmation);
        dialog.setCancelable(false);
        dialog.show();
        TextView tvTitle = dialog.findViewById(R.id.tvTitleDialog);
        tvTitle.setText("Create Passcode");
        TextView tvData = dialog.findViewById(R.id.tvTitleData);
        tvData.setText("You don't have any passcode yet");
        Button btnCancel = dialog.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fromDialog = true;
                dialog.dismiss();
                switchPasscode.setChecked(false);
            }
        });
        Button btnContinue = dialog.findViewById(R.id.btnContinue);
        btnContinue.setText("Create Now");
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                toggleSwitch();
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

                switchFaceId.setChecked(false);
                dialog.dismiss();

            }
        });
        Button btnContinue = dialog.findViewById(R.id.btnContinue);
        btnContinue.setText("Allow");
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                //biometricPrompt();
            }
        });
    }
    private void gotoChild(Class child){
        Intent intent = new Intent(this, child);
        startActivity(intent);
    }
    private void biometricPrompt() {
        util.toastMisc(this, "Begin biomteric");
        UtilWidget utilWidget = new UtilWidget(this);
        utilWidget.biometricPrompt();
    }

    private void toggleSwitch(){
        Observable<SimpleResponse> callPasscode = apiInterface.TogglePasscode(token);
        callPasscode.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(this::onSuccessToggle, this::onFailedToggle);
    }

    private void onSuccessToggle(SimpleResponse simpleResponse) {
        util.toastMisc(this, "Success!");
    }

    private void onFailedToggle(Throwable throwable) {
        util.toastError(this, "API Toggle Passcode", throwable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        observer.dispose();
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
}