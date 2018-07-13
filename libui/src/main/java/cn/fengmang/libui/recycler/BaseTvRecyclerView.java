package cn.fengmang.libui.recycler;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;

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
    protected OnItemFocusListener onItemListener;
    protected ItemViewClickListener mViewClickListener;
    protected ViewFocusListener mViewFocusListener;

    private OnChildAttachStateChangeListener childAttachListener = new OnChildAttachStateChangeListener() {
        @Override
        public void onChildViewAttachedToWindow(View view) {
            final int childPosition = getChildAdapterPosition(view);
        }

        @Override
        public void onChildViewDetachedFromWindow(View view) {
            final int childPosition = getChildAdapterPosition(view);
        }
    };

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
        addOnChildAttachStateChangeListener(childAttachListener);
    }

    private void initItemFocusEvent() {
        mViewFocusListener = new ViewFocusListener() {
            @Override
            public void onFocusChange(final View view, boolean hasFocus) {
                if (null != onItemListener) {
                    if (view != null) {
                        if (hasFocus) {
                            onItemListener.onItemSelected(BaseTvRecyclerView.this, view, getChildLayoutPosition(view));
                        } else {
                            final int position = getChildLayoutPosition(view);
                            view.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    onItemListener.onItemPreSelected(BaseTvRecyclerView.this, view, position);
                                }
                            }, 9);
                        }
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
    public void stopScroll() {
        super.stopScroll();
        ELog.v("stopScroll");
    }

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, @Nullable Rect previouslyFocusedRect) {
        ELog.v("gainFocus:" + gainFocus + ",direction:" + direction);
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
    }

    @Override
    public void setLayoutManager(LayoutManager layout) {
        super.setLayoutManager(layout);
    }

    @Override
    public void onChildAttachedToWindow(View child) {
        if (!ViewCompat.hasOnClickListeners(child)) {
            child.setOnClickListener(mViewClickListener);
        }
        child.setOnLongClickListener(mViewClickListener);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        return super.dispatchKeyEvent(event);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    public void setOnItemFocusListener(OnItemFocusListener onItemListener) {
        this.onItemListener = onItemListener;
    }

    @Override
    public void scrollBy(int x, int y) {
        super.scrollBy(x, y);
    }

    @Override
    public void smoothScrollBy(int dx, int dy) {
        ELog.v("smoothScrollBy:" + (dx != 0 ? dx : dy));
        super.smoothScrollBy(dx, dy);
    }
}


