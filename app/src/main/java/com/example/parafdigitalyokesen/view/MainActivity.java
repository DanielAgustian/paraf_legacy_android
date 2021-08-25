package com.example.parafdigitalyokesen.view;


import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.parafdigitalyokesen.R;
import com.example.parafdigitalyokesen.view.forgot_password.ForgotPassword;


public class MainActivity extends AppCompatActivity {
    EditText etPassword;
    ImageView ivPassword;
    TextView tvSignUp, tvForgotPas;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvSignUp = findViewById(R.id.tvSignUp);
        Button buttonLogin = findViewById(R.id.buttonLogin);
        // To Implement Toolbar Back Button
        etPassword = findViewById(R.id.etPassword);
        ivPassword = findViewById(R.id.show_pass_btn);
        tvForgotPas = findViewById(R.id.forgotPassword);
        //onClick for Back
        buttonLogin.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        gotoNavigationBar();
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
            }
            else{
                //((ImageView)(view)).setImageResource(R.drawable.show_password);

                //Hide Password
                etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());

            }
        }
    }

}