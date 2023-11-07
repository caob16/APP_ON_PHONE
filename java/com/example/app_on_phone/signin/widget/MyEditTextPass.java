package com.example.app_on_phone.signin.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.Animation;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatEditText;

import com.example.app_on_phone.R;


/**
 * @author gsn
 * @date 2020/5/20.
 * description：
 */
public class MyEditTextPass extends AppCompatEditText {
    TextView tv;
    /**
     * 删除按钮的引用
     */
    private Drawable mPassShowDrawable;

    public MyEditTextPass(Context context) {
        this(context, null);
    }

    public MyEditTextPass(Context context, AttributeSet attrs) {
        // 这里构造方法也很重要，不加这个很多属性不能再XML里面定义
        this(context, attrs, android.R.attr.editTextStyle);
    }

    public MyEditTextPass(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        this.setTransformationMethod(PasswordTransformationMethod.getInstance());
        // 获取EditText的DrawableRight,假如没有设置我们就使用默认的图片
        mPassShowDrawable = getCompoundDrawables()[2];
        if (mPassShowDrawable == null) {
            mPassShowDrawable = getResources().getDrawable(R.drawable.widget_ed_pass);
        }
        mPassShowDrawable.setBounds(0, 0, mPassShowDrawable.getIntrinsicWidth(),
                mPassShowDrawable.getIntrinsicHeight());
        setCompoundDrawables(getCompoundDrawables()[0],
                getCompoundDrawables()[1], mPassShowDrawable, getCompoundDrawables()[3]);
    }

    public void SetTextView(TextView tv) {
        this.tv = tv;
    }

    public void SetTextViewStatiu(Boolean selected) {
        setSelected(selected);
    }

    /**
     * 因为我们不能直接给EditText设置点击事件，所以我们用记住我们按下的位置来模拟点击事件 当我们按下的位置 在 EditText的宽度 -
     * 图标到控件右边的间距 - 图标的宽度 和 EditText的宽度 - 图标到控件右边的间距之间我们就算点击了图标，竖直方向没有考虑
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (getCompoundDrawables()[2] != null) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                boolean touchable = event.getX() > (getWidth()
                        - getPaddingRight() - mPassShowDrawable
                        .getIntrinsicWidth())
                        && (event.getX() < ((getWidth() - getPaddingRight())));
                if (touchable) {
                    if (getTransformationMethod().equals(PasswordTransformationMethod.getInstance())) {
                        SetTextViewStatiu(true);
                        this.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    } else {
                        SetTextViewStatiu(false);
                        this.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    }
                }
            }
        }

        return super.onTouchEvent(event);
    }

    /**
     * 设置清除图标的显示与隐藏，调用setCompoundDrawables为EditText绘制上去
     *
     * @param visible
     */
    protected void setClearIconVisible(boolean visible) {
        Drawable right = mPassShowDrawable;

    }

    /**
     * 当输入框里面内容发生变化的时候回调的方法
     */
    @Override
    public void onTextChanged(CharSequence s, int start, int count, int after) {
        setClearIconVisible(s.length() > 0);
    }

    /**
     * 设置晃动动画
     */
    public void setShakeAnimation() {
        this.setAnimation(shakeAnimation(5));
    }

    /**
     * 晃动动画
     *
     * @param counts 1秒钟晃动多少下
     * @return
     */
    public static Animation shakeAnimation(int counts) {
        Animation translateAnimation = new TranslateAnimation(0, 10, 0, 0);
        translateAnimation.setInterpolator(new CycleInterpolator(counts));
        translateAnimation.setDuration(1000);
        return translateAnimation;
    }

}

