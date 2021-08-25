package com.example.parafdigitalyokesen.view.ui.draft;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.parafdigitalyokesen.R;
import com.example.parafdigitalyokesen.adapter.SignersAdapter;
import com.example.parafdigitalyokesen.model.SignersModel;

import java.util.List;


public class RespondSignature extends AppCompatActivity implements View.OnClickListener {

    List<SignersModel> signers;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_respond_signature);
        initRecyclerView();
        initToolbar();
        iniComponent();
    }
    public void initRecyclerView(){
        RecyclerView rv = findViewById(R.id.rvRespondDraft);
        rv.setNestedScrollingEnabled(false);
        signers = SignersModel.generateModel(3);
        SignersAdapter adapter =  new SignersAdapter(signers, 1, getSupportFragmentManager());
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(this));
    }
    public void initToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarRespond);
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
    public void iniComponent(){
        Button btnSignNow = findViewById(R.id.btnSignNow);
        Button btnDecline = findViewById(R.id.btnDecline);
        btnSignNow.setOnClickListener(this);
        btnDecline.setOnClickListener(this);
    }

    public void back(){
        this.finish();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnSignNow:
                gotoResult("1");
                break;
            case R.id.btnDecline:
                gotoReson();
                break;
            default:
                Log.d("RespondSignature", "Error BUtton");
                break;
        }
    }
    public void gotoResult(String result){
        Intent intent = new Intent(this, ResultAfterRespond.class);
        intent.putExtra("Result", result);
        startActivity(intent);
    }
    public void gotoReson(){
        Intent intent = new Intent(this, DeclineReason.class);

        startActivity(intent);
    }

}
