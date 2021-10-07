package com.yokesen.parafdigitalyokesen.view.add_sign;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.PictureDrawable;
import android.os.Bundle;
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

import com.yokesen.parafdigitalyokesen.R;
import com.yokesen.parafdigitalyokesen.Repository.APIClient;
import com.yokesen.parafdigitalyokesen.Repository.APIInterface;
import com.yokesen.parafdigitalyokesen.Repository.PreferencesRepo;
import com.yokesen.parafdigitalyokesen.constant.Variables;
import com.yokesen.parafdigitalyokesen.constant.refresh;
import com.yokesen.parafdigitalyokesen.util.DynamicLinkUtil;
import com.yokesen.parafdigitalyokesen.util.Util;
import com.yokesen.parafdigitalyokesen.adapter.InviteSignersDialogAdapter;
import com.yokesen.parafdigitalyokesen.adapter.SignersAdapter;
import com.yokesen.parafdigitalyokesen.model.GetSignDetailModel;
import com.yokesen.parafdigitalyokesen.model.InviteSignersModel;
import com.yokesen.parafdigitalyokesen.model.SignatureDetailModel;
import com.yokesen.parafdigitalyokesen.model.SignersModel;
import com.yokesen.parafdigitalyokesen.model.SimpleResponse;
import com.yokesen.parafdigitalyokesen.util.UtilFile;
import com.yokesen.parafdigitalyokesen.util.UtilWidget;
import com.yokesen.parafdigitalyokesen.view.add_sign.child_result.RecreateSignActivity;
import com.yokesen.parafdigitalyokesen.view.ui.profile.child_profile.security.PasscodeView;
import com.yokesen.parafdigitalyokesen.viewModel.SignCollabState;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ResultSignature extends AppCompatActivity implements View.OnClickListener {
    List<SignersModel> signers;
    private Dialog deleteDialog, regenerateDialog, dialogRename;
    LinearLayout llDelete, llRegen, llSave, llDuplicate, llInvite, llShare, llRename;
    LinearLayout llPersonRespond;

    String type = "";
    ArrayList<String> typeShare =  new ArrayList<>();
    String token;
    APIInterface apiInterface;
    PreferencesRepo preferencesRepo;
    SignatureDetailModel detailModel;
    //SignatureDetailModel detailModel;
    Util util = new Util();
    Bitmap pd;
    String choosenDate = "";
    String choosenTime = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_signature);
        apiInterface = APIClient.getClient().create(APIInterface.class);
        preferencesRepo = new PreferencesRepo(this);
        token = preferencesRepo.getToken();
        initData();
        initRecyclerView();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.llDelete:
                delete();
                break;
            case R.id.llRegen:
                regenerate();
                break;
            case R.id.llSave:
                save();
                break;
            case R.id.llRename:
                rename();
                break;
            case R.id.llShare:
                sharing();
                break;
            case R.id.llInvite:
                inviteSigners();
                break;
            case R.id.llDuplicate:
                duplicate();
                break;
        }
    }

    private void initData() {
        typeShare.add("png");
        typeShare.add("jpg");
        typeShare.add("pdf");
        int id = getIntent().getIntExtra("id", -1) ;
        if(id != -1){

            Observable<GetSignDetailModel> getDetails = apiInterface.getMySignDetail(token, id);
            getDetails.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(this::onSuccess, this::onFailed);
        }
    }

    private void onSuccess(GetSignDetailModel model) {
        if(model != null){
            detailModel = model.getHome();
            setData();
        }
    }


    private void setData() {
        pd = util.makeQRCOde(detailModel.getQr_code());

        ImageView iv = findViewById(R.id.ivQRScan);
        iv.setImageBitmap(pd);

        TextView tvCreatedBy = findViewById(R.id.tvCreatedBy);
        tvCreatedBy.setText(detailModel.getCreatedBy());

        TextView tvEmail = findViewById(R.id.tvEmailRS);
        tvEmail.setText(detailModel.getEmail());

        TextView tvInitiated = findViewById(R.id.tvInitiated);
        tvInitiated.setText(detailModel.getInitiatedOn());

        TextView tvTitle = findViewById(R.id.tvDocName);
        tvTitle.setText(detailModel.getTitle());

        TextView tvDesc = findViewById(R.id.tvDescRes);
        tvDesc.setText(detailModel.getDescription());

        TextView tvCategory = findViewById(R.id.tvCategoryRes);
        tvCategory.setText(detailModel.getCategory());

        TextView tvTypes = findViewById(R.id.tvTypeRes);
        tvTypes.setText(detailModel.getType());

        TextView tvlink = findViewById(R.id.tvLinkRes);
        tvlink.setText(detailModel.getLink());

        TextView tvStatus = findViewById(R.id.tvStatusRes);
        tvStatus.setText(detailModel.getStatus());

        TextView tvSize = findViewById(R.id.tvSizeRes);
        tvSize.setText(detailModel.getSize());

        TextView tvStatusSign = findViewById(R.id.tvStatusSign);
        tvStatusSign.setText("Sign Number: " + detailModel.getSignNumber());
    }

    private void onFailed(Throwable throwable) {
        Toast.makeText(this, "ERROR IN FETCHING API GET DETAILS. "+ throwable.getMessage(),
                Toast.LENGTH_LONG).show();
    }

    //-------------------------- OpenDialog -----------------------
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

                    data = util.dpToPx(data, ResultSignature.this);
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
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dismissDialog(dialog);
            }
        });
    }

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

    private void sharing(){
        DynamicLinkUtil dlUtil = new DynamicLinkUtil(this);
        Variables var = new Variables();
        util.shareLink(this, dlUtil.dynamicLinkParaf(var.typeSign[0], detailModel.getId()));
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


    //------------------Init-----------------------------
    public void initComponent(){
        llDelete = findViewById(R.id.llDelete);
        llRegen = findViewById(R.id.llRegen);
        llSave  =findViewById(R.id.llSave);
        llDuplicate = findViewById(R.id.llDuplicate);
        llShare = findViewById(R.id.llShare);
        llRename = findViewById(R.id.llRename);
        llInvite = findViewById(R.id.llInvite);


        llDelete.setOnClickListener(this); //Done
        llRegen.setOnClickListener(this); //Done
        llSave.setOnClickListener(this); //Done
        llDuplicate.setOnClickListener(this);
        llShare.setOnClickListener(this);
        llRename.setOnClickListener(this);//Done
        llInvite.setOnClickListener(this);
    }

    public void initToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarResultSig);
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

    public void initRecyclerView(){
        RecyclerView rv = findViewById(R.id.recyclerViewSigners);
        String from = getIntent().getStringExtra("where") != null? getIntent().getStringExtra("where"):"";

        //if it is from my Sign, Recycler View will be invisible.

        if(from.equals("mysign")){
            rv.setVisibility(View.GONE);
            llPersonRespond = findViewById(R.id.llPersonRespond);
            llPersonRespond.setVisibility(View.GONE);
        }else{
            //rv.setNestedScrollingEnabled(false);
            signers = SignersModel.generateModel(3);
            SignersAdapter adapter =  new SignersAdapter(signers, 0, getSupportFragmentManager(), detailModel.getId());
            rv.setAdapter(adapter);
            rv.setLayoutManager(new LinearLayoutManager(this));
        }

    }
    public void back(){
        this.finish();
    }


    // -----------------------LOGIC FOR MODAL-------------------------//
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
            refreshMySign();
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
            refreshMySign();
            back();

        }
    }

    private void onFailedDelete(Throwable throwable) {
        Toast.makeText(this, "ERROR IN FETCHING API RENAME. " + throwable.getMessage(),
                Toast.LENGTH_LONG).show();
    }

    private void gotoRecreateForm() {
        Intent intent = new Intent(this, RecreateSignActivity.class);

        intent.putExtra("id", detailModel.getId());
        //intent.putExtra("model", detailModel);
        startActivity(intent);
    }




    private void doSave(String type){
        UtilFile utilFile = new UtilFile(this);
        if(type.equals("") ){
        }else{
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                utilFile.downloadFileAPI29(pd, type, typeShare, detailModel.getTitle()+detailModel.getInitiatedOn());
            }else{
                utilFile.downloadFile(pd, type, typeShare, detailModel.getTitle()+detailModel.getInitiatedOn());
            }
        }
    }



    void doInviteSigners(ArrayList<String> emails, String date, String time){
        Observable<SimpleResponse> putInviteSign = apiInterface.putInviteSign(token, detailModel.getId(), emails, date, time );
        putInviteSign.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(this::onSuccessInvite, this::onFailedInvite);
    }

    private void onSuccessInvite(SimpleResponse simpleResponse) {
        if(simpleResponse != null){
            util.toastMisc(this, "Successfully Add Signers!");
            refreshMySign();
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
        datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis());
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


    private void refreshMySign(){
        SignCollabState.getSubject().onNext(refresh.MY_SIGN);
    }

}