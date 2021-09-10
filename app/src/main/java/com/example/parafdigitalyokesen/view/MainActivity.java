package com.example.parafdigitalyokesen.view;


import androidx.appcompat.app.AppCompatActivity;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.parafdigitalyokesen.Repository.APIClient;
import com.example.parafdigitalyokesen.Repository.APIInterface;
import com.example.parafdigitalyokesen.R;
import com.example.parafdigitalyokesen.Repository.PreferencesRepo;
import com.example.parafdigitalyokesen.model.LoginModel;
import com.example.parafdigitalyokesen.view.forgot_password.ForgotPassword;


import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


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
                        //gotoNavigationBar();
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
        Observable<LoginModel> callLogin = apiInterface.loginUser(email, password);
        callLogin.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleResult, this::handleError);
//        callLogin.enqueue(new Callback<LoginModel>() {
//            @Override
//            public void onResponse(Call<LoginModel> call, Response<LoginModel> response) {
//                if(response.isSuccessful()){
//                    LoginModel result = response.body();
//                    Log.d("RegisterAPI", "RegisterAPI"+ response.body());
//                    sharedPrefLogin(result.getToken());
//                    gotoNavigationBar();
//                }
//                else{
//                    Log.d("RegisterAPI", "RegisterAPIError"+ response.body());
//                }
//            }
//
//            @Override
//            public void onFailure(Call<LoginModel> call, Throwable t) {
//
//            }
//        });
    }




    boolean validation(String email, String password){
        if(email.trim().equals("")){
            return false;
        }else if (password.trim().equals("")){
            return false;
        } else if (password.length() < 6){
            return false;
        } else{
            return true;
        }
    }
    private void handleResult(LoginModel loginModel) {
        if(loginModel != null){

            PreferencesRepo preferencesRepo = new PreferencesRepo(this);
            preferencesRepo.setToken(loginModel.getToken());
            gotoNavigationBar();
        }else{
            Log.d("LoginAPI", "LoginAPI "+  "Emptty" );
        }

    }
    private void handleError(Throwable throwable) {
        Toast.makeText(this, "ERROR IN FETCHING API RESPONSE. Try again",
                Toast.LENGTH_LONG).show();
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
        finish();
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