package com.example.parafdigitalyokesen.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.parafdigitalyokesen.API.APIClient;
import com.example.parafdigitalyokesen.API.APIInterface;
import com.example.parafdigitalyokesen.R;
import com.example.parafdigitalyokesen.model.AuthModel;
import com.google.android.material.badge.BadgeUtils;


public class register_activity extends AppCompatActivity {
    Button btnRegister;
    TextView tvSignIn;
    EditText etPassword, etEmail, etUsername;
    EditText etConfirmPassword;
    ImageView ivPassRegister, ivConfirmPassRegister;
    APIInterface apiInterface;
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
        AuthModel authModel = new AuthModel(email, name, password, passwordConfirmation);
        Call<AuthModel> callRegister = apiInterface.registerUser(authModel);
        callRegister.enqueue(new Callback<AuthModel>() {
            @Override
            public void onResponse(Call<AuthModel> call, Response<AuthModel> response) {
                if(response.isSuccessful()){
                    AuthModel result = response.body();
                    Log.d("RegisterAPI", "RegisterAPI"+ response.body());
                    
                }
                else{
                    Log.d("RegisterAPI", "RegisterAPIError"+ response.body());
                }
            }

            @Override
            public void onFailure(Call<AuthModel> call, Throwable t) {

            }
        });
    }

    private boolean validation(String email, String name, String password, String passwordConfirmation){
        if(email.trim().equals("")){
            return false;
        } else if (name.trim().equals("")){
            return false;
        } else if (password.trim().equals("") || password.length() < 7){
            return false;
        } else if (passwordConfirmation.trim().equals("") || passwordConfirmation.length()< 7){
            return false;
        } else if (!password.equals(passwordConfirmation)){
            return false;
        }
        return true;
    }


    public void gotoRegister() {
        Intent intent = new Intent(this, MainActivity.class);
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