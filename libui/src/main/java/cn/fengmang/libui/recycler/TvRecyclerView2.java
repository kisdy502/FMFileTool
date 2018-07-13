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
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Interpolator;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import cn.fengmang.baselib.ELog;
import cn.fengmang.libui.R;

/**
 * Created by Administrator on 2018/6/27.
 */

public class TvRecyclerView2 extends BaseTvRecyclerView {

    private final static String TAG = "TvRecycler";
    protected Rect mSpacesRect;
    protected int paddingLeft;
    protected int paddingRight;
    protected int paddingTop;
    protected int paddingBottom;
    protected int mColumnCount;
    protected int mOrientation;


    private boolean mAllowItemSelected = false;

    private boolean mSelectedItemCentered = false;
    private int mSelectedItemOffsetStart, mSelectedItemOffsetEnd;


    private int mDuration;
    private Method smoothScrollByMethod;
    private Field mViewFlingerField;

    public TvRecyclerView2(Context context) {
        this(context, null);
    }

    public TvRecyclerView2(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TvRecyclerView2(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TvRecyclerView2);
            int left = a.getDimensionPixelSize(R.styleable.TvRecyclerView2_itemSpaceLeft, 0);
            int top = a.getDimensionPixelSize(R.styleable.TvRecyclerView2_itemSpaceTop, 0);
            int right = a.getDimensionPixelSize(R.styleable.TvRecyclerView2_itemSpaceRight, 0);
            int bottom = a.getDimensionPixelSize(R.styleable.TvRecyclerView2_itemSpaceBottom, 0);
            // padding值只能通过recyclerPadding设置

            paddingLeft = a.getDimensionPixelSize(R.styleable.TvRecyclerView2_recyclerPaddingLeft, 0);
            paddingRight = a.getDimensionPixelSize(R.styleable.TvRecyclerView2_recyclerPaddingRight, 0);
            paddingTop = a.getDimensionPixelSize(R.styleable.TvRecyclerView2_recyclerPaddingTop, 0);
            paddingBottom = a.getDimensionPixelSize(R.styleable.TvRecyclerView2_recyclerPaddingBottom, 0);

            int padding = a.getDimensionPixelSize(R.styleable.TvRecyclerView2_recyclerPadding, 0);
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
        initScrollEvent();

        initViewFlinger();
    }

    private void setItemSpaces(int left, int top, int right, int bottom) {
        if (mSpacesRect == null) {
            mSpacesRect = new Rect(left, top, right, bottom);
            super.addItemDecoration(new SpacesItemDecoration(mSpacesRect));
        }
    }

    private void initScrollEvent() {
        addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                ELog.v("newState:" + newState);
                if (newState == SCROLL_STATE_IDLE) {
                    final View child = getFocusedChild();
                    final int position = getChildLayoutPosition(child);
                    if (onItemListener != null) {
                        onItemListener.onReviseFocusFollow(TvRecyclerView2.this, child, position);
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }



    private void initViewFlinger() {
        try {
            Class<?> c = Class.forName("android.support.v7.widget.RecyclerView");//获得Class对象

            Field mLayoutField = c.getDeclaredField("mLayout");     //根据属性名称，获得类的属性成员Field
            mLayoutField.setAccessible(true);                       //设置为可访问状态

            LayoutManager mLayout = (LayoutManager) mLayoutField.get(this);   //获得该属性对应的对象
            Field mLayoutFrozen = c.getDeclaredField("mLayoutFrozen");
            mLayoutFrozen.setAccessible(true);
            Object value = mLayoutFrozen.get(this);
            ELog.v(TAG, "mLayoutFrozen:" + value);

            mViewFlingerField = c.getDeclaredField("mViewFlinger");
            mViewFlingerField.setAccessible(true);

            Class<?> ViewFlingerClass = Class.forName(mViewFlingerField.getType().getName());
            smoothScrollByMethod = ViewFlingerClass.getDeclaredMethod("smoothScrollBy",
                    int.class, int.class, int.class);
            smoothScrollByMethod.setAccessible(true);//设置为可操作状态
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onChildAttachedToWindow(View child) {
        super.onChildAttachedToWindow(child);
        if (child.getOnFocusChangeListener() == null) {
            child.setOnFocusChangeListener(mViewFocusListener);
        }
    }

    @Override
    public void onChildDetachedFromWindow(View child) {
        child.setSelected(false);
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
        if (null != child) {
            if (mSelectedItemCentered) {
                mSelectedItemOffsetStart = !isVertical() ? (getFreeWidth() - child.getWidth()) : (getFreeHeight() - child.getHeight());
                mSelectedItemOffsetStart /= 2;
                mSelectedItemOffsetEnd = mSelectedItemOffsetStart;
            }
        }
        super.requestChildFocus(child, focused);
    }


    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        return super.dispatchKeyEvent(event);
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
                if (mDuration == 0) {
                    smoothScrollBy(dx, dy);
                } else {
                    smoothScrollBy(dx, dy, mDuration);
                }
            }
            return true;
        }
        postInvalidate();
        return false;
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

    @Override
    public void smoothScrollBy(int dx, int dy, Interpolator interpolator) {
        setScrollValue(dx, dy);
        super.smoothScrollBy(dx, dy, interpolator);
    }

    /**
     * 设置选中的Item居中；
     *
     * @param isCentered
     */
    public void setSelectedItemAtCentered(boolean isCentered) {
        this.mSelectedItemCentered = isCentered;
    }

    public boolean isVertical() {
        LayoutManager lm = getLayoutManager();
        if (lm instanceof GridLayoutManager) {
            return ((GridLayoutManager) getLayoutManager()).getOrientation() == GridLayoutManager.VERTICAL;
        }
        if (lm instanceof LinearLayoutManager) {
            LinearLayoutManager llm = (LinearLayoutManager) lm;
            return llm.getOrientation() == LinearLayoutManager.VERTICAL;
        }
        if (lm instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager sglm = (StaggeredGridLayoutManager) lm;
            return sglm.getOrientation() == StaggeredGridLayoutManager.VERTICAL;
        }
        return false;
    }

    public void smoothScrollBy(int dx, int dy, int duration) {
        ELog.v(TAG, "smoothScrollBy:dx=" + dx + " dy=" + dy);
        if (dx != 0 || dy != 0) {
            try {
                smoothScrollByMethod.invoke(mViewFlingerField.get(this), dx, dy, duration);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    private int getFreeWidth() {
        return getWidth() - getPaddingLeft() - getPaddingRight();
    }

    private int getFreeHeight() {
        return getHeight() - getPaddingTop() - getPaddingBottom();
    }


    /***set method***/

    public void setAllowItemSelected(boolean allowItemSelected) {
        this.mAllowItemSelected = allowItemSelected;
    }

    public void setDuration(int duration) {
        this.mDuration = duration;
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
}
