package cn.fengmang.libui.scroller;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

/**
 * @author hxy
 */
public class FlowHorScrollView extends HorTouchViewGroup {

    //DecelerateInterpolator,AccelerateInterpolator
    private static final Interpolator mInterpolator = new LinearInterpolator();

    protected FlowScroller mScroller;
    // 最小滚动距离
    private int minScroll;

    public FlowHorScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public FlowHorScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlowHorScrollView(Context context) {
        this(context, null);
    }

    private void init(Context context) {
        int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        minScroll = (int) (screenWidth * 0.09f);
        mScroller = new FlowScroller(context, mInterpolator);
    }

    public void setMinScroll(int minScroll) {
        this.minScroll = minScroll;
    }

    public int getOffsetX() {
        int[] locParent = new int[2];
        getLocationInWindow(locParent);
        return mScroller.getFinalX() - getScrollX() + locParent[0];
    }

    public void scrollToFront() {
        scrollTo(0, 0);
        mScroller.setFinalX(0);
        if (onFlowScrollChangedListenerList != null) {
            for (OnFlowScrolledListener listener : onFlowScrollChangedListenerList) {
                listener.onScrolled(0, 0);
            }
        }
    }

    public void scrollToBack() {
        int totalScroll = getScrollRange();
        scrollTo(totalScroll, 0);
        mScroller.setFinalX(totalScroll);
        if (onFlowScrollChangedListenerList != null) {
            for (OnFlowScrolledListener listener : onFlowScrollChangedListenerList) {
                listener.onScrolled(0, totalScroll);
            }
        }
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), 0);
            if (onFlowScrollChangedListenerList != null) {
                for (OnFlowScrolledListener listener : onFlowScrollChangedListenerList) {
                    listener.onScrolled(0, mScroller.getCurrY());
                }
            }
            postInvalidate();
        }
    }

    @Override
    public void requestChildFocus(View child, View focused) {
        super.requestChildFocus(child, focused);
        startScroll(this, focused, getTotalWidth());
        postInvalidate();
    }

    protected int getTotalWidth() {
        return getChildAt(0).getMeasuredWidth();
    }

    protected void startScroll(View parent, View destView, int totalWidth) {
        int destWidth = destView.getWidth();
        if (destWidth <= 0 || totalWidth <= 0) {
            return;
        }

        int[] locDest = new int[2];
        destView.getLocationInWindow(locDest);

        int[] locParent = new int[2];
        parent.getLocationInWindow(locParent);

        int centerX = locDest[0] + destWidth / 2 - locParent[0];
        int parentWidth = getShowWidth();
        if (parentWidth > totalWidth) {
            parentWidth = totalWidth;
        }
        int scrollX = centerX - parentWidth / 2;

        if (getScrollX() + parentWidth + scrollX > totalWidth) {
            scrollX = totalWidth - getScrollX() - parentWidth;
        } else if (getScrollX() + scrollX <= 0) {
            scrollX = -getScrollX();
        } else if (Math.abs(scrollX) < minScroll) {
            scrollX = 0;
        } else if (getScrollX() + parentWidth + scrollX + minScroll > totalWidth) {
            scrollX = totalWidth - getScrollX() - parentWidth;
        } else if (getScrollX() + scrollX - minScroll <= 0) {
            scrollX = -getScrollX();
        }

        if (scrollX != 0) {
            mScroller.startScroll(getScrollX(), getScrollY(), scrollX, 0);
        }
    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new ViewGroup.MarginLayoutParams(getContext(), attrs);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int startX = getPaddingLeft();
        int startY = getPaddingTop();
        for (int i = 0; i < getChildCount(); i++) {
            View childView = getChildAt(i);
            int childW = childView.getMeasuredWidth();
            int childH = childView.getMeasuredHeight();
            if (childView.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) childView.getLayoutParams();
                startX += layoutParams.leftMargin;
                startY = getPaddingTop() + layoutParams.topMargin;
            }
            childView.layout(startX, startY, startX + childW, startY + childH);
            startX += childW;
            if (childView.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) childView.getLayoutParams();
                startX += layoutParams.rightMargin;
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        final int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);
        if (widthMode == View.MeasureSpec.UNSPECIFIED) {
            return;
        }

        if (getChildCount() > 0) {
            final View child = getChildAt(0);
            int width = getMeasuredWidth();
            if (child.getMeasuredWidth() < width) {
                final ViewGroup.LayoutParams lp = (ViewGroup.LayoutParams) child.getLayoutParams();

                int childHeightMeasureSpec = getChildMeasureSpec(heightMeasureSpec,
                        getPaddingTop() + getPaddingBottom(), lp.height);
                width -= getPaddingLeft();
                width -= getPaddingRight();
                int childWidthMeasureSpec = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);

                child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
            }
        }

        measureChildren(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void measureChild(View child, int parentWidthMeasureSpec, int parentHeightMeasureSpec) {
        if (Build.VERSION.SDK_INT < 23) {
            ViewGroup.LayoutParams lp = child.getLayoutParams();

            int childWidthMeasureSpec;
            int childHeightMeasureSpec;

            childHeightMeasureSpec = getChildMeasureSpec(parentHeightMeasureSpec, getPaddingTop() + getPaddingBottom(),
                    lp.height);

            childWidthMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
        } else {
            parentWidthMeasureSpec = View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(parentWidthMeasureSpec),
                    View.MeasureSpec.UNSPECIFIED);
            super.measureChild(child, parentWidthMeasureSpec, parentHeightMeasureSpec);
        }
    }

    protected void measureChildWithMargins(View child, int parentWidthMeasureSpec, int widthUsed,
                                           int parentHeightMeasureSpec, int heightUsed) {
        if (Build.VERSION.SDK_INT < 23) {
            final ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) child.getLayoutParams();

            final int childHeightMeasureSpec = getChildMeasureSpec(parentHeightMeasureSpec,
                    getPaddingTop() + getPaddingBottom() + lp.topMargin + lp.bottomMargin + heightUsed, lp.height);
            final int childWidthMeasureSpec = View.MeasureSpec.makeMeasureSpec(lp.leftMargin + lp.rightMargin,
                    View.MeasureSpec.UNSPECIFIED);

            child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
        } else {
            parentWidthMeasureSpec = View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(parentWidthMeasureSpec),
                    View.MeasureSpec.UNSPECIFIED);
            super.measureChildWithMargins(child, parentWidthMeasureSpec, widthUsed, parentHeightMeasureSpec,
                    heightUsed);
        }
    }

}
