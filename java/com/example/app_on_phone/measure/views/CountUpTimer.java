package com.example.app_on_phone.measure.views;

import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

public class CountUpTimer {

    private boolean isPaused = false;
    private long startTime;
    private TextView timerTextView;
    private long timeDifference = 0;
    private int hours,minutes,seconds;

    private Handler handler = new Handler();

    public CountUpTimer(TextView timerTextView){
        Log.e("CountUpTimer", "CountUpTimer!!!!");
        this.timerTextView = timerTextView;
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            long elapsedTime = System.currentTimeMillis() - startTime;
            seconds = (int) (elapsedTime / 1000) % 60;
            minutes = (int) ((elapsedTime / (1000 * 60)) % 60);
            hours = (int) ((elapsedTime / (1000 * 60 * 60)) % 24);
            timerTextView.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
            if (!isPaused) {
                handler.postDelayed(this, 1000);
            }
        }
    };

    public void start(){
        Log.e("start", "start!!!!");
        if(isPaused){
            startTime=System.currentTimeMillis()-timeDifference;
            isPaused=false;
            handler.postDelayed(runnable, 1000-timeDifference%1000);
        }else{
            timeDifference = 0;
            startTime = System.currentTimeMillis();
            handler.postDelayed(runnable, 0);
        }
    }

    public void stop(){
        Log.e("stop", "stop!!!!");
        timeDifference = System.currentTimeMillis() - startTime;
        isPaused = true;
    }

    public void reset(){
        timeDifference = 0;
        timerTextView.setText("00:00:00");
        handler.removeCallbacks(runnable);
    }

    public boolean getPause(){
        return isPaused;
    }
    public int getHours(){
        return hours;
    }
    public int getMinutes(){
        return minutes;
    }
    public int getSeconds(){
        return seconds;
    }

}
