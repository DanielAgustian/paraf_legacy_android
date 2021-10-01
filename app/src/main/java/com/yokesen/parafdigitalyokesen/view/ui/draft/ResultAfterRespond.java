package com.yokesen.parafdigitalyokesen.view.ui.draft;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yokesen.parafdigitalyokesen.R;
import com.yokesen.parafdigitalyokesen.Repository.APIClient;
import com.yokesen.parafdigitalyokesen.Repository.APIInterface;
import com.yokesen.parafdigitalyokesen.Repository.PreferencesRepo;
import com.yokesen.parafdigitalyokesen.model.GetDownloadModel;
import com.yokesen.parafdigitalyokesen.util.Util;
import com.yokesen.parafdigitalyokesen.adapter.SignersAdapter;
import com.yokesen.parafdigitalyokesen.model.GetMyReqDetailModel;
import com.yokesen.parafdigitalyokesen.model.MyReqDetailModel;
import com.yokesen.parafdigitalyokesen.model.SignersModel;
import com.yokesen.parafdigitalyokesen.model.SimpleResponse;
import com.yokesen.parafdigitalyokesen.util.UtilFile;
import com.yokesen.parafdigitalyokesen.view.ui.profile.child_profile.security.PasscodeView;

import java.util.Calendar;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ResultAfterRespond extends AppCompatActivity implements View.OnClickListener {

    int result, id;
    String token;
    APIInterface apiInterface;
    PreferencesRepo preferencesRepo;

    Util util = new Util();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_after_respond);
        apiInterface = APIClient.getClient().create(APIInterface.class);
        preferencesRepo = new PreferencesRepo(this);
        token = preferencesRepo.getToken();
        //initComponent();
        //initRecyclerView();
        initToolbar();
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


    private void initData(){
        result = getIntent().getIntExtra("Result", -1) ;
        id = getIntent().getIntExtra("id", id);
        Observable<GetMyReqDetailModel> getDetails = apiInterface.getMyRequestDetail(token, id);
        getDetails.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(this::onSuccess, this::onFailed);


    }

    private void onFailed(Throwable throwable) {
        Toast.makeText(this, "ERROR IN FETCHING API Get Detail Signature. " + throwable.getMessage(),
                Toast.LENGTH_LONG).show();
    }

    private void onSuccess(GetMyReqDetailModel getMyReqDetailModel) {
        if(getMyReqDetailModel != null){
            initRecyclerView(getMyReqDetailModel.getData().getSigners(), getMyReqDetailModel.getData().getId());
            initComponent(getMyReqDetailModel.getData());
        }
    }

    public void initRecyclerView(List<SignersModel> signers, int id){
        RecyclerView rv = findViewById(R.id.rvRespondResultDraft);
        rv.setNestedScrollingEnabled(false);

        SignersAdapter adapter =  new SignersAdapter(signers, 2, getSupportFragmentManager(), id);
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(this));
    }
    public void initToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarRespondResult);
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

    public void initComponent(MyReqDetailModel model){
        LinearLayout layoutAccepted = findViewById(R.id.layoutAccepted);
        LinearLayout layoutRejected = findViewById(R.id.layoutRejected);
        LinearLayout llRespond = findViewById(R.id.llRespond);

//        Button btnRequestDoc = findViewById(R.id.btnRequestFinalDoc);
//        btnRequestDoc.setOnClickListener(this);

        Button btnDownloadDoc = findViewById(R.id.btnDownloadFinalDoc);
        btnDownloadDoc.setOnClickListener(this);

        TextView tvPersonRespond = findViewById(R.id.tvPersonRespond);
        tvPersonRespond.setText(model.getTotalSigners()+" Person is left to sign");
        if(model.getStatus().toLowerCase().equals("accepted")){
            llRespond.setBackground(getResources().getDrawable(R.color.colorBackgroundGreen));
            tvPersonRespond.setText("Everyone has signed this docs");
            tvPersonRespond.setTextColor(getResources().getColor(R.color.colorSuccess));

            if(layoutAccepted.getVisibility() == View.GONE){
                layoutAccepted.setVisibility(View.VISIBLE);
                layoutRejected.setVisibility(View.GONE);
                ImageView ivQRScan = findViewById(R.id.ivQRScanResultRespond);
                ivQRScan.setImageDrawable(util.makeQRCOde(model.getQr_code()));
            }
        } else if (model.getStatus().toLowerCase().equals("rejected")){
            TextView tvRejectedReason = findViewById(R.id.tvRejectedReason);
            tvRejectedReason.setText(model.getReason());
            util.toastMisc(this, model.getReason());
            if(layoutRejected.getVisibility()== View.GONE){
                layoutAccepted.setVisibility(View.GONE);
                layoutRejected.setVisibility(View.VISIBLE);
                //btnRequestDoc.setVisibility(View.GONE);

            }
        } else{
            layoutAccepted.setVisibility(View.GONE);
            layoutRejected.setVisibility(View.GONE);
        }

//        if(model.isCanRequestDocument()){
//            btnRequestDoc.setVisibility(View.VISIBLE);
//        }else{
//            btnRequestDoc.setVisibility(View.GONE);
//        }
        if(model.isCanDownload()){
            btnDownloadDoc.setVisibility(View.VISIBLE);
        }else{
            btnDownloadDoc.setVisibility(View.GONE);
        }


        TextView tvReqBy = findViewById(R.id.tvReqByRespondResult);
        tvReqBy.setText(model.getRequestBy());


        TextView tvEmail = findViewById(R.id.tvEmailRespondResult);
        tvEmail.setText(model.getEmailReq());

        TextView tvInitiated = findViewById(R.id.tvInitiatedRespondResult);
        tvInitiated.setText(model.getInitiatedOn());

        TextView tvTitle = findViewById(R.id.tvDocNameRespondResult);
        tvTitle.setText(model.getTitle());

        TextView tvDesc = findViewById(R.id.tvDescRespondResult);
        tvDesc.setText(model.getDescription());

        TextView tvCategory = findViewById(R.id.tvCategoryRespondResult);
        tvCategory.setText(model.getCategory());

        TextView tvTypes = findViewById(R.id.tvTypeRespondResult);
        tvTypes.setText(model.getType());

        TextView tvlink = findViewById(R.id.tvLinkRespondResult);
        tvlink.setText(model.getLink());

        TextView tvStatus = findViewById(R.id.tvStatusRespondResult);
        tvStatus.setText(model.getStatus());

        TextView tvSize = findViewById(R.id.tvSizeRespondResult);
        tvSize.setText(model.getSize());
    }

    public void back(){
        this.finish();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
//            case R.id.btnRequestFinalDoc:
//                sentRequest();
//                break;
            case R.id.btnDownloadFinalDoc:
                downloadFinalDoc();
                break;
        }
    }

    private void downloadFinalDoc() {
        Observable<GetDownloadModel> GetFinalDocument = apiInterface.DownloadFinalDocument(token, id);
        GetFinalDocument.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(this::onSuccessGetLink, this::onFailedGetLink);
    }

    private void onFailedGetLink(Throwable throwable) {
        util.toastError(this, "API FINAL DOC", throwable);
    }

    private void onSuccessGetLink(GetDownloadModel getDownloadModel) {
        if (getDownloadModel!=null){
           String link = getDownloadModel.getData().getLink();
           UtilFile utilFile = new UtilFile(this);
           utilFile.downloadFileURL(link);
        }
    }

    private void sentRequest() {
        Observable<SimpleResponse> senRequest = apiInterface.RequestDocument(token, id);
        senRequest.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(this::onSuccessRequest, this::onFailedrequestDoc);
    }

    private void onFailedrequestDoc(Throwable throwable) {
        util.toastError(this, "API Request DOc", throwable);
    }

    private void onSuccessRequest(SimpleResponse simpleResponse) {
        initDialog();
    }
    public void initDialog(){
        Log.d("RequestAPI", "Success");
        Dialog customDialog = new Dialog(this);
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        customDialog.setContentView(R.layout.dialog_approval);
        Button btnContinue = customDialog.findViewById(R.id.btnCloseDialog);
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customDialog.dismiss();
            }
        });

    }
}