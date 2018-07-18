package cn.fengmang.libui.widget;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

import cn.fengmang.baselib.ELog;


/**
 * supportv7.27+版本
 */

public class V27GridLayoutManager extends GridLayoutManager {

    public V27GridLayoutManager(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr, 0);
    }

    public V27GridLayoutManager(Context context, int spanCount) {
        super(context, spanCount);
    }

    public V27GridLayoutManager(Context context, int spanCount, int orientation, boolean reverseLayout) {
        super(context, spanCount, orientation, reverseLayout);
    }


//    @Override
//    public boolean requestChildRectangleOnScreen(RecyclerView parent, View child, Rect rect, boolean immediate, boolean focusedChildVisible) {
//        if (parent instanceof XRecyclerView) {
//            return parent.requestChildRectangleOnScreen(child, rect, immediate);
//        }
//        return super.requestChildRectangleOnScreen(parent, child, rect, immediate, focusedChildVisible);
//    }

    @Override
    public View onInterceptFocusSearch(View focused, int direction) {
//        ELog.e("focused is null:" + (focused == null));
//        int count = getItemCount();//获取item的总数
//        int fromPos = getPosition(focused);//当前焦点的位置
//        int lastVisibleItemPos = findLastVisibleItemPosition();//最新的已显示的Item的位置
//        ELog.i(String.format("fromPos:%d,count%d,lastVisibleItemPos:%d", fromPos, count, lastVisibleItemPos));
//        switch (direction) {//根据按键逻辑控制position
//            case View.FOCUS_DOWN:
//                fromPos++;
//                break;
//            case View.FOCUS_UP:
//                fromPos--;
//                break;
//        }
//        ELog.i(String.format("fromPos:%d,count%d,lastVisibleItemPos:%d", fromPos, count, lastVisibleItemPos));
//        if (fromPos < 0 || fromPos >= count) {
//            //如果下一个位置<0,或者超出item的总数，则返回当前的View，即焦点不动
//            return focused;
//        } else {
//            //如果下一个位置大于最新的已显示的item，即下一个位置的View没有显示，则滑动到那个位置，让他显示，就可以获取焦点了
//            if (fromPos > lastVisibleItemPos) {
//               scrollToPosition(fromPos);//
//            }
//        }
        return super.onInterceptFocusSearch(focused, direction);
    }

}
