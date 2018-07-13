package cn.fengmang.file.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.fengmang.file.R;
import cn.fengmang.file.bean.FileItem;
import cn.fengmang.file.utils.DateUtil;
import cn.fengmang.file.utils.FileIconHelper;
import cn.fengmang.file.utils.FileOptHelper;

/**
 * Created by Administrator on 2018/6/27.
 */

public class FileListAdapter extends RecyclerView.Adapter<FileListAdapter.ViewHolder> {

    private int checkStatus;
    private int mViewMode;
    private List<FileItem> fileItemList;
    private Context mContext;
    private LayoutInflater mLayoutInflater;

    public FileListAdapter(int checkStatus, int viewMode, List<FileItem> fileItemList, Context context) {
        this.checkStatus = checkStatus;
        this.mViewMode = viewMode;
        this.fileItemList = fileItemList;
        this.mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);
        setHasStableIds(true);
    }

    public void setCheckStatus(int checkStatus) {
        this.checkStatus = checkStatus;
        notifyDataSetChanged();
    }

    public void setViewMode(int viewMode) {
        this.mViewMode = viewMode;
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        if (mViewMode == FileOptHelper.VIEW_MODE_LIST) {
            view = mLayoutInflater.inflate(R.layout.fm_file_item_list_layout, parent, false);
        } else if (mViewMode == FileOptHelper.VIEW_MODE_GRID) {
            view = mLayoutInflater.inflate(R.layout.fm_file_item_grid_layout, parent, false);
        } else {
            throw new IllegalArgumentException("ViewMode 参数不正确");
        }
        return new ViewHolder(view, mViewMode);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        FileItem fileItem = fileItemList.get(position);
        holder.fileName.setText(fileItem.getFileName());
        String ext = FileIconHelper.getExtion(fileItem.getFileName());
        int iconResId = FileIconHelper.getFileIcon(ext);
        if (fileItem.isDirectory()) {
            if (fileItem.isEmpty()) {
                holder.fileIcon.setBackgroundResource(R.drawable.file_icon_folder_n);
            } else {
                holder.fileIcon.setBackgroundResource(R.drawable.file_icon_folder);
            }
        } else {
            holder.fileIcon.setBackgroundResource(iconResId);
        }
        if (mViewMode == FileOptHelper.VIEW_MODE_LIST) {
            if (!TextUtils.isEmpty(fileItem.getFileTag())) {
                holder.fileTag.setVisibility(View.VISIBLE);
                holder.fileTag.setText(fileItem.getFileTag());
            } else {
                holder.fileTag.setVisibility(View.GONE);
            }
            if (fileItem.isFile()) {
                holder.fileSize.setVisibility(View.VISIBLE);
                holder.fileSize.setText(FileOptHelper.convertStorage(fileItem.getFileSize()));
            } else {
                holder.fileSize.setVisibility(View.GONE);
            }
            holder.fileModifyDate.setText(DateUtil.timestampToDateString(fileItem.getLastModifyDate()));
        }

        holder.fileIsChecked.setChecked(fileItem.isChecked());
        if (checkStatus == FileOptHelper.SELECT_STATUS_ONE) {
            holder.fileIsChecked.setVisibility(View.GONE);
        } else {
            holder.fileIsChecked.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return fileItemList != null ? fileItemList.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView fileIcon;
        private TextView fileName;
        private TextView fileModifyDate;
        private TextView fileTag;
        private CheckBox fileIsChecked;
        private TextView fileSize;

        public ViewHolder(View itemView, int mViewMode) {
            super(itemView);
            fileIcon = itemView.findViewById(R.id.img_file_icon);
            fileName = itemView.findViewById(R.id.fileName);
            fileIsChecked = itemView.findViewById(R.id.fileChecked);

            if (mViewMode == FileOptHelper.VIEW_MODE_LIST) {
                fileModifyDate = itemView.findViewById(R.id.fileDate);
                fileTag = itemView.findViewById(R.id.fileTag);
                fileSize = itemView.findViewById(R.id.fileSize);
            }
        }
    }
}
