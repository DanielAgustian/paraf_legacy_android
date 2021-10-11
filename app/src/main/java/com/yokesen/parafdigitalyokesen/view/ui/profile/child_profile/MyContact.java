package com.yokesen.parafdigitalyokesen.view.ui.profile.child_profile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.yokesen.parafdigitalyokesen.R;
import com.yokesen.parafdigitalyokesen.Repository.APIClient;
import com.yokesen.parafdigitalyokesen.Repository.APIInterface;
import com.yokesen.parafdigitalyokesen.Repository.PreferencesRepo;
import com.yokesen.parafdigitalyokesen.model.SignModel;
import com.yokesen.parafdigitalyokesen.util.Util;
import com.yokesen.parafdigitalyokesen.adapter.MyContactListAdapter;
import com.yokesen.parafdigitalyokesen.model.ContactModel;
import com.yokesen.parafdigitalyokesen.model.GetConnectModel;
import com.yokesen.parafdigitalyokesen.util.UtilWidget;
import com.yokesen.parafdigitalyokesen.view.ui.profile.child_profile.security.PasscodeView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MyContact extends AppCompatActivity {
    //List<ContactModel> contact = new ArrayList<ContactModel>();
    RecyclerView rvContact;
    int position;
    PreferencesRepo preferencesRepo;
    Util util = new Util();
    List<ContactModel> contactList = new ArrayList<>();
    LinearLayout llWaiting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_contact);
        initComponentLoading();
        initData();
        initToolbar();
        initSpinner();
        initComponent();
    }

    private void initComponentLoading() {
        llWaiting = findViewById(R.id.llWaiting);
        rvContact = findViewById(R.id.rvMyContact);
    }


    long milisStart = 0;
    @Override
    protected void onPause() {
        super.onPause();
        milisStart = Calendar.getInstance().getTimeInMillis();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        String passcode = preferencesRepo.getPasscode();
        int isActive = preferencesRepo.getAllowPasscode();
        long intervetion = 30 * 60 * 1000;
        long milisNow = Calendar.getInstance().getTimeInMillis();
        long milisSelisih = milisNow - milisStart;

        if(isActive == 1 && passcode!= null && !passcode.equals("")){

            if(intervetion < milisSelisih && milisSelisih!= milisNow){
                Intent intent = new Intent(this, PasscodeView.class);
                startActivity(intent);
            }
        }

        int isBiometricActive = preferencesRepo.getBiometric();
        if(isBiometricActive == 1){
            if(intervetion < milisSelisih && milisSelisih!= milisNow){
                UtilWidget uw = new UtilWidget(this);
                uw.biometricPrompt();
            }
        }

    }

    private void initComponent() {
        EditText etSearch = findViewById(R.id.etSearchContact);
        etSearch.addTextChangedListener(tw);
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
        endLoading();
    }
    public void initSpinner(){
        String[] spinnerList = new String[]{
                "Interact Most", "A-Z", "Z-A"
        };
        Spinner spinnerContact = findViewById(R.id.spinnerContact);
        ArrayAdapter<String> adapterContact = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerList);
        adapterContact.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerContact.setAdapter(adapterContact);
        spinnerContact.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i == 0){
                    initData();
                }else if (i==1){
                    initDataSort("asc");
                } else if (i==2){
                    initDataSort("desc");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    private void initData() {
        beginLoading();
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        preferencesRepo = new PreferencesRepo(this);
        String token = preferencesRepo.getToken();
        Observable<GetConnectModel> callHome = apiInterface.getMyContact(token);
        callHome.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(this::onSuccess, this::onFailed);
    }
    private void initDataSort(String sort) {
        beginLoading();
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        preferencesRepo = new PreferencesRepo(this);
        String token = preferencesRepo.getToken();
        Observable<GetConnectModel> callHome = apiInterface.getMyContactSort(token, sort);
        callHome.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(this::onSuccess, this::onFailed);
    }
    private void onFailed(Throwable throwable) {
        endLoading();
        util.toastError(this, "API GET CONTACT", throwable);
    }

    private void onSuccess(GetConnectModel getConnectModel) {
        if(getConnectModel !=null){
            List<ContactModel> contact = getConnectModel.getData();
            if(contact.size()>0){
                contactList = contact;
                initRecyclerView(contact);
            }else{
                endLoading();
                emptyList();
            }
        }
    }

    private void emptyList() {


        LinearLayout llEmptyList = findViewById(R.id.llEmptyContact);
        rvContact.setVisibility(View.GONE);
        llEmptyList.setVisibility(View.VISIBLE);

//      TextView tvEmptyNotice = root.findViewById(R.id.tvEmptyNotice);
//      tvEmptyNotice.setText("My Signature is Empty");
    }

    TextWatcher tw = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if(charSequence.length()>0){
                List<ContactModel> filteredData = search(contactList, charSequence.toString());
                initRecyclerView(filteredData);
            } else{
                initRecyclerView(contactList);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    private List<ContactModel> search(List<ContactModel> sign, String query){
        List<ContactModel> arraySearched = new ArrayList<>();
        for(int i=0; i< sign.size(); i++){
            ContactModel element = sign.get(i);
            if(element.getName().toLowerCase().contains(query.toLowerCase())){
                arraySearched.add(element);
            }
        }
        return arraySearched;
    }

    public void back(){
        this.finish();
    }

    private void beginLoading(){
        llWaiting.setVisibility(View.VISIBLE);
        rvContact.setVisibility(View.GONE);
    }
    private void endLoading(){
        llWaiting.setVisibility(View.GONE);
        rvContact.setVisibility(View.VISIBLE);
    }
}