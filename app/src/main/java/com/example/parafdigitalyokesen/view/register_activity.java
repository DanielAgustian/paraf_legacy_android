package com.example.parafdigitalyokesen.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.parafdigitalyokesen.R;


public class register_activity extends AppCompatActivity {
    TextView tvSignIn;
    EditText etPassword;
    EditText etConfirmPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        tvSignIn = findViewById(R.id.tvSignIn);
        etPassword = findViewById(R.id.etPasswordSignUp);
        etConfirmPassword = findViewById(R.id.etCPasswordSU);
        tvSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoRegister();
            }
        });
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
            }
            else{
                //((ImageView)(view)).setImageResource(R.drawable.show_password);

                //Hide Password
                etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());

            }
        }else if(view.getId()==R.id.show_pass_btnCPSU){
            if(etConfirmPassword.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())){
                //Show Password
                etConfirmPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            }
            else{
                //((ImageView)(view)).setImageResource(R.drawable.show_password);
                //Hide Password
                etConfirmPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        }
    }
}