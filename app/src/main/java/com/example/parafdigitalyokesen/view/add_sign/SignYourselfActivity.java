package com.example.parafdigitalyokesen.view.add_sign;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.parafdigitalyokesen.R;
import com.example.parafdigitalyokesen.viewModel.SignYourselfViewModel;


import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import pub.devrel.easypermissions.EasyPermissions;

public class SignYourselfActivity extends AppCompatActivity  {
    private Uri fileUri;
    private int EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE = 101;
    private String filePath;
    Thread thread;
    Button btnContinue, btnCancel ;
    private Dialog customDialog, waitingDialog;
    LinearLayout llWaitingData, llDoneData, llUploadData;
    private boolean result;
    SignYourselfViewModel VM;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sign_yourself);
        getPermission();
        initComponent();
        initSpinner();
        initDialog();
        initDialogWaiting();

    }

    private void getPermission() {
        if (!EasyPermissions.hasPermissions(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

            EasyPermissions.requestPermissions(
                    this,
                    getString(R.string.rationale_storage),
                    EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE,
                    Manifest.permission.CAMERA);
        }
    }

    public void initSpinner(){
        String[] spinnerCategoryList = new String[]{
                "Message", "Report", "Accounting"
        };
        String[] spinnerDocTypeList = new String[]{
                ".doc", ".pdf"
        };
        Spinner spinnerCategory = findViewById(R.id.spinnerCategoryNewSign);
        Spinner spinnerDocType = findViewById(R.id.spinnerDocTypeyNewSign);
        ArrayAdapter<String> adapterCategory = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerCategoryList);
        ArrayAdapter<String> adapterDocType = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerDocTypeList);
        adapterCategory.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterDocType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapterCategory);
        spinnerCategory.setPrompt("Select Category");
        spinnerDocType.setAdapter(adapterDocType);
        spinnerDocType.setPrompt("Select Document Type");
    }

    public void initComponent(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarSignYourself);
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
        Button btnCreate = findViewById(R.id.btnSignYourself);
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customDialog.show();
            }
        });
        llUploadData = findViewById(R.id.uploadData);
        llUploadData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filePicker();
            }
        });


        llDoneData = findViewById(R.id.doneUploadData);
        llDoneData.setVisibility(View.GONE);
        llWaitingData = findViewById(R.id.waitingUploadData);
        llWaitingData.setVisibility(View.GONE);
        ImageView ivEmptyFile = findViewById(R.id.ivEmptyFile);
        ivEmptyFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                emptyFile();
            }
        });
    }

    private void emptyFile() {
        filePath = "";
        llDoneData.setVisibility(View.GONE);
        llUploadData.setVisibility(View.VISIBLE);
    }


    //Method to get Trigger File Picker
    private void filePicker() {
//        Intent intent = new Intent(this, FilePickerActivity.class);
//        intent.putExtra(FilePickerActivity.CONFIGS, new Configurations.Builder()
//                .setCheckPermission(true)
//                .setShowFiles(true)
//                .setShowVideos(false)
//                .setShowImages(false)
//                .setShowAudios(false)
//                .setMaxSelection(1)
//                .setSuffixes("pdf", "docx", "doc")
//                .setSkipZeroSizeFiles(true)
//                .build());
//        startActivityForResult(intent, 100);

        Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);

        chooseFile.setType("*/*");

        chooseFile = Intent.createChooser(chooseFile, "Choose a file");

        startActivityForResult(chooseFile, EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE);
    }

    //For get data from filepicker
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && data !=null){
//            List<MediaFile> mediaFiles = data.<MediaFile>getParcelableArrayListExtra(FilePickerActivity.MEDIA_FILES);
            if(requestCode == EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE){
                fileUri = data.getData();

                filePath = fileUri.getPath();

                Log.d("PATH", filePath);
                llUploadData.setVisibility(View.GONE);

                llWaitingData.setVisibility(View.VISIBLE);
                TextView tvWaitingData = findViewById(R.id.tvWaitingUpload);
                tvWaitingData.setText(filePath);
                setProgressValue(filePath);
            }
        }
    }

    int progress = 0;
    //For progress bar
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
                TextView tvUploadData = findViewById(R.id.tvDoneUpload);
                tvUploadData.setText(filePath);
            }
        };
        mCountDownTimer.start();


    }


    public void initDialogWaiting(){
        waitingDialog = new Dialog(this);
        waitingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        waitingDialog.setContentView(R.layout.dialog_waiting);
        waitingDialog.setCancelable(false);
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

    public void back(){
        this.finish();
    }

    void gotoResultPage(){
        this.finish();
        Intent intent = new Intent(this, ResultSignature.class);
        startActivity(intent);
    }

}