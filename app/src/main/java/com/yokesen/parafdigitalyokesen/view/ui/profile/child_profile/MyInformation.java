package com.yokesen.parafdigitalyokesen.view.ui.profile.child_profile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.yokesen.parafdigitalyokesen.R;
import com.yokesen.parafdigitalyokesen.Repository.APIClient;
import com.yokesen.parafdigitalyokesen.Repository.APIInterface;
import com.yokesen.parafdigitalyokesen.Repository.PreferencesRepo;
import com.yokesen.parafdigitalyokesen.model.GetMyInfoModel;
import com.yokesen.parafdigitalyokesen.model.MyInformationModel;
import com.yokesen.parafdigitalyokesen.model.SimpleResponse;
import com.yokesen.parafdigitalyokesen.view.ui.profile.child_profile.security.PasscodeView;

import java.util.Calendar;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MyInformation extends AppCompatActivity {

    EditText etName, etEmail, etPhone, etInitial, etCompany;
    APIInterface apiInterface;
    PreferencesRepo preferencesRepo;
    MyInformationModel model;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_information);
        initSpinner();
        initToolbar();
        initComponent();
        initData();
    }

    long milisStart = 0;
    @Override
    protected void onPause() {
        super.onPause();
        milisStart = Calendar.getInstance().getTimeInMillis();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        String passcode = preferencesRepo.getPasscode();
        int isActive = preferencesRepo.getAllowPasscode();

        if(isActive == 1 && passcode!= null && passcode.equals("")){
            long intervetion = 30 * 60 * 1000;
            long milisNow = Calendar.getInstance().getTimeInMillis();
            long milisSelisih = milisNow - milisStart;
            if(intervetion < milisSelisih && milisSelisih!= milisNow){
                Intent intent = new Intent(this, PasscodeView.class);
                startActivity(intent);
            }
        }

        //biometricPrompt();
    }
    //------------------------------Get My Info Data-------------------------------
    private void initData() {
        apiInterface = APIClient.getClient().create(APIInterface.class);
        preferencesRepo = new PreferencesRepo(this);

        String token = preferencesRepo.getToken();
        Observable<GetMyInfoModel> callHome = apiInterface.getMyInfo(token);
        try {
            callHome.subscribeOn(Schedulers.io()).
                    observeOn(AndroidSchedulers.mainThread()).
                    subscribe(this::onSuccess, this::onFailed);
        } catch (Exception e){
            Log.e("ProfileCrash", e.getMessage());
        }
    }

    private void onFailed(Throwable throwable) {
        Log.e("MyInfoCrash", throwable.getMessage());
        Toast.makeText(this, "ERROR IN FETCHING API PROFILE. Try again" + throwable.getMessage(),
                Toast.LENGTH_LONG).show();
    }

    private void onSuccess(GetMyInfoModel getMyInfoModel) {
        if(getMyInfoModel != null){
            model = getMyInfoModel.getHome();
            etName.setText(model.getName());
            etEmail.setText(model.getEmail());
            etInitial.setText(model.getInitial()== null
                    ?"":
                    model.getInitial());
            etCompany.setText(model.getCompany()== null
                    ?"":
                    model.getCompany());
            etPhone.setText(model.getPhone() == null
                    ?"":
                    model.getPhone());
        }
    }


    //------------------------------Update My Info Data Start----------------------------------

    private void clickButton(){
        boolean nameVal = false,
                emailVal = false,
                phoneVal = false,
                companyVal =false,
                initialVal= false;
        if(!(etName.getText().toString().equals(model.getName()))){
            nameVal = true;
        } else{
            nameVal = false;
        }

        if(!etEmail.getText().toString().equals(model.getEmail())){
            emailVal = true;
        }else{
            emailVal = false;
        }

        if(!etPhone.getText().toString().equals(model.getPhone())){
            phoneVal = true;
        }else{
            phoneVal = false;
        }

        if(!etCompany.getText().toString().equals(model.getCompany())){
            companyVal = true;
        }else{
            companyVal = false;
        }
        if(!etInitial.getText().toString().equals(model.getCompany())){
            initialVal = true;
        }else{
            initialVal = false;
        }

        if(nameVal || emailVal || phoneVal || companyVal || initialVal){
            MyInformationModel modelInfo = new MyInformationModel(
                    etName.getText().toString(),
                    etEmail.getText().toString(),
                    etPhone.getText().toString(),
                    etInitial.getText().toString(),
                    model.getPosition(),
                    etCompany.getText().toString()
            );
            updateData(modelInfo);
        }
    }

    private void updateData(MyInformationModel model) {
        //apiInterface = APIClient.getClient().create(APIInterface.class);
        PreferencesRepo preferencesRepo = new PreferencesRepo(this);

        String token = preferencesRepo.getToken();
        Observable<SimpleResponse> callHome = apiInterface.putMyInfo(token, model);
        try {
            callHome.subscribeOn(Schedulers.io()).
                    observeOn(AndroidSchedulers.mainThread()).
                    subscribe(this::onUpdateSuccess, this::onUpdateFailed);
        } catch (Exception e){
            Log.e("ProfileCrash", e.getMessage());
        }
    }

    private void onUpdateSuccess(SimpleResponse response) {
        back();
    }

    private void onUpdateFailed(Throwable throwable) {
        Log.e("MyInfoUpdateCrash", throwable.getMessage());
        Toast.makeText(this, "ERROR IN UPDATE API MY INFO. Try again" + throwable.getMessage(),
                Toast.LENGTH_LONG).show();
    }



    //-------------------initialization
    private void initComponent() {
        etName = findViewById(R.id.etNameMyInfo);
        etCompany = findViewById(R.id.etCompMyInfo);
        etEmail = findViewById(R.id.etEmailMyInfo);
        etPhone = findViewById(R.id.etPhoneMyInfo);
        etInitial = findViewById(R.id.etInitialMyInfo);

        Button btnUpdateInfo= findViewById(R.id.btnUpdateInfo);
        btnUpdateInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickButton();
            }
        });
    }
    public void initSpinner(){
        String[] spinnerPhoneNumber = new String[]{
                "+62", "+82"
        };

        Spinner spinnerPhone = findViewById(R.id.spinnerPhoneNumber);

        ArrayAdapter<String> adapterCategory = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerPhoneNumber);

        adapterCategory.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPhone.setAdapter(adapterCategory);
        spinnerPhone.setPrompt("Select Category");

    }
    public void initToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarMyInfo);
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


}