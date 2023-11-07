package com.example.app_on_phone.appmaneger.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.ck.rtcckinfo.rtc.AvEngine;
import com.example.app_on_phone.R;
import com.example.app_on_phone.appmaneger.pojo.TimeSlot;
import com.example.app_on_phone.utils.ApiService;
import com.example.app_on_phone.appmaneger.adapter.TimeAdapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class AppActivity extends AppCompatActivity {
    //preference 域
    private List<TimeSlot> timeList = new ArrayList<>();

    private ListView lvContent;

    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app);

        lvContent = findViewById(R.id.timelvContent);
        lvContent.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //换成time
//                getPreference();
//                List<String> timeList = new ArrayList<>(timeSet);
                TimeSlot timeSlot = timeList.get(position);
//                String time = timeList.get(position);
                Intent intent = new Intent(AppActivity.this, UpdateAppActivity.class);
//                intent.putExtra("time", time);
                intent.putExtra("id", timeSlot.getId());
                intent.putExtra("start", timeSlot.getStartTime());
                intent.putExtra("end", timeSlot.getEndTime());
                intent.putExtra("isWork", timeSlot.getIsWork());
                intent.putExtra("status", timeSlot.getStatus());
                startActivity(intent);
            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
        apiService = new ApiService();
        /**
         * 调用服务器接口
         *
         * **/
        flushView();
    }

    private void flushView(){
        apiService.getTimeSlotsList("4114").thenAccept(timeSlots -> {
            timeList = timeSlots;
            AppActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    show();
                }
            });
        }).exceptionally(e -> {
            System.out.println("An error occurred: " + e.getCause());
            return null;
        });
    }

    /**
     * 所有时间段
     */
    private void show() {
//        int setSize = timeSet.size();
        //List<String> timeList = new ArrayList<>(timeSet);

        int listSize = timeList.size();
        if(listSize > 0){
//            String[] timeZone = new String[setSize];
//            for(int i = 0; i < setSize; i++) timeZone[i] = timeList.get(i).substring(8);
            TimeAdapter timeAdapter = new TimeAdapter(this, timeList, new ActiveListener());
            lvContent.setAdapter(timeAdapter);
        }else{
            Toast.makeText(this, "暂无时间段！", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 返回
     *
     * @param view
     */
    public void back(View view) {
        this.finish();
    }

    /**
     * 添加时间段
     * 7.10/zxy
     * @param view
     */
    public void addTime(View view) {
        final View inflate = getLayoutInflater().inflate(R.layout.add_time_item, null);
        final CheckBox workButton = ((CheckBox) inflate.findViewById(R.id.checkBoxWork));
        final CheckBox restButton = ((CheckBox) inflate.findViewById(R.id.checkBoxRest));


        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setView(inflate)
                .setPositiveButton("添加", null) // 注意这里，我们暂时设置监听器为null
                .setNegativeButton("取消", null)
                .create();

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button addButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                addButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TimeSlot temp = new TimeSlot();
                        boolean work = workButton.isChecked();
                        boolean rest = restButton.isChecked();
                        String start = ((EditText) inflate.findViewById(R.id.editTextTimeStart)).getText().toString();
                        String end = ((EditText) inflate.findViewById(R.id.editTextTimeEnd)).getText().toString();
                        temp.setStartTime(start);
                        temp.setEndTime(end);
                        temp.setStatus(0);
                        /**
                         * 未动态获取AccID
                         */
                        temp.setAccID("4114");
                        if(work&&rest) {
                            temp.setIsWork(2);
                        }else if(work){
                            temp.setIsWork(0);
                        }else if(rest){
                            temp.setIsWork(1);
                        }

                        Pattern pattern = Pattern.compile("^([01]?[0-9]|2[0-3]):[0-5][0-9]$");
                        Matcher matcherStart = pattern.matcher(start);
                        Matcher matcherEnd = pattern.matcher(end);


                        if (!matcherStart.matches() || !matcherEnd.matches()) {
                            Toast.makeText(AppActivity.this, "时间格式不正确，必须为 HH:MM！", Toast.LENGTH_SHORT).show();
                        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            start = formatTime(start);
                            end = formatTime(end);
                            Log.e("Start:",start);
                            Log.e("End:",end);
                            if (!isEndTimeAfterStartTime(start, end)) {
                                Toast.makeText(AppActivity.this, "结束时间必须在开始时间之后！", Toast.LENGTH_SHORT).show();
                            } else if(!work && !rest){
                                Toast.makeText(AppActivity.this, "请选择休息日OR工作日！", Toast.LENGTH_SHORT).show();
                            }else {
                                /**
                                 *####
                                 * 7.10/zxy/调用服务器API
                                 *#####
                                 * **/
                                apiService.insertTimeSlot(temp).thenAccept(message -> {
                                    if(message.equals("success")){
                                        System.out.println("insert success");
                                        flushView();
                                        dialog.dismiss(); // 在API调用成功后，关闭对话框
                                    }else{
                                        AppActivity.this.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(AppActivity.this, "添加时间段失败！", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                        //保留弹窗？
                                    }
                                }).exceptionally(e -> {
                                    AppActivity.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(AppActivity.this, "添加时间段失败！", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    //保留弹窗？
                                    return null;
                                });
                            }
                        }
                    }
                });
            }
        });

        alertDialog.show();
    }

    private void timeZoneActive(View v) {
        int timeSlotID = (int) v.getTag();
        TimeSlot temp = new TimeSlot();
        for(TimeSlot timeslot : timeList){
            if(timeslot.getId() == timeSlotID){
                temp.setId(timeslot.getId());
                temp.setAccID(timeslot.getAccID());
                temp.setStartTime(timeslot.getStartTime());
                temp.setEndTime(timeslot.getEndTime());
                temp.setIsWork(timeslot.getIsWork());
                if(timeslot.getStatus()==0){
                    temp.setStatus(1);
                    timeslot.setStatus(1);
                } else{
                    temp.setStatus(0);
                    timeslot.setStatus(0);
                }
                break;
            }
        }
        apiService.updateTimeSlot(temp).thenAccept(message -> {
            if(message.equals("success")){
                System.out.println("update success");
            }else{
                System.out.println("update failed");
                AppActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(AppActivity.this, "网络异常！", Toast.LENGTH_SHORT).show();
                        show();
                    }
                });
            }
        }).exceptionally(e -> {
            System.out.println("An error occurred: " + e.getCause());
            AppActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(AppActivity.this, "网络异常！", Toast.LENGTH_SHORT).show();
                    show();
                }
            });
            return null;
        });
    }

    private class ActiveListener implements CompoundButton.OnClickListener {
        @Override
        public void onClick(View v) {
            timeZoneActive(v);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static boolean isEndTimeAfterStartTime(String start, String end) {
        try {
            LocalTime startTime = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startTime = LocalTime.parse(start);
            }
            LocalTime endTime = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                endTime = LocalTime.parse(end);
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                return endTime.isAfter(startTime);
            }
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid time format, must be HH:MM", e);
        }
        return false;
    }

    public static String formatTime(String time) {
        if (time.equals("12:00")) {
            return "12:00";
        }
        SimpleDateFormat inputFormat = new SimpleDateFormat("h:mm", Locale.getDefault());
        SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        try {
            return outputFormat.format(inputFormat.parse(time));
        } catch (ParseException e) {
            e.printStackTrace();
            return time;
        }
    }
}
