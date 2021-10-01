package com.yokesen.parafdigitalyokesen.view.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.yokesen.parafdigitalyokesen.R;
import com.yokesen.parafdigitalyokesen.Repository.APIClient;
import com.yokesen.parafdigitalyokesen.Repository.APIInterface;
import com.yokesen.parafdigitalyokesen.Repository.PreferencesRepo;
import com.yokesen.parafdigitalyokesen.util.Util;
import com.yokesen.parafdigitalyokesen.adapter.NotifListAdapter;
import com.yokesen.parafdigitalyokesen.model.GetNotifListModel;
import com.yokesen.parafdigitalyokesen.model.NotificationModel;
import com.yokesen.parafdigitalyokesen.view.ui.profile.child_profile.security.PasscodeView;
import com.yokesen.parafdigitalyokesen.viewModel.NotificationState;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class NotificationListActivity extends AppCompatActivity {
    RecyclerView rvNotif;
    String token;
    APIInterface apiInterface;
    PreferencesRepo preferencesRepo;
    Util util = new Util();
    Disposable disposableRefresh;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_list);
        initToolbar();
        initData();
        observer();
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
    private void observer(){
         disposableRefresh = NotificationState.getSubject().
                subscribeWith(new DisposableObserver<String>() {
                    @Override
                    public void onNext(String t) {
                        if(t.equals("ref")){
                            //util.toastMisc(NotificationListActivity.this, "Update.....");
                            initData();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposableRefresh.dispose();
    }

    private void initData() {
        apiInterface = APIClient.getClient().create(APIInterface.class);
        preferencesRepo = new PreferencesRepo(this);
        token = preferencesRepo.getToken();

        Observable<GetNotifListModel> GetSearchData = apiInterface.GetNotificationList(token);
        GetSearchData.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                this::onSuccessGetNotif, this::onFailedGetNotif);
    }

    private void onFailedGetNotif(Throwable throwable) {
        util.toastError(this, "API NOTIF LIST", throwable);

    }

    private void onSuccessGetNotif(GetNotifListModel getNotifListModel) {
        if(getNotifListModel != null){
            initRecyclerView(filterNotif(getNotifListModel.getData()));
        }
    }



    private void initRecyclerView(List<NotificationModel> model){
        rvNotif= findViewById(R.id.rvNotif);
        Log.d("NOTIFRECYCLERVIEW", "Begin");
        if(model.size() > 0){
            NotifListAdapter adapter = new NotifListAdapter(model);
            rvNotif.setLayoutManager(new LinearLayoutManager(this));

            rvNotif.setAdapter(adapter);
        } else{
            emptyList();
        }

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

    private List<NotificationModel> filterNotif(List<NotificationModel> model){

        List<NotificationModel> result = new ArrayList<>();
        for(int i = 0;i< model.size(); i++){
            NotificationModel element = model.get(i);
            if(element.getUnread().equals("unread")){
                result.add(element);
            }
        }
        return result;
    }


    private void emptyList(){
        LinearLayout llRecycler =findViewById( R.id.llNotifRecycler);
        LinearLayout llEmptyList = findViewById(R.id.llEmptyNotif);

        llRecycler.setVisibility(View.GONE);
        llEmptyList.setVisibility(View.VISIBLE);
    }


    public void back(){
        this.finish();
    }
}