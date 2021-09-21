package com.yokesen.parafdigitalyokesen.view.ui.draft;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DraftViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public DraftViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is draft fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}