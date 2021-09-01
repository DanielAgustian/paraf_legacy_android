package com.example.parafdigitalyokesen.view;


import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.parafdigitalyokesen.API.APIClient;
import com.example.parafdigitalyokesen.API.APIInterface;
import com.example.parafdigitalyokesen.R;
import com.example.parafdigitalyokesen.model.AuthModel;
import com.example.parafdigitalyokesen.model.LoginModel;
import com.example.parafdigitalyokesen.view.forgot_password.ForgotPassword;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity {
    EditText etPassword, etEmail;
    ImageView ivPassword;
    TextView tvSignUp, tvForgotPas;
    APIInterface apiInterface;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvSignUp = findViewById(R.id.tvSignUp);
        Button buttonLogin = findViewById(R.id.btnLogin);
        // To Implement Toolbar Back Button
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        ivPassword = findViewById(R.id.show_pass_btn);
        tvForgotPas = findViewById(R.id.forgotPassword);

        apiInterface = APIClient.getClient().create(APIInterface.class);
        //onClick for Back
        buttonLogin.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        loginClick();
                    }
                }
        );

        //onClick for Goto Register
        tvSignUp.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        gotoRegister();
                    }
                }

        );
        tvForgotPas.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        gotoForgotPass();
                    }
                }
        );
    }

    void loginClick(){
        String email, password;
        email = etEmail.getText().toString();
        password = etPassword.getText().toString();
        if (validation(email, password)){
            doLogin(email, password);
        }
    }
    void doLogin(String email, String password){
        Call<LoginModel> callLogin = apiInterface.loginUser(email, password);
        callLogin.enqueue(new Callback<LoginModel>() {
            @Override
            public void onResponse(Call<LoginModel> call, Response<LoginModel> response) {
                if(response.isSuccessful()){
                    LoginModel result = response.body();
                    Log.d("RegisterAPI", "RegisterAPI"+ response.body());
                    sharedPrefLogin(result.getToken());
                    gotoNavigationBar();
                }
                else{
                    Log.d("RegisterAPI", "RegisterAPIError"+ response.body());
                }
            }

            @Override
            public void onFailure(Call<LoginModel> call, Throwable t) {

            }
        });
    }

    boolean validation(String email, String password){
        if(email.trim().equals("")){
            return false;
        }else if (password.trim().equals("")){
            return false;
        } else if (password.length() < 7){
            return false;
        } else{
            return true;
        }
    }

    void sharedPrefLogin(String token){
        SharedPreferences.Editor editor = getSharedPreferences("LOGIN", MODE_PRIVATE).edit();
        editor.putString("token", token);
        editor.apply();
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.toolbar_login, menu);
        return true;
    }

    public void gotoRegister() {
        Intent intent = new Intent(this, register_activity.class);
        startActivity(intent);
    }

    public void gotoNavigationBar() {
        Intent intent = new Intent(this, NavBarActivity.class);
        startActivity(intent);
    }
    public void gotoForgotPass() {
        Intent intent = new Intent(this, ForgotPassword.class);
        startActivity(intent);
    }
    public void ShowHidePass(View view){

        if(view.getId()==R.id.show_pass_btn){

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
        }
    }

}