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
import android.widget.LinearLayout;

import com.example.parafdigitalyokesen.R;
import com.example.parafdigitalyokesen.adapter.SignersAdapter;
import com.example.parafdigitalyokesen.model.SignersModel;

import java.util.List;

public class ResultAfterRespond extends AppCompatActivity {
    List<SignersModel> signers;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_after_respond);
        initComponent();
        initRecyclerView();
        initToolbar();


    }
    public void initRecyclerView(){
        RecyclerView rv = findViewById(R.id.rvRespondResultDraft);
        rv.setNestedScrollingEnabled(false);
        signers = SignersModel.generateModel(3);
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

    public void initComponent(){
        LinearLayout layoutAccepted = findViewById(R.id.layoutAccepted);
        LinearLayout layoutRejected = findViewById(R.id.layoutRejected);
        Button btnRequestDoc = findViewById(R.id.btnRequestFinalDoc);
        Bundle extras = getIntent().getExtras();
        String result = extras.getString("Result");

        Log.d("Sent My Data", result);
        if(result.equals("1")){
            if(layoutAccepted.getVisibility() == View.GONE){
                layoutAccepted.setVisibility(View.VISIBLE);

            }
        } else if (result.equals("0")){
            if(layoutRejected.getVisibility()== View.GONE){
                layoutRejected.setVisibility(View.VISIBLE);
                btnRequestDoc.setVisibility(View.GONE);
            }

        }
    }

    public void back(){
        this.finish();
    }
}