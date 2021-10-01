package com.yokesen.parafdigitalyokesen.view.ui.draft;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.yokesen.parafdigitalyokesen.R;
import com.yokesen.parafdigitalyokesen.Repository.APIClient;
import com.yokesen.parafdigitalyokesen.Repository.APIInterface;
import com.yokesen.parafdigitalyokesen.Repository.PreferencesRepo;
import com.yokesen.parafdigitalyokesen.model.SimpleResponse;
import com.yokesen.parafdigitalyokesen.view.ui.profile.child_profile.security.PasscodeView;

import java.util.Calendar;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class DeclineReason extends AppCompatActivity {
    EditText etDeclineReason;
    int id;
    PreferencesRepo preferencesRepo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decline_reason);

         id = getIntent().getIntExtra("id", -1) ;
        preferencesRepo = new PreferencesRepo(this);
        initToolbar();
        initComponent();
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


    public void initComponent(){
        etDeclineReason = findViewById(R.id.etDeclineReason);
        Button btnSaveReason = findViewById(R.id.btnSaveReason);
        btnSaveReason.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getToResult();
            }
        });
    }

    public void initToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarDeclineReason);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationIcon(R.drawable.ic_back_gray);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Respond Signature", "Back Clicked");
                back();
            }
        });

    }
    public void back(){
        this.finish();
    }
    public void getToResult(){

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        PreferencesRepo preferencesRepo = new PreferencesRepo(this);

        String token = preferencesRepo.getToken();

        if(etDeclineReason.getText().toString().trim() != ""){
            Observable<SimpleResponse> getDetails = apiInterface.putRejectedReq(token, id, etDeclineReason.getText().toString());

            getDetails.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(this::onSuccessRejected, this::onFailedReject);
        }else{
            Toast.makeText(this, "Reason Text is Empty. Please fill the reason text to reject this request sign.",
                    Toast.LENGTH_LONG).show();
        }

    }

    private void onFailedReject(Throwable throwable) {
        Toast.makeText(this, "ERROR IN PUT REJECTED. "+ throwable.getMessage(),
                Toast.LENGTH_LONG).show();
    }

    private void onSuccessRejected(SimpleResponse simpleResponse) {
        if (simpleResponse!=null){
            Intent intent = new Intent(this, ResultAfterRespond.class);
            intent.putExtra("Result", 0);
            intent.putExtra("id", id);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }




}