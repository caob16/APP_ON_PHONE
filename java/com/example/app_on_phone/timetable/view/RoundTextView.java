package com.example.app_on_phone.timetable.view;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;

import com.example.app_on_phone.R;

import androidx.annotation.ColorInt;
import androidx.appcompat.widget.AppCompatTextView;

public class RoundTextView extends AppCompatTextView {
    public int row = -1;
    public int col = -1;
    public boolean isColored = false;
    public RoundTextView(Context context) {
        super(context);
    }
    public RoundTextView(Context context, int radius, @ColorInt int bkColor){
        super(context);
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setCornerRadius(radius);
        gradientDrawable.setColor(bkColor);
        if(bkColor == getResources().getColor(R.color.blue)){
            isColored = true;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            setBackground(gradientDrawable);
        }
    }
}
