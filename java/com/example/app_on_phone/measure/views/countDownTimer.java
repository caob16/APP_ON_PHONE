package com.example.app_on_phone.measure.views;

import android.graphics.Color;
import android.util.Log;
import android.widget.TextView;

import devlight.io.library.ArcProgressStackView;

public class countDownTimer {

    private android.os.CountDownTimer mCountDownTimer;
    private TextView timerTextView;
    private ArcProgressStackView apsv;
    private boolean mTimerRunning = false;
    private long mTimeLeftInMillis;
    private long mStartTimeInMillis;
    private float percentage;

    private OnCountDownFinishListener mListener;

    public countDownTimer(long startTimeInMillis, TextView timerTextView, ArcProgressStackView apsv, OnCountDownFinishListener listener) {
        mStartTimeInMillis = startTimeInMillis;
        mTimeLeftInMillis = startTimeInMillis;
        this.timerTextView = timerTextView;
        this.apsv = apsv;
        this.mListener = listener;
    }

    public void start() {
        mCountDownTimer = new android.os.CountDownTimer(mTimeLeftInMillis, 50) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                int seconds = (int) (mTimeLeftInMillis / 1000) % 60;
                int minutes = (int) ((mTimeLeftInMillis / (1000 * 60)) % 60);
                int hours = (int) ((mTimeLeftInMillis / (1000 * 60 * 60)) % 24);
                timerTextView.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
                percentage = (((float) mTimeLeftInMillis) / ((float) mStartTimeInMillis)) * 100;
                apsv.setTextColor(Color.parseColor("#FF000000"));
                apsv.getModels().get(0).setProgress(percentage);
            }

            @Override
            public void onFinish() {
                apsv.setTextColor(Color.parseColor("#FF000000"));
                apsv.getModels().get(0).setProgress(0);
                timerTextView.setText("00:00:00");
                mTimerRunning = false;
                if (mListener != null) {
                    mListener.onCountDownFinished();
                }
            }
        }.start();

        mTimerRunning = true;
    }

    public void pause() {
        mCountDownTimer.cancel();
        mTimerRunning = false;
    }

    public void reset() {
        mTimeLeftInMillis = mStartTimeInMillis;
        int seconds = (int) (mTimeLeftInMillis / 1000) % 60;
        int minutes = (int) ((mTimeLeftInMillis / (1000 * 60)) % 60);
        int hours = (int) ((mTimeLeftInMillis / (1000 * 60 * 60)) % 24);
        timerTextView.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
    }

    public boolean isRunning() {
        return mTimerRunning;
    }

    public long getTimeLeftInMillis() {
        return mTimeLeftInMillis;
    }

    public void setTimeLeftInMillis(long timeLeftInMillis) {
        mTimeLeftInMillis = timeLeftInMillis;
    }

    public void setStartTimeInMillis(long startTimeInMillis) {
        mStartTimeInMillis = startTimeInMillis;
    }

    public interface OnCountDownFinishListener {
        void onCountDownFinished();
    }
}