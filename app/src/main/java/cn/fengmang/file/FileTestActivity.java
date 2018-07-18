package cn.fengmang.file;

import android.annotation.SuppressLint;
import android.os.Environment;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.fengmang.baselib.ELog;
import cn.fengmang.file.adapter.FileListAdapter;
import cn.fengmang.file.bean.FileItem;
import cn.fengmang.file.utils.FileOptHelper;
import cn.fengmang.libui.flying.DrawableFlyingFrameView;
import cn.fengmang.libui.recycler.FRecyclerView;

public class FileTestActivity extends FMBaseTitleActivity {
    private FRecyclerView mRecyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private FileListAdapter mAdapter;
    private List<FileItem> mFileItemList;
    private String mCurrentDir;
    private int mSortType = FileOptHelper.SORT_BYNAME;
    private DrawableFlyingFrameView mFlyingView;
    private int mCheckStatus = FileOptHelper.SELECT_STATUS_ONE;
    private int mViewtMode = FileOptHelper.VIEW_MODE_GRID;
    private TextView mTvSelectFileCount;
    private TextView mTvSelectFileName;
    private int mCurrentSelectPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_test);
    }

    @Override
    protected void initUI() {
        super.initUI();
        mRecyclerView = (FRecyclerView) findViewById(R.id.fileList);
        mTvSelectFileCount = (TextView) findViewById(R.id.selectFileCount);
        mTvSelectFileName = (TextView) findViewById(R.id.selectFileName);

        mRecyclerView.setOnItemFocusChangeListener(new FRecyclerView.OnItemFocusChangeListener() {
            @Override
            public boolean onItemPreSelected(RecyclerView parent, View itemView, int position) {
                return false;
            }

            @Override
            public boolean onItemSelected(RecyclerView parent, View itemView, int position) {
                mFlyingView.onMoveTo(itemView, 1, 1, 0);
                mCurrentSelectPosition = position;
                setBottomView();
                return false;
            }

            @Override
            public boolean onReviseFocusFollow(RecyclerView parent, View itemView, int position) {
                ELog.e("onReviseFocusFollow:" + position);
                mFlyingView.onMoveTo(itemView, 1, 1, 0);
                return false;
            }
        });
        layoutManager = new GridLayoutManager(this, 6);
        mRecyclerView.setLayoutManager(layoutManager);
        mFlyingView = DrawableFlyingFrameView.build(this);
        mFlyingView.setFlyingDrawable(getResources().getDrawable(R.drawable.hover_item));
        mCurrentDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/index";
        setMainTitle(mCurrentDir);
        initFileList(new File(mCurrentDir));
    }

    private void initFileList(final File rootDir) {
        mFlyingView.setVisibility(View.GONE);
        new Thread() {
            @Override
            public void run() {

                if (mFileItemList == null) {
                    mFileItemList = new ArrayList<>();
                } else {
                    mFileItemList.clear();
                }
                if (rootDir.exists()) {
                    File[] subFiles = rootDir.listFiles();

                    if (subFiles != null && subFiles.length > 0) {
                        for (File subFile : subFiles) {
                            FileItem item = new FileItem();
                            item.setFullPath(subFile.getAbsolutePath());
                            item.setLastModifyDate(subFile.lastModified());
                            item.setIsDirectory(subFile.isDirectory());
                            item.setFileName(subFile.getName());
                            if (subFile.isDirectory()) {
                                File[] subFileArray = subFile.listFiles();
                                if (subFileArray != null && subFileArray.length > 0) {
                                    int childDirCount = 0;
                                    int childFileCount = 0;
                                    item.setEmpty(false);
                                    item.setChildDirCount(childDirCount);
                                    item.setChildFileCount(childFileCount);
                                } else {
                                    item.setEmpty(true);
                                }
                            } else {
                                item.setEmpty(true);
                                item.setFileSize(subFile.length());
                            }
                            mFileItemList.add(item);
                        }
                        if (mSortType == FileOptHelper.SORT_BYNAME) {
                            FileOptHelper.orderByName(mFileItemList, true);
                        } else if (mSortType == FileOptHelper.SORT_BYDATE) {
                            FileOptHelper.orderByDate(mFileItemList, false);
                        }
                    }
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mFileItemList != null && mFileItemList.size() > 0) {
                            mAdapter = new FileListAdapter(mCheckStatus, mViewtMode, mFileItemList, getApplicationContext());
                            mRecyclerView.setAdapter(mAdapter);
                            mRecyclerView.requestFocus();
                        } else {
                            mRecyclerView.setVisibility(View.GONE);
                        }
                    }
                });
            }
        }.start();
    }


    @SuppressLint("StringFormatMatches")
    private void setBottomView() {
        String name = mFileItemList.get(mCurrentSelectPosition).getFileName();
        ELog.d("fileName:" + name);
//        mTvSelectFileName.setText(name);
    }
}
