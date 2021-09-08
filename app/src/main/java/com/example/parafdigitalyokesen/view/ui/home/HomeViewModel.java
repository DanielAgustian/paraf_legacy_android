package com.example.parafdigitalyokesen.view.ui.home;

import android.content.SharedPreferences;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.parafdigitalyokesen.Repository.APIClient;
import com.example.parafdigitalyokesen.Repository.APIInterface;
import com.example.parafdigitalyokesen.Repository.PreferencesRepo;
import com.example.parafdigitalyokesen.model.StatHomeModel;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<StatHomeModel> homeStateModel;

    public HomeViewModel() {

    }

    public LiveData<StatHomeModel> getData(){
        return null;

    }

    private void handleResult(Response<StatHomeModel> statHomeModelResponse) {

    }


}