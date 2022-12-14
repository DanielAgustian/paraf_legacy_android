package com.yokesen.parafdigitalyokesen.view.ui.draft;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.yokesen.parafdigitalyokesen.R;
import com.yokesen.parafdigitalyokesen.Repository.APIClient;
import com.yokesen.parafdigitalyokesen.Repository.APIInterface;
import com.yokesen.parafdigitalyokesen.Repository.PreferencesRepo;
import com.yokesen.parafdigitalyokesen.adapter.SignersAdapter;
import com.yokesen.parafdigitalyokesen.adapter.SignersMyRequestAdapter;
import com.yokesen.parafdigitalyokesen.constant.refresh;
import com.yokesen.parafdigitalyokesen.model.GetMyReqDetailModel;
import com.yokesen.parafdigitalyokesen.model.MyReqDetailModel;
import com.yokesen.parafdigitalyokesen.model.SignersModel;
import com.yokesen.parafdigitalyokesen.model.SimpleResponse;
import com.yokesen.parafdigitalyokesen.util.UtilWidget;
import com.yokesen.parafdigitalyokesen.view.ui.profile.child_profile.security.PasscodeView;
import com.yokesen.parafdigitalyokesen.viewModel.SignCollabState;

import java.util.Calendar;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


public class RespondSignature extends AppCompatActivity implements View.OnClickListener {
    int id;
    List<SignersModel> signers;
    String token;
    APIInterface apiInterface;
    PreferencesRepo preferencesRepo;
    MyReqDetailModel detailModel;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_respond_signature);

        apiInterface = APIClient.getClient().create(APIInterface.class);
        preferencesRepo = new PreferencesRepo(this);
        token = preferencesRepo.getToken();

        InitData();
        //initRecyclerView();
        initToolbar();
        iniComponent();
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
        long intervetion = 30 * 60 * 1000;
        long milisNow = Calendar.getInstance().getTimeInMillis();
        long milisSelisih = milisNow - milisStart;

        if(isActive == 1 && passcode!= null && !passcode.equals("")){

            if(intervetion < milisSelisih && milisSelisih!= milisNow){
                Intent intent = new Intent(this, PasscodeView.class);
                startActivity(intent);
            }
        }

        int isBiometricActive = preferencesRepo.getBiometric();
        if(isBiometricActive == 1){
            if(intervetion < milisSelisih && milisSelisih!= milisNow){
                UtilWidget uw = new UtilWidget(this);
                uw.biometricPrompt();
            }
        }

    }
    void InitData(){
        id = getIntent().getIntExtra("id", -1) ;

        if(id!=-1){
            Observable<GetMyReqDetailModel> getDetails = apiInterface.getMyRequestDetail(token, id);
            getDetails.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(this::onSuccess, this::onFailed);
        }
    }

    private void onFailed(Throwable throwable) {
        Toast.makeText(this, "ERROR IN FETCHING API GET DETAILS. "+ throwable.getMessage(),
                Toast.LENGTH_LONG).show();
    }

    private void onSuccess(GetMyReqDetailModel model) {
        if(model != null){
            detailModel = model.getData();
            setData(detailModel);
        }
    }

    private void setData(MyReqDetailModel model) {

        TextView tvPersonRespond = findViewById(R.id.tvPersonRespond);
        tvPersonRespond.setText(model.getTotalSigners()+" Person is left to sign");

        TextView tvReqBy = findViewById(R.id.tvReqBYRespond);
        tvReqBy.setText(model.getRequestBy());

        TextView tvEmail = findViewById(R.id.tvEmailRespond);
        tvEmail.setText(model.getEmailReq());

        TextView tvDocName = findViewById(R.id.tvDocNameRespond);
        tvDocName.setText(model.getTitle());

        TextView tvDesc = findViewById(R.id.tvDescRespond);
        tvDesc.setText(model.getDescription());

        TextView tvCategory = findViewById(R.id.tvCategoryRespond);
        tvCategory.setText(model.getCategory());

        TextView tvTypes = findViewById(R.id.tvTypeRespond);
        tvTypes.setText(model.getType());

        TextView tvlink = findViewById(R.id.tvLinkRespond);
        tvlink.setText(model.getLink());

        TextView tvStatus = findViewById(R.id.tvStatusRespond);
        tvStatus.setText(model.getStatus());

        TextView tvSize = findViewById(R.id.tvSizeRespond);
        tvSize.setText(model.getSize());

        initRecyclerView(model.getSigners());
    }

    public void initRecyclerView(List<SignersModel> signers){
        RecyclerView rv = findViewById(R.id.rvRespondDraft);
        rv.setNestedScrollingEnabled(false);
        SignersMyRequestAdapter adapter =  new SignersMyRequestAdapter(signers,  getSupportFragmentManager(), detailModel.getId());
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(this));
    }
    public void initToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarRespond);
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
    public void iniComponent(){
        Button btnSignNow = findViewById(R.id.btnSignNow);
        Button btnDecline = findViewById(R.id.btnDecline);
        btnSignNow.setOnClickListener(this);
        btnDecline.setOnClickListener(this);
    }

    public void back(){
        this.finish();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnSignNow:
                gotoResult("1");
                break;
            case R.id.btnDecline:
                gotoReson();
                break;
            default:
                Log.d("RespondSignature", "Error BUtton");
                break;
        }
    }
    public void gotoResult(String result){
        Observable<SimpleResponse> getDetails = apiInterface.putAcceptReq(token, id);
        getDetails.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(this::onSuccessAccept, this::onFailedAccept);

    }

    private void onSuccessAccept(SimpleResponse simpleResponse) {
        if(simpleResponse != null){
            refreshMyReq();
            finish();
            Intent intent = new Intent(this, ResultAfterRespond.class);
            intent.putExtra("id" ,id);
            intent.putExtra("Result", 1);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);

        }
    }

    private void onFailedAccept(Throwable throwable) {
        Toast.makeText(this, "ERROR IN PUT Requested Accept. "+ throwable.getMessage(),
                Toast.LENGTH_LONG).show();
    }

    public void gotoReson(){
        Intent intent = new Intent(this, DeclineReason.class);

        intent.putExtra("id" , id);
        finish();
        startActivity(intent);
    }
    private void refreshMyReq(){
        SignCollabState.getSubject().onNext(refresh.MY_REQ);
    }

}
