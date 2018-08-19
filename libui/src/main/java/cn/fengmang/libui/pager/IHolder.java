package cn.fengmang.libui.pager;

import android.content.Context;
import android.view.ViewGroup;

/**
 * Created by Administrator on 2018/8/6.
 */

public interface IHolder<T> {
    ViewPagerHolder createView(Context context,
                               ViewGroup parent, int position, int viewType);
    void UpdateUI(Context context, ViewPagerHolder viewHolder, int position, T data);
    int getViewType(int position);
}
