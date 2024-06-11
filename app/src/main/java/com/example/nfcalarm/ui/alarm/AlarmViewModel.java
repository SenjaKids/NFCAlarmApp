package com.example.nfcalarm.ui.alarm;

import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import androidx.lifecycle.ViewModel;

public class AlarmViewModel extends ViewModel {

//    private final MutableLiveData<String> mText;
    private int alarmPageStatus = 0;


    final LinearLayout alarmLayout0, alarmLayout1, alarmLayout2;
    public AlarmViewModel(LinearLayout alarmLayout0, LinearLayout alarmLayout1, LinearLayout alarmLayout2) {
        this.alarmLayout0 = alarmLayout0;
        this.alarmLayout1 = alarmLayout1;
        this.alarmLayout2 = alarmLayout2;
    }

    void changeAlarmPageStatus(int newStatus){
        alarmPageStatus = newStatus;
        refreshView();
    }

    int getAlarmPageStatus(){
        return alarmPageStatus;
    }


    public void refreshView(){
        Log.i("DEBUG", "refreshView: " + alarmLayout0);
        if (alarmPageStatus == 0) {
            alarmLayout0.setVisibility(View.VISIBLE);
            alarmLayout1.setVisibility(View.GONE);
            alarmLayout2.setVisibility(View.GONE);
        } else if (alarmPageStatus == 1) {
            alarmLayout0.setVisibility(View.GONE);
            alarmLayout1.setVisibility(View.VISIBLE);
            alarmLayout2.setVisibility(View.GONE);
        } else if (alarmPageStatus == 2) {
            alarmLayout0.setVisibility(View.GONE);
            alarmLayout1.setVisibility(View.GONE);
            alarmLayout2.setVisibility(View.VISIBLE);
        }
    }



}