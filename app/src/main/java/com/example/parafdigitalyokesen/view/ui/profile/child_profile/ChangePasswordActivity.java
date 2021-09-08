package com.example.parafdigitalyokesen.view.ui.profile.child_profile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.ViewCompat;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.parafdigitalyokesen.R;
import com.example.parafdigitalyokesen.Repository.APIClient;
import com.example.parafdigitalyokesen.Repository.APIInterface;
import com.example.parafdigitalyokesen.Repository.PreferencesRepo;
import com.example.parafdigitalyokesen.model.SimpleResponse;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ChangePasswordActivity extends AppCompatActivity implements View.OnClickListener{
    EditText etPassword, etNewPassword, etConfirmPassword;
    ImageView ivPassword, ivNewPassword, ivConfirmPassword;
    Button btnChangePassword;

    boolean passValidation= false,
            newPassValidation = false,
            confirmPassValidation = false,
            diffPassValidation = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        initToolbar();
        initComponent();
    }

    //-------------------------------Logic to API--------------------------
    private void doChangePassword(){

        if (validation()){
            APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
            PreferencesRepo preferencesRepo = new PreferencesRepo(this);

            String token = preferencesRepo.getToken();
            Observable<SimpleResponse> updatePassword = apiInterface.putChangePassword(
                    token,
                    etPassword.getText().toString(),
                    etNewPassword.getText().toString(),
                    etConfirmPassword.getText().toString());
            try {
                updatePassword.subscribeOn(Schedulers.io()).
                        observeOn(AndroidSchedulers.mainThread()).
                        subscribe(this::onSuccess, this::onFailed);
            } catch (Exception e){
                Log.e("UpdatePasswordCrash", e.getMessage());
            }
        }
    }

    private void onFailed(Throwable throwable) {
        Log.e("FailedMessage", throwable.getMessage());
        Toast.makeText(this, "ERROR IN PUT CHANGE PASSWORD. Try again" + throwable.getMessage(),
                Toast.LENGTH_LONG).show();
        if(throwable.getMessage().contains("400")){

        }
    }

    private void onSuccess(SimpleResponse simpleResponse) {
        if(simpleResponse.getStatus().equals("Success")){
            back();
        }
    }

    //------------------------Validator for TextBox -----------------------------
    private boolean validation(){
        if (etPassword.getText().toString().trim().length()<6){
            passValidation = true;
        } else {
            passValidation = false;
        }
        if (etNewPassword.getText().toString().trim().length()<6){
            newPassValidation = true;
        } else {
            newPassValidation = false;
        }
        if (etConfirmPassword.getText().toString().trim().length()<6){
            confirmPassValidation = true;
        } else {
            confirmPassValidation = false;
        }
        if(etNewPassword.getText().toString().trim()
                .equals(etConfirmPassword.getText().toString().trim())){
            diffPassValidation = false;
        } else{
            diffPassValidation = true;
        }

        changeColor(etPassword, passValidation);
        if(diffPassValidation || confirmPassValidation){
            changeColor(etConfirmPassword , true);
        }else{
            changeColor(etConfirmPassword, false);
        }

        if(diffPassValidation || newPassValidation){
            changeColor(etNewPassword , true);
        }else{
            changeColor(etNewPassword, false);
        }

        if(!passValidation && !newPassValidation && !confirmPassValidation && !diffPassValidation){
            return true;
        } else{

            return false;
        }
    }


    private void changeColor(EditText editText, boolean validator){
        if(validator){
            editText.setTextColor(getResources().getColor(R.color.colorError));
            ColorStateList colorStateList = ColorStateList.valueOf(getResources().getColor(R.color.colorError));
            ViewCompat.setBackgroundTintList(editText, colorStateList);
        } else{
            editText.setTextColor(getResources().getColor(R.color.colorGrayText));
            ColorStateList colorStateList = ColorStateList.valueOf(getResources().getColor(R.color.colorLightGrayText));
            ViewCompat.setBackgroundTintList(editText, colorStateList);
        }
    }


    //--------------------------Init Data + Component ------------------------
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

        btnChangePassword = findViewById(R.id.btnChangePassword);
        btnChangePassword.setOnClickListener(this);
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
        if(view.getId()==R.id.ivCurrentPassword){
            if(etPassword.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())){
                //Show Password
                etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                ivPassword.setImageResource(R.drawable.ic_hide_2);
            }
            else{
                //Hide Password
                etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                ivPassword.setImageResource(R.drawable.ic_hide);
            }
        } else if( view.getId() == R.id.ivNewChangePassword){
            if(etNewPassword.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())){
                //Show Password
                etNewPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                ivNewPassword.setImageResource(R.drawable.ic_hide_2);
            }
            else{
                //Hide Password
                etNewPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                ivNewPassword.setImageResource(R.drawable.ic_hide);
            }
        }else if( view.getId() == R.id.ivReEnterPassword){
            if(etConfirmPassword.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())){
                //Show Password
                etConfirmPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                ivConfirmPassword.setImageResource(R.drawable.ic_hide_2);
            }
            else{
                //Hide Password
                etConfirmPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                ivConfirmPassword.setImageResource(R.drawable.ic_hide);
            }
        }
    }

    @Override
    public void onClick(View view) {
        int numeric = view.getId();
        if(numeric == R.id.ivCurrentPassword || numeric== R.id.ivNewChangePassword || numeric == R.id.ivReEnterPassword){
            ShowHidePass(view);
        } else if (numeric == R.id.btnChangePassword){
            doChangePassword();
        }
    }
}