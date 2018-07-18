package cn.fengmang.file.dialog;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.fengmang.baselib.ELog;
import cn.fengmang.file.R;
import cn.fengmang.file.adapter.FileOptAdapter;
import cn.fengmang.file.bean.FileOptItem;
import cn.fengmang.file.utils.FileClipboard;
import cn.fengmang.file.utils.FileOptHelper;
import cn.fengmang.libui.flying.DrawableFlyingFrameView;
import cn.fengmang.libui.recycler.FMRecyclerView;
import cn.fengmang.libui.recycler.OnItemClickListener;
import cn.fengmang.libui.recycler.OnItemFocusChangeListener;

/**
 * Created by Administrator on 2018/6/29.
 */
public class FileOptDialogFragment extends BaseDialogFragment {
    private final static String TAG = "FileOptDilog";

    private FMRecyclerView mRecyclerView;
    private ImageView mImgArrowLeft;
    private ImageView mImgArrowRight;
    private DrawableFlyingFrameView mFlyingView;
    private FileOptAdapter mAdapter;

    private int mViewtMode = FileOptHelper.VIEW_MODE_LIST;
    private int mSortType = FileOptHelper.SORT_BYNAME;
    private int mSelectStatus = FileOptHelper.SELECT_STATUS_ONE;
    private File mCurrentDir;

    private List<FileOptItem> mOptList = new ArrayList<>();

    @Override
    protected String getUmengTag() {
        return "FileOptDialogFragment";
    }

    @Override
    protected int setContentView() {
        return R.layout.fm_file_opt_fragment_layout;
    }

    @Override
    protected void initUI() {
        mRecyclerView = mRootView.findViewById(R.id.optList);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 7, GridLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setOnItemFocusListener(new OnItemFocusChangeListener() {
            @Override
            public boolean onItemPreSelected(@NonNull RecyclerView parent, @NonNull View itemView, int position) {
                return false;
            }

            @Override
            public boolean onItemSelected(@NonNull RecyclerView parent, @NonNull View itemView, int position) {
                mFlyingView.onMoveTo(itemView, 1.0f, 1.0f, 0f);
                return false;
            }

            @Override
            public boolean onReviseFocusFollow(@NonNull RecyclerView parent, @NonNull View itemView, int position) {
                return false;
            }
        });

        mRecyclerView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView parent, View itemView, int position) {
                ELog.d("onItemClick:"+position);
                if (onFileOptItemClickListener != null) {
                    onFileOptItemClickListener.onExecItem(mOptList.get(position));
                }
                dismiss();
            }
        });

        mFlyingView = DrawableFlyingFrameView.build((ViewGroup) mRootView);
        mFlyingView.setFlyingDrawable(getResources().getDrawable(R.drawable.hover_item));

    }

    @Override
    protected void fillData() {
        initOptList();
        mAdapter = new FileOptAdapter(mOptList, getContext());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.requestFocus();
    }

    @Override
    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_UP) {
            if (keyCode == KeyEvent.KEYCODE_MENU) {
                dismiss();
                return true;
            }
        }
        return false;
    }

    public void setViewtMode(int mViewtMode) {
        this.mViewtMode = mViewtMode;
    }

    public void setSortType(int mSortType) {
        this.mSortType = mSortType;
    }

    public void setmSelectStatus(int mSelectStatus) {
        this.mSelectStatus = mSelectStatus;
    }


    public void setmCurrentDir(File mCurrentDir) {
        this.mCurrentDir = mCurrentDir;
    }


    private void initOptList() {
        mOptList.clear();
        if (mCurrentDir.list() != null && mCurrentDir.list().length > 0) {
            mOptList.add(new FileOptItem("delete", "删除", R.drawable.delete));
            mOptList.add(new FileOptItem("copy", "复制", R.drawable.copy));
            mOptList.add(new FileOptItem("cut", "剪切", R.drawable.cut));
        }
        if (mSelectStatus == FileOptHelper.SELECT_STATUS_ONE) {
            if (FileClipboard.hasContent()) {
                int size = FileClipboard.getClipboardSize();
                mOptList.add(new FileOptItem("paste", String.format("粘贴(%d)项", size), R.drawable.paste));
            }
            if (mCurrentDir.list() != null && mCurrentDir.list().length > 0) {
                mOptList.add(new FileOptItem("muilt_select", "多选", R.drawable.select_mode));
                if (mViewtMode == FileOptHelper.VIEW_MODE_LIST) {
                    mOptList.add(new FileOptItem("grid_view", "网格视图", R.drawable.file_view_mode_grid));
                } else {
                    mOptList.add(new FileOptItem("list_view", "列表视图", R.drawable.file_view_mode_list));
                }
                mOptList.add(new FileOptItem("rename", "重命名", R.drawable.rename));
                mOptList.add(new FileOptItem("file_prop", "文件属性", R.drawable.file_prop));
                if (mSortType == FileOptHelper.SORT_BYNAME) {
                    mOptList.add(new FileOptItem("sort_bydate", "日期排序", R.drawable.meun_sort_date));
                } else {
                    mOptList.add(new FileOptItem("sort_byname", "名称排序", R.drawable.meun_sort_name));
                }
            }
            mOptList.add(new FileOptItem("rescan", "重新扫描", R.drawable.rescan));
            mOptList.add(new FileOptItem("new_file", "新建文件夹", R.drawable.new_folder));
        } else if (mSelectStatus == FileOptHelper.SELECT_STATUS_MUILT) {
            if (mCurrentDir.list() != null && mCurrentDir.list().length > 0) {
                mOptList.add(new FileOptItem("all_select", "全选", R.drawable.select_all));
                mOptList.add(new FileOptItem("exit_muilt_select", "退出多选", R.drawable.cancel));
            }
        } else if (mSelectStatus == FileOptHelper.SELECT_STATUS_ALL) {
            if (mCurrentDir.list() != null && mCurrentDir.list().length > 0) {
                mOptList.add(new FileOptItem("select_none", "取消全选", R.drawable.select_none));
                mOptList.add(new FileOptItem("exit_muilt_select", "退出多选", R.drawable.cancel));
            }
        }
    }

    private OnFileOptItemClickListener onFileOptItemClickListener;

    public void setOnFileOptItemClickListener(OnFileOptItemClickListener onFileOptItemClickListener) {
        this.onFileOptItemClickListener = onFileOptItemClickListener;
    }

    public interface OnFileOptItemClickListener {
        void onExecItem(FileOptItem fileOptItem);
    }


}
