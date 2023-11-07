package com.example.app_on_phone;

import android.app.Application;

import com.ck.rtcckinfo.rtc.AvEngine;

public class MyApplication extends Application {
    private static MyApplication app;
    public static MyApplication getInstance() {
        return app;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        AvEngine.init(this);//初始化 用户信息  需要手动给权限
        AvEngine.ParamInfo.setWsaddress("115.159.79.144");  //初始化IP
        AvEngine.ParamInfo.setWsport("8099");//初始化端口
    }
    public static MyApplication getAppContext() {
        return app;
    }
}
