package com.yokesen.parafdigitalyokesen.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

import com.google.firebase.FirebaseApp;
import com.yokesen.parafdigitalyokesen.R;
import com.yokesen.parafdigitalyokesen.Repository.APIClient;
import com.yokesen.parafdigitalyokesen.Repository.APIInterface;
import com.yokesen.parafdigitalyokesen.Repository.PreferencesRepo;
import com.yokesen.parafdigitalyokesen.model.LoginModel;
import com.yokesen.parafdigitalyokesen.util.Util;
import com.yokesen.parafdigitalyokesen.util.UtilWidget;
import com.yokesen.parafdigitalyokesen.view.ui.profile.child_profile.security.PasscodeView;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        FirebaseApp.initializeApp(this);
        //initFacebook();
        Timer obRunTime = new Timer();
        obRunTime.schedule(new TimerTask() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                gotoMainActivity();
            }
        },2000);

    }
//    void initFacebook(){
//        try {
//            PackageInfo info = getPackageManager().getPackageInfo(
//                    "com.yokesen.parafdigitalyokesen",
//                    PackageManager.GET_SIGNATURES);
//            for (Signature signature : info.signatures) {
//                MessageDigest md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
//            }
//        }
//        catch (PackageManager.NameNotFoundException e) {
//        }
//        catch (NoSuchAlgorithmException e) {
//        }
//    }
    private void gotoMainActivity() {
        PreferencesRepo preferencesRepo = new PreferencesRepo(this);
        String token = preferencesRepo.getToken();
        if(token != null  && !(token.equals(""))){
            refreshToken(token);

        }else{
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }

    }

    private void refreshToken(String token) {
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);

        Observable<LoginModel> callToken = apiInterface.refreshToken(token);
        callToken.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleResult, this::handleError);
    }

    private void handleError(Throwable throwable) {
        PreferencesRepo preferencesRepo = new PreferencesRepo(this);
        preferencesRepo.deleteToken();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void handleResult(LoginModel loginModel) {
        if(loginModel!= null){
            PreferencesRepo preferencesRepo = new PreferencesRepo(this);
            preferencesRepo.setToken(loginModel.getToken());
            int allowPassCode = preferencesRepo.getAllowPasscode();
            String passcode = preferencesRepo.getPasscode();

            int allowBiometric = preferencesRepo.getBiometric();
            if(allowBiometric != 0){
                UtilWidget uw = new UtilWidget(this);
                uw.biometricPrompt();
            }

            if(allowPassCode != 0 && !passcode.equals("")){
                gotoPasswordView();
            }else{
                gotoNavigationBar();
            }

        }
    }

    public void gotoPasswordView(){
        Intent intent = new Intent(this, PasscodeView.class);
        finish();
        intent.putExtra("from", "splash");
        startActivity(intent);
    }


    public void gotoNavigationBar() {
        Intent intent = new Intent(this, NavBarActivity.class);
        finish();
        startActivity(intent);
    }
}