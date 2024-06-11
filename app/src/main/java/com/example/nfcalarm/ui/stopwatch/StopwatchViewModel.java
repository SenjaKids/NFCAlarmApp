package com.example.nfcalarm.ui.stopwatch;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class StopwatchViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public StopwatchViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("00:00:00:00");
    }

    public LiveData<String> getText() {
        return mText;
    }
}