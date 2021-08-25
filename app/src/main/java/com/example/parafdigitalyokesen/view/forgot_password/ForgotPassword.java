package com.example.parafdigitalyokesen.view.forgot_password;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.parafdigitalyokesen.R;

public class ForgotPassword extends AppCompatActivity implements View.OnClickListener {
    Button btnResetPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        btnResetPassword = findViewById(R.id.buttonResetPassword);
        btnResetPassword.setOnClickListener(this);

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
        Intent intent = new Intent(this, VerifyEmailActivity.class);
        startActivity(intent);
    }
}