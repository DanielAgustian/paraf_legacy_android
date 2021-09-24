package com.yokesen.parafdigitalyokesen.view;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.yokesen.parafdigitalyokesen.Repository.APIClient;
import com.yokesen.parafdigitalyokesen.Repository.APIInterface;
import com.yokesen.parafdigitalyokesen.R;
import com.yokesen.parafdigitalyokesen.Repository.PreferencesRepo;
import com.yokesen.parafdigitalyokesen.model.LoginModel;
import com.yokesen.parafdigitalyokesen.util.Util;
import com.yokesen.parafdigitalyokesen.model.AuthModel;
import com.yokesen.parafdigitalyokesen.util.UtilWidget;


public class register_activity extends AppCompatActivity {
    Button btnRegister;
    TextView tvSignIn;
    EditText etPassword, etEmail, etUsername;
    EditText etConfirmPassword;
    ImageView ivPassRegister, ivConfirmPassRegister;
    APIInterface apiInterface;
    Util util = new Util();
    Dialog dialog;
    UtilWidget uw = new UtilWidget(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        apiInterface = APIClient.getClient().create(APIInterface.class);

        tvSignIn = findViewById(R.id.tvSignIn);
        etEmail = findViewById(R.id.etEmailSignUp);
        etUsername = findViewById(R.id.etNameSignUp);
        etPassword = findViewById(R.id.etPasswordSignUp);
        etConfirmPassword = findViewById(R.id.etCPasswordSU);
        btnRegister = findViewById(R.id.btnRegister);
        ivPassRegister = findViewById(R.id.show_pass_btnSU);
        ivConfirmPassRegister = findViewById(R.id.show_pass_btnCPSU);

        tvSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoRegister();
            }
        });
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //gotoRegister();
                registerClick();
            }
        });
    }

    private void registerClick() {
        String email, name, password, passwordConfirmation;
        email = etEmail.getText().toString();
        name = etUsername.getText().toString();
        password = etPassword.getText().toString();
        passwordConfirmation = etConfirmPassword.getText().toString();

        if(validation(email, name, password, passwordConfirmation)){
            doRegister(email, name, password, passwordConfirmation);
        }
    }

    private void doRegister(String email, String name, String password, String passwordConfirmation){
        AuthModel authModel = new AuthModel(email, name, password);

        dialog = uw.makeLoadingDialog();
        Observable<LoginModel> callRegister = apiInterface.registerUser(authModel);
        callRegister.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(this::handleResult, this::handleError);
//        callRegister.enqueue(new Callback<AuthModel>() {
//            @Override
//            public void onResponse(Call<AuthModel> call, Response<AuthModel> response) {
//                if(response.isSuccessful()){
//                    AuthModel result = response.body();
//                    Log.d("RegisterAPI", "RegisterAPI"+ response.body());
//
//                }
//                else{
//                    Log.d("RegisterAPI", "RegisterAPIError"+ response.body());
//                }
//            }
//
//            @Override
//            public void onFailure(Call<AuthModel> call, Throwable t) {
//
//            }
//        });
    }

    private void handleError(Throwable throwable) {
        dialog.dismiss();
        util.toastError(this, "API REGISTER", throwable);
    }

    private void handleResult(LoginModel loginModel) {
        dialog.dismiss();
        if(loginModel !=null){
            PreferencesRepo preferencesRepo = new PreferencesRepo(this);
            preferencesRepo.setToken(loginModel.getToken());
            gotoNavBar();
        }else{
            Log.e("Error Data", "Error");
        }
    }


    private boolean validation(String email, String name, String password, String passwordConfirmation){
        boolean emailValidator = false,
                nameValidator= false,
                passValidator= false,
                confirmValidator = false,
                sameValidator = false;
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if(email.length()>0 && email.matches(emailPattern)){
            emailValidator = false;
        }else{
            emailValidator= true;
        }

        if(name.trim().length()>0){
            nameValidator = false;
        }else{
            nameValidator = true;
        }

        if(password.trim().length()>5){
            passValidator = false;
        }else{
            passValidator = true;
        }

        if(passwordConfirmation.trim().length()>5){
            confirmValidator = false;
        }else{
            confirmValidator = true;
        }
        if(password.equals(passwordConfirmation)){
            sameValidator = false;
        }else{
            sameValidator = true;
        }

        util.changeColorEditText(etEmail, emailValidator, this);
        util.changeColorEditText(etUsername, nameValidator, this);
        if(sameValidator || passValidator){
            util.changeColorEditText(etPassword, true, this);
        }else{
            util.changeColorEditText(etPassword, false, this);
        }
        if(sameValidator || confirmValidator){
            util.changeColorEditText(etConfirmPassword, true, this);
        }else{
            util.changeColorEditText(etConfirmPassword, false, this);
        }

        if(!passValidator && !nameValidator && !emailValidator && !confirmValidator && !sameValidator){
            return true;
        }else{
            return false;
        }

    }

    private void gotoRegister(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void gotoNavBar() {
        Intent intent = new Intent(this, NavBarActivity.class);
        startActivity(intent);
    }
    public void ShowHidePass(View view){

        if(view.getId()==R.id.show_pass_btnSU){
            if(etPassword.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())){
                //Show Password
                etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                ivPassRegister.setImageResource(R.drawable.ic_hide_2);
            }
            else{
                //((ImageView)(view)).setImageResource(R.drawable.show_password);

                //Hide Password
                etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                ivPassRegister.setImageResource(R.drawable.ic_hide);
            }
        }else if(view.getId()==R.id.show_pass_btnCPSU){
            if(etConfirmPassword.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())){
                //Show Password
                etConfirmPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                ivConfirmPassRegister.setImageResource(R.drawable.ic_hide_2);
            }
            else{
                //((ImageView)(view)).setImageResource(R.drawable.show_password);
                //Hide Password
                etConfirmPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                ivConfirmPassRegister.setImageResource(R.drawable.ic_hide);
            }
        }
    }
}