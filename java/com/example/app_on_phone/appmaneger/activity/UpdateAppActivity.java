package com.example.app_on_phone.appmaneger.activity;

import static com.example.app_on_phone.appmaneger.activity.AppActivity.formatTime;
import static com.example.app_on_phone.appmaneger.activity.AppActivity.isEndTimeAfterStartTime;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.ck.rtcckinfo.rtc.AvEngine;
import com.example.app_on_phone.R;
import com.example.app_on_phone.appmaneger.pojo.APP;
import com.example.app_on_phone.appmaneger.pojo.TimeSlot;
import com.example.app_on_phone.utils.ApiService;
import com.example.app_on_phone.appmaneger.adapter.APPAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class UpdateAppActivity extends AppCompatActivity {

    private List<String> testAppList = new ArrayList<>();
    private List<String> allAppList = new ArrayList<>();
    private List<APP> APPList = new ArrayList<>();
    private ListView lvAPP;
    private CheckBox restButton,workButton;
    private APPAdapter APPAdapter;
    private ApiService apiService = new ApiService();
    /**
     *
     * 7.10/zxy
     */
    private String startTime;
    private String endTime;
    private int isWork;
    private int timeSlotID;
    private int status;
    private String accID = "4114";
    private int scrollPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_app);
        lvAPP = findViewById(R.id.lvApp);

        timeSlotID = getIntent().getIntExtra("id",0);
        startTime = getIntent().getStringExtra("start");
        endTime = getIntent().getStringExtra("end");
        isWork = getIntent().getIntExtra("isWork",0);
        status = getIntent().getIntExtra("status",0);
        Log.e("status",status+"");

        APPAdapter = new APPAdapter(this,APPList,new AppListener());
        lvAPP.setAdapter(APPAdapter);

//        getAppList();
        showTimeAndWork();

        flushAPP();
    }

    private void getAppList(){
        int count = 0;
        Intent intent = new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER);
        PackageManager pm = getPackageManager();
        List<ResolveInfo> packages  = pm.queryIntentActivities(intent, 0);
        for (ResolveInfo resolveInfo : packages){
            count++;
            if(count<=6){
                allAppList.add(resolveInfo.loadLabel(pm).toString());
            }
            testAppList.add(resolveInfo.loadLabel(pm).toString());
            Log.e("appList",testAppList.toString());
        }
    }
    private void showTimeAndWork(){
        ((EditText) findViewById(R.id.editTextTimeStart2)).setText(startTime);
        ((EditText) findViewById(R.id.editTextTimeEnd2)).setText(endTime);
        workButton = (CheckBox) findViewById(R.id.checkBoxWork2);
        restButton = (CheckBox) findViewById(R.id.checkBoxRest2);
        if(isWork == 0){
            workButton.setChecked(true);
        }else if(isWork ==1){
            restButton.setChecked(true);
        }else{
            workButton.setChecked(true);
            restButton.setChecked(true);
        }
    }

    private void showAPP(){
    }

    private void flushAPP(){
        apiService.getAPPsList("4114",timeSlotID+"").thenAccept(Apps -> {
            APPList = Apps;
            APPAdapter.setAPPList(APPList);
            UpdateAppActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    APPAdapter.notifyDataSetChanged();
                }
            });
        }).exceptionally(e -> {
            System.out.println("An error occurred: " + e.getCause());
            return null;
        });
    }

    private class AppListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            AppActive(v);
        }
    }

    public void save(View view) {
        /**
         *
         * 7.10/zxy
         */
        TimeSlot temp = new TimeSlot();
        temp.setAccID(accID);
        temp.setId(timeSlotID);
        temp.setStatus(status);

        boolean work = workButton.isChecked();
        boolean rest = restButton.isChecked();

        if(work&&rest) {
            temp.setIsWork(2);
            //day = "2";
        }else if(work){
            temp.setIsWork(0);
            //day = "0";
        }else if(rest){
            temp.setIsWork(1);
            //day = "1";
        }else{
            Toast.makeText(UpdateAppActivity.this, "请选择休息日OR工作日！", Toast.LENGTH_SHORT).show();
            return;
        }
        String startTime = ((EditText) findViewById(R.id.editTextTimeStart2)).getText().toString();
        String endTime = ((EditText) findViewById(R.id.editTextTimeEnd2)).getText().toString();

        Pattern pattern = Pattern.compile("^([01]?[0-9]|2[0-3]):[0-5][0-9]$");
        Matcher matcherStart = pattern.matcher(startTime);
        Matcher matcherEnd = pattern.matcher(endTime);


        if (!matcherStart.matches() || !matcherEnd.matches()) {
            Toast.makeText(UpdateAppActivity.this, "时间格式不正确，必须为 HH:MM！", Toast.LENGTH_SHORT).show();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startTime = formatTime(startTime);
            endTime = formatTime(endTime);
            if (!isEndTimeAfterStartTime(startTime, endTime)) {
                Toast.makeText(UpdateAppActivity.this, "结束时间必须在开始时间之后！", Toast.LENGTH_SHORT).show();
            }else{
                temp.setStartTime(startTime);
                temp.setEndTime(endTime);
                apiService.updateTimeSlot(temp).thenAccept(message -> {
                    if(message.equals("success")){
                        System.out.println("update success");
                        closeKeyboard();
                        UpdateAppActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(UpdateAppActivity.this, "更新成功！", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }else{
                        System.out.println("update failed");
                        UpdateAppActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showTimeAndWork();
                                Toast.makeText(UpdateAppActivity.this, "网络异常！", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).exceptionally(e -> {
                    System.out.println("An error occurred: " + e.getCause());
                    UpdateAppActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showTimeAndWork();
                            Toast.makeText(UpdateAppActivity.this, "网络异常！", Toast.LENGTH_SHORT).show();
                        }
                    });
                    return null;
                });
            }
        }

    }
    private void AppActive(View v) {
        int id = (Integer) v.getTag(); //Position
        int tsId = timeSlotID;
        for(APP app : APPList){
            if(app.getId() == id){
                if(app.getIsOn() > 0){
                    apiService.deleteAPPStatus(id,tsId).thenAccept(message -> {
                        if(message.equals("success")){
                            System.out.println("update success");
                            UpdateAppActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    flushAPP();
                                    Toast.makeText(UpdateAppActivity.this, "更新成功！", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }else{
                            System.out.println("update failed");
                            UpdateAppActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    flushAPP();
                                    Toast.makeText(UpdateAppActivity.this, "网络异常！", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }).exceptionally(e -> {
                        System.out.println("An error occurred: " + e.getCause());
                        UpdateAppActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                flushAPP();
                                Toast.makeText(UpdateAppActivity.this, "网络异常！", Toast.LENGTH_SHORT).show();
                            }
                        });
                        return null;
                    });
                }else{
                    apiService.updateAPPStatus(id,tsId).thenAccept(message -> {
                        if(message.equals("success")){
                            System.out.println("update success");
                            UpdateAppActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    flushAPP();
                                    Toast.makeText(UpdateAppActivity.this, "更新成功！", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }else{
                            System.out.println("update failed");
                            UpdateAppActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    flushAPP();
                                    Toast.makeText(UpdateAppActivity.this, "网络异常！", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }).exceptionally(e -> {
                        System.out.println("An error occurred: " + e.getCause());
                        UpdateAppActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                flushAPP();
                                Toast.makeText(UpdateAppActivity.this, "网络异常！", Toast.LENGTH_SHORT).show();
                            }
                        });
                        return null;
                    });
                }
            }
        }
    }
    public void back(View view) {
        this.finish();
    }
    public void delTime(View view) {
        new AlertDialog.Builder(this)
                .setTitle("删除时间段")
                .setPositiveButton("确定删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        apiService.deleteTimeSlot(timeSlotID).thenAccept(message -> {
                            if(message.equals("success")){
                                System.out.println("insert success");
                                UpdateAppActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(UpdateAppActivity.this, "删除成功！", Toast.LENGTH_SHORT).show();
                                        UpdateAppActivity.this.finish();
                                    }
                                });
                            }else{
                                Toast.makeText(UpdateAppActivity.this, "删除时间段失败！", Toast.LENGTH_SHORT).show();
                                //保留弹窗？
                            }
                        }).exceptionally(e -> {
                            Toast.makeText(UpdateAppActivity.this, "添加时间段失败！", Toast.LENGTH_SHORT).show();
                            //保留弹窗？
                            return null;
                        });
                    }
                })
                .setNegativeButton("取消", null)
                .create()
                .show();
    }

    private void closeKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        View currentFocus = getCurrentFocus();
        if (currentFocus != null) {
            inputMethodManager.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
        }
    }

}
