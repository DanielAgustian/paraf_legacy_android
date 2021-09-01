package com.example.parafdigitalyokesen.view.ui.collab;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.parafdigitalyokesen.R;
import com.example.parafdigitalyokesen.adapter.InviteSignersDialogAdapter;
import com.example.parafdigitalyokesen.model.InviteSignersModel;

import java.util.ArrayList;

public class FinalDocumentActivity extends AppCompatActivity implements View.OnClickListener {
    TextView tvAddSigners;
    LinearLayout llUploadData, llWaitingData, llDoneData;
    ImageView ivEmptyFile;
    Button btnFinalDoc;
    int progress = 0;
    private Uri fileUri;
    private final int EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE = 101;
    private String filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_document);
        initToolbar();
        initComponent();
    }



    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.tvAddSignersFinalDoc:
                break;
            case R.id.uploadDataFinal:
                filePicker();
                break;
            case R.id.ivEmptyFileFinal:
                emptyFile();
                break;
            case R.id.btnFinalDoc:
                back();
                break;
            default:
                break;
        }
    }

    //------------------------File Picker--------------------------//

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
                TextView tvWaitingData = findViewById(R.id.tvWaitingUploadFinal);
                tvWaitingData.setText(filePath);
                setProgressValue(filePath);
            }
        }
    }
    private void setProgressValue(String filePath) {
        ProgressBar simpleProgressBar = findViewById(R.id.simpleProgressBarFinal);
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
                TextView tvUploadData = findViewById(R.id.tvDoneUploadFinal);
                tvUploadData.setText(filePath);
            }
        };
        mCountDownTimer.start();


    }


    //------------------------------------------------------------


    private void initComponent() {
        tvAddSigners = findViewById(R.id.tvAddSignersFinalDoc);
        llUploadData = findViewById(R.id.uploadDataFinal);
        llWaitingData = findViewById(R.id.waitingUploadDataFinal);
        llDoneData = findViewById(R.id.doneUploadDataFinal);
        ivEmptyFile = findViewById(R.id.ivEmptyFileFinal);
        btnFinalDoc = findViewById(R.id.btnFinalDoc);

        tvAddSigners.setOnClickListener(this);
        llUploadData.setOnClickListener(this);
        ivEmptyFile.setOnClickListener(this);
        btnFinalDoc.setOnClickListener(this);

        llDoneData.setVisibility(View.GONE);
        llWaitingData.setVisibility(View.GONE);

    }
    public void initToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarFinalDoc);
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
        ListView lvSigners = findViewById(R.id.lvSignersFinalDocument);
        lvSigners.setNestedScrollingEnabled(false);
        ArrayList<InviteSignersModel> list = populateList(1);
        InviteSignersDialogAdapter invAdapter = new InviteSignersDialogAdapter(
                this, list
        );
        lvSigners.setAdapter(invAdapter);
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
    public void back(){
        this.finish();
    }


}