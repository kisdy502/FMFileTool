package cn.fengmang.file.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import cn.fengmang.file.R;
import cn.fengmang.file.bean.TitleBean;
import cn.fengmang.file.bean.VideoListBean;
import cn.fengmang.libui.recycler.FMRecyclerView;
import cn.fengmang.libui.recycler.OnItemFocusChangeListener;
import cn.fengmang.libui.recycler.V7GridLayoutManager;

/**
 * Created by Administrator on 2018/7/30.
 */

public class VideoListAdapter extends RecyclerView.Adapter<VideoListAdapter.ViewHolder> {

    private Context mContext;
    private VideoListBean videoListBean;
    private OnItemFocusChangeListener itemFocusChangeListener;

    public void setItemFocusChangeListener(OnItemFocusChangeListener itemFocusChangeListener) {
        this.itemFocusChangeListener = itemFocusChangeListener;
    }

    public VideoListAdapter(Context mContext, VideoListBean videoListBean) {
        this.mContext = mContext;
        this.videoListBean = videoListBean;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(new FMRecyclerView(mContext));
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        if (position == 0) {
            PerformerAdapter adapter = new PerformerAdapter(mContext, videoListBean.getPerformerList());
            V7GridLayoutManager layoutManager = new V7GridLayoutManager(mContext, 7, LinearLayoutManager.VERTICAL, false);
            holder.mRecyclerView.setSpaceVerticalAndHorizontal(mContext.getResources().getDimensionPixelOffset(R.dimen.h36),
                    mContext.getResources().getDimensionPixelOffset(R.dimen.w36));
            holder.mRecyclerView.setLayoutManager(layoutManager);
            holder.mRecyclerView.setAdapter(adapter);

        } else if (position == 1) {
            VideoInfoAdapter adapter = new VideoInfoAdapter(mContext, videoListBean.getVideoInfoList());
            V7GridLayoutManager layoutManager = new V7GridLayoutManager(mContext, 5, LinearLayoutManager.VERTICAL, false);
            holder.mRecyclerView.setSpaceVerticalAndHorizontal(mContext.getResources().getDimensionPixelOffset(R.dimen.h36),
                    mContext.getResources().getDimensionPixelOffset(R.dimen.w36));
            holder.mRecyclerView.setLayoutManager(layoutManager);
            holder.mRecyclerView.setAdapter(adapter);
        } else if (position == 2) {
            HotAppAdapter adapter = new HotAppAdapter(mContext, videoListBean.getHotInfoList());
            V7GridLayoutManager layoutManager = new V7GridLayoutManager(mContext, 3, LinearLayoutManager.VERTICAL, false);
            holder.mRecyclerView.setSpaceVerticalAndHorizontal(mContext.getResources().getDimensionPixelOffset(R.dimen.h36),
                    mContext.getResources().getDimensionPixelOffset(R.dimen.w36));
            holder.mRecyclerView.setLayoutManager(layoutManager);
            holder.mRecyclerView.setAdapter(adapter);
        }
        holder.mRecyclerView.setOnItemFocusListener(itemFocusChangeListener);
    }

    @Override
    public int getItemCount() {
        return 3;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private FMRecyclerView mRecyclerView;

        public ViewHolder(View itemView) {
            super(itemView);
            mRecyclerView = (FMRecyclerView) itemView;
            mRecyclerView.setNestedScrollingEnabled(false);


        }
    }
}
