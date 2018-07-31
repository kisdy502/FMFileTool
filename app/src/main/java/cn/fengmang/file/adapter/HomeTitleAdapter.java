package cn.fengmang.file.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import cn.fengmang.file.R;
import cn.fengmang.file.bean.TitleBean;

/**
 * Created by Administrator on 2018/7/30.
 */

public class HomeTitleAdapter extends RecyclerView.Adapter<HomeTitleAdapter.ViewHolder> {

    private Context mContext;
    private List<TitleBean> titleBeanList;

    public HomeTitleAdapter(Context mContext, List<TitleBean> titleBeanList) {
        this.mContext = mContext;
        this.titleBeanList = titleBeanList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.fm_title_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.tvTitle.setText(titleBeanList.get(position).getTitleName());
    }

    @Override
    public int getItemCount() {
        return titleBeanList != null ? titleBeanList.size() : 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTitle;

        public ViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.titleName);
        }
    }
}
