package com.example.app_on_phone.measure.activity;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.app_on_phone.MainActivity;
import com.example.app_on_phone.R;
//import com.google.android.gms.common.api.GoogleApiClient;
//import com.google.android.gms.common.api.PendingResult;
//import com.google.android.gms.wearable.DataApi;
//import com.google.android.gms.wearable.DataEvent;
//import com.google.android.gms.wearable.DataEventBuffer;
//import com.google.android.gms.wearable.DataItem;
//import com.google.android.gms.wearable.DataMap;
//import com.google.android.gms.wearable.PutDataMapRequest;
//import com.google.android.gms.wearable.PutDataRequest;
//import com.google.android.gms.wearable.Wearable;
import com.example.app_on_phone.measure.utils.MyAlarmManager;
import com.example.app_on_phone.measure.views.CountDownAnimation;
import com.example.app_on_phone.measure.views.countDownTimer;
import com.example.app_on_phone.measure.views.CountUpTimer;
import com.example.app_on_phone.utils.ApiService;
import com.shawnlin.numberpicker.NumberPicker;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import androidx.appcompat.app.AppCompatActivity;

import devlight.io.library.ArcProgressStackView;

import static android.content.ContentValues.TAG;


public class MeasureActivity extends AppCompatActivity implements countDownTimer.OnCountDownFinishListener{

    private static final UUID MY_UUID = UUID.fromString("b2dd023e-0b70-11ee-be56-0242ac120002");

    private boolean getRad, getVib, getNoti, getDelay;
    ;
    private boolean countMode = true;
    //    private GoogleApiClient googleApiClient;//服务对象
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothServerSocket serverSocket;
    private BluetoothSocket socket;
    private BluetoothDevice device;
    private MyAlarmManager alarmManager;
    private MyAlarmManager.PlayerCompleteListener listener;
    private SharedPreferences preferences;
    private Toast mToast;
    private TextView countDown;
    private TextView instruction;
    private TextView timerView;
    private TextView modeView;
    private Button btnPause;

    private NumberPicker numberPickerSec;
    private NumberPicker numberPickerMin;
    private NumberPicker numberPickerHour;
    private CountDownAnimation countDownAnimation;
    private ArcProgressStackView apsv;
    private int startCount = 5;
    private int period, angle;
    private CountUpTimer countupTimer;
    private countDownTimer countdownTimer;
    private ArrayList<ArcProgressStackView.Model> models = new ArrayList<>();
    private MeasureActivity.ConnectedThread mConnectedThread;
    private OutputStream outputStream;
    private ApiService apiService = new ApiService();
    private int currentMeasureID = -1;
    private int currentAlarmID = -1;
    private Animation scaleAnimation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measure);
        //按钮id
        findViewById(R.id.btnEnd).setVisibility(View.INVISIBLE);
        findViewById(R.id.btnReset).setVisibility(View.INVISIBLE);
        findViewById(R.id.btnInitEnd).setVisibility(View.INVISIBLE);
        findViewById(R.id.btnPause).setVisibility(View.INVISIBLE);
        findViewById(R.id.countDownColon).setVisibility(View.INVISIBLE);
        findViewById(R.id.countDownColon2).setVisibility(View.INVISIBLE);
        timerView = (TextView) findViewById(R.id.timerView);
        countDown = (TextView) findViewById(R.id.countDown);
        instruction = (TextView) findViewById(R.id.Instruction);
        modeView = (TextView) findViewById(R.id.modeView);
        btnPause = (Button) findViewById(R.id.btnPause);
        apsv = (ArcProgressStackView) findViewById(R.id.apsv);
        numberPickerSec = findViewById(R.id.number_pickerSecond);
        numberPickerMin = findViewById(R.id.number_pickerMinute);
        numberPickerHour = findViewById(R.id.number_pickerHour);
        countDown.setVisibility(View.INVISIBLE);
        numberPickerSec.setVisibility(View.INVISIBLE);
        numberPickerMin.setVisibility(View.INVISIBLE);
        numberPickerHour.setVisibility(View.INVISIBLE);
        apsv.setVisibility(View.INVISIBLE);

        //Toast线程
        mToast = Toast.makeText(getApplicationContext(), "请坐直！", Toast.LENGTH_SHORT);
        //从preference中取值
        preferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        getRad = preferences.getBoolean("flagRad", false);
        getVib = preferences.getBoolean("flagVib", false);
        getNoti = preferences.getBoolean("flagNoti", false);
        getDelay = preferences.getBoolean("flagDelay",true);

        countupTimer = new CountUpTimer(timerView);
        countDownAnimation = new CountDownAnimation(countDown, startCount);

        scaleAnimation = new ScaleAnimation(1.0f, 0.0f, 1.0f, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        //创建服务对象
        try {
            serverSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord("MyApp", MY_UUID);
        } catch (IOException e) {
            Log.e(TAG, "listen() failed", e);
            e.printStackTrace();
        }

        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            // Assume the server device is the first device in the paired devices list
            device = (BluetoothDevice) pairedDevices.toArray()[0];
        }

        models.add(new ArcProgressStackView.Model("", 100, Color.parseColor("#FFB8B8B8"), Color.parseColor("#FF000000")));
//        models.add(new ArcProgressStackView.Model("Progress", 50, Color.parseColor(bgColors[1]), mStartColors[1]));#FFB8B8B8
//        models.add(new ArcProgressStackView.Model("Stack", 75, Color.parseColor(bgColors[2]), mStartColors[2]));#FF000000
//        models.add(new ArcProgressStackView.Model("View", 100,Color.parseColor(bgColors[3]), mStartColors[3]));
        apsv.setModels(models);
    }

    @Override
    protected void onStart() {
        super.onStart();
        countDownAnimation.setAnimation(scaleAnimation);

        timerView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do something before the text is changed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(countupTimer.getHours()!=0&&countupTimer.getMinutes()==0&&countupTimer.getSeconds()==0){
                    sendSimpleNotify("已经坐直小时了！","休息一下吧！");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Do something after the text has been changed
            }
        });
        //countdownTimer = new countDownTimer(10000, timerView);

        // Use scale animation
        countDownAnimation.setCountDownListener(new CountDownAnimation.CountDownListener() {
            @Override
            public void onCountDownEnd(CountDownAnimation animation) {
                sendStart();
                LoadMeasure();
                if(countMode){
                    countupTimer.start();
                }else{
                    countdownTimer = new countDownTimer(getCountDownTime(), timerView, apsv,MeasureActivity.this);
                    countdownTimer.start();
                }
            }
        });

    }

    public void sendInitStart(View v){
        if(socket == null){
            try {
                socket = device.createRfcommSocketToServiceRecord(MY_UUID);
                socket.connect();
                outputStream = socket.getOutputStream();
            } catch (IOException e) {
                Toast.makeText(this, "请打开手表端的测姿态应用！", Toast.LENGTH_SHORT).show();
                return;
//                throw new RuntimeException(e);
            }
            mConnectedThread = new MeasureActivity.ConnectedThread();
            Thread bluetoothThread = new Thread(mConnectedThread);
            bluetoothThread.start();
        }
        //sendSettings();
        String alarmMessage = "InitStart";
        try {
            if(outputStream == null){
                outputStream = socket.getOutputStream();
            }
            outputStream.write(alarmMessage.getBytes());
        } catch (IOException e) {
            Log.e(TAG, "Error occurred when sending data", e);
            Toast.makeText(this, "请打开手表端的测姿态应用！", Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(this, "初始化开始！", Toast.LENGTH_SHORT).show();
        countDownAnimation.start();
        LoadInit();
    }

    public void sendReset(View view){
        apiMeasureEnd(currentMeasureID);
        LoadInit();
        countDownAnimation.start();

        String alarmMessage = "InitStart";
        try {
            outputStream.write(alarmMessage.getBytes());
        } catch (IOException e) {
            Log.e(TAG, "Error occurred when sending data", e);
        }
        Toast.makeText(this, "已重置！", Toast.LENGTH_SHORT).show();
    }

    public void sendEnd(View v){
        LoadMain();
        if(countMode){
            if(countupTimer.getPause()){
                countupTimer.stop();
                btnPause.setText("暂停");
            }
            countupTimer.reset();
        }else{
            if(countupTimer.getPause()){
                countdownTimer.pause();
                btnPause.setText("暂停");
            }
            countdownTimer.reset();
        }
        String alarmMessage = "END";
        try {
            outputStream.write(alarmMessage.getBytes());
        } catch (IOException e) {
            Log.e(TAG, "Error occurred when sending data", e);
        }

        apiMeasureEnd(currentMeasureID);
        cancelNotify(R.string.app_name);
        Toast.makeText(this, "结束！", Toast.LENGTH_SHORT).show();
    }

    public void sendPause(View v){
        if(countMode){
            if(countupTimer.getPause()){
                countupTimer.start();
                btnPause.setText("暂停");

                String alarmMessage = "Resume";
                try {
                    outputStream.write(alarmMessage.getBytes());
                } catch (IOException e) {
                    Log.e(TAG, "Error occurred when sending data", e);
                }

                apiMeasureStart("1");
                /***************************
                 * 添加api调用
                 * ****************************/
                Toast.makeText(this, "继续！", Toast.LENGTH_SHORT).show();
            }else{
                countupTimer.stop();
                btnPause.setText("继续");

                String alarmMessage = "Pause";
                try {
                    outputStream.write(alarmMessage.getBytes());
                } catch (IOException e) {
                    Log.e(TAG, "Error occurred when sending data", e);
                }
                apiMeasureEnd(currentMeasureID);
                /***************************
                 * 添加api调用
                 * ****************************/
                Toast.makeText(this, "暂停！", Toast.LENGTH_SHORT).show();
            }
        }else{
            if(countdownTimer.isRunning()){
                countdownTimer.pause();
                btnPause.setText("继续");
                String alarmMessage = "Pause";
                try {
                    outputStream.write(alarmMessage.getBytes());
                } catch (IOException e) {
                    Log.e(TAG, "Error occurred when sending data", e);
                }
                apiMeasureEnd(currentMeasureID);
                /***************************
                 * 添加api调用
                 * ****************************/
                Toast.makeText(this, "暂停！", Toast.LENGTH_SHORT).show();
            }else{
                countdownTimer.start();
                btnPause.setText("暂停");
                String alarmMessage = "Resume";
                try {
                    outputStream.write(alarmMessage.getBytes());
                } catch (IOException e) {
                    Log.e(TAG, "Error occurred when sending data", e);
                }
                apiMeasureStart("1");
                /***************************
                 * 添加api调用
                 * ****************************/
                Toast.makeText(this, "继续！", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void sendInitStop(View v){
        countDownAnimation.cancel();
        LoadMain();

        String alarmMessage = "InitStop";
        try {
            outputStream.write(alarmMessage.getBytes());
        } catch (IOException e) {
            Log.e(TAG, "Error occurred when sending data", e);
        }

        Toast.makeText(this, "返回！", Toast.LENGTH_SHORT).show();
    }

    public void modeSwitch(View v){
        if(countMode){
            countMode = false;
            modeView.setText("倒计时");
            //timerView.setText("00:00:10");
        }else{
            countMode = true;
            modeView.setText("正计时");
            timerView.setText("00:00:00");
        }
        LoadCounter();
    }

    public void sendStart(){
        String alarmMessage = "START";
        try {
            outputStream.write(alarmMessage.getBytes());
        } catch (IOException e) {
            Log.e(TAG, "Error occurred when sending data", e);
        }
        apiMeasureStart("1");
        Toast.makeText(this, "开始！", Toast.LENGTH_SHORT).show();
    }

    public void sendSettings(){
        /***************************
         * 改成蓝牙通信！！！！！！！！！！！！！！！！！！！！！
         * ****************************/
        String alarmMessage = "";
        if(getDelay){
            alarmMessage="Settings;"+"5;"+String.valueOf(angle)+";"+String.valueOf(period);
        } else{
            alarmMessage="Settings;"+"0;"+String.valueOf(angle)+";"+String.valueOf(period);
        }
        try {
            outputStream.write(alarmMessage.getBytes());
        } catch (IOException e) {
            Log.e(TAG, "Error occurred when sending data", e);
        }
    }

    public void LoadInit(){
        instruction.setText("初始化中...");
        findViewById(R.id.btnStart).setVisibility(View.INVISIBLE);
        findViewById(R.id.btnReset).setVisibility(View.INVISIBLE);
        findViewById(R.id.btnConnect).setVisibility(View.INVISIBLE);
        findViewById(R.id.btnSetting).setVisibility(View.INVISIBLE);
        findViewById(R.id.btnEnd).setVisibility(View.INVISIBLE);
        findViewById(R.id.btnInitEnd).setVisibility(View.VISIBLE);
        findViewById(R.id.btnPause).setVisibility(View.INVISIBLE);
        findViewById(R.id.btnMode).setVisibility(View.INVISIBLE);
        findViewById(R.id.timerView).setVisibility(View.INVISIBLE);
        findViewById(R.id.modeView2).setVisibility(View.INVISIBLE);
        findViewById(R.id.modeView).setVisibility(View.INVISIBLE);
        numberPickerSec.setVisibility(View.INVISIBLE);
        numberPickerMin.setVisibility(View.INVISIBLE);
        numberPickerHour.setVisibility(View.INVISIBLE);
        findViewById(R.id.countDownColon).setVisibility(View.INVISIBLE);
        findViewById(R.id.countDownColon2).setVisibility(View.INVISIBLE);
    }

    public void LoadMain(){
        instruction.setText("请保持正确坐姿");
        findViewById(R.id.btnStart).setVisibility(View.VISIBLE);
        findViewById(R.id.btnReset).setVisibility(View.INVISIBLE);
        findViewById(R.id.btnConnect).setVisibility(View.VISIBLE);
        findViewById(R.id.btnSetting).setVisibility(View.VISIBLE);
        findViewById(R.id.btnInitEnd).setVisibility(View.INVISIBLE);
        findViewById(R.id.btnEnd).setVisibility(View.INVISIBLE);
        findViewById(R.id.btnPause).setVisibility(View.INVISIBLE);
        findViewById(R.id.btnMode).setVisibility(View.VISIBLE);
        findViewById(R.id.modeView2).setVisibility(View.VISIBLE);
        findViewById(R.id.modeView).setVisibility(View.VISIBLE);
        LoadCounter();
    }

    public void LoadMeasure(){
        Toast.makeText(this, "初始化完成！", Toast.LENGTH_SHORT).show();
        instruction.setText("测姿中");
        findViewById(R.id.btnReset).setVisibility(View.VISIBLE);
        findViewById(R.id.btnSetting).setVisibility(View.VISIBLE);
        findViewById(R.id.btnEnd).setVisibility(View.VISIBLE);
        findViewById(R.id.btnPause).setVisibility(View.VISIBLE);
        findViewById(R.id.btnInitEnd).setVisibility(View.INVISIBLE);
        findViewById(R.id.timerView).setVisibility(View.VISIBLE);
        numberPickerSec.setVisibility(View.INVISIBLE);
        numberPickerMin.setVisibility(View.INVISIBLE);
        numberPickerHour.setVisibility(View.INVISIBLE);
        findViewById(R.id.countDownColon).setVisibility(View.INVISIBLE);
        findViewById(R.id.countDownColon2).setVisibility(View.INVISIBLE);
        if(!countMode){
            apsv.setVisibility(View.VISIBLE);
        }
    }

    public void LoadCounter(){
        apsv.setVisibility(View.INVISIBLE);
        if(countMode){
            findViewById(R.id.timerView).setVisibility(View.VISIBLE);
            numberPickerSec.setVisibility(View.INVISIBLE);
            numberPickerMin.setVisibility(View.INVISIBLE);
            numberPickerHour.setVisibility(View.INVISIBLE);
            findViewById(R.id.countDownColon).setVisibility(View.INVISIBLE);
            findViewById(R.id.countDownColon2).setVisibility(View.INVISIBLE);
        }else{
            numberPickerSec.setVisibility(View.VISIBLE);
            numberPickerMin.setVisibility(View.VISIBLE);
            numberPickerHour.setVisibility(View.VISIBLE);
            findViewById(R.id.timerView).setVisibility(View.INVISIBLE);
            findViewById(R.id.countDownColon).setVisibility(View.VISIBLE);
            findViewById(R.id.countDownColon2).setVisibility(View.VISIBLE);
        }
    }

    public Long getCountDownTime(){
        Long countDownTime = Long.valueOf(numberPickerHour.getValue()*1000*3600+numberPickerMin.getValue()*1000*60+numberPickerSec.getValue()*1000);
        return countDownTime;
    }

    public void ConnectionCheck(View v){
        if(socket.isConnected()){
            Toast.makeText(this, "已连接！", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "未连接！", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendSimpleNotify(String title, String message) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // 创建通知渠道
            NotificationChannel channel = new NotificationChannel(
                    getString(R.string.app_name),
                    "Channel name",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription("Channel description");
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
        // 发送消息之前要先创建通知渠道，创建代码见MainApplication.java
        // 创建一个跳转到活动页面的意图
        Intent clickIntent = new Intent(this, MeasureActivity.class);
        clickIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        // 创建一个用于页面跳转的延迟意图
        PendingIntent contentIntent = PendingIntent.getActivity(this,
                R.string.app_name, clickIntent, PendingIntent.FLAG_NO_CREATE);
        // 创建一个通知消息的建造器
        Notification.Builder builder = new Notification.Builder(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Android 8.0开始必须给每个通知分配对应的渠道
            builder = new Notification.Builder(this, getString(R.string.app_name));
        }
        builder.setContentIntent(contentIntent) // 设置内容的点击意图
                .setAutoCancel(true) // 点击通知栏后是否自动清除该通知
                .setSmallIcon(R.mipmap.ic_launcher) // 设置应用名称左边的小图标
                .setContentTitle(title) // 设置通知栏里面的标题文本
                .setContentText(message); // 设置通知栏里面的内容文本
        Notification notify = builder.build(); // 根据通知建造器构建一个通知对象
        // 从系统服务中获取通知管理器
        NotificationManager notifyMgr = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);
        // 使用通知管理器推送通知，然后在手机的通知栏就会看到该消息
        notifyMgr.notify(R.string.app_name, notify);
    }



    public void cancelNotify(int ID){
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(ID);
    }

    public void Setting(View v){
        Intent intent = new Intent(MeasureActivity.this,NotificationsActivity.class);
        startActivityForResult(intent,0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,  Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==RESULT_OK){//判断是不是setting界面传回来的
            getRad = data.getBooleanExtra("Rad",false);
            getVib = data.getBooleanExtra("Vib",false);
            getNoti = data.getBooleanExtra("Noti",false);
            getDelay = data.getBooleanExtra("Delay",true);
            angle = data.getIntExtra("Angle",15);
            period = data.getIntExtra("Interval",5);
        }
        //sendSettings();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != socket && socket.isConnected()) {
//            Wearable.DataApi.removeListener(googleApiClient,dataListener);
//            googleApiClient.disconnect();
            try {
                socket.close();
                serverSocket.close();
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            sendEnd(null);
            mConnectedThread.cancel();
        }
    }

    public void btnBackClick(View v){
        Intent intent = new Intent(MeasureActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }


    class ConnectedThread implements Runnable{
        private InputStream inputStream;

        private boolean shouldContinue = true;
        String receivedMessage = "";

        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;
            try {
                inputStream = socket.getInputStream();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            while (shouldContinue) {
                try {
                    bytes = inputStream.read(buffer);
                    receivedMessage = new String(buffer, 0, bytes);
                    Log.d(TAG, "Received message: " + receivedMessage);
                    // Handle received message
                    handleReceivedMessage(receivedMessage);
                } catch (IOException e) {
                    Log.d(TAG, "Input stream was disconnected", e);
                    cancel();
                    MeasureActivity.this.runOnUiThread(new Runnable() { public void run() {
                        LoadMain();
                        if(countMode){
                            if(countupTimer.getPause()){
                                countupTimer.stop();
                                btnPause.setText("暂停");
                            }
                            countupTimer.reset();
                        }else{
                            if(countupTimer.getPause()){
                                countdownTimer.pause();
                                btnPause.setText("暂停");
                            }
                            countdownTimer.reset();
                        }
                        apiMeasureEnd(currentMeasureID);
                        apiService.endAlarm(currentAlarmID).thenAccept(data -> {
                            if(data.equals("success")){
                                System.out.println("update success");
                            }else{
                                System.out.println("update failed");
                                MeasureActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(MeasureActivity.this, "网络异常！", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }).exceptionally(e -> {
                            System.out.println("An error occurred: " + e.getCause());
                            MeasureActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MeasureActivity.this, "网络异常！", Toast.LENGTH_SHORT).show();
                                }
                            });
                            return null;
                        });
                        cancelNotify(R.string.app_name);
                        currentAlarmID = -1;
                    } });
                }
            }
        }

        // Call this method from the main activity to shut down the connection.
        public void cancel() {
            try {
                shouldContinue = false;
                inputStream.close();
                outputStream.close();
                if(socket!=null){
                    socket.close();
                }
                socket = null;
            } catch (IOException e) {
                Log.e(TAG, "Could not close the connect socket", e);
            }
        }

        private void handleReceivedMessage(String message) {
            if ("ALARM".equals(message)) {
                mToast.show();
                cancelNotify(R.string.app_name);
                if(getVib) {
                    alarmManager.vibrate(getApplicationContext(),1500);
                }
                if(getRad) {
                    alarmManager.playBee(getApplicationContext(), listener);
                }
                if(getNoti){
                    sendSimpleNotify("请坐直！","请调整坐姿");
                    /***************************
                     * 添加api调用
                     * ****************************/
                }
                // Do something...
            } else if ("RestartInit".equals(message)) {
                MeasureActivity.this.runOnUiThread(new Runnable() { public void run() {
                    Toast.makeText(getApplicationContext(), "初始化失败!", Toast.LENGTH_SHORT);
                    Toast.makeText(getApplicationContext(), "请保持正确坐姿5秒!", Toast.LENGTH_SHORT);
                    countDownAnimation.cancel();
                    countDownAnimation.start();
                }});
            } else if("ALARMENDREPORT".equals(message)){
                apiService.endAlarm(currentAlarmID).thenAccept(data -> {
                    if(data.equals("success")){
                        System.out.println("update success");
                    }else{
                        System.out.println("update failed");
                        MeasureActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MeasureActivity.this, "网络异常！", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).exceptionally(e -> {
                    System.out.println("An error occurred: " + e.getCause());
                    MeasureActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MeasureActivity.this, "网络异常！", Toast.LENGTH_SHORT).show();
                        }
                    });
                    return null;
                });
                cancelNotify(R.string.app_name);
                currentAlarmID = -1;
            } else if("Report".equals(message.substring(0,6))){
                apiService.startAlarm("1",message.substring(6)).thenAccept(data -> {
                    if(data >-1){
                        currentAlarmID = data;
                        System.out.println("update success");
                    }else{
                        System.out.println("update failed");
                        MeasureActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MeasureActivity.this, "网络异常！", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).exceptionally(e -> {
                    System.out.println("An error occurred: " + e.getCause());
                    MeasureActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MeasureActivity.this, "网络异常！", Toast.LENGTH_SHORT).show();
                        }
                    });
                    return null;
                });
            }
            else {
                Log.d(TAG, "Unhandled message: " + message);
            }
        }
    }

    /***************************
     * 2023/07/15/zxy
     * ****************************/

    @Override
    public void onCountDownFinished() {
        sendEnd(null);
    }

    private void apiMeasureEnd(int id){
        apiService.endMeasure(id).thenAccept(message -> {
            if(message.equals("success")){
                System.out.println("update success");
            }else{
                System.out.println("update failed");
                MeasureActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MeasureActivity.this, "网络异常！", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).exceptionally(e -> {
            System.out.println("An error occurred: " + e.getCause());
            MeasureActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MeasureActivity.this, "网络异常！", Toast.LENGTH_SHORT).show();
                }
            });
            return null;
        });
        currentMeasureID=-1;
    }

    private void apiMeasureStart(String accID){
        apiService.startMeasure(accID).thenAccept(data -> {
            if(data >-1){
                currentMeasureID = data;
                System.out.println("update success");
            }else{
                System.out.println("update failed");
                MeasureActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MeasureActivity.this, "网络异常！", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).exceptionally(e -> {
            System.out.println("An error occurred: " + e.getCause());
            MeasureActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MeasureActivity.this, "网络异常！", Toast.LENGTH_SHORT).show();
                }
            });
            return null;
        });
    }

}
