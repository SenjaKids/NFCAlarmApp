package com.example.nfcalarm.ui.alarm;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;

import com.example.nfcalarm.AlarmReceiver;
import com.example.nfcalarm.R;
import com.example.nfcalarm.adapter.AlarmListAdapter;
import com.example.nfcalarm.databinding.FragmentAlarmBinding;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class AlarmFragment extends Fragment {

    private FragmentAlarmBinding binding;
    AlertDialog setAlarmDialog;
    LinearLayout alarmLayout0, alarmLayout1, alarmLayout2;
    AlarmViewModel alarmViewModel;
    SharedPreferences sharedPreferences;
    NfcAdapter mNfcAdapter;
    ListView alarmListView;
    int hour, minute;
    ArrayList<AlarmModel> savedAlarm;
    AlarmListAdapter adapter;
    AlarmReceiver alarmReceiver;
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String KEY_TAG = "tagID";
    public static final String KEY_ALARM = "alarmModel";

    AlarmManager alarmManager;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        requireActivity().addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {

                if (alarmViewModel.getAlarmPageStatus() != 0){
                    menuInflater.inflate(R.menu.add_alarm_menu, menu);
                }
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                setAlarmDialog.show();
                return false;
            }
        },  getViewLifecycleOwner(), Lifecycle.State.RESUMED);
        sharedPreferences = getContext().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);


        alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
//        sharedPreferences.edit().remove(KEY_ALARM).commit();
        savedAlarm = getArrayList();
        binding = FragmentAlarmBinding.inflate(inflater, container, false);
        alarmListView = binding.listViewAlarm;

        adapter = new AlarmListAdapter(getContext(), savedAlarm, getActivity(), alarmManager);
        alarmListView.setAdapter(adapter);

        alarmLayout0 = binding.alarmStatus0View;
        alarmLayout1 = binding.alarmStatus1View;
        alarmLayout2 = binding.alarmStatus2View;
        alarmViewModel =
                new AlarmViewModel(alarmLayout0, alarmLayout1, alarmLayout2);

        View root = binding.getRoot();
        buildSetAlarmDialog();
        mNfcAdapter = NfcAdapter.getDefaultAdapter(super.getActivity());


        checkViewStatus();

        return root;
    }

    void checkViewStatus(){
        String tagID = sharedPreferences.getString(KEY_TAG, null);
        if (tagID == null){
            alarmViewModel.changeAlarmPageStatus(0);
        } else if (savedAlarm == null || savedAlarm.size() == 0){
            Log.i("DEBUG", "checkViewStatus: 1");
            alarmViewModel.changeAlarmPageStatus(1);
        } else {
            Log.i("DEBUG", "checkViewStatus: 2");
            alarmViewModel.changeAlarmPageStatus(2);
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    @Override
    public void onResume() {
        super.onResume();

        savedAlarm = getArrayList();
        if(mNfcAdapter!= null) {
            Bundle options = new Bundle();
            options.putInt(NfcAdapter.EXTRA_READER_PRESENCE_CHECK_DELAY, 250);

            mNfcAdapter.enableReaderMode(super.getActivity(),
                    this::onTagDiscovered,
                    NfcAdapter.FLAG_READER_NFC_A |
                            NfcAdapter.FLAG_READER_NFC_B |
                            NfcAdapter.FLAG_READER_NFC_F |
                            NfcAdapter.FLAG_READER_NFC_V |
                            NfcAdapter.FLAG_READER_NFC_BARCODE |
                            NfcAdapter.FLAG_READER_NO_PLATFORM_SOUNDS,
                    options);
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        if(mNfcAdapter!= null) {
            mNfcAdapter.disableReaderMode(super.getActivity());
            mNfcAdapter.disableForegroundDispatch(super.getActivity());
        }
    }


    public void onTagDiscovered(Tag tag) {
//        SharedPreferences.Editor editor = sharedPreferences.edit();

        if (alarmViewModel != null){
            if (alarmViewModel.getAlarmPageStatus() == 2){
                if (sharedPreferences.getString(KEY_TAG, null).equals(bytesToHexString(tag.getId())) ){

                    alarmReceiver.stopAlarm();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            checkViewStatus();
                        }
                    });
//                    Toast.makeText(getContext(), "Alarm Turned Off", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(getContext(), "Tag Not Recognized", Toast.LENGTH_SHORT).show();
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        checkViewStatus();
                    }
                });
            }
        }
    }

    private String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("0x");
        if (src == null || src.length == 0) {
            return null;
        }

        char[] buffer = new char[2];
        for (int i = 0; i < src.length; i++) {
            buffer[0] = Character.forDigit((src[i] >>> 4) & 0x0F, 16);
            buffer[1] = Character.forDigit(src[i] & 0x0F, 16);
            System.out.println(buffer);
            stringBuilder.append(buffer);
        }

        return stringBuilder.toString();
    }

    private void buildSetAlarmDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View view = getLayoutInflater().inflate(R.layout.register_card_dialog, null);

        TimePicker timePickerAlarm = view.findViewById(R.id.timePickerAlarm);


        builder.setView(view);
        builder.setTitle("Set Alarm")
                .setPositiveButton(
                        "Set Alarm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                hour = timePickerAlarm.getHour();
                                minute = timePickerAlarm.getMinute();
                                savedAlarm = getArrayList();

                                if (savedAlarm == null || savedAlarm.size() == 0){
                                    savedAlarm = new ArrayList<AlarmModel>();

                                    alarmReceiver = new AlarmReceiver();
                                    savedAlarm.add(new AlarmModel((hour < 10 ? "0" + hour : hour) + ":" + (minute < 10 ? "0" + minute : minute), alarmReceiver, hour, minute));
                                    saveArrayList(savedAlarm);
                                    adapter = new AlarmListAdapter(getContext(), savedAlarm, getActivity(), alarmManager);
                                    alarmListView.setAdapter(adapter);
                                }else {
                                    alarmReceiver = new AlarmReceiver();
                                    savedAlarm.add(new AlarmModel((hour < 10 ? "0" + hour : hour) + ":" + (minute < 10 ? "0" + minute : minute), alarmReceiver, hour, minute));
                                    saveArrayList(savedAlarm);
                                }

                                adapter.notifyDataSetChanged();
                                checkViewStatus();
                            }
                        })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        setAlarmDialog = builder.create();
    }

    public void saveArrayList(ArrayList<AlarmModel> listArray){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(listArray);
        editor.putString(KEY_ALARM, json);  ///"TAG_LIST" is a key must same for getting data
        editor.apply();
    }

    public ArrayList<AlarmModel> getArrayList(){
        Gson gson = new Gson();
        String json = sharedPreferences.getString(KEY_ALARM, null);
        Type listType = new TypeToken<ArrayList<AlarmModel>>() {}.getType();
//        mSomeArraylist= gson.fromJson(json, listType);
        return gson.fromJson(json, listType);
    }
}

