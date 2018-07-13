package cn.fengmang.file.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.fengmang.file.R;
import cn.fengmang.file.bean.FileOptItem;

/**
 * Created by Administrator on 2018/6/27.
 */

public class FileOptAdapter extends RecyclerView.Adapter<FileOptAdapter.ViewHolder> {

    private List<FileOptItem> mOptItems;
    private Context mContext;
    private LayoutInflater mLayoutInflater;


    public FileOptAdapter(List<FileOptItem> optItems, Context context) {
        this.mOptItems = optItems;
        this.mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.fm_file_opt_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        FileOptItem optItem = mOptItems.get(position);
        holder.optIcon.setBackgroundResource(optItem.getResId());
        holder.optName.setText(optItem.getOptName());
    }

    @Override
    public int getItemCount() {
        return mOptItems != null ? mOptItems.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView optIcon;
        private TextView optName;

        public ViewHolder(View itemView) {
            super(itemView);
            optIcon = itemView.findViewById(R.id.optIcon);
            optName = itemView.findViewById(R.id.optName);
        }
    }
}
