package com.example.parafdigitalyokesen.view.ui.collab;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.PictureDrawable;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.parafdigitalyokesen.R;
import com.example.parafdigitalyokesen.Repository.APIClient;
import com.example.parafdigitalyokesen.Repository.APIInterface;
import com.example.parafdigitalyokesen.Repository.PreferencesRepo;
import com.example.parafdigitalyokesen.model.GetSaveAllSign;
import com.example.parafdigitalyokesen.model.SaveSignModel;
import com.example.parafdigitalyokesen.util.Util;
import com.example.parafdigitalyokesen.adapter.InviteSignersDialogAdapter;
import com.example.parafdigitalyokesen.adapter.SignersAdapter;
import com.example.parafdigitalyokesen.model.GetMyReqDetailModel;
import com.example.parafdigitalyokesen.model.InviteSignersModel;
import com.example.parafdigitalyokesen.model.MyReqDetailModel;
import com.example.parafdigitalyokesen.model.SignersModel;
import com.example.parafdigitalyokesen.model.SimpleResponse;
import com.example.parafdigitalyokesen.util.UtilFile;
import com.example.parafdigitalyokesen.view.add_sign.ResultSignature;
import com.example.parafdigitalyokesen.view.add_sign.child_result.RecreateSignActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class CollabResultActivity extends AppCompatActivity implements View.OnClickListener{
    List<SignersModel> signers;
    LinearLayout llRemind, llShare, llSave, llDuplicate, llInviteSigners, llRegenerate, llRename, llDelete, llSentFinal;
    TextView tvPerson;
    private Dialog remindDialog;
    private Dialog deleteDialog, regenerateDialog, dialogRename;
    int where;
    String type;
    ArrayList<String> typeShare =  new ArrayList<>();
    String token;
    APIInterface apiInterface;
    PreferencesRepo preferencesRepo;
    MyReqDetailModel detailModel;
    PictureDrawable pd;
    Util util = new Util();
    String choosenDate = "";
    String choosenTime = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collab_result);
        where = getIntent().getIntExtra("type", 0);
        initData();
        initToolbar();
    }

    /*
     * where is identifier where are they from
     *  0=> DraftCompletedFragment
     * 1=> Draft Request Fragment
     * 2=> FragmentRequested in Collab
     * 3=> FragmentAccepted
     * 4=> FragmentRejected
     * */
    private void initData() {
        typeShare.add("png");
        typeShare.add("jpg");
        typeShare.add("pdf");
        apiInterface = APIClient.getClient().create(APIInterface.class);
        preferencesRepo = new PreferencesRepo(this);
        token = preferencesRepo.getToken();
        int id = getIntent().getIntExtra("id", -1) ;
        if(id != -1){

            Observable<GetMyReqDetailModel> getDetails = apiInterface.getCollabDetail(token, id);
            getDetails.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(this::onSuccess, this::onFailed);
        }
    }

    private void onSuccess(GetMyReqDetailModel getMyReqDetailModel) {
        if(getMyReqDetailModel != null){
            signers = getMyReqDetailModel.getData().getSigners();
            detailModel = getMyReqDetailModel.getData();
            initRecyclerView();
            initComponent();
            setData(getMyReqDetailModel.getData());
        }
    }



    private void onFailed(Throwable throwable) {
        util.toastError(this, "API Collab Detail", throwable);
        throwable.printStackTrace();
    }

    private void setData(MyReqDetailModel data) {

        pd = util.makeQRCOde(data.getQr_code());

        ImageView iv = findViewById(R.id.ivQRScan);
        iv.setImageDrawable(pd);

        TextView tvCreatedBy = findViewById(R.id.tvCreatedBy);
        tvCreatedBy.setText(data.getCreatedBy());

        TextView tvEmail = findViewById(R.id.tvEmailRS);
        tvEmail.setText(data.getEmailReq());

        TextView tvInitiated = findViewById(R.id.tvInitiated);
        tvInitiated.setText(data.getInitiatedOn());

        TextView tvTitle = findViewById(R.id.tvDocName);
        tvTitle.setText(data.getTitle());

        TextView tvDesc = findViewById(R.id.tvDescRes);
        tvDesc.setText(data.getDescription());

        TextView tvCategory = findViewById(R.id.tvCategoryRes);
        tvCategory.setText(data.getCategory());

        TextView tvTypes = findViewById(R.id.tvTypeRes);
        tvTypes.setText(data.getType());

        TextView tvlink = findViewById(R.id.tvLinkRes);
        tvlink.setText(data.getLink());

        TextView tvStatus = findViewById(R.id.tvStatusRes);
        tvStatus.setText(data.getStatus());

        TextView tvSize = findViewById(R.id.tvSizeRes);
        tvSize.setText(data.getSize());

        LinearLayout llRespond = findViewById(R.id.llRespond);

        TextView tvPersonRespond = findViewById(R.id.tvPersonRespond);
        tvPersonRespond.setText(data.getTotalSigners()+ "person left to sign");

        TextView tvRejectedReason = findViewById(R.id.tvRejectedReason);

        String status = data.getStatus().toLowerCase();
        if(status.contains("wait")){
            tvStatus.setTextColor(getResources().getColor(R.color.colorPending));
        } else if(status.contains("accept")){
            tvStatus.setTextColor(getResources().getColor(R.color.colorSuccess));
            llRespond.setBackground(getResources().getDrawable(R.color.colorBackgroundGreen));
            tvPersonRespond.setText("Everyone has already signed");
            tvPersonRespond.setTextColor(getResources().getColor(R.color.colorSuccess));
            llSentFinal.setVisibility(View.VISIBLE);
        }else if(status.contains("reject")){
            tvStatus.setTextColor(getResources().getColor(R.color.colorError));
            tvPersonRespond.setText("Sign Rejected");
            tvRejectedReason.setText(data.getReason());
        }

    }
    public void initRecyclerView(){
        RecyclerView rv = findViewById(R.id.recyclerCollabResult);
        rv.setNestedScrollingEnabled(false);

        Log.d("SignersModel", signers.size()+ "");
        SignersAdapter adapter = null;
        String status = detailModel.getStatus().toLowerCase();

        if(status.contains("wait")){
            util.toastMisc(this, status);
            adapter = new SignersAdapter(signers, 3, getSupportFragmentManager(), detailModel.getId());
            rv.setAdapter(adapter);
        } else if (status.contains("accept")){

            adapter = new SignersAdapter(signers, 4, getSupportFragmentManager(), detailModel.getId());
            rv.setAdapter(adapter);
        }
        else {
            adapter = new SignersAdapter(signers, 5, getSupportFragmentManager(), detailModel.getId());
            rv.setAdapter(adapter);
        }

//        if(where == 2){
//            adapter = new SignersAdapter(signers, 3, getSupportFragmentManager(), detailModel.getId());
//            rv.setAdapter(adapter);
//        } else if(where == 3){
//            adapter = new SignersAdapter(signers, 4, getSupportFragmentManager(), detailModel.getId());
//            rv.setAdapter(adapter);
//        } else {
//            adapter = new SignersAdapter(signers, 5, getSupportFragmentManager(), detailModel.getId());
//            rv.setAdapter(adapter);
//        }

        rv.setLayoutManager(new LinearLayoutManager(this));
    }
    public void initToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarCollabResult);
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
    public void initComponent(){


        llRemind = findViewById(R.id.llRemindCollab);
        llRemind.setOnClickListener(this);

        llSave = findViewById(R.id.llSaveCollabRes);
        llSave.setOnClickListener(this);

        llShare = findViewById(R.id.llShareCollabRes);
        llShare.setOnClickListener(this);

        llDuplicate = findViewById(R.id.llDuplicateCollabRes);
        llDuplicate.setOnClickListener(this);

        llInviteSigners = findViewById(R.id.llInviteCollabRes);
        llInviteSigners.setOnClickListener(this);

        llRegenerate = findViewById(R.id.llRegenCollabRes);
        llRegenerate.setOnClickListener(this);

        llRename = findViewById(R.id.llRenameCollabRes);
        llRename.setOnClickListener(this);

        llDelete = findViewById(R.id.llDeleteCollabRes);
        llDelete.setOnClickListener(this);

        llSentFinal = findViewById(R.id.llSentDocCollabRes);
        llSentFinal.setOnClickListener(this);

        LinearLayout llRejected = findViewById(R.id.llRejected);
        LinearLayout llApproved = findViewById(R.id.llApproved);
        tvPerson = findViewById(R.id.tvPersonRespond);
        if(where == 2){
            llSave.setVisibility(View.GONE);
            llRejected.setVisibility(View.GONE);
        } else if (where == 3){
            llRemind.setVisibility(View.GONE);
            llRejected.setVisibility(View.GONE);
        } else if (where == 4){
            //Rejected
            llApproved.setVisibility(View.GONE);
            tvPerson.setText("Sign Rejected");
            tvPerson.setTextColor(ContextCompat.getColor(this, R.color.colorError));
        }

    }

    public void back(){
        this.finish();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.llRemindCollab:
                remindDialog();
                break;
            case R.id.llSaveCollabRes:
                save();
                break;
            case R.id.llShareCollabRes:
                sharing();
                break;
            case R.id.llDuplicateCollabRes:
                duplicate();
                break;
            case R.id.llInviteCollabRes:
                inviteSigners();
                break;
            case R.id.llRegenCollabRes:
                regenerate();
                break;
            case R.id.llRenameCollabRes:
                rename();
                break;
            case R.id.llDeleteCollabRes:
                delete();
                break;
            case R.id.llSentDocCollabRes:
                sentDoc();
                break;
            default:
                break;
        }
    }

    private void sentDoc() {
        ArrayList<String> emailString = new ArrayList<>();
        for(int i = 0; i< signers.size(); i++){
            SignersModel sign  = signers.get(i);
            emailString.add(sign.getEmail());
        }

        Intent intent = new Intent(this, FinalDocumentActivity.class);
        intent.putStringArrayListExtra("list", emailString);
        intent.putExtra("id", detailModel.getId());
        startActivity(intent);
    }
    //-------------Process For Bottom Menu---------------------------------//


    private void inviteSigners(){
        Dialog dialog  = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_invite);
        dialog.setCancelable(true);
        dialog.show();
        ListView lvSigners = dialog.findViewById(R.id.lvDialogInvite);
        lvSigners.setNestedScrollingEnabled(false);
        ArrayList<InviteSignersModel> list = populateList(1);
        InviteSignersDialogAdapter invAdapter = new InviteSignersDialogAdapter(
                this, list, false
        );
        lvSigners.setAdapter(invAdapter);
        Button btnContinue = dialog.findViewById(R.id.btnContinueInvite);
        Button btnCancel = dialog.findViewById(R.id.btnCancelInvite);
        LinearLayout btnDueDate = dialog.findViewById(R.id.llDueDate);
        LinearLayout btnTime = dialog.findViewById(R.id.llTimePicker);
        LinearLayout llAddSigners = dialog.findViewById(R.id.llAddSigners);
        btnDueDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dateTimePicker(dialog);
            }
        });
        btnTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timepIcker(dialog);
            }
        });
        llAddSigners.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InviteSignersModel model = new InviteSignersModel();
                model.setEtName("");
                model.setEtEmail("");
                list.add(model);
                invAdapter.notifyDataSetChanged();
                if(list.size()<6){
                    int data = list.size() * 90;
                    data = util.dpToPx(data, CollabResultActivity.this);
                    lvSigners.getLayoutParams().height = data;
                    lvSigners.requestLayout();
                }

            }
        });
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<String> listSigners = util.getDataEmails(lvSigners, list);
//                View viewListItem = lvSigners.getChildAt(0);
//                EditText editText = viewListItem.findViewById(R.id.etDialogItemInvEmail);
//                String string = editText.getText().toString();
//                Log.d("Dialog Invite", string);
                doInviteSigners(listSigners, choosenDate, choosenTime);
                dismissDialog(dialog);
                dismissDialog(dialog);
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dismissDialog(dialog);
            }
        });
    }
    private void save(){
        Dialog dialog  = new Dialog(this);
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
                dismissDialog(dialog);

            }
        });
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismissDialog(dialog);
                doSave(type);
            }
        });
    }

    private void sharing(){
        doShare("blbla");
    }

    private void rename(){
        dialogRename  = new Dialog(this);
        dialogRename.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogRename.setContentView(R.layout.dialog_rename);
        dialogRename.setCancelable(true);
        dialogRename.show();
        Button btnContinue = dialogRename.findViewById(R.id.btnContinueRename);
        Button btnCancel = dialogRename.findViewById(R.id.btnCancelRename);
        EditText etRename = dialogRename.findViewById(R.id.etRenameDoc);

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String rename = etRename.getText().toString();
                doRename(rename);
            }

        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dismissDialog(dialogRename);
            }
        });
    }

//    private void save(){
//        Dialog dialog  = new Dialog(this);
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.setContentView(R.layout.dialog_save);
//        dialog.setCancelable(true);
//        dialog.show();
//        Button btnContinue = dialog.findViewById(R.id.btnContinueSave);
//        Button btnCancel = dialog.findViewById(R.id.btnCancelSave);
//        TextView textTitle =dialog.findViewById(R.id.tvTitleDialogSave);
//        textTitle.setText(detailModel.getTitle() +"will be saved to Download");
//        btnContinue.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                dismissDialog(dialog);
//                doSave();
//            }
//        });
//        btnCancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                dismissDialog(dialog);
//            }
//        });
//    }



    public void delete(){
        deleteDialog = new Dialog(this);
        deleteDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        deleteDialog.setContentView(R.layout.dialog_confirmation);
        deleteDialog.setCancelable(true);
        deleteDialog.show();
        Button btnContinue = deleteDialog.findViewById(R.id.btnContinue);
        Button btnCancel = deleteDialog.findViewById(R.id.btnCancel);
        TextView textTitle =deleteDialog.findViewById(R.id.tvTitleDialog);
        TextView textData = deleteDialog.findViewById(R.id.tvTitleData);
        textTitle.setText("Are you sure want to delete this signature?");
        btnContinue.setText("Delete");
        textData.setVisibility(View.GONE);
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doDelete();

            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dismissDialog(deleteDialog);
            }
        });
    }
    public void duplicate(){
        Dialog duplicateDialog = new Dialog(this);
        duplicateDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        duplicateDialog.setContentView(R.layout.dialog_confirmation);
        duplicateDialog.setCancelable(true);
        duplicateDialog.show();
        Button btnContinue = duplicateDialog.findViewById(R.id.btnContinue);
        Button btnCancel = duplicateDialog.findViewById(R.id.btnCancel);
        TextView textTitle = duplicateDialog.findViewById(R.id.tvTitleDialog);
        TextView textData = duplicateDialog.findViewById(R.id.tvTitleData);
        textTitle.setText("Are you sure want to duplicate this signature?");
        btnContinue.setText("Duplicate");
        textData.setVisibility(View.GONE);
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dismissDialog(duplicateDialog);
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dismissDialog(duplicateDialog);
            }
        });
    }
    public void regenerate(){
        regenerateDialog = new Dialog(this);
        regenerateDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        regenerateDialog.setContentView(R.layout.dialog_confirmation);
        regenerateDialog.setCancelable(true);
        regenerateDialog.show();
        Button btnContinue = regenerateDialog.findViewById(R.id.btnContinue);
        Button btnCancel = regenerateDialog.findViewById(R.id.btnCancel);
        TextView textTitle = regenerateDialog.findViewById(R.id.tvTitleDialog);
        TextView textData = regenerateDialog.findViewById(R.id.tvTitleData);
        textTitle.setText("Are you sure want to re-create this signature?");
        btnContinue.setText("Re-create");
        textData.setVisibility(View.GONE);
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dismissDialog(regenerateDialog);
                gotoRecreateForm();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dismissDialog(regenerateDialog);
            }
        });
    }

    public void  dismissDialog(Dialog dialog){
        dialog.dismiss();
    }

    //To remind signers
    public void remindDialog(){
        remindDialog = new Dialog(this);
        remindDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        remindDialog.setContentView(R.layout.dialog_confirmation);
        remindDialog.setCancelable(true);
        remindDialog.show();
        TextView textTitle =remindDialog.findViewById(R.id.tvTitleDialog);
        TextView textData = remindDialog.findViewById(R.id.tvTitleData);
        textTitle.setText("Are you sure want to remind your signers?");
        textData.setVisibility(View.GONE);
        Button btnContinue = remindDialog.findViewById(R.id.btnContinue);
        btnContinue.setText("Remind");
        Button btnCancel = remindDialog.findViewById(R.id.btnCancel);
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                remindDialog.dismiss();
                doRemind();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                remindDialog.dismiss();
            }
        });
    }

    //Dialog to remind signers if has been reminded before
    public void remindDialog2(){
        remindDialog = new Dialog(this);
        remindDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        remindDialog.setContentView(R.layout.dialog_confirmation);
        remindDialog.setCancelable(true);
        remindDialog.show();
        TextView textTitle =remindDialog.findViewById(R.id.tvTitleDialog);
        TextView textData = remindDialog.findViewById(R.id.tvTitleData);
        textTitle.setText("Are you sure want to remind your signers?");
        textData.setText("You’ve already remind your signers before, want to remind them again?");
        Button btnContinue = remindDialog.findViewById(R.id.btnContinue);
        btnContinue.setText("Remind");
        Button btnCancel = remindDialog.findViewById(R.id.btnCancel);
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                remindDialog.dismiss();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                remindDialog.dismiss();
            }
        });
    }



    //--------------------------Dialog Logic ------------------------------//
     private void doRename(String rename) {
        Observable<SimpleResponse> putRename = apiInterface.putRenameSign(token, detailModel.getId(), rename);
        putRename.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(this::onSuccessRename, this::onFailedRename);
    }

    private void onFailedRename(Throwable throwable) {
        Toast.makeText(this, "ERROR IN FETCHING API RENAME. " + throwable.getMessage(),
                Toast.LENGTH_LONG).show();
    }

    private void onSuccessRename(SimpleResponse simpleResponse) {
        if(simpleResponse != null){
            dismissDialog(dialogRename);
            initData();
        }
    }

    private void doDelete() {
        Observable<SimpleResponse> putRename = apiInterface.putDeleteSign(token, detailModel.getId());
        putRename.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(this::onSuccessDelete, this::onFailedDelete);
    }

    private void onSuccessDelete(SimpleResponse simpleResponse) {
        if(simpleResponse != null){
            dismissDialog(deleteDialog);
            initData();
        }
    }

    private void onFailedDelete(Throwable throwable) {
        Toast.makeText(this, "ERROR IN FETCHING API RENAME. " + throwable.getMessage(),
                Toast.LENGTH_LONG).show();
    }

    private void gotoRecreateForm() {
        Intent intent = new Intent(this, RecreateCollabActivity.class);
        intent.putExtra("type", where);
        intent.putExtra("id", detailModel.getId());

        finish();
        startActivity(intent);
    }

    private void doSave(String type) {

        Observable<GetSaveAllSign> Remind= apiInterface.getSAveAllSign(token, detailModel.getId());
        Remind.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(this::onSuccessSaveAll, this::onFailedSaveAll);
    }

    private void onSuccessSaveAll(GetSaveAllSign getSaveAllSign) {
        if(getSaveAllSign != null){
            List<SaveSignModel> saveModel = getSaveAllSign.getData();

            savingSign(saveModel);
        }
    }

    private void savingSign(List<SaveSignModel> saveModel) {
        UtilFile utilFile = new UtilFile(this);
        for (int i=0; i<saveModel.size(); i++){
            PictureDrawable pdList = util.makeQRCOde(saveModel.get(i).getQr_code());
            ImageView iv = findViewById(R.id.emptyDrawable);
            iv.setImageDrawable(pdList);
            if(type.equals("") ){
            }else{
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                    utilFile.downloadFileAPI29(util.makeBitmap(pdList), type, typeShare, detailModel.getTitle()+detailModel.getInitiatedOn());
                }else{
                    utilFile.downloadFile(util.makeBitmap(pdList), type, typeShare, detailModel.getTitle()+util.milisNow());
                }
            }
        }
        if(type.equals("") ){
        }else{
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                utilFile.downloadFileAPI29(util.makeBitmap(pd), type, typeShare, detailModel.getTitle()+detailModel.getInitiatedOn());
            }else{
                utilFile.downloadFile(util.makeBitmap(pd), type, typeShare, detailModel.getTitle()+detailModel.getInitiatedOn());
            }
        }

    }

    private void onFailedSaveAll(Throwable throwable) {
        util.toastError(this, "API SAVE ALL SIGN", throwable);
        throwable.printStackTrace();
    }


    private void doShare(String type){
        util.shareLink(this, "paraf.yokesen.com");
    }

    private void doRemind() {
        Observable<SimpleResponse> Remind= apiInterface.GetRemindReq(token, detailModel.getId());
        Remind.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(this::onSuccessRemind, this::onFailedRemind);
    }

    private void onSuccessRemind(SimpleResponse simpleResponse) {
        if(simpleResponse!=null){
            util.toastMisc(this, "You have Successfully Remind Signers");
        }
    }

    private void onFailedRemind(Throwable throwable) {
        util.toastError(this , "API REMIND", throwable);
    }
    void doInviteSigners(ArrayList<String> emails, String date, String time){
        Observable<SimpleResponse> putInviteSign = apiInterface.putInviteSign(token, detailModel.getId(), emails, date, time );
        putInviteSign.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(this::onSuccessInvite, this::onFailedInvite);
    }

    private void onSuccessInvite(SimpleResponse simpleResponse) {
        if(simpleResponse != null){
            back();
        }
    }

    private void onFailedInvite(Throwable throwable) {
        util.toastError(this, "API INVITE ERROR", throwable);
    }
    //--------------------Date Picker
    void dateTimePicker(Dialog dialog){
        Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {


                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        c.set(Calendar.YEAR, year);
                        c.set(Calendar.MONTH, monthOfYear);
                        c.set(Calendar.DATE, dayOfMonth);
                        choosenDate = util.CalendarToDateString(c);
                        TextView tvDate = dialog.findViewById(R.id.tvDueDate);
                        tvDate.setText(choosenDate);
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();


    }

    //---------------------------------Time Picker------------------------
    void timepIcker(Dialog dialog)
    {
        Calendar c = Calendar.getInstance();
        int mHour = c.get(Calendar.HOUR_OF_DAY);
        int mMinute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {

                        c.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        c.set(Calendar.MINUTE, minute);
                        choosenTime = util.CalendarToTimeString(c);
                        TextView tvTime = dialog.findViewById(R.id.tvAddTime);
                        tvTime.setText(choosenTime);
                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();

    }


    //---------------------------Misc Logic---------------------
    public ArrayList<InviteSignersModel> populateList(int position){
        ArrayList<InviteSignersModel> list = new ArrayList<>();
        for(int i = 0; i < position; i++){
            InviteSignersModel invModel = new InviteSignersModel();
            invModel.setEtName("");
            invModel.setEtEmail("");
            list.add(invModel);
        }
        return list;
    }


}
