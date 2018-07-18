package cn.fengmang.libui.recycler;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Interpolator;

import cn.fengmang.baselib.ELog;

/**
 * Created by Administrator on 2018/7/17.
 */

public class FRecyclerView extends RecyclerView {

    private OnFocusChangeListener onFocusChangeListener;
	private OnItemFocusChangeListener onItemFocusChangeListener;
    private int itemSpaceLeft, itemSpaceTop, itemSpaceRight, itemSpaceBottom;
    private int itemPaddingLeft, itemPaddingTop, itemPaddingRight, itemPaddingBottom;
    protected Rect mSpacesRect;

    private int mOrientation;
    private int mColumnCount;

    private boolean mAllowItemSelected = false;
    private boolean mSelectedItemCentered = true;
    private int mSelectedItemOffsetStart, mSelectedItemOffsetEnd;

    

    public FRecyclerView(Context context) {
        this(context, null);
    }

    public FRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        setDescendantFocusability(FOCUS_AFTER_DESCENDANTS);
        setChildrenDrawingOrderEnabled(true);
        setWillNotDraw(true);
        setHasFixedSize(false);
        setOverScrollMode(View.OVER_SCROLL_NEVER);
        setClipChildren(false);
        setClipToPadding(false);
        setClickable(false);
        setFocusable(true);
        setFocusableInTouchMode(true);
        initListener();

        if (itemSpaceLeft != 0 || itemSpaceTop != 0 || itemSpaceRight != 0 || itemSpaceBottom != 0) {
            setItemSpaces(itemSpaceLeft, itemSpaceTop, itemSpaceRight, itemSpaceBottom);
        }
    }

    private void initListener() {
        onFocusChangeListener = new OnFocusChangeListener() {
            @Override
            public void onFocusChange(final View itemView, boolean hasFocus) {
                if (itemView != null) {
                    if (hasFocus) {
                        onItemFocusChangeListener.onItemSelected(FRecyclerView.this, itemView, getChildLayoutPosition(itemView));
                    } else {
                        postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                onItemFocusChangeListener.onItemPreSelected(FRecyclerView.this, itemView, getChildLayoutPosition(itemView));
                            }
                        }, 6);
                    }
                }
            }
        };
    }

    private void setItemSpaces(int left, int top, int right, int bottom) {
        if (mSpacesRect == null) {
            mSpacesRect = new Rect(left, top, right, bottom);
            super.addItemDecoration(new SpacesItemDecoration(mSpacesRect));
        }
    }

    @Override
    public void setLayoutManager(LayoutManager layout) {
        super.setLayoutManager(layout);
        if (layout instanceof GridLayoutManager) {
            GridLayoutManager gridLayoutManager = (GridLayoutManager) layout;
            mOrientation = gridLayoutManager.getOrientation();
            mColumnCount = gridLayoutManager.getSpanCount();
        } else if (layout instanceof LinearLayoutManager) {
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layout;
            mOrientation = linearLayoutManager.getOrientation();
            mColumnCount = 1;
        } else {
            throw new IllegalArgumentException("not support StaggeredGridLayoutManager");
        }
        // 竖向布局的时候，paddingLeft属性有错误，必须替换成View padding属性,且不能设置top和bottom
        if (mOrientation == VERTICAL) {
            setPadding(itemPaddingLeft, 0, itemPaddingRight, 0);
            setClipToPadding(false);
            itemPaddingLeft = 0;
            itemPaddingRight = 0;
        }

    }

    @Override
    public void requestChildFocus(View child, View focused) {
        // 获取焦点框居中的位置
        if (null != child) {
            if (mSelectedItemCentered) {
                mSelectedItemOffsetStart = (mOrientation == HORIZONTAL) ? (getFreeWidth() - child.getWidth()) : (getFreeHeight() - child.getHeight());
                mSelectedItemOffsetStart /= 2;
                mSelectedItemOffsetEnd = mSelectedItemOffsetStart;
                ELog.d("mSelectedItemOffsetStart:" + mSelectedItemOffsetStart);
            }
        }
        super.requestChildFocus(child, focused);
    }

    @Override
    public void onChildAttachedToWindow(View child) {
        if (child.isFocusable() && child.getOnFocusChangeListener() == null) {
            child.setOnFocusChangeListener(onFocusChangeListener);
        }
    }

    @Override
    public void onScrollStateChanged(int state) {
        //用来微调位置
        if (SCROLL_STATE_IDLE == state) {
            setScrollValue(0, 0);
            if (onItemFocusChangeListener != null)
                onItemFocusChangeListener.onReviseFocusFollow(this, getFocusedChild(), getChildLayoutPosition(getFocusedChild()));
        }
    }

    @Override
    public boolean requestChildRectangleOnScreen(View child, Rect rect, boolean immediate) {
        //计算出当前viewGroup即是RecyclerView的内容区域
        final int parentLeft = getPaddingLeft();
        final int parentTop = getPaddingTop();
        final int parentRight = getWidth() - getPaddingRight();
        final int parentBottom = getHeight() - getPaddingBottom();
        ELog.v(String.format("pL:%d,pT:%d,pR:%d,pB:%d", parentLeft, parentTop, parentRight, parentBottom));

        //计算出child,此时是获取焦点的view请求的区域
        final int childLeft = child.getLeft() + rect.left;
        final int childTop = child.getTop() + rect.top;
        final int childRight = childLeft + rect.width();
        final int childBottom = childTop + rect.height();
        ELog.v(String.format("cL:%d,cT:%d,cR:%d,cB:%d", childLeft, childTop, childRight, childBottom));

        //获取请求区域四个方向与RecyclerView内容四个方向的距离
        final int offScreenLeft = Math.min(0, childLeft - parentLeft - mSelectedItemOffsetStart);
        final int offScreenTop = Math.min(0, childTop - parentTop - mSelectedItemOffsetStart);
        final int offScreenRight = Math.max(0, childRight - parentRight + mSelectedItemOffsetEnd);
        final int offScreenBottom = Math.max(0, childBottom - parentBottom + mSelectedItemOffsetEnd);

        final boolean canScrollHorizontal = getLayoutManager().canScrollHorizontally();
        int dx;
        if (canScrollHorizontal) {
            if (ViewCompat.getLayoutDirection(this) == ViewCompat.LAYOUT_DIRECTION_RTL) {
                dx = offScreenRight != 0 ? offScreenRight
                        : Math.max(offScreenLeft, childRight - parentRight);
            } else {
                dx = offScreenLeft != 0 ? offScreenLeft
                        : Math.min(childLeft - parentLeft, offScreenRight);
            }
        } else {
            dx = 0;
        }
        int dy = offScreenTop != 0 ? offScreenTop
                : Math.min(childTop - parentTop, offScreenBottom);
        //在这里可以微调滑动的距离,根据项目的需要
        if (dx != 0 || dy != 0) {
            //最后执行滑动
            if (immediate) {
                scrollBy(dx, dy);
            } else {
                smoothScrollBy(dx, dy);
            }
            return true;
        }
        postInvalidate();
        return false;
    }

    @Override
    public void smoothScrollBy(int dx, int dy, Interpolator interpolator) {
        setScrollValue(dx, dy);
        super.smoothScrollBy(dx, dy, interpolator);
    }

    private int getFreeWidth() {
        return getWidth() - getPaddingLeft() - getPaddingRight();
    }

    private int getFreeHeight() {
        return getHeight() - getPaddingTop() - getPaddingBottom();
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

    private class SpacesItemDecoration extends ItemDecoration {
        private Rect rect;

        public SpacesItemDecoration(Rect rect) {
            this.rect = rect;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.left = rect.left;
            outRect.top = rect.top;
            outRect.right = rect.right;
            outRect.bottom = rect.bottom;

            if (itemPaddingLeft == 0 || itemPaddingTop == 0 || itemPaddingRight == 0 || itemPaddingBottom == 0) {
                return;
            }

            int position = parent.getChildAdapterPosition(view);
            int count = getAdapter().getItemCount();
            int clunmCount = Math.min(mColumnCount, count);
            if (mOrientation == VERTICAL) {
                if (position < clunmCount) {
                    outRect.top += itemPaddingTop;
                }

                if (position % clunmCount == 0) {
                    outRect.left += itemPaddingLeft;
                }

                if (position % clunmCount == clunmCount - 1) {
                    outRect.right += itemPaddingRight;
                }

                if (position >= getRowsCount(count, clunmCount) * clunmCount - clunmCount) {
                    outRect.bottom += itemPaddingBottom;
                }

            } else {
                if (position < clunmCount) {
                    outRect.left += itemPaddingLeft;
                }

                if (position % clunmCount == 0) {
                    outRect.top += itemPaddingTop;
                }

                if (position % clunmCount == clunmCount - 1) {
                    outRect.bottom += itemPaddingBottom;
                }

                if (position >= getRowsCount(count, clunmCount) * clunmCount - clunmCount) {
                    outRect.right += itemPaddingRight;
                }
            }
        }

        private int getRowsCount(int totalCount, int clunmCount) {
            return (totalCount - 1) / clunmCount + 1;
        }
    }


    public void setOnItemFocusChangeListener(OnItemFocusChangeListener onItemFocusChangeListener) {
        this.onItemFocusChangeListener = onItemFocusChangeListener;
    }

    public interface OnItemFocusChangeListener {
        boolean onItemPreSelected(RecyclerView parent, View itemView, int position);

        boolean onItemSelected(RecyclerView parent, View itemView, int position);

        boolean onReviseFocusFollow(RecyclerView parent, View itemView, int position);
    }

}
