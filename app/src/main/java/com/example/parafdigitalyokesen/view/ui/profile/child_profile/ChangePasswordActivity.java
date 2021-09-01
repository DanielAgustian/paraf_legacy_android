package com.example.parafdigitalyokesen.view.ui.profile.child_profile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.parafdigitalyokesen.R;

public class ChangePasswordActivity extends AppCompatActivity implements View.OnClickListener{
    EditText etPassword, etNewPassword, etConfirmPassword;
    ImageView ivPassword, ivNewPassword, ivConfirmPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        initToolbar();
        initComponent();
    }

    private void initComponent() {
        etPassword = findViewById(R.id.etCurrentPassword);
        etNewPassword = findViewById(R.id.etNewChangePassword);
        etConfirmPassword = findViewById(R.id.etReEnterPassword);

        ivPassword = findViewById(R.id.ivCurrentPassword);
        ivPassword.setOnClickListener(this);
        ivNewPassword = findViewById(R.id.ivNewChangePassword);
        ivNewPassword.setOnClickListener(this);
        ivConfirmPassword = findViewById(R.id.ivReEnterPassword);
        ivConfirmPassword.setOnClickListener(this);
    }

    public void initToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarChangePassword);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationIcon(R.drawable.ic_back_gray);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("mainActivity", "Back Clicked");
                back();
            }
        });
    }
    public void back(){
        this.finish();
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

    @Override
    public void onClick(View view) {
        int numeric = view.getId();
        if(numeric == R.id.ivCurrentPassword || numeric== R.id.ivNewChangePassword || numeric == R.id.ivReEnterPassword){
            ShowHidePass(view);
        }
    }
}