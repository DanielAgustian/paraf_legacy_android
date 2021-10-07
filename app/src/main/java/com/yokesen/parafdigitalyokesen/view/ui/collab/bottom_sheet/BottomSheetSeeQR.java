package com.yokesen.parafdigitalyokesen.view.ui.collab.bottom_sheet;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.PictureDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.yokesen.parafdigitalyokesen.R;
import com.yokesen.parafdigitalyokesen.Repository.APIClient;
import com.yokesen.parafdigitalyokesen.Repository.APIInterface;
import com.yokesen.parafdigitalyokesen.Repository.PreferencesRepo;
import com.yokesen.parafdigitalyokesen.util.Util;
import com.yokesen.parafdigitalyokesen.model.GetCollabViewModel;
import com.yokesen.parafdigitalyokesen.model.SignersModel;
import com.yokesen.parafdigitalyokesen.util.UtilFile;
import com.yokesen.parafdigitalyokesen.view.ui.collab.FinalDocumentActivity;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class BottomSheetSeeQR extends BottomSheetDialogFragment implements View.OnClickListener {
    String type;
    SignersModel sign;
    int id;
    public BottomSheetSeeQR(String type, SignersModel sign, int id) {
        this.type = type;
        this.sign = sign;
        this.id = id;
    }
    LinearLayout llSave, llShare, llRequestFile;
    TextView tvCreatedBY, tvInitiatedOn, tvEmail;
    ImageView ivQR;
    View v;
    Util util = new Util();
    Bitmap pd;

    ArrayList<String> typeShare =  new ArrayList<>();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.bottom_sheet_see_qr,
                container, false);
        initData();
        //initComponent();

        return v;
    }

    private void initComponent(String qr_code) {
        llSave= v.findViewById(R.id.llSaveCollabDialog);
        llShare = v.findViewById(R.id.llShareCollabDialog);
        llRequestFile = v.findViewById(R.id.llRequestFileDialog);
        tvCreatedBY = v.findViewById(R.id.tvCreatedBy);
        tvInitiatedOn = v.findViewById(R.id.tvInitiated);
        tvEmail = v.findViewById(R.id.tvEmailRS);
        ivQR = v.findViewById(R.id.ivQRScanBotNav);


        tvCreatedBY.setText(sign.getName());
        tvInitiatedOn.setText(sign.getInfo());
        tvEmail.setText(sign.getEmail());

        if(qr_code != null && !qr_code.equals("")){
            pd = util.makeQRCOde(qr_code);
            ivQR.setImageBitmap(pd);
        }

        String status = sign.getStatus().trim().toLowerCase();
        if (status.contains("wait")){
            tvInitiatedOn.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPending));
        } else if (status.contains("accept")){
            tvInitiatedOn.setTextColor(ContextCompat.getColor(getContext(), R.color.colorSuccess));
        } else if (status.contains("reject")){
            tvInitiatedOn.setTextColor(ContextCompat.getColor(getContext(), R.color.colorError));
        }


        if(type.equals("view")){
            llSave.setVisibility(View.GONE);
            llShare.setVisibility(View.GONE);
            llRequestFile.setVisibility(View.GONE);
        }else{
            llSave.setOnClickListener(this);
            llShare.setOnClickListener(this);
            llRequestFile.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.llRequestFileDialog:
                Intent intent = new Intent(getActivity(), FinalDocumentActivity.class);
                intent.putExtra("id", id);
                startActivity(intent);
                break;
            case R.id.llSaveCollabDialog:
                save();
                break;
            case R.id.llShareCollabDialog:
                doShare();
                break;
            default:break;
        }
    }

    private void initData(){
        typeShare.add("png");
        typeShare.add("jpg");
        typeShare.add("pdf");

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        PreferencesRepo preferencesRepo = new PreferencesRepo(getContext());
        String token = preferencesRepo.getToken();

        Observable<GetCollabViewModel> GetSearchData = apiInterface.getCollabDetailView(token, sign.getId(), id);
        GetSearchData.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                this::onSuccessGetView, this::onFailedGetView);

    }

    private void onSuccessGetView(GetCollabViewModel getCollabViewModel) {
        if(getCollabViewModel != null){
            initComponent(getCollabViewModel.getData().getQrCode());
        }

    }

    private void onFailedGetView(Throwable throwable) {
        initComponent("");
        //util.toastError(getContext(), "API COLLAB VIEW", throwable);
    }

    private void save(){
        Dialog dialog  = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_doctype);
        dialog.setCancelable(true);
        dialog.show();
        Button btnContinue = dialog.findViewById(R.id.btnContinueType);
        Button btnCancel = dialog.findViewById(R.id.btnCancelType);
        RadioButton rbPng = dialog.findViewById(R.id.radio_png);
        RadioButton rbJpg = dialog.findViewById(R.id.radio_jpeg);
        RadioButton rbPdf = dialog.findViewById(R.id.radio_pdf);
        rbJpg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                type = typeShare.get(1);
            }
        });
        rbPng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                type = typeShare.get(0);
            }
        });
        rbPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                type = typeShare.get(2);
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();

            }
        });
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                doSave(type);
            }
        });
    }
    private void doSave(String type){
        UtilFile utilFile = new UtilFile(getContext());
        if(type.equals("") ){
        }else{
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                utilFile.downloadFileAPI29(pd, type, typeShare, sign.getName()+util.milisNow());
            }else{
                utilFile.downloadFile(pd, type, typeShare, sign.getName()+util.milisNow());
            }
        }
    }
    private void doShare(){
        util.shareLink(getContext(), "paraf.yokesen.com");
    }
}
