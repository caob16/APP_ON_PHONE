package com.example.app_on_phone.timetable.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.app_on_phone.R;
import com.example.app_on_phone.timetable.view.TimeTableView;
import com.example.app_on_phone.utils.ApiService;

import java.util.Date;

import androidx.appcompat.app.AppCompatActivity;

public class TimeActivity extends AppCompatActivity {

    private TimeTableView timeTable;
    private SharedPreferences sp;
    private ApiService apiService = new ApiService();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time);

        sp = getSharedPreferences("config", MODE_PRIVATE);
        timeTable = findViewById(R.id.timeTable);
        timeTable.addListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoryListener();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        //获取开学时间
        long date = sp.getLong("date", new Date().getTime());
        apiService.getCoursesList("1").thenAccept(courses -> {
            TimeActivity.this.runOnUiThread(() -> {
                timeTable.loadData(courses);
                Toast.makeText(TimeActivity.this, "获取课程成功", Toast.LENGTH_SHORT).show();
            });
        }).exceptionally(e -> {
            System.out.println("An error occurred: " + e.getCause());
            return null;
        });
        //timeTable.loadData(acquireData(), new Date(date));
    }
    /**
     * 菜单
     */
    public void categoryListener() {
        Intent intent = new Intent(this, OptionActivity.class);
        startActivity(intent);
    }

}
