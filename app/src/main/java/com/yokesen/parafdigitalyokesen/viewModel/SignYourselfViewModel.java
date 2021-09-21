package com.yokesen.parafdigitalyokesen.viewModel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Timer;
import java.util.TimerTask;

public class SignYourselfViewModel extends ViewModel {

    private MutableLiveData<Integer> result = new MutableLiveData<>();
    public LiveData<Integer> getResult(){
        if(result == null){
                result.setValue(100);
            Log.d("ViewModel", "FUCKING MOVE YOU STUPID SHIT");
                TimerTask task = new TimerTask() {
                    public void run() {
                        result.postValue(200);

                    }
                };
                Timer timer = new Timer("Timer");

                long delay = 2000l;
                timer.schedule(task, delay);

        }
       return result;
    }

}
