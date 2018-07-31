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

public class VideoInfoAdapter extends RecyclerView.Adapter<VideoInfoAdapter.ViewHolder> {

    private Context mContext;
    private List<VideoListBean.VideoInfo> videoInfoList;

    public VideoInfoAdapter(Context mContext, List<VideoListBean.VideoInfo> videoInfoList) {
        this.mContext = mContext;
        this.videoInfoList = videoInfoList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.fm_video_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.tvName.setText(videoInfoList.get(position).getVideoName());
        Glide.with(mContext).load(videoInfoList.get(position).getVideoPosterUrl()).placeholder(R.mipmap.ic_launcher).into(holder.imgVideo);
    }

    @Override
    public int getItemCount() {
        return videoInfoList != null ? videoInfoList.size() : 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName;
        private TextView tvScore;
        private ImageView imgVideo;

        public ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.videoName);
            tvScore = itemView.findViewById(R.id.videoScore);
            imgVideo = itemView.findViewById(R.id.img_video_poster);
        }
    }
}
