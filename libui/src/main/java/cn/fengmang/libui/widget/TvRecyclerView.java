package cn.fengmang.libui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;

import cn.fengmang.baselib.ELog;
import cn.fengmang.libui.R;

/**
 * Created by Administrator on 2018/7/6.
 */

public class TvRecyclerView extends RecyclerView implements View.OnClickListener, View.OnLongClickListener, View.OnFocusChangeListener {

    private final static String TAG = "TvRecyclerView";

    public int mVerticalSpacing = 0;
    public int mHorizontalSpacing = 0;
    private int mOldVerticalSpacing = 0;
    private int mOldHorizontalSpacing = 0;

    private int mSelectedItemOffsetStart;
    private int mSelectedItemOffsetEnd;
    private boolean mSelectedItemCentered;

    private boolean mIsSelectFirstVisiblePosition;

    private int mSelectedPosition = 0;

    private boolean mIsMenu;                       //菜单模式        以及三种状态效果

    private OnItemListener mOnItemListener;

    private final Rect mTempRect = new Rect();

    private boolean mHasFocusWithPrevious = false;
    private boolean mShouldReverseLayout = true;
    private boolean mOptimizeLayout;
    private final IRecyclerViewDataObserver mDataObserver = new IRecyclerViewDataObserver();

    public TvRecyclerView(Context context) {
        this(context, null);
    }

    public TvRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TvRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        init(context);

        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TvRecyclerView3, defStyle, 0);
        mSelectedItemCentered = a.getBoolean(R.styleable.TvRecyclerView_tvSelectedItemIsCentered, true);
        mIsSelectFirstVisiblePosition = a.getBoolean(R.styleable.TvRecyclerView_tvIsSelectFirstVisiblePosition, false);
        mSelectedItemOffsetStart = a.getDimensionPixelOffset(R.styleable.TvRecyclerView_tvSelectedItemOffsetStart, 0);
        mSelectedItemOffsetEnd = a.getDimensionPixelOffset(R.styleable.TvRecyclerView_tvSelectedItemOffsetEnd, 0);
        mIsMenu = a.getBoolean(R.styleable.TvRecyclerView_tvIsMenu, false);
        mVerticalSpacing = a.getDimensionPixelOffset(R.styleable.TvRecyclerView_tvVerticalSpacing, 0);
        mHorizontalSpacing = a.getDimensionPixelOffset(R.styleable.TvRecyclerView_tvHorizontalSpacing, 0);
        a.recycle();
    }

    private void init(Context context) {
        setChildrenDrawingOrderEnabled(true);
        setWillNotDraw(true); // 自身不作onDraw处理
        setHasFixedSize(true);
        setOverScrollMode(View.OVER_SCROLL_NEVER);

        setClipChildren(false);
        setClipToPadding(false);

        setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);
        setFocusable(true);
        setFocusableInTouchMode(true);

        //修复adapter.notifyItemChanged时焦点闪烁的问题
        ((SimpleItemAnimator) getItemAnimator()).setSupportsChangeAnimations(false);
        if (mVerticalSpacing != 0 || mHorizontalSpacing != 0) {
            setSpacing(mVerticalSpacing, mHorizontalSpacing);
        }
    }

    public int getSelectedPosition() {
        return mSelectedPosition;
    }

    public void setSelectFirstVisiblePosition(boolean selectFirstVisiblePosition) {
        mIsSelectFirstVisiblePosition = selectFirstVisiblePosition;
    }

    public boolean isSelectFirstVisiblePosition() {
        return mIsSelectFirstVisiblePosition;
    }


    public void setMenu(boolean menu) {
        mIsMenu = menu;
    }

    public boolean isMenu() {
        return mIsMenu;
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        long startMillis = System.currentTimeMillis();
        super.onLayout(changed, l, t, r, b);
        mHasFocusWithPrevious = mHasFocusWithPrevious || hasFocus();
        final boolean requestLayout = !mOptimizeLayout || (changed || mShouldReverseLayout);
        final boolean layoutAfterFocus;
        if (requestLayout) {
            super.onLayout(changed, l, t, r, b);
            mShouldReverseLayout = false;

            layoutAfterFocus = hasFocus();
            if (!layoutAfterFocus) {
                if (mSelectedPosition < 0) {
                    mSelectedPosition = getFirstVisiblePosition();
                } else if (mSelectedPosition >= getItemCount()) {
                    mSelectedPosition = getLastVisiblePosition();
                }
                if (mHasFocusWithPrevious && getPreserveFocusAfterLayout()) {
                    requestDefaultFocus();
                } else {
                    setItemActivated(mSelectedPosition);
                }
            }
        } else {
            layoutAfterFocus = hasFocus();
        }

        mHasFocusWithPrevious = false;
        ELog.v("onLayout...end  used time " + (System.currentTimeMillis() - startMillis) / 1000f + "s");
    }

    public int getItemCount() {
        if (null != getAdapter()) {
            return getAdapter().getItemCount();
        }
        return 0;
    }

    @Override
    public void onClick(View itemView) {
        if (null != mOnItemListener && this != itemView) {
            mOnItemListener.onItemClick(TvRecyclerView.this, itemView, getChildAdapterPosition(itemView));
        }
    }


    @Override
    public boolean onLongClick(View itemView) {
        if (null != mOnItemListener && this != itemView) {
            return mOnItemListener.onItemLongClick(TvRecyclerView.this, itemView, getChildAdapterPosition(itemView));
        }
        return false;
    }

    @Override
    public void onFocusChange(final View itemView, boolean hasFocus) {
        if (null != itemView && itemView != this) {
            final int position = getChildAdapterPosition(itemView);
            itemView.setSelected(hasFocus);
            if (hasFocus) {
                mSelectedPosition = position;
                if (mIsMenu && itemView.isActivated()) {
                    itemView.setActivated(false);
                }
                if (null != mOnItemListener)
                    mOnItemListener.onItemSelected(TvRecyclerView.this, itemView, position);
            } else {
                itemView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (!hasFocus()) {
                            if (mIsMenu) {
                                // 解决选中后无状态表达的问题，selector中使用activated代表选中后焦点移走
                                itemView.setActivated(true);
                            }
                            //模拟TvRecyclerView失去焦点
                            onFocusChanged(false, FOCUS_DOWN, null);
                        }
                    }
                }, 100);
                if (null != mOnItemListener)
                    mOnItemListener.onItemPreSelected(TvRecyclerView.this, itemView, position);
            }

        }
    }


    @Override
    public boolean requestFocus(int direction, Rect previouslyFocusedRect) {
        ELog.v(TAG, "requestFocus");
        if (null == getFocusedChild()) {
            //请求默认焦点
            requestDefaultFocus();
        }
        return false;
    }


    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, @Nullable Rect previouslyFocusedRect) {
        ELog.v("gainFocus=" + gainFocus + " hasFocus=" + hasFocus() + " direction=" + direction);
        if (gainFocus) {
            setDescendantFocusability(FOCUS_BEFORE_DESCENDANTS);
        } else {
            setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);
        }
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
    }

    public void requestDefaultFocus() {
        if (mIsMenu || !mIsSelectFirstVisiblePosition) {
            setSelection(mSelectedPosition);
        } else {
            setSelection(getFirstVisiblePosition());
        }
    }


    public void setSelection(int position) {
        if (null == getAdapter() || position < 0 || position >= getItemCount()) {
            return;
        }

        View view = getChildAt(position - getFirstVisiblePosition());
        if (null != view) {
            if (!hasFocus()) {
                //模拟TvRecyclerView获取焦点
                onFocusChanged(true, FOCUS_DOWN, null);
            }
            view.requestFocus();
        } else {
            TvSmoothScroller scroller = new TvSmoothScroller(getContext(), true, true, mSelectedItemOffsetStart);
            scroller.setTargetPosition(position);
            getLayoutManager().startSmoothScroll(scroller);
        }
    }

    private int getFreeHeight() {
        int freeHeight = getHeight() - getPaddingTop() - getPaddingBottom();
        ELog.v("freeHeight:" + freeHeight);
        return freeHeight;
    }

    private int getFreeWidth() {
        int freeWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        ELog.v("freeWidth:" + freeWidth);
        return freeWidth;
    }

    @Override
    public boolean requestChildRectangleOnScreen(View child, Rect rect, boolean immediate) {
        ELog.v(TAG, "requestChildRectangleOnScreen:" + rect.toString());
        if (null == child) {
            ELog.v(TAG, "child is null");
            return false;
        }

        if (mSelectedItemCentered) {
            getDecoratedBoundsWithMargins(child, mTempRect);
            ELog.d(TAG, "mTempRect:" + mTempRect.toString());
            mSelectedItemOffsetStart = (getLayoutManager().canScrollHorizontally() ? (getFreeWidth() - mTempRect.width())
                    : (getFreeHeight() - mTempRect.height())) / 2;
            mSelectedItemOffsetEnd = mSelectedItemOffsetStart;
        }

        int[] scrollAmount = getChildRectangleOnScreenScrollAmount2(child, rect, mSelectedItemOffsetStart, mSelectedItemOffsetEnd);
        int dx = scrollAmount[0];
        int dy = scrollAmount[1];
        ELog.v(TAG, String.format("dx:%d,dy:%d", dx, dy));
        smoothScrollBy(dx, dy);

        if (dx != 0 || dy != 0) {
            return true;
        }
        // 重绘是为了选中item置顶，具体请参考getChildDrawingOrder方法
        postInvalidate();
        return false;
    }


    @Override
    public void smoothScrollBy(int dx, int dy, Interpolator interpolator) {
        setScrollValue(dx, dy);
        super.smoothScrollBy(dx, dy, interpolator);
    }


    private int[] getChildRectangleOnScreenScrollAmount2(View focusView, Rect rect, int offsetStart, int offsetEnd) {
        //横向滚动
        int dx = 0;
        int dy = 0;

        getDecoratedBoundsWithMargins(focusView, mTempRect);
        ELog.v(TAG, "mTempRect:" + mTempRect.toString());
        ELog.v(TAG, "canScrollVertically:" + getLayoutManager().canScrollVertically());
        ELog.v(TAG, "canScrollHorizontally:" + getLayoutManager().canScrollHorizontally());

        if (getLayoutManager().canScrollHorizontally()) {
            final int right = mTempRect.right + getPaddingRight() - getWidth();
            final int left = mTempRect.left - getPaddingLeft();
            dx = computeScrollOffset(left, right, offsetStart, offsetEnd);
        }

        //竖向滚动
        if (getLayoutManager().canScrollVertically()) {
            final int bottom = mTempRect.bottom + getPaddingBottom() - getHeight();
            final int top = mTempRect.top - getPaddingTop();
            dy = computeScrollOffset(top, bottom, offsetStart, offsetEnd);
        }

        return new int[]{dx, dy};
    }


    private int computeScrollOffset(int start, int end, int offsetStart, int offsetEnd) {
//        ELog.v("start=" + start + " end=" + end + " offsetStart=" + offsetStart + " offsetEnd=" + offsetEnd);

        // focusView超出下/右边界
        if (end > 0) {
            if (getLastVisiblePosition() != (getItemCount() - 1)) {
                return end + offsetEnd;
            } else {
                return end;
            }
        }
        // focusView超出上/左边界
        else if (start < 0) {
            if (getFirstVisiblePosition() != 0) {
                return start - offsetStart;
            } else {
                return start;
            }
        }
        // focusView未超出上/左边界，但边距小于指定offset
        else if (start > 0 && start < offsetStart
                && (canScrollHorizontally(-1) || canScrollVertically(-1))) {
            return start - offsetStart;
        }
        // focusView未超出下/右边界，但边距小于指定offset
        else if (Math.abs(end) > 0 && Math.abs(end) < offsetEnd
                && (canScrollHorizontally(1) || canScrollVertically(1))) {
            return offsetEnd - Math.abs(end);
        }

        return 0;
    }

    /**
     * 通过Margins来设置布局的横纵间距；
     * (与addItemDecoration()方法可二选一)
     *
     * @param verticalSpacing
     * @param horizontalSpacing
     */
    public void setSpacing(int verticalSpacing, int horizontalSpacing) {
        if (this.mVerticalSpacing != verticalSpacing || this.mHorizontalSpacing != horizontalSpacing) {
            this.mOldVerticalSpacing = this.mVerticalSpacing;
            this.mOldHorizontalSpacing = this.mHorizontalSpacing;
            this.mVerticalSpacing = verticalSpacing;
            this.mHorizontalSpacing = horizontalSpacing;
            adjustPadding();
        }
    }

    /**
     * 根据Margins调整Padding值
     */
    private void adjustPadding() {
        if ((mVerticalSpacing >= 0 || mHorizontalSpacing >= 0)) {
            final int verticalSpacingHalf = mVerticalSpacing / 2;
            final int horizontalSpacingHalf = mHorizontalSpacing / 2;
            final int oldVerticalSpacingHalf = mOldVerticalSpacing / 2;
            final int oldHorizontalSpacingHalf = mOldHorizontalSpacing / 2;
            final int l = getPaddingLeft() + oldHorizontalSpacingHalf - horizontalSpacingHalf;
            final int t = getPaddingTop() + oldVerticalSpacingHalf - verticalSpacingHalf;
            final int r = getPaddingRight() + oldHorizontalSpacingHalf - horizontalSpacingHalf;
            final int b = getPaddingBottom() + oldVerticalSpacingHalf - verticalSpacingHalf;
            setPadding(l, t, r, b);
        }
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        boolean result = super.checkLayoutParams(p);
        if (result && (mVerticalSpacing >= 0 || mHorizontalSpacing >= 0)) {
            final int verticalSpacingHalf = mVerticalSpacing / 2;
            final int horizontalSpacingHalf = mHorizontalSpacing / 2;
            final LayoutParams lp = (LayoutParams) p;
            lp.setMargins(horizontalSpacingHalf, verticalSpacingHalf, horizontalSpacingHalf, verticalSpacingHalf);
        }
        return result;
    }


    @Override
    public void onChildAttachedToWindow(View child) {
        if (child.isClickable() && !ViewCompat.hasOnClickListeners(child)) {
            child.setOnClickListener(this);
        }
        child.setOnLongClickListener(this);
        if (child.isFocusable() && null == child.getOnFocusChangeListener()) {
            child.setOnFocusChangeListener(this);
        }
    }

    @Override
    public void onChildDetachedFromWindow(View child) {
        super.onChildDetachedFromWindow(child);
        if (child.isActivated())
            child.setActivated(false);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    @Override
    public void onScrollStateChanged(int state) {
        if (state == SCROLL_STATE_IDLE) {
            setScrollValue(0, 0);
        }
    }


    /**
     * 用途飞框位置校准
     */
    private Point mScrollPoint = new Point();

    void setScrollValue(int x, int y) {
        if (x != 0 || y != 0) {
            mScrollPoint.set(x, y);
            setTag(mScrollPoint);
        } else {
            setTag(null);
        }
    }


    public int getFirstVisiblePosition() {
        int index = 0;
        if (getChildCount() == 0)
            index = 0;
        else
            index = getChildAdapterPosition(getChildAt(0));
        return index;
    }

    public int getLastVisiblePosition() {
        final int childCount = getChildCount();
        if (childCount == 0)
            return 0;
        else
            return getChildAdapterPosition(getChildAt(childCount - 1));
    }


    /**
     * @param position
     * @deprecated Use {@link #scrollToPosition(int)} and
     * {@link #scrollToPositionWithOffset(int, int)}
     */
    @Deprecated
    public void scrollToPositionWithOffsetStart(int position) {
        scrollToPositionWithOffset(position, mSelectedItemOffsetStart, false);
    }

    public void scrollToPositionWithOffset(int position, int offset) {
        scrollToPositionWithOffset(position, offset, false);
    }

    public void scrollToPositionWithOffset(int position, int offset, boolean isRequestFocus) {
        scrollToPosition(position, isRequestFocus, false, offset);
    }

    @Override
    public void scrollToPosition(int position) {
        scrollToPosition(position, false);
    }

    public void scrollToPosition(int position, boolean isRequestFocus) {
        scrollToPosition(position, isRequestFocus, false, mSelectedItemOffsetStart);
    }

    @Override
    public void smoothScrollToPosition(int position) {
        smoothScrollToPosition(position, false);
    }

    public void smoothScrollToPosition(int position, boolean isRequestFocus) {
        scrollToPosition(position, isRequestFocus, true, mSelectedItemOffsetStart);
    }

    private void scrollToPosition(int position, boolean isRequestFocus, boolean isSmooth, int offset) {
        mSelectedPosition = position;
        TvSmoothScroller smoothScroller = new TvSmoothScroller(getContext(), isRequestFocus, isSmooth, offset);
        smoothScroller.setTargetPosition(position);
        getLayoutManager().startSmoothScroll(smoothScroller);
    }


    @Override
    protected int getChildDrawingOrder(int childCount, int i) {
        View view = getFocusedChild();
        if (null != view) {
            int tempPosition = getChildAdapterPosition(view) - getFirstVisiblePosition();
            if (tempPosition < 0) {
                return i;
            } else {
                if (i == childCount - 1) {//这是最后一个需要刷新的item
                    if (tempPosition > i) {
                        tempPosition = i;
                    }
                    return tempPosition;
                }
                if (i == tempPosition) {//这是原本要在最后一个刷新的item
                    return childCount - 1;
                }
            }
        }
        return i;
    }

    @Override
    public void setAdapter(Adapter adapter) {
        resetAdapter(adapter);
        super.setAdapter(adapter);
    }

    private void resetAdapter(Adapter newAdapter) {
        final Adapter oldAdapter = getAdapter();
        if (null != oldAdapter) {
            oldAdapter.unregisterAdapterDataObserver(mDataObserver);
            mShouldReverseLayout = true;
        }
        newAdapter.registerAdapterDataObserver(mDataObserver);
    }

    //选中效果
    public void setItemActivated(int position) {
        if (mIsMenu) {
            ViewHolder holder;
            if (position != mSelectedPosition) {
                holder = findViewHolderForLayoutPosition(mSelectedPosition);
                if (null != holder && holder.itemView.isActivated()) {
                    holder.itemView.setActivated(false);
                }
                mSelectedPosition = position;
            }
            holder = findViewHolderForLayoutPosition(position);
            if (null != holder && !holder.itemView.isActivated()) {
                holder.itemView.setActivated(true);
            }
        }
    }


    public void setOnItemListener(OnItemListener mOnItemListener) {
        this.mOnItemListener = mOnItemListener;
    }

    private class TvSmoothScroller extends LinearSmoothScroller {
        private boolean mRequestFocus;
        private boolean mIsSmooth;
        private int mOffset;

        public TvSmoothScroller(Context context, boolean isRequestFocus, boolean isSmooth, int offset) {
            super(context);
            mRequestFocus = isRequestFocus;
            mIsSmooth = isSmooth;
            mOffset = offset;
        }

        @Override
        protected int calculateTimeForScrolling(int dx) {
            return mIsSmooth ? super.calculateTimeForScrolling(dx) :
                    ((int) Math.ceil(Math.abs(dx) * (11f / getContext().getResources().getDisplayMetrics().densityDpi)));
        }

        @Override
        protected void onTargetFound(View targetView, State state, Action action) {
            if (mSelectedItemCentered && null != getLayoutManager()) {
                getDecoratedBoundsWithMargins(targetView, mTempRect);
                mOffset = (getLayoutManager().canScrollHorizontally() ? (getFreeWidth() - mTempRect.width())
                        : (getFreeHeight() - mTempRect.height())) / 2;
            }
            super.onTargetFound(targetView, state, action);
        }

        @Override
        public int calculateDtToFit(int viewStart, int viewEnd, int boxStart, int boxEnd, int snapPreference) {
            return boxStart - viewStart + mOffset;
        }

        @Override
        protected void onStop() {
            ELog.v(TAG, "TvSmoothScroller onStop");
            super.onStop();
            if (mRequestFocus) {
                final View itemView = findViewByPosition(getTargetPosition());
                if (null != itemView) {
                    itemView.post(new Runnable() {
                        @Override
                        public void run() {
                            if (!hasFocus()) {
                                onFocusChanged(true, FOCUS_DOWN, null);
                            }
                            itemView.requestFocus();
                        }
                    });
                }
            }
        }
    }

    private class IRecyclerViewDataObserver extends AdapterDataObserver {
        @Override
        public void onChanged() {
            ELog.v("RecyclerView Data Changed!!!");
            mShouldReverseLayout = true;
        }
    }


    public interface OnItemListener {
        void onItemPreSelected(TvRecyclerView parent, View itemView, int position);

        void onItemSelected(TvRecyclerView parent, View itemView, int position);

        void onItemClick(TvRecyclerView parent, View itemView, int position);

        boolean onItemLongClick(TvRecyclerView parent, View itemView, int position);
    }
}
