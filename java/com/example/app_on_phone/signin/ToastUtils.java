package com.example.app_on_phone.signin;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.Toast;

import com.example.app_on_phone.MyApplication;


public class ToastUtils {

    public static void show(final String content) {
        if (!TextUtils.isEmpty(content)) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {

                @Override
                public void run() {
                    Toast.makeText(MyApplication.getInstance(), content, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
//  public static Boolean HANGUP=false;
}
