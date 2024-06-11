package com.example.nfcalarm.ui.stopwatch;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.nfcalarm.R;
import com.example.nfcalarm.adapter.StopwatchLapListAdapter;
import com.example.nfcalarm.databinding.FragmentStopwatchBinding;
import com.example.nfcalarm.model.TimeLap;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

public class StopwatchFragment extends Fragment {

    private FragmentStopwatchBinding binding;

    //Stopwatch components
//    TimeLap laps[] = {
//            new TimeLap(1,"00:00:00:01"),
//            new TimeLap(2,"00:00:00:01"),
//            new TimeLap(3,"00:00:00:01"),
//    };
    ArrayList<TimeLap> laps = new ArrayList<>();
    ListView lapListView;

    TextView timerText;
    Button start, reset;

    Boolean isRunning = false;
    int hours, minutes, seconds, milliSeconds, lapsCount = 0;
    long milliSecondTime, startTime, timeBuff, updateTime = 0L;
    Handler handler;
    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            milliSecondTime = SystemClock.uptimeMillis() - startTime;
            updateTime = timeBuff + milliSecondTime;

            seconds = (int) (updateTime / 1000);
            hours = seconds / 3600;
            minutes = seconds / 60;
            seconds = seconds % 60;
            milliSeconds = (int) (updateTime % 1000)/10;

            timerText.setText(
                    MessageFormat.format(
                            "{0}:{1}:{2}:{3}",
                            String.format(Locale.getDefault(), "%02d", hours),
                            String.format(Locale.getDefault(), "%02d", minutes),
                            String.format(Locale.getDefault(), "%02d", seconds),
                            String.format(Locale.getDefault(), "%02d", milliSeconds)
                    )
            );

            handler.postDelayed(this, 0);
        }
    };

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        StopwatchViewModel stopwatchViewModel =
                new ViewModelProvider(this).get(StopwatchViewModel.class);

        binding = FragmentStopwatchBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

//        final TextView textView = binding.textStopwatch;
//        stopwatchViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);




        lapListView = binding.listViewStopwatchLaps;

        StopwatchLapListAdapter adapter = new StopwatchLapListAdapter(getContext(), laps);
        lapListView.setAdapter(adapter);

        adapter.notifyDataSetChanged();

        timerText = binding.textStopwatch;
        reset = binding.btnStopwatchReset;
        start = binding.btnStopwatchStart;

        handler = new Handler(Looper.getMainLooper());

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isRunning) {
                    // stop timer
                    timeBuff += milliSecondTime;
                    handler.removeCallbacks(runnable);

                    start.setText("Start");
                    reset.setText("Reset");
                    reset.setTextColor(ContextCompat.getColor(requireContext(), R.color.red));

                    isRunning = false;

                    Collections.reverse(laps);
                    laps.add(new TimeLap(lapsCount+1, timerText.getText().toString()));
                    lapsCount++;
                    Collections.reverse(laps);

                } else {
                    // start timer
                    startTime = SystemClock.uptimeMillis();
                    handler.postDelayed(runnable, 0);

                    start.setText("Stop");
                    reset.setText("Lap");
                    reset.setTextColor(ContextCompat.getColor(requireContext(), R.color.teal_700));

                    isRunning = true;
                }

                adapter.notifyDataSetChanged();

            }
        });

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isRunning) {
                    // Add laps
                    Collections.reverse(laps);
                    laps.add(new TimeLap(lapsCount+1, timerText.getText().toString()));
                    lapsCount++;
                    Collections.reverse(laps);

                } else {
                    // Reset Data
                    milliSecondTime = 0L;
                    startTime = 0L;
                    timeBuff = 0L;
                    updateTime = 0L;
                    seconds = 0;
                    minutes = 0;
                    hours = 0;
                    milliSeconds = 0;
//                timerText.setText("00:00:00:00");
                    stopwatchViewModel.getText().observe(getViewLifecycleOwner(), timerText::setText);

                    lapsCount = 0;
                    laps.removeAll(laps);
                }

                adapter.notifyDataSetChanged();
            }
        });

//        timerText.setText("00:00:00:00");
        stopwatchViewModel.getText().observe(getViewLifecycleOwner(), timerText::setText);


        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}