package cn.fengmang.libui.pager;

import android.support.v4.view.ViewPager;

import java.util.List;

/**
 * Created by Administrator on 2018/8/7.
 */

public interface PageIndicatorListener<T> extends ViewPager.OnPageChangeListener {
    void setDatas(List<T> mDatas);

    List<T> getDatas();
}
