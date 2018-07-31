package cn.fengmang.libui.recycler;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Administrator on 2018/7/31.
 */

public class NestFMRecyclerView extends FMRecyclerView {
    public NestFMRecyclerView(Context context) {
        super(context);
    }

    public NestFMRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public NestFMRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onChildAttachedToWindow(View child) {
        super.onChildAttachedToWindow(child);
        if (child instanceof FMRecyclerView) {
            ((FMRecyclerView) child).setDescendantFocusability(FOCUS_AFTER_DESCENDANTS);
        } else {
            ((ViewGroup) child).setDescendantFocusability(FOCUS_BEFORE_DESCENDANTS);
        }
    }
}
