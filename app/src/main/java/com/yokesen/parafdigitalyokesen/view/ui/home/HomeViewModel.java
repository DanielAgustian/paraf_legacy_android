package com.yokesen.parafdigitalyokesen.view.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.yokesen.parafdigitalyokesen.model.StatHomeModel;

import retrofit2.Response;

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