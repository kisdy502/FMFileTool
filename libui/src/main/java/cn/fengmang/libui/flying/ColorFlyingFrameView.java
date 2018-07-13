package cn.fengmang.libui.flying;

import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Region;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.ViewGroup;

import java.util.List;

import cn.fengmang.baselib.ELog;
import cn.fengmang.libui.R;


/**
 * Created by Administrator on 2018/7/10.
 */

public class ColorFlyingFrameView extends BaseFlyingFrameView {
    //阴影
    private Paint mShadowPaint;
    private int mShadowColor;
    private float mShadowWidth;
    //边框
    private float mBorderWidth;
    private float mRoundRadius;
    private int mBorderColor;
    private Paint mBorderPaint;

    public ColorFlyingFrameView(Context context) {
        super(context);
        init();
    }

    public ColorFlyingFrameView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ColorFlyingFrameView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ColorFlyingFrameView);
        mBorderWidth = a.getDimensionPixelOffset(R.styleable.ColorFlyingFrameView_flyingBorderWidth, 1);
        mBorderColor = a.getColor(R.styleable.ColorFlyingFrameView_flyingBorderColor, 0xFF3FBA91);
        mShadowColor = a.getColor(R.styleable.ColorFlyingFrameView_flyingShadowColor, 0xFF00FF00);
        mShadowWidth = a.getDimensionPixelOffset(R.styleable.ColorFlyingFrameView_flyingShadWidth, 0);
        mRoundRadius = a.getDimensionPixelOffset(R.styleable.ColorFlyingFrameView_flyingRadius, 0);
        a.recycle();
        init();
    }

    private void init() {
        final float padding = mShadowWidth + mBorderWidth;
        mPaddingRectF.set(padding, padding, padding, padding);

        mBorderPaint = new Paint();
//        mBorderPaint.setAntiAlias(true);
//        mBorderPaint.setDither(true);
        mBorderPaint.setColor(mBorderColor);
        mBorderPaint.setStrokeWidth(mBorderWidth);
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setMaskFilter(new BlurMaskFilter(0.5f, BlurMaskFilter.Blur.NORMAL));


        mShadowPaint = new Paint();
        mShadowPaint.setColor(mShadowColor);
//        mShadowPaint.setAntiAlias(true); //抗锯齿功能，会消耗较大资源，绘制图形速度会变慢
//        mShadowPaint.setDither(true);    //抖动处理，会使绘制出来的图片颜色更加平滑和饱满，图像更加清晰
        mShadowPaint.setMaskFilter(new BlurMaskFilter(mShadowWidth, BlurMaskFilter.Blur.OUTER));
    }

    @Override
    List<Animator> getTogetherAnimators(int newX, int newY, int newWidth, int newHeight, float scaleX, float scaleY) {
        return null;
    }

    @Override
    List<Animator> getSequentiallyAnimators(int newX, int newY, int newWidth, int newHeight, float scaleX, float scaleY) {
        return null;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        drawShadow(canvas);
        drawBroader(canvas);
        super.onDraw(canvas);

    }

    /**
     * 绘制外发光阴影
     *
     * @param canvas
     */
    private void drawShadow(Canvas canvas) {
        if (mShadowWidth > 0) {
            canvas.save();
            //裁剪处理(使阴影矩形框内变为透明)
            if (mRoundRadius > 0) {
                canvas.clipRect(0, 0, getWidth(), getHeight());
                mTempRectF.set(mFrameRectF);
                mTempRectF.inset(mRoundRadius / 2f, mRoundRadius / 2f);
                canvas.clipRect(mTempRectF, Region.Op.DIFFERENCE);
            }
            //绘制外发光阴影效果
            if (mRoundRadius == 0f) {
                canvas.drawRect(mFrameRectF, mShadowPaint);
            } else {
                canvas.drawRoundRect(mFrameRectF, mRoundRadius, mRoundRadius, mShadowPaint);
            }
            canvas.restore();
        }
    }

    private void drawBroader(Canvas canvas) {
        if (mBorderWidth > 0) {
            ELog.d("drawBroader:" + mFrameRectF.toString());
            canvas.save();
            mTempRectF.set(mFrameRectF);
            if (mRoundRadius == 0f) {
                canvas.drawRect(mTempRectF, mShadowPaint);
            } else {
                canvas.drawRoundRect(mTempRectF, mRoundRadius, mRoundRadius, mBorderPaint);
            }
            canvas.restore();
        }
    }

    public static BaseFlyingFrameView build(Activity activity) {
        if (null == activity) {
            throw new NullPointerException("The activity cannot be null");
        }
        final ViewGroup parent = activity.findViewById(android.R.id.content);
        return build(parent);
    }

    public static BaseFlyingFrameView build(ViewGroup parent) {
        if (null == parent) {
            throw new NullPointerException("The FlowView parent cannot be null");
        }
        ColorFlyingFrameView flowView = new ColorFlyingFrameView(parent.getContext());
        final ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        parent.addView(flowView, lp);
        return flowView;
    }


    public void setShadowColor(int mShadowColor) {
        this.mShadowColor = mShadowColor;
    }

    public void setShadowWidth(float mShadowWidth) {
        this.mShadowWidth = mShadowWidth;
    }

    public void setBorderWidth(float mBorderWidth) {
        this.mBorderWidth = mBorderWidth;
    }

    public void setBorderColor(int mBorderColor) {
        this.mBorderColor = mBorderColor;
    }

    protected void setRoundRadius(float roundRadius) {
        if (mRoundRadius != roundRadius) {
            mRoundRadius = roundRadius;
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }
}
