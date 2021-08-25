package com.example.parafdigitalyokesen.view.ui.profile.child_profile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.parafdigitalyokesen.R;
import com.example.parafdigitalyokesen.adapter.DraftListAdapter;
import com.example.parafdigitalyokesen.adapter.MyContactListAdapter;
import com.example.parafdigitalyokesen.model.ContactModel;
import com.example.parafdigitalyokesen.model.SignModel;

import java.util.ArrayList;
import java.util.List;

public class MyContact extends AppCompatActivity {
    List<ContactModel> contact = new ArrayList<ContactModel>();
    RecyclerView rvContact;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_contact);
        initToolbar();
        initRecyclerView();
        initSpinner();
    }
    public void initToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarMyContact);
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
    private void initRecyclerView(){
        rvContact= findViewById(R.id.rvMyContact);
        contact = ContactModel.generateContact(3);
        MyContactListAdapter contactListAdapter = new MyContactListAdapter(contact);

        rvContact.setLayoutManager(new LinearLayoutManager(this));
        //rvToday.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvContact.setAdapter(contactListAdapter);
    }
    public void initSpinner(){
        String[] spinnerList = new String[]{
                "Interact Most", "A-Z", "Z-A"
        };

        Spinner spinnerContact = findViewById(R.id.spinnerContact);

        ArrayAdapter<String> adapterContact = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerList);
        adapterContact.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerContact.setAdapter(adapterContact);


    }
    public void back(){
        this.finish();
    }
}