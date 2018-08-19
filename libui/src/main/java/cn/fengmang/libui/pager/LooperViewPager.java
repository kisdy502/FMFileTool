package cn.fengmang.libui.pager;

import android.content.Context;
import android.database.DataSetObserver;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import cn.fengmang.baselib.ELog;


/**
 * 无限轮播的ViewPager
 * Created by Administrator on 2018/8/6.
 */

public class LooperViewPager extends ViewPager {
    private LooperPagerAdapter mAdapter;

    private boolean isTouchScroll = true;  //手动滑动
    private boolean canLoop = true;
    private PageTransformer transformer;    //变换动画


    LooperViewPager(Context context) {
        this(context, null);
    }

    LooperViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        addOnPageChangeListener(onPageChangeListener);
    }


    public void setAdapter(LooperPagerAdapter adapter) {
        mAdapter = adapter;
        mAdapter.setCanLoop(canLoop);
        mAdapter.setViewPager(this);
        super.setAdapter(mAdapter);
        setSuperCurrentItem(mAdapter.startAdapterPosition(0), false);

        mAdapter.registerRealCanLoopObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                ELog.d("onChanged");
            }

            @Override
            public void onInvalidated() {
                super.onInvalidated();
                ELog.d("onInvalidated");
            }
        });
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (transformer != null) {
            final int scrollX = getScrollX();
            ELog.d("scrollX:" + scrollX);
            final int childCount = getChildCount();
            for (int i = 0; i < childCount; i++) {
                final View child = getChildAt(i);
                final LayoutParams lp = (LayoutParams) child.getLayoutParams();

                if (lp.isDecor) continue;
                final float transformPos = (float) (child.getLeft() - scrollX) / getClientWidth();
                ELog.d("transformPos:" + transformPos);
                transformer.transformPage(child, transformPos);
            }
        }
    }

    @Override
    public void setCurrentItem(int item) {
        super.setCurrentItem(mAdapter.realPostiton2AdapterPosition(getSuperCurrentItem(), item));
    }

    @Override
    public void setPageTransformer(boolean reverseDrawingOrder, PageTransformer transformer) {
        this.transformer = transformer;
        super.setPageTransformer(reverseDrawingOrder, transformer);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (isTouchScroll) {
            return super.onTouchEvent(ev);
        } else
            return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (isTouchScroll)
            return super.onInterceptTouchEvent(ev);
        else
            return false;
    }

    @Override
    public int getCurrentItem() {
        return mAdapter.adapterPostiton2RealDataPosition(super.getCurrentItem());
    }

    public int getSuperCurrentItem() {
        return super.getCurrentItem();
    }

    public void setSuperCurrentItem(int item, boolean smoothScroll) {
        super.setCurrentItem(item, smoothScroll);
    }

    boolean isTouchScroll() {
        return isTouchScroll;
    }

    void setTouchScroll(boolean isCanScroll) {
        this.isTouchScroll = isCanScroll;
    }

    void setCanLoop(boolean canLoop) {
        this.canLoop = canLoop;
        if (mAdapter == null) return;

        mAdapter.setCanLoop(canLoop);
    }

    boolean isCanLoop() {
        return canLoop;
    }

    int getRealItem(int position) {
        return mAdapter.adapterPostiton2RealDataPosition(position);
    }

    public LooperPagerAdapter getAdapter() {
        return mAdapter;
    }


    void setOnItemClickListener(OnPageClickListener onItemClickListener) {
        if (mAdapter != null)
            mAdapter.setOnItemClickListener(onItemClickListener);
    }

    private int getClientWidth() {
        int clientWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
        ELog.d("clientWidth:" + clientWidth);
        return clientWidth;
    }


    private OnPageChangeListener onPageChangeListener = new OnPageChangeListener() {
        private float mPreviousPosition = -1;

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            if (state == SCROLL_STATE_IDLE) {
                int currentItem = getSuperCurrentItem();
                int realAdapterPosition = mAdapter.controlAdapterPosition(currentItem);
                if (currentItem != realAdapterPosition) {
                    setSuperCurrentItem(realAdapterPosition, false);
                }
            }

        }
    };
}
