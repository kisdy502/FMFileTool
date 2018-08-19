package cn.fengmang.libui.pager;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import cn.fengmang.baselib.ELog;
import cn.fengmang.libui.R;


/**
 * Created by Administrator on 2018/8/7.
 */

public class DefaultPageIndicator<T> extends LinearLayout implements PageIndicatorListener<T> {

    List<T> mDatas;
    private int[] pageIndicatorRes;
    private ArrayList<ImageView> mPointViews = new ArrayList<>();
    LinearLayout hView;
    int position = -1;
    private int size = 0;

    private int paddingLeft, paddingTop, paddingRight, paddingBottom;

    public DefaultPageIndicator(@NonNull Context context) {
        this(context, null);
    }

    public DefaultPageIndicator(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DefaultPageIndicator(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        hView = (LinearLayout) LayoutInflater.from(context).inflate(
                R.layout.default_indicator, this, true);
        hView.setOrientation(LinearLayout.HORIZONTAL);
        pageIndicatorRes = new int[]{R.drawable.ic_page_indicator, R.drawable.ic_page_indicator_focused};
        paddingLeft = 10;
        paddingTop = 0;
        paddingRight = 10;
        paddingBottom = 20;
    }

    public DefaultPageIndicator setPageIndicator(@DrawableRes int[] page_indicatorId) {
        this.pageIndicatorRes = page_indicatorId;
        notifyDataChangedView();
        return this;
    }

    public void setDatas(List<T> mDatas) {
        this.mDatas = mDatas;
        notifyDataChangedView();
    }

    public void setPadding(int left, int top, int right, int bottom) {
        paddingLeft = left;
        paddingTop = top;
        paddingRight = right;
        paddingBottom = bottom;
    }

    @Override
    public List<T> getDatas() {
        return mDatas;
    }

    private void notifyDataChangedView() {
        mPointViews.clear();
        hView.removeAllViews();
        if (pageIndicatorRes == null) return;
        if (mDatas == null) return;
        size = mDatas.size();
        for (int count = 0; count < size; count++) {
            // 翻页指示的点
            ImageView pointView = new ImageView(getContext());
            pointView.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
            if (mPointViews.isEmpty())
                pointView.setImageResource(pageIndicatorRes[1]);
            else
                pointView.setImageResource(pageIndicatorRes[0]);
            mPointViews.add(pointView);
            hView.addView(pointView);
        }
        onPageSelected(position);
        return;
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int index) {
        ELog.d("index:" + index);
        // 指示器
        this.position = index % size;
        ELog.d("position:" + position);
        for (int i = 0; i < mPointViews.size(); i++) {
            mPointViews.get(i).setImageResource(pageIndicatorRes[1]);
            if (position != i) {
                mPointViews.get(i).setImageResource(pageIndicatorRes[0]);
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
