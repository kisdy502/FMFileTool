package cn.fengmang.file.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import cn.fengmang.file.R;
import cn.fengmang.file.bean.VideoListBean;

/**
 * Created by Administrator on 2018/7/30.
 */

public class PerformerAdapter extends RecyclerView.Adapter<PerformerAdapter.ViewHolder> {

    private Context mContext;
    private List<VideoListBean.Performer> performerList;

    public PerformerAdapter(Context mContext, List<VideoListBean.Performer> performerList) {
        this.mContext = mContext;
        this.performerList = performerList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.fm_performer_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.tvName.setText(performerList.get(position).getName());
        Glide.with(mContext).load(performerList.get(position).getPerformUrl()).placeholder(R.mipmap.ic_launcher).into(holder.imgPerformer);
    }

    @Override
    public int getItemCount() {
        return performerList != null ? performerList.size() : 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName;
        private ImageView imgPerformer;

        public ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.performerName);
            imgPerformer = itemView.findViewById(R.id.img_performer);
        }
    }
}
