package com.example.parafdigitalyokesen.view.forgot_password;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.parafdigitalyokesen.R;

public class NewPassword extends AppCompatActivity {
    EditText etPassword, etConfirmPassword;
    ImageView ivPassword, ivConfirmPassword;
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