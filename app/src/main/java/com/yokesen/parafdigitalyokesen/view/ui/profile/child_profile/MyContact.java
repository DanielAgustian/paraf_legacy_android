package com.yokesen.parafdigitalyokesen.view.ui.profile.child_profile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.yokesen.parafdigitalyokesen.R;
import com.yokesen.parafdigitalyokesen.Repository.APIClient;
import com.yokesen.parafdigitalyokesen.Repository.APIInterface;
import com.yokesen.parafdigitalyokesen.Repository.PreferencesRepo;
import com.yokesen.parafdigitalyokesen.util.Util;
import com.yokesen.parafdigitalyokesen.adapter.MyContactListAdapter;
import com.yokesen.parafdigitalyokesen.model.ContactModel;
import com.yokesen.parafdigitalyokesen.model.GetConnectModel;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MyContact extends AppCompatActivity {
    List<ContactModel> contact = new ArrayList<ContactModel>();
    RecyclerView rvContact;

    Util util = new Util();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_contact);
        initData();
        initToolbar();
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
    private void initRecyclerView(List<ContactModel> contact){
        rvContact= findViewById(R.id.rvMyContact);

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

    private void initData() {
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        PreferencesRepo preferencesRepo = new PreferencesRepo(this);

        String token = preferencesRepo.getToken();
        Observable<GetConnectModel> callHome = apiInterface.getMyContact(token);
        callHome.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(this::onSuccess, this::onFailed);
    }

    private void onFailed(Throwable throwable) {
        util.toastError(this, "API GET CONTACT", throwable);
    }

    private void onSuccess(GetConnectModel getConnectModel) {
        if(getConnectModel !=null){
            List<ContactModel> contact = getConnectModel.getData();
            initRecyclerView(contact);
        }
    }

    public void back(){
        this.finish();
    }
}