package cn.fengmang.libui.recycler;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by SDT13411 on 2018/1/12.
 */

public interface OnItemFocusChangeListener {
    /**
     * 焦点离开
     *
     * @param parent   TvRecyclerView
     * @param itemView 失去焦点的itemView
     * @param position 失去焦点item的位置
     * @return true表示用户处理焦点移动, TvRecyclerView不再处理焦点移动, 否则用户不再处理焦点移动，交给TvRecyclerView处理焦点移动
     */
    boolean onItemPreSelected(@NonNull RecyclerView parent, @NonNull View itemView, int position);

    /**
     * 获取焦点
     *
     * @param parent   TvRecyclerView
     * @param itemView 获取焦点的itemView
     * @param position 获取焦点item的位置
     * @return true表示用户处理焦点移动, TvRecyclerView不再处理焦点移动, 否则用户不再处理焦点移动，交给TvRecyclerView处理焦点移动
     */
    boolean onItemSelected(@NonNull RecyclerView parent,@NonNull View itemView, int position);

    /**
     * 处理一些偏差
     *
     * @param parent   TvRecyclerView
     * @param itemView 获取焦点的itemView
     * @param position 获取焦点item的位置
     * @return true表示用户处理焦点移动, TvRecyclerView不再处理焦点移动, 否则用户不再处理焦点移动，交给TvRecyclerView处理焦点移动
     */
    boolean onReviseFocusFollow(@NonNull RecyclerView parent,@NonNull  View itemView, int position);
}
