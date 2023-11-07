package com.example.app_on_phone.measure.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;

import com.example.app_on_phone.R;
import com.shawnlin.numberpicker.NumberPicker;

import androidx.appcompat.app.AppCompatActivity;

public class NotificationsActivity extends AppCompatActivity {

    private SharedPreferences preferences;
    private Switch switchRad,switchVib,switchNoti,switchDelay,switchDeadalarm;
    private NumberPicker npAngle;
    private NumberPicker npInterval;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        switchRad = (Switch) findViewById(R.id.swt_sound);//音效
        switchVib = (Switch) findViewById(R.id.swt_vibrate);//震动
        switchNoti = (Switch) findViewById(R.id.swt_notif);//提示
        switchDelay = (Switch) findViewById(R.id.swt_delay);//延时
        switchDeadalarm = (Switch) findViewById(R.id.swt_deadalarm);//久坐
        npAngle = findViewById(R.id.np_Angle);
        npInterval = findViewById(R.id.np_Interval);
        npAngle.setMinValue(12);
        npAngle.setMaxValue(18);
        npAngle.setValue(15);
        npInterval.setMinValue(0);
        npInterval.setMaxValue(60);
        npInterval.setValue(5);
        findViewById(R.id.btn_save).setOnClickListener(onClickListener);//保存
        findViewById(R.id.btn_cancel).setOnClickListener(onClickListener);//取消
        findViewById(R.id.btn_reset).setOnClickListener(onClickListener);//重置

        preferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        if (preferences != null) {
            boolean stateRad = preferences.getBoolean("flagRad", false);
            boolean stateVib = preferences.getBoolean("flagVib",false);
            boolean stateNoti = preferences.getBoolean("flagNoti",true);
            boolean stateDelay = preferences.getBoolean("flagDelay",true);
            boolean stateDeadalarm = preferences.getBoolean("flagDeadalarm",true);
            int angle =  preferences.getInt("angle",15);
            int interval =  preferences.getInt("interval",5);
            switchRad.setChecked(stateRad);
            switchVib.setChecked(stateVib);
            switchNoti.setChecked(stateNoti);
            switchDelay.setChecked(stateDelay);
            switchDeadalarm.setChecked(stateDeadalarm);
            npAngle.setValue(angle);
            npInterval.setValue(interval);
        }
    }

    //按钮监听器
    View.OnClickListener onClickListener = new View.OnClickListener() {
        //返回主页面
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btn_cancel:
                    Intent cancel = new Intent();
                    setResult(RESULT_CANCELED, cancel);
                    finish();
                    break;
                case R.id.btn_save:
                    Intent confirm = new Intent();
                    confirm.putExtra("Rad",switchRad.isChecked());   //设置回传的值
                    confirm.putExtra("Vib",switchVib.isChecked());   //设置回传的值
                    confirm.putExtra("Noti",switchNoti.isChecked());   //设置回传的值
                    confirm.putExtra("Delay",switchDelay.isChecked());
                    confirm.putExtra("Deadalarm",switchDeadalarm.isChecked());
                    confirm.putExtra("Angle",npAngle.getValue());
                    confirm.putExtra("Interval",npInterval.getValue());
                    setResult(RESULT_OK, confirm);
                    //保存按钮状态
                    SharedPreferences preferences = getSharedPreferences("user", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean("flagRad", switchRad.isChecked());
                    editor.putBoolean("flagVib", switchVib.isChecked());
                    editor.putBoolean("flagNoti", switchNoti.isChecked());
                    editor.putBoolean("flagDelay",switchDelay.isChecked());
                    editor.putBoolean("flagDeadalarm",switchDeadalarm.isChecked());
                    editor.putInt("angle",npAngle.getValue());
                    editor.putInt("interval",npInterval.getValue());
                    editor.commit();
                    finish();
                    break;
                case R.id.btn_reset:
                    switchRad.setChecked(false);
                    switchVib.setChecked(false);
                    switchNoti.setChecked(true);
                    switchDelay.setChecked(true);
                    switchDeadalarm.setChecked(true);
                    npAngle.setValue(15);
                    npInterval.setValue(5);
            }
        }
    };

}
