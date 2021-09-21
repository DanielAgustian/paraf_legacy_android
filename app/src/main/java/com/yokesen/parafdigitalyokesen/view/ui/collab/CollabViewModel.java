package com.yokesen.parafdigitalyokesen.view.ui.collab;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CollabViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public CollabViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is collab fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}