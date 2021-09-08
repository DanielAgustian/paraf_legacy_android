package com.example.parafdigitalyokesen.view.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Notification;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.parafdigitalyokesen.R;
import com.example.parafdigitalyokesen.adapter.MyContactListAdapter;
import com.example.parafdigitalyokesen.adapter.NotifListAdapter;
import com.example.parafdigitalyokesen.model.ContactModel;
import com.example.parafdigitalyokesen.model.NotificationModel;

import java.util.List;

public class NotificationListActivity extends AppCompatActivity {
    RecyclerView rvNotif;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_list);
        initToolbar();
        initRecyclerView();
    }
    private void initRecyclerView(){
        rvNotif= findViewById(R.id.rvNotif);
        Log.d("NOTIFRECYCLERVIEW", "Begin");
        List<NotificationModel> model = NotificationModel.generateModel(3);
        NotifListAdapter adapter = new NotifListAdapter(model);

        rvNotif.setLayoutManager(new LinearLayoutManager(this));
        //rvToday.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvNotif.setAdapter(adapter);
    }
    public void initToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarNotif);
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
    public void back(){
        this.finish();
    }
}