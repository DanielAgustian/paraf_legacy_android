package com.yokesen.parafdigitalyokesen.view.forgot_password;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.yokesen.parafdigitalyokesen.R;
import com.yokesen.parafdigitalyokesen.Repository.APIClient;
import com.yokesen.parafdigitalyokesen.Repository.APIInterface;
import com.yokesen.parafdigitalyokesen.util.Util;
import com.yokesen.parafdigitalyokesen.model.SimpleResponse;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class VerifyEmailActivity extends AppCompatActivity implements View.OnClickListener{
    Button btnSendEmail;
    EditText etTokenEmail;
    TextView tvResendCode;
    Util util = new Util();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_email);

        btnSendEmail = findViewById(R.id.btnSendEmail);
        btnSendEmail.setOnClickListener(this);

        etTokenEmail = findViewById(R.id.etTokenEmail);
        tvResendCode = findViewById(R.id.tvResendCode);
        tvResendCode.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnSendEmail:
                gotoNewPassword();
                break;
            case  R.id.tvResendCode:

                break;
        }
    }

    public void gotoNewPassword() {
        String email = getIntent().getStringExtra("email");
        String tokenPassword = etTokenEmail.getText().toString();
        if(email!= null && tokenPassword.length()>3){
            APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);


            Observable<SimpleResponse> callVerifyEmail = apiInterface.checkPasswordToken(email, tokenPassword);
            callVerifyEmail.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(this::onSuccess, this::onFailed);
        }

    }

    private void onFailed(Throwable throwable) {
        Util util = new Util();
        util.toastError(this, "API Token Password", throwable);
    }

    private void onSuccess(SimpleResponse simpleResponse) {
        if(simpleResponse != null){
            Intent intent = new Intent(this, NewPassword.class);
            intent.putExtra("email",getIntent().getStringExtra("email") );
            startActivity(intent);
        }
    }

    ///----------------------- Resend Code-------------------------------------

    public void gotoVerify() {


        String email = getIntent().getStringExtra("email");

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);


        Observable<SimpleResponse> callForgotPassword = apiInterface.forgotPassword(email);
        callForgotPassword.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(this::onSuccessResend, this::onFailedResend);



    }

    private void onFailedResend(Throwable throwable) {
        Util util = new Util();
        util.toastError(this, "API Forgot Password", throwable);
    }

    private void onSuccessResend(SimpleResponse simpleResponse) {
        if(simpleResponse != null){
            util.toastMisc(this, "Code has been sent. \nPlease check your email.");
        }
    }
}