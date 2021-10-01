package com.yokesen.parafdigitalyokesen.view.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yokesen.parafdigitalyokesen.R;
import com.yokesen.parafdigitalyokesen.Repository.APIClient;
import com.yokesen.parafdigitalyokesen.Repository.APIInterface;
import com.yokesen.parafdigitalyokesen.Repository.PreferencesRepo;
import com.yokesen.parafdigitalyokesen.model.SearchModel;
import com.yokesen.parafdigitalyokesen.util.Util;
import com.yokesen.parafdigitalyokesen.adapter.SearchListAdapter;
import com.yokesen.parafdigitalyokesen.model.GetSignatureModel;
import com.yokesen.parafdigitalyokesen.model.SignModel;
import com.yokesen.parafdigitalyokesen.view.ui.profile.child_profile.security.PasscodeView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class SearchActivity extends AppCompatActivity implements View.OnClickListener{
    List<String> strings = new ArrayList<String>();
    EditText etSearch;
    boolean[] suggestionBol = new boolean[5];
    List<TextView> textViewSuggestion = new ArrayList<>();
    List<LinearLayout> llSuggestion = new ArrayList<>();
    //List<SignModel> allModel;
    String token;
    APIInterface apiInterface;
    PreferencesRepo preferencesRepo;
    Util util = new Util();
    SearchModel modelSearch = new SearchModel();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        strings = Arrays.asList("MySignature", "My Request", "Waiting Signature", "Accepted Signature", "Rejected Signature");
        apiInterface = APIClient.getClient().create(APIInterface.class);
        preferencesRepo = new PreferencesRepo(this);
        token = preferencesRepo.getToken();
        loadValue();
        loadData(-1);
        initSuggestion();
        initComponent();
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

        if(isActive == 1 && passcode!= null && passcode.equals("")){
            long intervetion = 30 * 60 * 1000;
            long milisNow = Calendar.getInstance().getTimeInMillis();
            long milisSelisih = milisNow - milisStart;
            if(intervetion < milisSelisih && milisSelisih!= milisNow){
                Intent intent = new Intent(this, PasscodeView.class);
                startActivity(intent);
            }
        }

        //biometricPrompt();
    }
    private void loadValue() {
        Arrays.fill(suggestionBol, false);
        textViewSuggestion.add(findViewById(R.id.flexLL1));
        textViewSuggestion.add(findViewById(R.id.flexLL2));
        textViewSuggestion.add(findViewById(R.id.flexLL3));
        textViewSuggestion.add(findViewById(R.id.flexLL4));
        textViewSuggestion.add(findViewById(R.id.flexLL5));

        llSuggestion.add(findViewById(R.id.child_flex1));
        llSuggestion.add(findViewById(R.id.child_flex2));
        llSuggestion.add(findViewById(R.id.child_flex3));
        llSuggestion.add(findViewById(R.id.child_flex4));
        llSuggestion.add(findViewById(R.id.child_flex5));
    }

    private void initComponent() {
        etSearch  = findViewById(R.id.acTVSearch);
        etSearch.addTextChangedListener(tw);
        LinearLayout child_flex1 = findViewById(R.id.child_flex1);
        LinearLayout child_flex2 = findViewById(R.id.child_flex2);
        LinearLayout child_flex3 = findViewById(R.id.child_flex3);
        LinearLayout child_flex4 = findViewById(R.id.child_flex4);
        LinearLayout child_flex5 = findViewById(R.id.child_flex5);

        child_flex1.setOnClickListener(this);
        child_flex2.setOnClickListener(this);
        child_flex3.setOnClickListener(this);
        child_flex4.setOnClickListener(this);
        child_flex5.setOnClickListener(this);

    }

    private void initSuggestion() {
        TextView tvflex1 = findViewById(R.id.flexLL1);
        tvflex1.setText(strings.get(0));

        TextView tvflex2 = findViewById(R.id.flexLL2);
        tvflex2.setText(strings.get(1));

        TextView tvflex3 = findViewById(R.id.flexLL3);
        tvflex3.setText(strings.get(2));

        TextView tvflex4 = findViewById(R.id.flexLL4);
        tvflex4.setText(strings.get(3));

        TextView tvflex5 = findViewById(R.id.flexLL5);
        tvflex5.setText(strings.get(4));
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.child_flex1:
                //etSearch.setText(strings.get(0));
                clickButton(0);
                break;
            case R.id.child_flex2:
                //etSearch.setText(strings.get(1));
                clickButton(1);
                break;
            case R.id.child_flex3:
                //etSearch.setText(strings.get(2));
                clickButton(2);
                break;
            case R.id.child_flex4:
                //etSearch.setText(strings.get(3));
                clickButton(3);
                break;
            case R.id.child_flex5:
                //etSearch.setText(strings.get(4));
                clickButton(4);
                break;
            default:
                break;
        }
    }


    TextWatcher tw = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if(charSequence.length()>0){
                List<SignModel> filteredData = search(modelSearch.getSign(), charSequence.toString());
                setRecyclerView(filteredData, modelSearch.getType());
            } else{
                setRecyclerView(modelSearch.getSign(), modelSearch.getType());
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    private void loadData(int type) {
        if(type == -1){
            Observable<GetSignatureModel> GetSearchData = apiInterface.GetSearchData(token);
            GetSearchData.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                    this::onSuccessSearchData, this::onFailedSearchData);
        }else if(type == 0){
            Observable<GetSignatureModel> GetSearchData = apiInterface.getMySignList(token);
            GetSearchData.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                    this::onSuccessSearchDataMySign, this::onFailedSearchData);
        } else if(type == 1){
            Observable<GetSignatureModel> GetSearchData = apiInterface.getMyReqList(token);
            GetSearchData.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                    this::onSuccessSearchDataMyReq, this::onFailedSearchData);
        } else if(type == 2){
            Observable<GetSignatureModel> GetSearchData = apiInterface.GetSearchWaitingData(token);
            GetSearchData.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                    this::onSuccessSearchDataWaiting, this::onFailedSearchData);
        }
        else if(type == 3){
            Observable<GetSignatureModel> GetSearchData = apiInterface.GetSearchAcceptedData(token);
            GetSearchData.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                    this::onSuccessSearchDataAccepted, this::onFailedSearchData);
        }else if(type == 4){
            Observable<GetSignatureModel> GetSearchData = apiInterface.GetSearchRejectedData(token);
            GetSearchData.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                    this::onSuccessSearchDataRejected, this::onFailedSearchData);
        }

    }

    private void onSuccessSearchDataRejected(GetSignatureModel getSignatureModel) {
        if(getSignatureModel != null){
            modelSearch.setType(4);
            modelSearch.setSign(getSignatureModel.getData());

            setRecyclerView(modelSearch.getSign(), modelSearch.getType());
        }
    }

    private void onSuccessSearchDataAccepted(GetSignatureModel getSignatureModel) {
        if(getSignatureModel != null){
            modelSearch.setType(3);
            modelSearch.setSign(getSignatureModel.getData());

            setRecyclerView(modelSearch.getSign(), modelSearch.getType());
        }
    }

    private void onSuccessSearchDataWaiting(GetSignatureModel getSignatureModel) {
        if(getSignatureModel != null){
            modelSearch.setType(2);
            modelSearch.setSign(getSignatureModel.getData());

            setRecyclerView(modelSearch.getSign(), modelSearch.getType());
        }
    }

    private void onSuccessSearchDataMyReq(GetSignatureModel getSignatureModel) {
        if(getSignatureModel != null){
            modelSearch.setType(1);
            modelSearch.setSign(getSignatureModel.getData());

            setRecyclerView(modelSearch.getSign(), modelSearch.getType());
        }
    }

    private void onSuccessSearchDataMySign(GetSignatureModel getSignatureModel) {
        if(getSignatureModel != null){
            modelSearch.setType(0);
            modelSearch.setSign(getSignatureModel.getData());

            setRecyclerView(modelSearch.getSign(), modelSearch.getType());
        }
    }

    private void onFailedSearchData(Throwable throwable){
        util.toastError(this, "API SEARCH", throwable);
    }

    private void onSuccessSearchData(GetSignatureModel getSignatureModel) {
        if(getSignatureModel != null){
            modelSearch.setType(-1);
            modelSearch.setSign(getSignatureModel.getData());

            setRecyclerView(modelSearch.getSign(), modelSearch.getType());
        }
    }
    //---------------------------RecyclerView---------------------------------
    private List<SignModel> search(List<SignModel> sign, String query){
        List<SignModel> arraySearched = new ArrayList<>();
        for(int i=0; i< sign.size(); i++){
            SignModel element = sign.get(i);
            if(element.getTitle().toLowerCase().contains(query.toLowerCase())){
                arraySearched.add(element);
            }
        }
        return arraySearched;
    }
    
    private void setRecyclerView(List<SignModel> sign, int type){
        RecyclerView rvSearch = findViewById(R.id.rvSearchSuggest);
        SearchListAdapter adapter =  new SearchListAdapter(sign,  getSupportFragmentManager(), type);
        rvSearch.setAdapter(adapter);
        rvSearch.setLayoutManager(new LinearLayoutManager(this));
    }
    //------------------------ LOGIC FOR SUGGESTION BUTTON----------------------------------
    private void clickButton(int index){
        Arrays.fill(suggestionBol, false);

        suggestionBol[index] = !suggestionBol[index];
        for(int i= 0;i< suggestionBol.length; i++){
            changeButtonColor(suggestionBol[i], llSuggestion.get(i), textViewSuggestion.get(i));
        }
        loadData(index);
    }

    private void changeButtonColor(boolean value, LinearLayout btnTemp, TextView tv){
        if(value){
            tv.setTextColor(getResources().getColor(R.color.colorBackground));
            btnTemp.setBackground(ContextCompat.getDrawable(this, R.drawable.button_search_suggestion_clicked ));
        }else{
            tv.setTextColor(getResources().getColor(R.color.colorPrimary));
            btnTemp.setBackground(ContextCompat.getDrawable(this, R.drawable.button_search_suggestion_bg ));
        }
    }



}