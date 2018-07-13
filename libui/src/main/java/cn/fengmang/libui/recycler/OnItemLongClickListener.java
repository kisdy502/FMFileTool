package cn.fengmang.libui.recycler;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by SDT13411 on 2018/1/15.
 */

public interface OnItemLongClickListener {
    boolean onItemLongClick(RecyclerView parent, View itemView, int position);
}
