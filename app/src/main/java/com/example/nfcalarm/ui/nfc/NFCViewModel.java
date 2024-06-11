package com.example.nfcalarm.ui.nfc;

import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Objects;

public class NFCViewModel extends ViewModel {

    private int nfcPageStatus;
    final LinearLayout layout0;
    final LinearLayout layout1;
    final LinearLayout layout2;

    final String TAG = "DEBUG";

    public NFCViewModel(
            LinearLayout layout0,
            LinearLayout layout1,
            LinearLayout layout2) {
        this.layout0 = layout0;
        this.layout1 = layout1;
        this.layout2 = layout2;
//        nfcPageStatus = new MutableLiveData<>();
        nfcPageStatus = 0;
    }

    public int getPageStatus() {
        return nfcPageStatus;
    }

    public void changePageStatus(int newStatus){
        nfcPageStatus = newStatus;
        Log.i("DEBUG", "changePageStatus: " + newStatus);
    }

    public void refreshView(){
        if (nfcPageStatus == 0) {
            layout0.setVisibility(View.VISIBLE);
            layout1.setVisibility(View.GONE);
            layout2.setVisibility(View.GONE);
        } else if (nfcPageStatus == 1) {
            layout0.setVisibility(View.GONE);
            layout1.setVisibility(View.VISIBLE);
            layout2.setVisibility(View.GONE);
        } else if (nfcPageStatus == 2) {
            layout0.setVisibility(View.GONE);
            layout1.setVisibility(View.GONE);
            layout2.setVisibility(View.VISIBLE);
        }
    }
}