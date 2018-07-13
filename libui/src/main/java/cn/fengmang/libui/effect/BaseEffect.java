package cn.fengmang.libui.effect;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;

/**
 * Created by SDT13411 on 2018/1/15.
 */

public abstract class BaseEffect {

    private Context mContext;
    public FocusFrameView mFocusFrameView;
    protected Rect mFocusFramePadding;
    protected Drawable mFocusDrawable;

    public void init(Context context) {
        mContext = context;
    }

    /**
     * 拿到焦点框引用
     *
     * @param focusFrameView 焦点框
     */
    public void setFocusFrameView(FocusFrameView focusFrameView) {
        mFocusFrameView = focusFrameView;
    }


    /**
     * 根据实际情况放大或者是缩小焦点框
     *
     * @param framePadding paddingLeft paddingTop paddingRight,paddingBottom
     */
    public void setFocusFramePadding(Rect framePadding) {
        this.mFocusFramePadding = framePadding;
    }

    /**
     * 设置显示的焦点框
     *
     * @param resource 资源id
     */
    public void setFocusResource(int resource) {
        mFocusDrawable = mContext.getResources().getDrawable(resource);
    }

    /**
     * 设置显示的焦点框
     *
     * @param focusDrawable drawable
     */
    public void setFocusDrawable(Drawable focusDrawable) {
        mFocusDrawable = focusDrawable;
    }


    public abstract void onUnFocusView(View view);

    /**
     * 焦点移动
     *
     * @param focusView 当前获取焦点的View
     * @param scaleX    x轴的缩放比例
     * @param scaleY    Y轴的缩放比例
     */
    public abstract void onFocusView(View focusView, float scaleX, float scaleY);

    /**
     * 绘制移动的焦点框，由具体子类实现
     *
     * @param canvas 绘制焦点框时canvas实例
     */
    public abstract boolean onFocusViewDraw(Canvas canvas);

}
