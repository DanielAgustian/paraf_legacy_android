package com.example.parafdigitalyokesen.view.ui.draft;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.opengl.Visibility;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.parafdigitalyokesen.R;
import com.example.parafdigitalyokesen.Repository.APIClient;
import com.example.parafdigitalyokesen.Repository.APIInterface;
import com.example.parafdigitalyokesen.Repository.PreferencesRepo;
import com.example.parafdigitalyokesen.Util;
import com.example.parafdigitalyokesen.adapter.SignersAdapter;
import com.example.parafdigitalyokesen.model.GetMyReqDetailModel;
import com.example.parafdigitalyokesen.model.GetSignDetailModel;
import com.example.parafdigitalyokesen.model.MyReqDetailModel;
import com.example.parafdigitalyokesen.model.SignersModel;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ResultAfterRespond extends AppCompatActivity {

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
            initRecyclerView(getMyReqDetailModel.getData().getSigners());
            initComponent(getMyReqDetailModel.getData());
        }
    }

    public void initRecyclerView(List<SignersModel> signers){
        RecyclerView rv = findViewById(R.id.rvRespondResultDraft);
        rv.setNestedScrollingEnabled(false);

        SignersAdapter adapter =  new SignersAdapter(signers, 2, getSupportFragmentManager());
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
        Button btnRequestDoc = findViewById(R.id.btnRequestFinalDoc);



        if(result== 1){
            if(layoutAccepted.getVisibility() == View.GONE){
                layoutAccepted.setVisibility(View.VISIBLE);
                ImageView ivQRScan = findViewById(R.id.ivQRScanResultRespond);
                ivQRScan.setImageDrawable(util.makeQRCOde(model.getQr_code()));
            }
        } else if (result == 0){
            if(layoutRejected.getVisibility()== View.GONE){
                layoutRejected.setVisibility(View.VISIBLE);
                btnRequestDoc.setVisibility(View.GONE);
                TextView tvRejectedReason = findViewById(R.id.tvRejectedReason);
            }

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
}