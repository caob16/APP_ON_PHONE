package com.example.app_on_phone.signin.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.BatteryManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

public class NetUtil {

    private static final String TAG = "NetUtil";

    //-50dBm   表示信号极强 说明手机可能距离基站比较近
    //-60dBm-70dBm 左右表示信号很强
    //-75dBm-85dBm 表示信号属于正常范围
    //-85dBm-95dBm 表示能基本维持手机 通话合上网
    //-100-110  表示手机信号比较弱
    ///-110 以下表示手机基本出于无服务状态


    //网络 中国移动
    //信号强度 -86dBm 54asu
    //移动网络类型LTE
    //MCC,MNC 46000


    /***
     * 电量百分比
     * @param context
     * @return
     */
    public static int batteryPropertyCapacity(Context context) {
        BatteryManager manager = (BatteryManager) context.getSystemService(context.BATTERY_SERVICE);
        int currentLevel = manager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
        return currentLevel;
    }

    @SuppressLint("MissingPermission")
    public static String deviceId(Context context) {
        TelephonyManager telephonemanage = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return telephonemanage.getDeviceId();
    }

    @SuppressLint("MissingPermission")
    public static String getDeviceId(Context context) {
        String deviceId = "";
        try {
            if (Build.VERSION.SDK_INT < 29) {
                TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                if (tm != null) {
                    if (TextUtils.isEmpty(tm.getDeviceId()) == false)
                        deviceId = tm.getDeviceId();
                    else
                        deviceId = Settings.Secure.getString(context.getApplicationContext().getContentResolver(),
                                Settings.Secure.ANDROID_ID);
                } else {
                    deviceId = Settings.Secure.getString(context.getApplicationContext().getContentResolver(),
                            Settings.Secure.ANDROID_ID);
                }
            } else {
                deviceId = Settings.Secure.getString(context.getApplicationContext().getContentResolver(),
                        Settings.Secure.ANDROID_ID);
            }
        } catch (Exception e) {
            e.printStackTrace();
          return   null;
        }
        return deviceId;
    }





}
