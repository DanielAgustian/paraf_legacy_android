package com.yokesen.parafdigitalyokesen.view.ui.profile.child_profile.security;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.yokesen.parafdigitalyokesen.R;
import com.yokesen.parafdigitalyokesen.Repository.APIClient;
import com.yokesen.parafdigitalyokesen.Repository.APIInterface;
import com.yokesen.parafdigitalyokesen.Repository.PreferencesRepo;
import com.yokesen.parafdigitalyokesen.model.GetConnectModel;
import com.yokesen.parafdigitalyokesen.model.GetPasscodeModel;
import com.yokesen.parafdigitalyokesen.util.Util;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class CreatePasscodeActivity extends AppCompatActivity implements View.OnClickListener{
    ArrayList<String> passcode = new ArrayList<>();
    ArrayList<Integer> ivList = new ArrayList<>();
    Util util = new Util();
    APIInterface apiInterface;
    String token;
    PreferencesRepo preferencesRepo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_passcode);
        initToolbar();

        initComponent();
    }



    public void initToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarCreatePasscode);
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

    public void createPasscode(String passcode){

    }

    public void initComponent(){
//        Button btnSave = findViewById(R.id.buttonPickPasscode);
//        btnSave.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                gotoConfirmPasscode();
//            }
//        });


        ivList.add(R.id.dot1);
        ivList.add(R.id.dot2);
        ivList.add(R.id.dot3);
        ivList.add(R.id.dot4);
        ivList.add(R.id.dot5);
        ivList.add(R.id.dot6);

        LinearLayout btnKeypad1 = findViewById(R.id.keypad1);
        LinearLayout btnKeypad2 = findViewById(R.id.keypad2);
        LinearLayout btnKeypad3 = findViewById(R.id.keypad3);
        LinearLayout btnKeypad4 = findViewById(R.id.keypad4);
        LinearLayout btnKeypad5 = findViewById(R.id.keypad5);
        LinearLayout btnKeypad6 = findViewById(R.id.keypad6);
        LinearLayout btnKeypad7 = findViewById(R.id.keypad7);
        LinearLayout btnKeypad8 = findViewById(R.id.keypad8);
        LinearLayout btnKeypad9 = findViewById(R.id.keypad9);
        LinearLayout btnKeypad0 = findViewById(R.id.keypad0);
        LinearLayout btnKeypadBack = findViewById(R.id.keypad_back);

        btnKeypad0.setOnClickListener(this);
        btnKeypad1.setOnClickListener(this);
        btnKeypad2.setOnClickListener(this);
        btnKeypad3.setOnClickListener(this);
        btnKeypad4.setOnClickListener(this);
        btnKeypad5.setOnClickListener(this);
        btnKeypad6.setOnClickListener(this);
        btnKeypad7.setOnClickListener(this);
        btnKeypad8.setOnClickListener(this);
        btnKeypad9.setOnClickListener(this);
        btnKeypadBack.setOnClickListener(this);

    }
    void gotoConfirmPasscode(String passcode){
        finish();
        Intent intent = new Intent(this, ConfirmPasscode.class);
        intent.putExtra("passcode", passcode);
        startActivity(intent);
    }



    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.keypad0:
                clickKeypad("0");
                break;
            case R.id.keypad1:
                clickKeypad("1");
                break;
            case R.id.keypad2:
                clickKeypad("2");
                break;
            case R.id.keypad3:
                clickKeypad("3");
                break;
            case R.id.keypad4:
                clickKeypad("4");
                break;
            case R.id.keypad5:
                clickKeypad("5");
                break;
            case R.id.keypad6:
                clickKeypad("6");
                break;
            case R.id.keypad7:
                clickKeypad("7");
                break;

            case R.id.keypad8:
                clickKeypad("8");
                break;
            case R.id.keypad9:
                clickKeypad("9");
                break;
            case R.id.keypad_back:
                clickKeypad("back");
                break;
            default:
                Log.d("ERRORKEYPAD", "keypad button not recgonized");
                break;
        }
    }

    public void clickKeypad(String number){
        if(number.equals("back")){
            if(passcode.size() > 0){
                removeDot(passcode);
                passcode.remove(passcode.size()-1);

            }

        }else{
            if(passcode.size()< 6){
                passcode.add(number);
                addDot(passcode);
            }
            if (passcode.size() >= 6){
                StringBuilder data = new StringBuilder();
                for(String element: passcode){
                    data.append(element);
                }
                String password = data.toString();
                util.toastMisc(this, "password= " +password);
                gotoConfirmPasscode(password);
            }
        }
    }

    private void removeDot(ArrayList<String> passcode) {
        int index = passcode.size();
        Integer idDot = ivList.get(index-1);
        ImageView iv = findViewById(idDot);
        iv.setVisibility(View.GONE);
    }

    private void addDot(ArrayList<String> passcode) {
        int index = passcode.size();
        Integer idDot = ivList.get(index-1);
        ImageView iv = findViewById(idDot);
        iv.setVisibility(View.VISIBLE);
    }



    public void back(){
        this.finish();
    }
}