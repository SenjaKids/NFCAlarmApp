package com.example.nfcalarm.ui.timer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.media.AudioAttributes;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.nfcalarm.R;
import com.example.nfcalarm.databinding.FragmentTimerBinding;
public class TimerFragment extends Fragment {

    private FragmentTimerBinding binding;
    private String TAG = "DEBUG";
    private LinearLayout layout0, layout1;
    private TextView timerText;
    private Button setTimerBtn, deleteTimerBtn, startTimerBtn;
    private long timerTimeInMilli, pausedTimerInMilli;
    private int timerPageState;
    private boolean isDeleteBtnAppear = true;
    private int hour, minute, second;
    private AlertDialog setTimerDialog;
    private CountDownTimer countDown;
    private Ringtone r;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentTimerBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        layout0 = binding.timerLayout0;
        layout1 = binding.timerLayout1;
        timerText = binding.timerTimeText;
        setTimerBtn = binding.setTimerBtn;
        deleteTimerBtn = binding.deleteTimerBtn;
        startTimerBtn = binding.startTimerBtn;

        buildSetTimerDialog();

        Uri sound = RingtoneManager.getActualDefaultRingtoneUri(getContext(), RingtoneManager.TYPE_RINGTONE);
        r = RingtoneManager.getRingtone(getContext(), sound);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            r.setLooping(true);
        }
        r.setAudioAttributes(new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ALARM)
                .build());

        startTimerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "onClick: test");
                if(startTimerBtn.getText().equals("Start Timer")){
                    Log.i(TAG, "onClick: test");
                    countDown.start();
                } else {
                    if (isDeleteBtnAppear){
                        Log.i(TAG, "onClick: paused" +pausedTimerInMilli);
                        hour = (int)pausedTimerInMilli / (1000*60*60);
                        minute = (int)pausedTimerInMilli / (1000*60) - (hour * 60 * 60);
                        second = (int) (pausedTimerInMilli / 1000) - (minute * 60) - (hour * 60 * 60);
                        countDown = new CountDownTimer(pausedTimerInMilli, 1000){
                            @Override
                            public void onTick(long milliTillFinish) {
                                Log.i(TAG, "onTick: " + milliTillFinish);
                                pausedTimerInMilli =  milliTillFinish;
                                if (second == 0){
                                    if (minute == 0){
                                        if (hour != 0) {
                                            hour--;
                                            minute = 59;
                                            second = 59;
                                        }
                                    }else {
                                        minute--;
                                        second = 59;
                                    }
                                }else {
                                    second--;
                                }
                                timerText.setText(String.format("%s:%s:%s", formatTime(hour), formatTime(minute), formatTime(second)));
                            }

                            @Override
                            public void onFinish() {
                                Toast.makeText(getContext(), "Timer Finish", Toast.LENGTH_SHORT).show();
                                startTimerBtn.setVisibility(View.GONE);
                                r.play();
                                deleteTimerBtn.setVisibility(View.VISIBLE);
                                deleteTimerBtn.setText("Stop Ringtone");
                            }
                        };
                        countDown.start();
                    }else {
                        countDown.cancel();
                    }
                }
                isDeleteBtnAppear = !isDeleteBtnAppear;
                if (isDeleteBtnAppear){
                    startTimerBtn.setText("Resume Timer");
                }else {
                    startTimerBtn.setText("Pause Timer");
                }
                refreshTimerPage();
            }
        });

        deleteTimerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTimerPageState(0);
                startTimerBtn.setText("Start Timer");
                deleteTimerBtn.setText("Delete Timer");
                startTimerBtn.setVisibility(View.VISIBLE);
                isDeleteBtnAppear = true;
                r.stop();
            }
        });

        setTimerPageState(0);
        return root;
    }

    private void setTimerPageState(int value){
        timerPageState = value;
        refreshTimerPage();
    }
    private void refreshTimerPage(){
        if (timerPageState == 0){
            layout0.setVisibility(View.VISIBLE);
            layout1.setVisibility(View.GONE);
        } else if (timerPageState == 1){
            layout0.setVisibility(View.GONE);
            layout1.setVisibility(View.VISIBLE);
            if (isDeleteBtnAppear){
                deleteTimerBtn.setVisibility(View.VISIBLE);
            }else {
                deleteTimerBtn.setVisibility(View.GONE);
            }
        }
    }


    private void buildSetTimerDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View view = getLayoutInflater().inflate(R.layout.timer_picker_dialog, null);

        NumberPicker hourPicker = view.findViewById(R.id.timePickerHour);
        NumberPicker minutePicker = view.findViewById(R.id.timePickerMinute);
        NumberPicker secondPicker = view.findViewById(R.id.timePickerSecond);

        String[] hours = new String[]{"00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23"};
        String[] secondAndMinute = new String[]{"00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59"};
        hourPicker.setMinValue(0);
        hourPicker.setMaxValue(hours.length - 1);
        hourPicker.setDisplayedValues(hours);
        minutePicker.setMinValue(0);
        minutePicker.setMaxValue(secondAndMinute.length - 1);
        minutePicker.setDisplayedValues(secondAndMinute);
        secondPicker.setMinValue(0);
        secondPicker.setMaxValue(secondAndMinute.length - 1);
        secondPicker.setDisplayedValues(secondAndMinute);

        builder.setView(view);
        builder.setTitle("Set Timer")
                .setPositiveButton(
                        "Set Timer", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                setTimerPageState(1);

                                hour = hourPicker.getValue();
                                minute = minutePicker.getValue();
                                second = secondPicker.getValue();
                                timerText.setText(String.format("%s:%s:%s", formatTime(hour), formatTime(minute), formatTime(second)));

                                timerTimeInMilli = (second * 1000) + (minute * 60 * 1000) + (hour * 60 * 60 * 1000);

                                Log.i(TAG, "onClick: "+timerTimeInMilli);

                                countDown = new CountDownTimer(timerTimeInMilli, 1000){
                                    @Override
                                    public void onTick(long milliTillFinish) {
                                        Log.i(TAG, "onTick: " + milliTillFinish);
                                        pausedTimerInMilli =  milliTillFinish;
                                        if (second == 0){
                                            if (minute == 0){
                                                if (hour != 0) {
                                                    hour--;
                                                    minute = 59;
                                                    second = 59;
                                                }
                                            }else {
                                                minute--;
                                                second = 59;
                                            }
                                        }else {
                                            second--;
                                        }
                                        timerText.setText(String.format("%s:%s:%s", formatTime(hour), formatTime(minute), formatTime(second)));

                                    }

                                    @Override
                                    public void onFinish() {
                                        Log.i(TAG, "onFinish: finished");
                                        Toast.makeText(getContext(), "Timer Finish", Toast.LENGTH_SHORT).show();
                                        startTimerBtn.setVisibility(View.GONE);
                                        deleteTimerBtn.setVisibility(View.VISIBLE);
                                        r.play();
                                        deleteTimerBtn.setText("Stop Ringtone");
                                    }
                                };
                            }
                        })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {}
                });
        setTimerDialog = builder.create();

        setTimerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTimerDialog.show();
            }
        });
    }

    private String formatTime(int time){
        if (time < 10){
            return "0" + time;
        }else {
            return String.valueOf(time);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}