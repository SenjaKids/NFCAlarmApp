package com.example.nfcalarm.adapter;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nfcalarm.AlarmReceiver;
import com.example.nfcalarm.R;
import com.example.nfcalarm.ui.alarm.AlarmModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class AlarmListAdapter extends BaseAdapter {

    Context context;
    ArrayList<AlarmModel> alarms = new ArrayList<>();
    SharedPreferences sharedPreferences;
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String KEY_ALARM = "alarmModel";


    LayoutInflater inflater;
    Activity activity;
    AlarmManager alarmManager;
    Calendar calendar, calendar_now;

    public AlarmListAdapter(Context context, ArrayList<AlarmModel> alarms, Activity activity, AlarmManager alarmManager) {
        this.context = context;
        this.alarms = alarms;
        this.activity = activity;
        this.alarmManager = alarmManager;
        inflater = LayoutInflater.from(context);
        sharedPreferences = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
    }


    void setAlarm(Context context, int hour, int minute, int position){
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Date date = new Date();
        alarms = getArrayList();
        createNotificationChannel(context);

        Log.i("DEBUG", "setAlarm: "+ hour + ":" + minute);
        calendar_now = Calendar.getInstance();
        calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar_now.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);


        if (calendar.before(calendar_now)){
            calendar.add(Calendar.DATE, 1);
        }

        PendingIntent pendingIntent = PendingIntent.getBroadcast(activity,position,new Intent(context.getApplicationContext(), AlarmReceiver.class),PendingIntent.FLAG_IMMUTABLE);

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

        Toast.makeText(context, "Alarm Created", Toast.LENGTH_SHORT).show();
    }

    @Override
    public int getCount() {
        if (alarms != null){
            return alarms.size();
        }else {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        return alarms.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        alarms = getArrayList();
        convertView = inflater.inflate(R.layout.alarm_list_item, null);
        TextView textAlarm = (TextView) convertView.findViewById(R.id.alarmText);
        Switch switchAlarm = (Switch) convertView.findViewById(R.id.alarmStatusSwitch);
        ImageView deleteBtn = (ImageView) convertView.findViewById(R.id.alarmItemDeleteBtn);

        textAlarm.setText(alarms.get(position).getAlarmTime());
        switchAlarm.setChecked(alarms.get(position).getIsActive());
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!alarms.get(position).getIsActive()){
                    alarms.remove(position);
                    saveArrayList(alarms);
                    notifyDataSetChanged();
                }else {

                    Toast.makeText(context, "Can't turn off alarm manually", Toast.LENGTH_SHORT).show();
                }
            }
        });
        switchAlarm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (alarms.get(position).getIsActive()){

                    alarms.get(position).setIsActive(true);
                    switchAlarm.setChecked(alarms.get(position).getIsActive());
                    Toast.makeText(context, "Use NFC to turn off the alarm", Toast.LENGTH_SHORT).show();
                } else {
                    alarms.get(position).setIsActive(true);
                    switchAlarm.setChecked(true);
                    saveArrayList(alarms);
                    setAlarm(context, alarms.get(position).getHour(), alarms.get(position).getMinute(), position);
                }
            }
        });
        return convertView;
    }

    public void saveArrayList(ArrayList<AlarmModel> listArray){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(listArray);
        editor.putString(KEY_ALARM, json);
        editor.apply();
    }

    public ArrayList<AlarmModel> getArrayList(){
        Gson gson = new Gson();
        String json = sharedPreferences.getString(KEY_ALARM, null);
        Type listType = new TypeToken<ArrayList<AlarmModel>>() {}.getType();
        return gson.fromJson(json, listType);
    }


    private void createNotificationChannel(Context context){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "Alarm Reminder";
            String description = "Tap NFC to turn off alarm";
            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel channel = new NotificationChannel("AlarmNotif", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
