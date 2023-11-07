package com.example.app_on_phone.signin.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.IBinder;
import android.os.Process;
import android.util.Log;

import androidx.annotation.Nullable;


import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.ck.rtcckinfo.rtc.AvEngine;
import com.ck.rtcckinfo.websocket.WebOkhttpSerVer;
import com.ck.rtcckinfo.websocket.entity.Heart;
import com.ck.rtcckinfo.websocket.entity.HeartLocation;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 高德地圖
 */

/**
 * 手表心率服务
 */

public class HeartAmapService extends Service implements SensorEventListener {
    private static final String TAG = "HeartAmapService";
    //线程池
    private ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(1);
    private Sensor mSensorAccelerometer;
    private int step = 0;   //步数
    private double oriValue = 0;  //原始值
    private double lstValue = 0;  //上次的值
    private double curValue = 0;  //当前值
    private boolean motiveState = true;   //是否处于运动状态
    private boolean processState = true;   //标记当前是否已经在计步
    private static int curStep;
    private static int lastStep;
    private SensorManager sm;
    private int initgdgps = 0;
    @Override
    public void onSensorChanged(android.hardware.SensorEvent event) {
        //设定一个精度范围
        double range = 1;
        float[] value = event.values;

        //计算当前的模
        curValue = magnitude(value[0], value[1], value[2]);

        //向上加速的状态
        if (motiveState == true) {
            if (curValue >= lstValue) lstValue = curValue;
            else {
                //检测到一次峰值
                if (Math.abs(curValue - lstValue) > range) {
                    oriValue = curValue;
                    motiveState = false;
                }
            }
        }
        //向下加速的状态
        if (motiveState == false) {
            if (curValue <= lstValue) lstValue = curValue;
            else {
                if (Math.abs(curValue - lstValue) > range) {
                    //检测到一次峰值
                    oriValue = curValue;
                    if (processState == true) {
                        step++;  //步数 + 1
                        if (processState == true) {
                            //ms_step.setText(step + "");    //读数更新
                            curStep = step;
                        }
                    }
                    motiveState = true;
                }
            }
        }
    }

    //向量求模
    public double magnitude(float x, float y, float z) {
        double magnitude = 0;
        magnitude = Math.sqrt(x * x + y * y + z * z);
        return magnitude;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Nullable
    @Override

    public IBinder onBind(Intent intent) {
        return null;
    }

    private NotificationManager manager;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("HeartAmapService", "HeartAmapName", NotificationManager.IMPORTANCE_LOW);
            manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            manager.createNotificationChannel(channel);
            Notification notification = new Notification.Builder(this, "HeartAmapService").build();
            startForeground(Process.myPid(), notification);
        }
        Integer time ;
        try {
            time = Integer.valueOf(intent.getStringExtra("time"));
        } catch (Exception e) {
            e.printStackTrace();
            time=15;
        }

        exec.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                Log.e(TAG, "差值:== "+(curStep - lastStep) );

//                if (initgdgps <4) {
//                    initgdgps++;
//                    getLocation();
//                }
//                if (curStep - lastStep > 15) {
//                    getLocation();
//                }
                getLocation();
                lastStep = curStep;
                Log.e(TAG, "curStep=" + curStep + "lastStep=" + lastStep);

            }
        }, 0, time, TimeUnit.SECONDS);

        getLocation();
        sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensorAccelerometer = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sm.registerListener(this, mSensorAccelerometer, SensorManager.SENSOR_DELAY_UI);
        return super.onStartCommand(intent, flags, startId);
    }


    public AMapLocationClient mLocationClient = null;
    //声明定位回调监听器
    public AMapLocationListener mLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {

            if (aMapLocation != null) {
                if (aMapLocation.getErrorCode() == 0) {
                    //可在其中解析amapLocation获取相应内容。
                    try {
                        AvEngine.ParamInfo.setAddress(aMapLocation.getAddress().substring(aMapLocation.getAddress().lastIndexOf("省") + 1));
                        updatePOI(aMapLocation.getLatitude(), aMapLocation.getLongitude(), 0,
                                aMapLocation.getSpeed(), aMapLocation.getBearing());
                    } catch (Exception e) {
                        Log.e(TAG, ": " + e.getMessage());
                    }
                } else {
                    try {
                        //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                        updatePOI(0, 0, 0,
                                0, 0);
                        Log.e(TAG, " 定位失败时，可通过ErrCode（错误码"+aMapLocation.getErrorCode()+"）信息来确定失败的原因，errInfo是错误信息，详见错误码表。");
                    } catch (Exception e) {
                        Log.e(TAG, ": " + e.getMessage());
                        ;
                    }
                }
            }
        }
    };
    //声明AMapLocationClientOption对象
    public AMapLocationClientOption mLocationOption = null;

    public void getLocation() {
        if (mLocationClient == null) {
            //初始化定位
            mLocationClient = new AMapLocationClient(getApplicationContext());
        }
        //设置定位回调监听
        mLocationClient.setLocationListener(mLocationListener);
        //初始化AMapLocationClientOption对象
        mLocationOption = new AMapLocationClientOption();
        /**
         * 设置定位场景，目前支持三种场景（签到、出行、运动，默认无场景）
         */
        //不允许模拟定位
        mLocationOption.setMockEnable(false);
        mLocationOption.setLocationPurpose(AMapLocationClientOption.AMapLocationPurpose.Sport);
        if (null != mLocationClient) {
            mLocationClient.setLocationOption(mLocationOption);
            //设置场景模式后最好调用一次stop，再调用start以保证场景模式生效
            mLocationClient.stopLocation();
            mLocationClient.startLocation();
            //设置定位间隔,单位毫秒,默认为2000ms，最低1000ms。
            mLocationOption.setInterval(5000);
        }
        //设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();

    }

    @Override
    public void onDestroy() {
        try {
            if (mLocationClient != null) {
                mLocationClient.stopLocation();//停止定位后，本地定位服务并不会被销毁
                mLocationClient.onDestroy();
            }
            exec.shutdown();
            super.onDestroy();
        } catch (Throwable e) {

        } finally {
            if (AvEngine.ParamInfo.getInitWs()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    this.startForegroundService(new Intent(this, HeartAmapService.class));
                    manager.cancelAll();
                } else {
                    this.startService(new Intent(this, HeartAmapService.class));
                }
            }
        }
    }

    private void updatePOI(double Lat, double Lon, float battery, double speed, float direction) {
        Log.e(TAG, "Lat=" + Lat + "," + "Lon=" + Lon + "," + "battery=" + direction + "," + "speed=" + speed + "," + "direction=" + direction);
        WebOkhttpSerVer webOkhttpSerVer = new WebOkhttpSerVer();
        webOkhttpSerVer.setHeart(new Heart() {
            @Override
            public HeartLocation getLoaction() {
                //  Log.e(TAG, "每5秒钟发一个心跳");
                HeartLocation location = null;
                try {
                    if (mLocationClient != null) {
                        //停止服务
                        mLocationClient.stopLocation();
                    }
//                    LatLonPoint latLonPoint = Gstogps.toGPSPoint(Lat, Lon);
//                    String lat = latLonPoint.getLatitude() <= 0 ? "0" : latLonPoint.getLatitude() + "";
//                    String lon = latLonPoint.getLongitude() <= 0 ? "0" : latLonPoint.getLongitude() + "";
                    AvEngine.ParamInfo.setLat(Lat+""); //
                    AvEngine.ParamInfo.setLng(Lon+"");
                    //TODO  需要自己实现地图定位信息
                    location = new HeartLocation();
                    location.Lat =Lat;
                    location.Lon = Lon;
                    location.battery = battery;
                    location.speed = speed;
                    location.direction = direction;
                    location.map_type = "2";
                } catch (Throwable e) {
                    Log.e(TAG, e.getMessage());
                }
                return location;
            }


        });
    }


}
