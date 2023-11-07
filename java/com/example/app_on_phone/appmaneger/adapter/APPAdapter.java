package com.example.app_on_phone.appmaneger.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Switch;

import com.example.app_on_phone.R;
import com.example.app_on_phone.appmaneger.pojo.APP;

import java.util.List;

import androidx.annotation.NonNull;

public class APPAdapter extends BaseAdapter {

    private View.OnClickListener APPListener;
    private Context context;
    private LayoutInflater layoutInflater;
    private List<APP> APPList;

    public APPAdapter(Context context, @NonNull List<APP> AppList, View.OnClickListener APPListener){
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        this.APPList = AppList;
        this.APPListener = APPListener;
    }

    @Override
    public int getCount() {
        return APPList.size();
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
        convertView = layoutInflater.inflate(R.layout.app_item, null);
        holder = new ViewHolder();
        holder.APPSwitch = convertView.findViewById(R.id.AppSwitch);
        holder.APPSwitch.setTag(APPList.get(position).getId());
        holder.APPSwitch.setOnClickListener(APPListener);
        convertView.setTag(holder);
        setContent(position, holder);
        return convertView;
    }
    private void setContent(int i, ViewHolder view){
        view.APPSwitch.setText(String.format("应用名:%s", APPList.get(i).getAPPName()));
        if(APPList.get(i).getIsOn()==1){
            view.APPSwitch.setChecked(true);
        }else{
            view.APPSwitch.setChecked(false);
        }
    }
    public void setAPPList(List<APP> APPList){
        this.APPList = APPList;
    }
    static class ViewHolder{
        Switch APPSwitch;
    }
}
