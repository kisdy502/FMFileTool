package cn.fengmang.libui.pager;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.LayoutRes;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Administrator on 2018/8/6.
 */

public class ViewPagerHolder {

    private SparseArray<View> mViews;
    private View mConvertView;
    private Context mContext;
    private int viewType = 0;
    private int position = -1;

    public ViewPagerHolder(Context context, View itemView) {
        mContext = context;
        mConvertView = itemView;
        mViews = new SparseArray<View>();
    }

    public static ViewPagerHolder createViewHolder(Context context, View itemView) {
        ViewPagerHolder holder = new ViewPagerHolder(context, itemView);
        return holder;
    }

    public static ViewPagerHolder createViewHolder(Context context, ViewGroup parent,
                                                   @LayoutRes int layoutId, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(layoutId, parent, false);
        ViewPagerHolder holder = new ViewPagerHolder(context, itemView);
        return holder;
    }

    public int getViewType() {
        return viewType;
    }

    public int getPosition() {
        return position;
    }

    public View getConvertView() {
        return mConvertView;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public ViewPagerHolder setText(int viewId, String text) {
        TextView tv = getView(viewId);
        tv.setText(text);
        return this;
    }

    public void setBackgroundColor(int viewId, int re) {

    }

    public ViewPagerHolder setImageBitmap(int viewId, Bitmap bitmap) {
        ImageView view = getView(viewId);
        view.setImageBitmap(bitmap);
        return this;
    }

    public ViewPagerHolder setBackgroundRes(int viewId, int backgroundRes) {
        View view = getView(viewId);
        view.setBackgroundResource(backgroundRes);
        return this;
    }

    public <T extends View> T getView(int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = mConvertView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }
}
