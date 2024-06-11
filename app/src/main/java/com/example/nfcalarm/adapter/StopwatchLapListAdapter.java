package com.example.nfcalarm.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.nfcalarm.R;
import com.example.nfcalarm.model.TimeLap;

import java.util.ArrayList;

public class StopwatchLapListAdapter extends BaseAdapter {

    Context context;
    ArrayList<TimeLap> timeLaps = new ArrayList<>();

    LayoutInflater inflater;

    public StopwatchLapListAdapter(Context context, ArrayList<TimeLap> timeLaps) {
        this.context = context;
        this.timeLaps = timeLaps;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return timeLaps.size();
    }

    @Override
    public Object getItem(int position) {
//        return timeLaps.get(timeLaps.size() - position - 1);
        return timeLaps.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.activity_stopwatch_lap_listview, null);
        TextView textLapCount = (TextView) convertView.findViewById(R.id.textLapCount);
        TextView textLapTime = (TextView) convertView.findViewById(R.id.textLapTime);

        textLapCount.setText("Lap " + timeLaps.get(position).getLap());
        textLapTime.setText(timeLaps.get(position).getTime());

        Log.d("TAG", "getView: "+ getCount());
        return convertView;
    }
}
