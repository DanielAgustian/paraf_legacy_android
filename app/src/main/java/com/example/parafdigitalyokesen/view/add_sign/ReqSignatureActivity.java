package com.example.parafdigitalyokesen.view.add_sign;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;

import com.example.parafdigitalyokesen.R;
import com.example.parafdigitalyokesen.adapter.InviteSignersDialogAdapter;
import com.example.parafdigitalyokesen.model.InviteSignersModel;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class ReqSignatureActivity extends AppCompatActivity {
    Button btnContinue, btnCancel ;
    private Dialog customDialog, waitingDialog;
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
        Button btnCreate = findViewById(R.id.btnReqSign);
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customDialog.show();
            }
        });
    }
    void gotoResultPage(){
        this.finish();
        Intent intent = new Intent(this, ResultSignature.class);
        startActivity(intent);
    }
}