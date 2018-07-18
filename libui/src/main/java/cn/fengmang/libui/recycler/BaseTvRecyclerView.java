package cn.fengmang.libui.recycler;

import android.content.Context;
import android.graphics.Point;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Interpolator;

import cn.fengmang.baselib.ELog;

/**
 * Created by Administrator on 2018/6/22.
 * 电视机使用的RecyclerView
 * 添加点击事件和长按事件
 */

public class BaseTvRecyclerView extends RecyclerView {

    private final static String TAG = "TvRec";

    protected OnItemClickListener onItemClickListener;
    protected OnItemLongClickListener onItemLongClickListener;
    protected OnItemFocusChangeListener onItemListener;
    protected ItemViewClickListener mViewClickListener;
    protected ViewFocusListener mViewFocusListener;
    protected int mCurrentFocusPosition;

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

    public BaseTvRecyclerView(Context context) {
        this(context, null);
    }

    public BaseTvRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseTvRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
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
        initClickEvent();
        initItemFocusEvent();
    }

    private void initItemFocusEvent() {
        mViewFocusListener = new ViewFocusListener() {
            @Override
            public void onFocusChange(final View view, boolean hasFocus) {
                if (null != onItemListener && view != null) {
                    if (hasFocus) {
                        mCurrentFocusPosition = getChildLayoutPosition(view);
                        onItemListener.onItemSelected(BaseTvRecyclerView.this, view, getChildLayoutPosition(view));
                        ELog.d(TAG, "onFocusChange:" + mCurrentFocusPosition);
                    } else {
                        view.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                onItemListener.onItemPreSelected(BaseTvRecyclerView.this, view, getChildLayoutPosition(view));
                            }
                        }, 9);
                    }
                }
            }
        };
    }


    private void initClickEvent() {
        mViewClickListener = new ItemViewClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(BaseTvRecyclerView.this, view, getChildLayoutPosition(view));
                }
            }

            @Override
            public boolean onLongClick(View view) {
                if (onItemLongClickListener != null) {
                    return onItemLongClickListener.onItemLongClick(BaseTvRecyclerView.this, view, getChildLayoutPosition(view));
                }
                return false;
            }
        };

    }

    @Override
    public void onScrollStateChanged(int state) {
        if (state == SCROLL_STATE_IDLE) {
            ELog.d("SCROLL_STATE_IDLE");
            setScrollValue(0, 0);
            if (onItemListener != null) {
                onItemListener.onReviseFocusFollow(BaseTvRecyclerView.this, getFocusedChild(), getChildLayoutPosition(getFocusedChild()));
            }
        }
    }

    @Override
    public void onChildAttachedToWindow(View child) {
        if (!ViewCompat.hasOnClickListeners(child)) {
            child.setOnClickListener(mViewClickListener);
        }
        child.setOnLongClickListener(mViewClickListener);
        if (child.getOnFocusChangeListener() == null) {
            child.setOnFocusChangeListener(mViewFocusListener);
        }
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


    @Override
    public void smoothScrollBy(int dx, int dy, Interpolator interpolator) {
        setScrollValue(dx, dy);
        super.smoothScrollBy(dx, dy, interpolator);
    }
}


