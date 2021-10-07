package com.yokesen.parafdigitalyokesen.view.ui.profile.child_profile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.concurrent.Executor;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

import static androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG;
import static androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL;

public class SecurityPrivacyActivity extends AppCompatActivity implements View.OnClickListener{
    Switch switchFaceId;
    Switch switchPasscode;
    APIInterface apiInterface;
    String token;
    PreferencesRepo preferencesRepo;
    Util util = new Util();
    boolean fromDialog = false;
    DisposableObserver observer;
    int REQUEST_CODE_BIOMETRIC = 100;
    int isActive = -1;
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
            isActive = model.getIsActive();
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
        int allowBiometric = preferencesRepo.getBiometric();
        if(allowBiometric == 0){
            switchFaceId.setChecked(false);
        }else{
            switchFaceId.setChecked(true);
        }

        switchFaceId.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    dialogFaceID();
                } else{
                    preferencesRepo.setBiometric(0);
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
        tvTitle.setText("Do you want to allow “Paraf” to use Fingerprint ID?");
        TextView tvData = dialog.findViewById(R.id.tvTitleData);
        tvData.setText("Paraf can use Fingerprint  to verify your identity\n" +
                "and to secure your data.");
        Button btnCancel = dialog.findViewById(R.id.btnCancel);
        btnCancel.setText("Don't Allow");
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                switchFaceId.setChecked(false);
                preferencesRepo.setBiometric(0);
                dialog.dismiss();

            }
        });
        Button btnContinue = dialog.findViewById(R.id.btnContinue);
        btnContinue.setText("Allow");
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                biometricPrompt();
            }
        });
    }
    private void gotoChild(Class child){
        Intent intent = new Intent(this, child);
        startActivity(intent);
    }
//    private void biometricPrompt() {
////        util.toastMisc(this, "Begin biomteric");
//        UtilWidget utilWidget = new UtilWidget(this);
//        utilWidget.biometricPrompt();
//    }

    private void toggleSwitch(){
        Observable<SimpleResponse> callPasscode = apiInterface.TogglePasscode(token);
        callPasscode.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(this::onSuccessToggle, this::onFailedToggle);
    }

    private void onSuccessToggle(SimpleResponse simpleResponse) {
        util.toastMisc(this, "Success!");
        if(isActive == 0){
            isActive = 1;
            preferencesRepo.setAllowPasscode(1);
        }else{
            isActive = 0;
            preferencesRepo.setAllowPasscode(0);
        }
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

    public void biometricPrompt(){
//        BiometricManager biometricManager = BiometricManager.from(this);
//
//        switch (biometricManager.canAuthenticate(BIOMETRIC_STRONG | DEVICE_CREDENTIAL)) {
//            case BiometricManager.BIOMETRIC_SUCCESS:
//                Log.d("MY_APP_TAG", "App can authenticate using biometrics.");
//                util.toastMisc(this, "Success!");
//                break;
//            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
//                Log.e("MY_APP_TAG", "No biometric features available on this device.");
//                switchFaceId.setChecked(false);
//                util.toastMisc(this, "No biometric features available on this device.");
//                break;
//            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
//                Log.e("MY_APP_TAG", "Biometric features are currently unavailable.");
//                switchFaceId.setChecked(false);
//                util.toastMisc(this, "Biometric features are currently unavailable.");
//                break;
//            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
//                // Prompts the user to create credentials that your app accepts.
//                final Intent enrollIntent = new Intent(Settings.ACTION_BIOMETRIC_ENROLL);
//                enrollIntent.putExtra(Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
//                        BIOMETRIC_STRONG | DEVICE_CREDENTIAL);
//                startActivityForResult(enrollIntent, REQUEST_CODE_BIOMETRIC);
//                break;
//        }

        Executor executor;
        BiometricPrompt biometricPrompt;
        BiometricPrompt.PromptInfo promptInfo;
        executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(this,
                executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode,
                                              @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                switchFaceId.setChecked(false);
                String error = errString.toString().toLowerCase();
                if(error.contains("enrolled")){
                    util.toastMisc(SecurityPrivacyActivity.this,"You dont have any fingerprint recorded. Please record new one and try again.");
                    Intent intent = new Intent(Settings.ACTION_BIOMETRIC_ENROLL);
                    startActivity(intent);
                }else if(error.contains("unavailable")|| error.contains("biometric sensor")){
                    util.toastMisc(SecurityPrivacyActivity.this,"Your device isnt compatible with this feature.");

                }else if(error.contains("have pin")){
                    util.toastMisc(SecurityPrivacyActivity.this,"You need pin, pattern, or password in this device to use fingerprint feature");
                }
                else{
                    Toast.makeText(SecurityPrivacyActivity.this,
                            "Authentication error: " + errString, Toast.LENGTH_SHORT)
                            .show();
                }
                preferencesRepo.setBiometric(0);
            }

            @Override
            public void onAuthenticationSucceeded(
                    @NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Toast.makeText(SecurityPrivacyActivity.this,
                        "Authentication succeeded!", Toast.LENGTH_SHORT).show();
                switchFaceId.setChecked(true);
                preferencesRepo.setBiometric(1);
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(SecurityPrivacyActivity.this, "Authentication failed",
                        Toast.LENGTH_SHORT)
                        .show();
                switchFaceId.setChecked(false);
                preferencesRepo.setBiometric(0);
            }
        });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric login for Teken")
                .setSubtitle("Log in using your biometric credential")
                .setNegativeButtonText("Use account password")
                .build();

        biometricPrompt.authenticate(promptInfo);

    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if(requestCode == REQUEST_CODE_BIOMETRIC && resultCode == RESULT_OK){
//            switchFaceId.setChecked(true);
//        }
//    }
}