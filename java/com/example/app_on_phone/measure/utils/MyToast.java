package com.example.app_on_phone.measure.utils;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;


public class MyToast {
    private Context mContext = null;
    private Toast mToast = null;
    private Handler mHandler = null;
    private boolean isRunning = false;

    private Runnable mToastThread  = new Runnable() {
        @Override
        public void run() {
            if(isRunning){
                mToast.cancel();// 把最后一个线程的显示效果cancel掉，就一了百了了
                isRunning = false;
            }
            mToast.show();
            isRunning = true;
            mHandler.postDelayed(mToastThread, 3000);//每隔3秒显示一次，经测试，这个时间间隔效果是最好
        }
    };
    public MyToast(Context context){
        mContext = context;
        mHandler = new Handler(mContext.getMainLooper());
        mToast = Toast.makeText(mContext, "请坐直！", Toast.LENGTH_SHORT);
    }
    public void setText(String text){
        mToast.setText(text);
    }
    public void show(){
        mHandler.post(mToastThread);
    }
    public void cancel() {
        mHandler.removeCallbacks(mToastThread);//先把显示线程删除
        mToast.cancel();// 把最后一个线程的显示效果cancel掉，就一了百了了
    }
}
