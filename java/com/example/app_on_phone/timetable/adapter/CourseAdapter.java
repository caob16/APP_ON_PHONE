package com.example.app_on_phone.timetable.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;

import com.example.app_on_phone.R;
import com.example.app_on_phone.timetable.pojo.Course;

import java.util.List;

import androidx.annotation.NonNull;

public class CourseAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater layoutInflater;
    private List<Course> courseList;
    private int viewMode;


    public CourseAdapter(Context context, @NonNull List<Course> courseList,int mode){
        this.context = context;
        this.courseList = courseList;
        this.layoutInflater = LayoutInflater.from(context);
        viewMode = mode; //1:默认状态 2:批量编辑 3：批量删除
    }

    @Override
    public int getCount() {
        return courseList.size();
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
        CourseAdapter.ViewHolder holder = null;
        if(null == convertView){
            convertView = layoutInflater.inflate(R.layout.course_item, null);
            holder = new ViewHolder();
            holder.courseName = convertView.findViewById(R.id.courseName);
            holder.courseCheck = convertView.findViewById(R.id.courseCheck);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        setContent(position, holder);
        return convertView;
    }

    private void setContent(int i, CourseAdapter.ViewHolder view){
        switch(viewMode){
            case 1:
                view.courseName.setFocusable(false);
                view.courseName.setFocusableInTouchMode(false);
                view.courseCheck.setVisibility(View.INVISIBLE);
                break;
            case 2:
                view.courseName.setFocusable(true);
                view.courseName.setFocusableInTouchMode(true);
                view.courseCheck.setVisibility(View.INVISIBLE);
                view.courseName.requestFocus();
                break;
            case 3:
                view.courseCheck.setVisibility(View.VISIBLE);
                break;
        }
        view.courseName.setText(String.format("%s", courseList.get(i).getCourseName()));

    }


    static class ViewHolder{
        EditText courseName;
        CheckBox courseCheck;
    }

}
