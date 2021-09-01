package com.example.parafdigitalyokesen.view.ui.collab;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.parafdigitalyokesen.R;
import com.example.parafdigitalyokesen.adapter.SignersAdapter;
import com.example.parafdigitalyokesen.model.SignModel;
import com.example.parafdigitalyokesen.model.SignersModel;

import java.util.List;

public class CollabResultActivity extends AppCompatActivity implements View.OnClickListener{
    List<SignersModel> signers;
    LinearLayout llRemind;
    private Dialog remindDialog;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collab_result);
        initRecyclerView();
        initToolbar();
        initComponent();
    }

    /*
     * where is identifier where are they from
     *  0=> DraftCompletedFragment
     * 1=> Draft Request Fragment
     * 2=> FragmentRequested in Collab
     * 3=> FragmentAccepted
     * 4=> FragmentRejected
     * */

    public void initRecyclerView(){
        RecyclerView rv = findViewById(R.id.recyclerCollabResult);
        rv.setNestedScrollingEnabled(false);
        signers = SignersModel.generateModel(3);
        int where = getIntent().getIntExtra("type", 0);
        SignersAdapter adapter = null;
        if(where == 2){
            adapter = new SignersAdapter(signers, 3, getSupportFragmentManager());
            rv.setAdapter(adapter);
        } else if(where == 3){
            adapter = new SignersAdapter(signers, 4, getSupportFragmentManager());
            rv.setAdapter(adapter);
        } else {
            adapter = new SignersAdapter(signers, 5, getSupportFragmentManager());
            rv.setAdapter(adapter);
        }

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
            default:
                break;
        }
    }
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
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                remindDialog.dismiss();
            }
        });
    }
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
}
