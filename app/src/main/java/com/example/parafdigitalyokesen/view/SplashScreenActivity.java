package com.example.parafdigitalyokesen.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.parafdigitalyokesen.R;
import com.example.parafdigitalyokesen.Repository.APIClient;
import com.example.parafdigitalyokesen.Repository.APIInterface;
import com.example.parafdigitalyokesen.Repository.PreferencesRepo;
import com.example.parafdigitalyokesen.Util;
import com.example.parafdigitalyokesen.model.LoginModel;
import com.example.parafdigitalyokesen.view.add_sign.ResultSignature;

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
        Timer obRunTime = new Timer();
        obRunTime.schedule(new TimerTask() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                gotoMainActivity();
            }
        },2000);
    }

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
            gotoNavigationBar();
        }
    }
    public void gotoNavigationBar() {
        Intent intent = new Intent(this, NavBarActivity.class);
        finish();
        startActivity(intent);
    }
}