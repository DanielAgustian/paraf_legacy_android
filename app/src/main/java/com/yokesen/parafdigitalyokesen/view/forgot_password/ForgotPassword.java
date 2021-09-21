package com.yokesen.parafdigitalyokesen.view.forgot_password;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.yokesen.parafdigitalyokesen.R;
import com.yokesen.parafdigitalyokesen.Repository.APIClient;
import com.yokesen.parafdigitalyokesen.Repository.APIInterface;
import com.yokesen.parafdigitalyokesen.util.Util;
import com.yokesen.parafdigitalyokesen.model.SimpleResponse;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ForgotPassword extends AppCompatActivity implements View.OnClickListener {
    Button btnResetPassword;
    EditText etUsername;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        btnResetPassword = findViewById(R.id.buttonResetPassword);
        btnResetPassword.setOnClickListener(this);
        etUsername = findViewById(R.id.etUsername);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.buttonResetPassword:
                    gotoVerify();
                break;
        }
    }
    public void gotoVerify() {

        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        String email = etUsername.getText().toString();
        if(email.matches(emailPattern)){
            APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);


            Observable<SimpleResponse> callForgotPassword = apiInterface.forgotPassword(email);
            callForgotPassword.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(this::onSuccess, this::onFailed);
        }


    }

    private void onFailed(Throwable throwable) {
        Util util = new Util();
        util.toastError(this, "API Forgot Password", throwable);
    }

    private void onSuccess(SimpleResponse simpleResponse) {
        if(simpleResponse != null){
            Intent intent = new Intent(this, VerifyEmailActivity.class);
            intent.putExtra("email",etUsername.getText().toString() );
            startActivity(intent);
        }
    }
}