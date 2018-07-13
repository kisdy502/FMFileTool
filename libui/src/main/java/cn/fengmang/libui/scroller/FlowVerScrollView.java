package cn.fengmang.libui.scroller;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

import cn.fengmang.libui.R;

/**
 * @author hxy
 */
public class FlowVerScrollView extends VerTouchViewGroup {

    private static final Interpolator mInterpolator = new DecelerateInterpolator();

    protected FlowScroller mScroller;
    // 最小滚动距离
    private int minScroll;
    //中心点偏移量
    private int centerOffset;

    public FlowVerScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FlowScrollView);
            centerOffset = 0;//a.getDimensionPixelSize(R.styleable.FlowScrollView_center_offset, 0);
            a.recycle();
        }
        init(context);
    }

    public FlowVerScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlowVerScrollView(Context context) {
        this(context, null);
    }

    private void init(Context context) {
        int screenHeight = context.getResources().getDisplayMetrics().heightPixels;
        minScroll = (int) (screenHeight * 0.1f);
        mScroller = new FlowScroller(context, mInterpolator);
    }

    public void setMinScroll(int minScroll) {
        this.minScroll = minScroll;
    }

    public int getOffsetY() {
        int[] locParent = new int[2];
        getLocationInWindow(locParent);
        return mScroller.getFinalY() - getScrollY() + locParent[1];
    }

    public void scrollToFront() {
        scrollTo(0, 0);
        mScroller.setFinalY(0);
        if (onFlowScrollChangedListenerList != null) {
            for (OnFlowScrolledListener listener : onFlowScrollChangedListenerList) {
                listener.onScrolled(0, 0);
            }
        }
    }

    public void smoothScrollToFront() {
        mScroller.startScroll(getScrollX(), getScrollY(), 0, -getScrollY());
        postInvalidate();
    }

    public void smoothScrollToBack() {
        int totalScroll = getScrollRange();
        int y = totalScroll - getScrollY();
        mScroller.startScroll(getScrollX(), getScrollY(), 0, y);
        postInvalidate();
    }

    public void scrollToBack() {
        int totalScroll = getScrollRange();
        scrollTo(0, totalScroll);
        mScroller.setFinalY(totalScroll);
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
            scrollTo(0, mScroller.getCurrY());
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
        startScroll(this, focused, getTotalHeight());
        postInvalidate();
    }

    protected int getTotalHeight() {
        return getChildAt(0).getMeasuredHeight();
    }

    protected int getCenterY(View parent, View destview, int totalHeight) {
        int destHeight = destview.getHeight();
        if (destHeight <= 0 || totalHeight <= 0) {
            return 0;
        }
        int[] locDest = new int[2];
        destview.getLocationInWindow(locDest);

        int[] locParent = new int[2];
        parent.getLocationInWindow(locParent);

        int centerY = locDest[1] + destHeight / 2 - locParent[1];
        return centerY;
    }

    protected void startScroll(View parent, View destView, int totalHeight) {
        int centerY = getCenterY(parent, destView, totalHeight);
        if (centerY == 0) {
            return;
        }
        int parentHeight = getShowHeight();
        if (parentHeight > totalHeight) {
            parentHeight = totalHeight;
        }
        int scrollY = centerY - (parentHeight / 2 + centerOffset);

        if (getScrollY() + parentHeight + scrollY > totalHeight) {
            scrollY = totalHeight - getScrollY() - parentHeight;
        } else if (getScrollY() + scrollY <= 0) {
            scrollY = -getScrollY();
        } else if (Math.abs(scrollY) < minScroll) {
            scrollY = 0;
        } else if (getScrollY() + parentHeight + scrollY + minScroll > totalHeight) {
            scrollY = totalHeight - getScrollY() - parentHeight;
        } else if (getScrollY() + scrollY - minScroll <= 0) {
            scrollY = -getScrollY();
        }

        if (getChildAt(0) != null && getChildAt(0) instanceof OnScrollToNextListener) {
            ((OnScrollToNextListener) getChildAt(0)).onScrollToNext(getScrollX(), getScrollY(), 0, scrollY);
        }
        if (scrollY != 0) {
            mScroller.startScroll(getScrollX(), getScrollY(), 0, scrollY);
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
        int endX;
        int endY;
        for (int i = 0; i < getChildCount(); i++) {
            View childView = getChildAt(i);
            int childW = childView.getMeasuredWidth();
            int childH = childView.getMeasuredHeight();
            endX = childW;
            endY = childH;
            if (childView.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) childView.getLayoutParams();
                startX = getPaddingLeft() + layoutParams.leftMargin;
                startY = getPaddingTop() + layoutParams.topMargin;
                endX += layoutParams.rightMargin;
                endY += layoutParams.bottomMargin;
            }
            childView.layout(startX, startY, endX, endY);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        final int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);
        if (heightMode == View.MeasureSpec.UNSPECIFIED) {
            return;
        }

        if (getChildCount() > 0) {
            final View child = getChildAt(0);
            final int height = getMeasuredHeight();
            if (child.getMeasuredHeight() < height) {
                final int widthPadding;
                final int heightPadding;
                final ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) child.getLayoutParams();
                final int targetSdkVersion = getContext().getApplicationInfo().targetSdkVersion;
                if (targetSdkVersion >= Build.VERSION_CODES.M) {
                    widthPadding = getPaddingLeft() + getPaddingRight() + lp.leftMargin + lp.rightMargin;
                    heightPadding = getPaddingTop() + getPaddingBottom() + lp.topMargin + lp.bottomMargin;
                } else {
                    widthPadding = getPaddingLeft() + getPaddingRight();
                    heightPadding = getPaddingTop() + getPaddingBottom();
                }

                final int childWidthMeasureSpec = getChildMeasureSpec(
                        widthMeasureSpec, widthPadding, lp.width);
                final int childHeightMeasureSpec = View.MeasureSpec.makeMeasureSpec(
                        height - heightPadding, View.MeasureSpec.EXACTLY);
                child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
            }
        }
        measureChildren(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void measureChild(View child, int parentWidthMeasureSpec,
                                int parentHeightMeasureSpec) {
        ViewGroup.LayoutParams lp = child.getLayoutParams();

        int childWidthMeasureSpec;
        int childHeightMeasureSpec;

        childWidthMeasureSpec = getChildMeasureSpec(parentWidthMeasureSpec, getPaddingLeft()
                + getPaddingRight(), lp.width);

        childHeightMeasureSpec = View.MeasureSpec.makeMeasureSpec(
                View.MeasureSpec.getSize(parentHeightMeasureSpec), View.MeasureSpec.UNSPECIFIED);

        child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
    }

    @Override
    protected void measureChildWithMargins(View child, int parentWidthMeasureSpec, int widthUsed,
                                           int parentHeightMeasureSpec, int heightUsed) {
        final ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) child.getLayoutParams();

        final int childWidthMeasureSpec = getChildMeasureSpec(parentWidthMeasureSpec,
                getPaddingLeft() + getPaddingRight() + lp.leftMargin + lp.rightMargin
                        + widthUsed, lp.width);
        final int childHeightMeasureSpec = View.MeasureSpec.makeMeasureSpec(
                View.MeasureSpec.getSize(parentHeightMeasureSpec), View.MeasureSpec.UNSPECIFIED);

        child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
    }

    public int getScrollRange() {
        int scrollRange = 0;
        if (getChildCount() > 0) {
            View child = getChildrenParent();
            scrollRange = Math.max(0, child.getHeight() - getShowHeight());
        }
        return scrollRange;
    }

    protected int getShowHeight() {
        return getHeight() - getPaddingTop() - getPaddingBottom();
    }

    protected ViewGroup getChildrenParent() {
        return (ViewGroup) getChildAt(0);
    }

}
