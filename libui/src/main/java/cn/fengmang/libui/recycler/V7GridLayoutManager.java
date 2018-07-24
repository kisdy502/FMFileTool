package cn.fengmang.libui.recycler;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Administrator on 2018/7/18.
 */

public class V7GridLayoutManager extends GridLayoutManager {
    public V7GridLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public V7GridLayoutManager(Context context, int spanCount) {
        super(context, spanCount);
    }

    public V7GridLayoutManager(Context context, int spanCount, int orientation, boolean reverseLayout) {
        super(context, spanCount, orientation, reverseLayout);
    }

    @Override
    public boolean requestChildRectangleOnScreen(RecyclerView parent, View child, Rect rect, boolean immediate) {
        return super.requestChildRectangleOnScreen(parent, child, rect, immediate);
    }

    @Override
    public boolean requestChildRectangleOnScreen(RecyclerView parent, View child, Rect rect, boolean immediate, boolean focusedChildVisible) {
        return super.requestChildRectangleOnScreen(parent, child, rect, immediate, focusedChildVisible);
    }
}
