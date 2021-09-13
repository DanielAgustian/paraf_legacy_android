package com.example.parafdigitalyokesen.view.forgot_password;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.parafdigitalyokesen.R;
import com.example.parafdigitalyokesen.Repository.APIClient;
import com.example.parafdigitalyokesen.Repository.APIInterface;
import com.example.parafdigitalyokesen.Util;
import com.example.parafdigitalyokesen.model.SimpleResponse;
import com.example.parafdigitalyokesen.view.MainActivity;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class NewPassword extends AppCompatActivity implements View.OnClickListener {
    EditText etPassword, etConfirmPassword;
    ImageView ivPassword, ivConfirmPassword;
    Button btnReset;
    boolean passValidator = false, confirmValidator=false, sameValidator = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_password);
        initComponent();
    }

    private void initComponent() {
        etPassword = findViewById(R.id.etNewPassword);
        etConfirmPassword = findViewById(R.id.etCNewPassword);
        ivPassword = findViewById(R.id.show_pass_new);
        ivConfirmPassword = findViewById(R.id.show_pass_confirm_new);
        btnReset = findViewById(R.id.buttonNewPassword);
        btnReset.setOnClickListener(this);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.buttonNewPassword:
                executeNewPassword();
                break;
        }

    }

    public void executeNewPassword() {
        String email = getIntent().getStringExtra("email");
        String password = etPassword.getText().toString();
        String confirmPassword = etConfirmPassword.getText().toString();
        if(email!= null && validation(password, confirmPassword)){
            APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);

            Observable<SimpleResponse> callVerifyEmail = apiInterface.resetPassword(email, password, confirmPassword);
            callVerifyEmail.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(this::onSuccess, this::onFailed);
        }

    }

    private void onFailed(Throwable throwable) {
        Util util = new Util();
        util.toastError(this, "API Token Password", throwable);
    }

    private void onSuccess(SimpleResponse simpleResponse) {
        if(simpleResponse != null){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }

    private boolean validation(String password, String confirmPass){
        if(password.length() <6){
            passValidator = true;
        } else{
            passValidator = false;
        }

        if(confirmPass.length()< 6){
            confirmValidator = true;
        }else{
            confirmValidator = false;
        }

        if(confirmPass.equals(password)){
            sameValidator= false;
        }else{
            sameValidator = true;
        }

        if(!passValidator && !confirmValidator && !sameValidator){
            return true;
        }else{
            return false;
        }
    }



    public void ShowHidePass(View view){

        if(view.getId()==R.id.show_pass_new){

            if(etPassword.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())){


                //Show Password
                etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                ivPassword.setImageResource(R.drawable.ic_hide_2);
            }
            else{
                //((ImageView)(view)).setImageResource(R.drawable.show_password);
                //Hide Password
                etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                ivPassword.setImageResource(R.drawable.ic_hide);

            }
        }else if(view.getId()==R.id.show_pass_confirm_new){

            if(etConfirmPassword.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())){


                //Show Password
                etConfirmPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                ivConfirmPassword.setImageResource(R.drawable.ic_hide_2);
            }
            else{
                //((ImageView)(view)).setImageResource(R.drawable.show_password);

                //Hide Password
                etConfirmPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                ivConfirmPassword.setImageResource(R.drawable.ic_hide);

            }
        }
    }


}