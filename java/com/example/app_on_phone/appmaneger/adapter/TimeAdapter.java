package com.example.app_on_phone.appmaneger.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Switch;
import android.widget.TextView;

import com.example.app_on_phone.R;
import com.example.app_on_phone.appmaneger.pojo.TimeSlot;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import androidx.annotation.NonNull;

public class TimeAdapter extends BaseAdapter {

    private View.OnClickListener listener;
    //private CourseDao ApiUtils;
    private Context context;
    private LayoutInflater layoutInflater;
    private List<TimeSlot> timeList;

    //private View.OnClickListener listener;

    public TimeAdapter(Context context, @NonNull List<TimeSlot> timeList, Switch.OnClickListener listener){
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        this.timeList = timeList;

        this.listener = listener;
        //ApiUtils = new CourseDao(context);
    }



    @Override
    public int getCount() {
        return timeList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if(null == convertView){
            convertView = layoutInflater.inflate(R.layout.time_item, null);
            holder = new ViewHolder();
            holder.TimeSwitch = convertView.findViewById(R.id.timeSwitch);
            holder.TimeSwitch.setTag(timeList.get(position).getId());
            holder.TimeSwitch.setOnClickListener(listener);
            holder.TimeZone = convertView.findViewById(R.id.timeZone);
            holder.TimeZone.setTag(timeList.get(position).getId());
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        setContent(position, holder);
        return convertView;
    }

    private void setContent(int i, ViewHolder view){
        view.TimeZone.setText(String.format("%s-%s", timeList.get(i).getStartTime(), timeList.get(i).getEndTime()));
        if(timeList.get(i).getStatus() == 1){
            view.TimeSwitch.setChecked(true);
        }else{
            view.TimeSwitch.setChecked(false);
        }
    }

    static class ViewHolder{
        TextView TimeZone;
        Switch TimeSwitch;
    }
}
