package cn.fengmang.libui.recycler;

import android.content.Context;
import android.content.res.TypedArray;
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
import cn.fengmang.libui.R;

/**
 * Created by Administrator on 2018/6/27.
 */

public class FMRecyclerView extends RecyclerView implements View.OnClickListener, View.OnLongClickListener {

    private final static String TAG = "TvRecycler";


    protected int mCurrentFocusPosition;
    protected OnItemFocusChangeListener onItemListener;
    protected OnFocusChangeListener mOnFocusChangeListener;
    protected OnItemClickListener onItemClickListener;
    protected OnItemLongClickListener onItemLongClickListener;

    protected Rect mSpacesRect;
    protected int paddingLeft;
    protected int paddingRight;
    protected int paddingTop;
    protected int paddingBottom;
    protected int mColumnCount;
    protected int mOrientation;

    private boolean mSelectedItemCentered = true;
    private int mSelectedItemOffsetStart, mSelectedItemOffsetEnd;

    public FMRecyclerView(Context context) {
        this(context, null);
    }

    public FMRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FMRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FMRecyclerView);
            int left = a.getDimensionPixelSize(R.styleable.FMRecyclerView_itemSpaceLeft, 0);
            int top = a.getDimensionPixelSize(R.styleable.FMRecyclerView_itemSpaceTop, 0);
            int right = a.getDimensionPixelSize(R.styleable.FMRecyclerView_itemSpaceRight, 0);
            int bottom = a.getDimensionPixelSize(R.styleable.FMRecyclerView_itemSpaceBottom, 0);
            // padding值只能通过recyclerPadding设置

            paddingLeft = a.getDimensionPixelSize(R.styleable.FMRecyclerView_recyclerPaddingLeft, 0);
            paddingRight = a.getDimensionPixelSize(R.styleable.FMRecyclerView_recyclerPaddingRight, 0);
            paddingTop = a.getDimensionPixelSize(R.styleable.FMRecyclerView_recyclerPaddingTop, 0);
            paddingBottom = a.getDimensionPixelSize(R.styleable.FMRecyclerView_recyclerPaddingBottom, 0);

            int padding = a.getDimensionPixelSize(R.styleable.FMRecyclerView_recyclerPadding, 0);
            if (padding != 0) {
                paddingLeft = padding;
                paddingRight = padding;
                paddingTop = padding;
                paddingBottom = padding;
            }
            a.recycle();
            if (left != 0 || top != 0 || right != 0 || bottom != 0) {
                setItemSpaces(left, top, right, bottom);
            }
        }

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
//        initClickEvent();
        initItemFocusEvent();
    }

    private void initItemFocusEvent() {
        mOnFocusChangeListener = new OnFocusChangeListener() {
            @Override
            public void onFocusChange(final View view, boolean hasFocus) {
                if (null != onItemListener && view != null) {
                    if (hasFocus) {
                        mCurrentFocusPosition = getChildLayoutPosition(view);
                        onItemListener.onItemSelected(FMRecyclerView.this, view, getChildLayoutPosition(view));
                        ELog.d(TAG, "onFocusChange:" + mCurrentFocusPosition);
                    } else {
                        view.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                onItemListener.onItemPreSelected(FMRecyclerView.this, view, getChildLayoutPosition(view));
                            }
                        }, 9);
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
            setPadding(paddingLeft, 0, paddingRight, 0);
            setClipToPadding(false);
            paddingLeft = 0;
            paddingRight = 0;
        }

    }

    @Override
    public void requestChildFocus(View child, View focused) {
        // 获取焦点框居中的位置
        ELog.d("requestChildFocus");
        if (null != child) {
            if (mSelectedItemCentered) {
                mSelectedItemOffsetStart = (mOrientation == HORIZONTAL) ? (getFreeWidth() - child.getWidth()) : (getFreeHeight() - child.getHeight());
                mSelectedItemOffsetStart /= 2;
                mSelectedItemOffsetEnd = mSelectedItemOffsetStart;
            }
        }
        super.requestChildFocus(child, focused);
    }


    @Override
    public void onChildAttachedToWindow(View child) {
        if (child.getOnFocusChangeListener() == null) {
            child.setOnFocusChangeListener(mOnFocusChangeListener);
        }
        if (child.isClickable()) {
            child.setOnClickListener(this);
        }
        if (child.isClickable()) {
            child.setOnLongClickListener(this);
        }
    }


    @Override
    public void onScrollStateChanged(int state) {
        if (state == SCROLL_STATE_IDLE) {
            ELog.d("SCROLL_STATE_IDLE");
            setScrollValue(0, 0);
            if (onItemListener != null) {
                onItemListener.onReviseFocusFollow(FMRecyclerView.this, getFocusedChild(), getChildLayoutPosition(getFocusedChild()));
            }
        }
    }


    /**
     * 重写这个方法，可以控制焦点框距离父容器的距离,以及由于recyclerView的滚动
     * 产生的偏移量，导致焦点框错位，这里可以记录滑动偏移量。
     */
    @Override
    public boolean requestChildRectangleOnScreen(View child, Rect rect, boolean immediate) {
        //计算出当前viewGroup即是RecyclerView的内容区域
        final int parentLeft = getPaddingLeft();
        final int parentTop = getPaddingTop();
        final int parentRight = getWidth() - getPaddingRight();
        final int parentBottom = getHeight() - getPaddingBottom();
        ELog.v(TAG, String.format("pL:%d,pT:%d,pR:%d,pB:%d", parentLeft, parentTop, parentRight, parentBottom));

        //计算出child,此时是获取焦点的view请求的区域
        final int childLeft = child.getLeft() + rect.left;
        final int childTop = child.getTop() + rect.top;
        final int childRight = childLeft + rect.width();
        final int childBottom = childTop + rect.height();
        ELog.v(TAG, String.format("cL:%d,cT:%d,cR:%d,cB:%d", childLeft, childTop, childRight, childBottom));

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

    public void setSelectedItemAtCentered(boolean isCentered) {
        this.mSelectedItemCentered = isCentered;
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

            if (paddingLeft == 0 && paddingRight == 0 && paddingTop == 0 && paddingBottom == 0) {
                return;
            }

            int position = parent.getChildAdapterPosition(view);
            int count = getAdapter().getItemCount();
            int clunmCount = Math.min(mColumnCount, count);
            if (mOrientation == VERTICAL) {
                if (position < clunmCount) {
                    outRect.top += paddingTop;
                }

                if (position % clunmCount == 0) {
                    outRect.left += paddingLeft;
                }

                if (position % clunmCount == clunmCount - 1) {
                    outRect.right += paddingRight;
                }

                if (position >= getRowsCount(count, clunmCount) * clunmCount - clunmCount) {
                    outRect.bottom += paddingBottom;
                }

            } else {
                if (position < clunmCount) {
                    outRect.left += paddingLeft;
                }

                if (position % clunmCount == 0) {
                    outRect.top += paddingTop;
                }

                if (position % clunmCount == clunmCount - 1) {
                    outRect.bottom += paddingBottom;
                }

                if (position >= getRowsCount(count, clunmCount) * clunmCount - clunmCount) {
                    outRect.right += paddingRight;
                }
            }
        }

        private int getRowsCount(int totalCount, int clunmCount) {
            return (totalCount - 1) / clunmCount + 1;
        }
    }


    @Override
    public void onClick(View itemView) {
        if (onItemClickListener != null)
            onItemClickListener.onItemClick(this, itemView, getChildLayoutPosition(itemView));
    }

    @Override
    public boolean onLongClick(View itemView) {
        if (onItemLongClickListener != null)
            return onItemLongClickListener.onItemLongClick(this, itemView, getChildLayoutPosition(itemView));
        return false;
    }


    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    public void setOnItemFocusListener(OnItemFocusChangeListener onItemListener) {
        this.onItemListener = onItemListener;
    }


}
