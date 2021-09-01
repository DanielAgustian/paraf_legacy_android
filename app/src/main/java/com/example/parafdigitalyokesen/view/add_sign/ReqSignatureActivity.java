package com.example.parafdigitalyokesen.view.add_sign;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.parafdigitalyokesen.R;
import com.example.parafdigitalyokesen.adapter.InviteSignersDialogAdapter;
import com.example.parafdigitalyokesen.model.InviteSignersModel;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class ReqSignatureActivity extends AppCompatActivity implements View.OnClickListener{
    Button btnContinue, btnCancel, btnDueDate, btnTime;
    LinearLayout llUploadData, llWaitingData, llDoneData;
    private Dialog customDialog, waitingDialog;
    private Uri fileUri;
    private final int EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE = 101;
    private String filePath;
    int progress = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_req_signature);
        initToolbar();
        initRecyclerView();
        initSpinner();
        initDialog();
        initDialogWaiting();
        initComponent();
    }
    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.btnDueDate){
            dateTimePicker();
        } else if(view.getId() ==R.id.btnTime){
            timepIcker();
        } else if (view.getId() == R.id.uploadDataReq){
            filePicker();
        } else if (view.getId()== R.id.ivEmptyFileReq){
            emptyFile();
        }
    }
    //------------------------File Picker Method---------------------------------
    private void filePicker() {
        Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);

        chooseFile.setType("*/*");

        chooseFile = Intent.createChooser(chooseFile, "Choose a file");

        startActivityForResult(chooseFile, EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE);
    }
    private void emptyFile() {
        filePath = "";
        llDoneData.setVisibility(View.GONE);
        llUploadData.setVisibility(View.VISIBLE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && data !=null){
            if(requestCode == EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE){
                fileUri = data.getData();

                filePath = fileUri.getPath();

                Log.d("PATH", filePath);
                llUploadData.setVisibility(View.GONE);
                llWaitingData.setVisibility(View.VISIBLE);
                TextView tvWaitingData = findViewById(R.id.tvWaitingUploadReq);
                tvWaitingData.setText(filePath);
                setProgressValue(filePath);
            }
        }
    }
    private void setProgressValue(String filePath) {
        ProgressBar simpleProgressBar = findViewById(R.id.simpleProgressBar);
        // set the progress


        simpleProgressBar.setProgress(progress);

        // thread is used to change the progress value
        CountDownTimer mCountDownTimer=new CountDownTimer(2000,200) {

            @Override
            public void onTick(long millisUntilFinished) {
                progress = progress+10;
                simpleProgressBar.setProgress(progress);

            }
            @Override
            public void onFinish() {
                //Do what you want

                simpleProgressBar.setProgress(100);
                llWaitingData.setVisibility(View.GONE);
                llDoneData.setVisibility(View.VISIBLE);
                TextView tvUploadData = findViewById(R.id.tvDoneUploadReq);
                tvUploadData.setText(filePath);
            }
        };
        mCountDownTimer.start();


    }
    //----------------------------------------------------------------


    //---------------------------Date picker----------------------
    Calendar dateTimePicker(){
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
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();

        return c;
    }

    //---------------------------------Time Picker------------------------
    Calendar timepIcker()
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

                        c.set(Calendar.HOUR, hourOfDay);
                        c.set(Calendar.MINUTE, minute);
                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();
        return c;
    }

    public void initToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarReqSign);
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
        ListView lvSigners = findViewById(R.id.lvInviteSigners);
        lvSigners.setNestedScrollingEnabled(false);
        ArrayList<InviteSignersModel> list = populateList(1);
        InviteSignersDialogAdapter invAdapter = new InviteSignersDialogAdapter(
                this, list
        );
        lvSigners.setAdapter(invAdapter);
    }
    public void back(){
        this.finish();
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

        public void initSpinner(){
        String[] spinnerCategoryList = new String[]{
                "Message", "Report", "Accounting"
        };
        String[] spinnerDocTypeList = new String[]{
                ".doc", ".xls"
        };
        Spinner spinnerCategory = findViewById(R.id.spinnerCategoryReqSign);
        Spinner spinnerDocType = findViewById(R.id.spinnerDocTypeReqSign);
        ArrayAdapter<String> adapterCategory = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerCategoryList);
        ArrayAdapter<String> adapterDocType = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerDocTypeList);
        adapterCategory.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterDocType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapterCategory);
        spinnerCategory.setPrompt("Select Category");
        spinnerDocType.setAdapter(adapterDocType);
        spinnerDocType.setPrompt("Select Document Type");
    }
    public void initDialog(){
        customDialog = new Dialog(this);
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        customDialog.setContentView(R.layout.dialog_confirmation);
        customDialog.setCancelable(true);
        btnContinue = customDialog.findViewById(R.id.btnContinue);
        btnCancel = customDialog.findViewById(R.id.btnCancel);
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                customDialog.dismiss();
                waitingDialog.show();
                Timer obRunTime = new Timer();
                obRunTime.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        waitingDialog.dismiss();
                        gotoResultPage();
                    }
                },2000);
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                customDialog.dismiss();
            }
        });
    }
    public void initDialogWaiting(){
        waitingDialog = new Dialog(this);
        waitingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        waitingDialog.setContentView(R.layout.dialog_waiting);
        waitingDialog.setCancelable(false);
    }

    public void initComponent(){
        llUploadData = findViewById(R.id.uploadDataReq);
        btnDueDate = findViewById(R.id.btnDueDate);
        btnTime = findViewById(R.id.btnTime);
        Button btnCreate = findViewById(R.id.btnReqSign);
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customDialog.show();
            }
        });
        btnDueDate.setOnClickListener(this);
        btnTime.setOnClickListener(this);
        llUploadData.setOnClickListener(this);
        llDoneData = findViewById(R.id.doneUploadDataReq);
        llDoneData.setVisibility(View.GONE);
        llWaitingData = findViewById(R.id.waitingUploadDataReq);
        llWaitingData.setVisibility(View.GONE);
        ImageView ivEmptyFile = findViewById(R.id.ivEmptyFileReq);
        ivEmptyFile.setOnClickListener(this);
    }
    void gotoResultPage(){
        this.finish();
        Intent intent = new Intent(this, ResultSignature.class);
        startActivity(intent);
    }



}