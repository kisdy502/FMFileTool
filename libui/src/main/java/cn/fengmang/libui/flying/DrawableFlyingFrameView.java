package cn.fengmang.libui.flying;

import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.ViewGroup;

import java.util.List;

import cn.fengmang.libui.R;


/**
 * Created by Administrator on 2018/7/11.
 */

public class DrawableFlyingFrameView extends BaseFlyingFrameView {

    private Drawable mFlyingDrawable;

    public DrawableFlyingFrameView(Context context) {
        this(context, null);
    }

    public DrawableFlyingFrameView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DrawableFlyingFrameView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ColorFlyingFrameView);
        mFlyingDrawable = a.getDrawable(R.styleable.DrawableFlyingFrameView_flyingDrawable);
        a.recycle();
        init();
    }

    private void init() {
        if (mFlyingDrawable != null) {
            final Rect paddingRect = new Rect();
            mFlyingDrawable.getPadding(paddingRect);
            mPaddingRectF.set(paddingRect);
            if (Build.VERSION.SDK_INT >= 16) {
                setBackground(mFlyingDrawable);
            } else {
                setBackgroundDrawable(mFlyingDrawable);
            }
        }
    }

    @Override
    List<Animator> getTogetherAnimators(int newX, int newY, int newWidth, int newHeight, float scaleX, float scaleY) {
        return null;
    }

    @Override
    List<Animator> getSequentiallyAnimators(int newX, int newY, int newWidth, int newHeight, float scaleX, float scaleY) {
        return null;
    }

    public static DrawableFlyingFrameView build(Activity activity) {
        if (null == activity) {
            throw new NullPointerException("The activity cannot be null");
        }
        final ViewGroup parent = activity.findViewById(android.R.id.content);
        return build(parent);
    }


    public static DrawableFlyingFrameView build(ViewGroup viewGroup) {
        if (null == viewGroup) {
            throw new NullPointerException("The FocusBorder parent cannot be null");
        }
        DrawableFlyingFrameView flowView = new DrawableFlyingFrameView(viewGroup.getContext());
        final ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(1, 1);
        viewGroup.addView(flowView, lp);
        return flowView;
    }

    public void setFlyingDrawable(Drawable flyingDrawable) {
        this.mFlyingDrawable = flyingDrawable;
        init();
    }
}
