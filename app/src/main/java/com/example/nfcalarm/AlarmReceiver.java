package com.example.nfcalarm;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.AudioAttributes;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.nfcalarm.adapter.AlarmListAdapter;
import com.example.nfcalarm.ui.alarm.AlarmModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class AlarmReceiver extends BroadcastReceiver {

    private static Ringtone r;
    private static double id = -1;

    SharedPreferences sharedPreferences;
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String KEY_ALARM = "alarmModel";


    @Override
    public void onReceive(Context context, Intent intent) {

        sharedPreferences = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        Log.i("DEBUG", "onReceive: i");
        Intent i = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, i, PendingIntent.FLAG_MUTABLE);

        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(2000);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "AlarmNotif")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("Alarm Reminder")
                .setContentText("Tap NFC to turn off alarm")
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);

        Log.i("DEBUG", "onReceive: " + ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS));
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
//             TODO: Consider calling
//                ActivityCompat#requestPermissions
//             here to request the missing permissions, and then overriding
//               public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                                                      int[] grantResults)
//             to handle the case where the user grants the permission. See the documentation
//             for ActivityCompat#requestPermissions for more details.

            Log.i("DEBUG", "onReceive: GADAPAT PERMISSION");
            return;
        }
        notificationManagerCompat.notify(200, builder.build());

        Uri sound = RingtoneManager.getActualDefaultRingtoneUri(context, RingtoneManager.TYPE_RINGTONE);
        r = RingtoneManager.getRingtone(context, sound);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            r.setLooping(true);
        }
        r.setAudioAttributes(new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ALARM)
                .build());
        r.play();

        ArrayList<AlarmModel> alarmModels = getArrayList();

        for (AlarmModel x: alarmModels) {

            if (x.getAlarmTime().equals(new SimpleDateFormat("HH:mm").format(new Date()))){
                Log.i("DEBUG", "onReceive: same" +
                        x.getAlarmTime());
                alarmModels.get(alarmModels.indexOf(x)).setIsActive(false);
                saveArrayList(alarmModels);
//                adapter.notifyDataSetChanged();
            }else {
                Log.i("DEBUG", "onReceive: " +
                        x.getHour() +":"+ x.getMinute() +" "+x.getAlarmTime());
                Log.i("DEBUG", "onReceive: not same");
            }

        }
    }


    public static void stopAlarm(){
        r.stop();
        Log.i("DEBUG", "stopAlarm: " + id);
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

}
