package cn.fengmang.libui;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;


/**
 * Created by Administrator on 2018/6/14.
 */

@SuppressLint("AppCompatCustomView")
public class FMProgressBar extends ImageView {

    public static final int STATE_PLAYING = 1;//正在播放
    public static final int STATE_PAUSE = 2;//暂停
    public static final int STATE_STOP = 3;//停止
    private int state = STATE_STOP;

    private ObjectAnimator objectAnimator;//添加旋转动画，旋转中心默认为控件中点

    public FMProgressBar(Context context) {
        this(context,null);
    }

    public FMProgressBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public FMProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        objectAnimator = ObjectAnimator.ofFloat(this, "rotation", 0f, 360f);
        objectAnimator.setDuration(2000);//设置动画时间
        objectAnimator.setInterpolator(new AccelerateDecelerateInterpolator());//动画时间线性渐变
        objectAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        objectAnimator.setRepeatMode(ObjectAnimator.RESTART);
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        if (objectAnimator == null) {
            init();
        }
        if (visibility == View.VISIBLE) {
            objectAnimator.start();//动画开始
            state = STATE_PLAYING;
        } else {
            objectAnimator.end();//动画结束
            state = STATE_STOP;
        }
    }

    public void start() {
        objectAnimator.start();//动画开始
        state = STATE_PLAYING;
    }

    public void stop() {
        objectAnimator.end();//动画结束
        state = STATE_STOP;
    }
}
