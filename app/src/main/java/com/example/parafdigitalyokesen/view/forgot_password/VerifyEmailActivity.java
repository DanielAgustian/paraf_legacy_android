package com.example.parafdigitalyokesen.view.forgot_password;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.parafdigitalyokesen.R;

public class VerifyEmailActivity extends AppCompatActivity implements View.OnClickListener{
    Button btnSendEmail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_email);
        btnSendEmail = findViewById(R.id.btnSendEmail);

        btnSendEmail.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnSendEmail:
                gotoNewPassword();
                break;
        }
    }

    public void gotoNewPassword() {
        Intent intent = new Intent(this, NewPassword.class);
        startActivity(intent);
    }
}