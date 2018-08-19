package cn.fengmang.file;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import cn.fengmang.baselib.ELog;
import cn.fengmang.file.adapter.FileListAdapter;
import cn.fengmang.file.bean.FileItem;
import cn.fengmang.file.bean.FileOptItem;
import cn.fengmang.file.dialog.FileCommonDialogFragment;
import cn.fengmang.file.dialog.FileOptDialogFragment;
import cn.fengmang.file.dialog.FilePropertyDialogFragment;
import cn.fengmang.file.dialog.FileRenameDialogFragment;
import cn.fengmang.file.dialog.OnOptClickListener;
import cn.fengmang.file.prefs.FileSettingSharePref;
import cn.fengmang.file.utils.ExtFileHelper;
import cn.fengmang.file.utils.FileClipboard;
import cn.fengmang.file.utils.FileIntentHelper;
import cn.fengmang.file.utils.FileOptHelper;
import cn.fengmang.file.utils.FileTagHelper;
import cn.fengmang.file.utils.MemHelper;
import cn.fengmang.file.widget.FMToast;
import cn.fengmang.libui.flying.DrawableFlyingFrameView;
import cn.fengmang.libui.recycler.FMRecyclerView;
import cn.fengmang.libui.recycler.OnItemClickListener;
import cn.fengmang.libui.recycler.OnItemFocusChangeListener;
import cn.fengmang.libui.recycler.OnItemLongClickListener;
import cn.fengmang.libui.recycler.V7GridLayoutManager;
import cn.fengmang.libui.recycler.V7LinearLayoutManager;

public class FMFileActivity extends FMBaseTitleActivity implements OnOptClickListener {

    private final static String TAG = "FMFileActivity";

    private final static String DLG_FILE_OPT_TAG = "FILE_OPT_DIALOG";
    private final static String DLG_FILE_COMMON_TAG = "FILE_COMMON_DIALOG";
    private final static String DLG_RENAME_COMMON_TAG = "FILE_RENAME_DIALOG";
    private final static String DLG_FILEPROP_COMMON_TAG = "FILE_PROP_DIALOG";

    private FMRecyclerView mFileRecyclerView;
    private FileListAdapter mAdapter;
    private List<FileItem> mFileItemList;
    private View mEmptyLayout;
    private TextView mTvSelectFileCount;
    private TextView mTvSelectFileName;
    private DrawableFlyingFrameView mFlyingView;

    private int mSortType;
    private int mCheckStatus = FileOptHelper.SELECT_STATUS_ONE;
    private int mViewtMode;
    private String mCurrentDir;
    private List<String> mSelectFileList = new ArrayList<>();


    private ArrayMap<String, String> tagList;
    private FileOptDialogFragment mDialogFragment;
    private Stack<String> mVisitHostiry;
    private int mSelectFileCount;
    private int mCurrentSelectPosition;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSortType = FileSettingSharePref.getInstance().getFileSortType(this, FileOptHelper.SORT_BYNAME);
        mViewtMode = FileSettingSharePref.getInstance().getViewMode(this, FileOptHelper.VIEW_MODE_LIST);
        initReceiver();
        setContentView(R.layout.fm_activity_fmfile);
        getExtPathList();

    }

    @Override
    public void initUI() {
        super.initUI();
        mFileRecyclerView = (FMRecyclerView) findViewById(R.id.fileList);
        mEmptyLayout = findViewById(R.id.content_empty_layout);
        mTvSelectFileCount = (TextView) findViewById(R.id.selectFileCount);
        mTvSelectFileName = (TextView) findViewById(R.id.selectFileName);
        mTvSelectFileName.setSelected(true);
        mTvSelectFileName.setVisibility(mViewtMode == FileOptHelper.VIEW_MODE_GRID ? View.VISIBLE : View.GONE);

        mFlyingView = DrawableFlyingFrameView.build(this);
        mFlyingView.setFlyingDrawable(getResources().getDrawable(R.drawable.hover_item));
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            mCurrentDir = Environment.getExternalStorageDirectory().getAbsolutePath();
        } else {
            mCurrentDir = "/";
        }
        mVisitHostiry = new Stack<>();
        initRecyclerView();
        initFileList(new File(mCurrentDir));
    }

    private void initReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_MEDIA_EJECT);              //sd插入
        filter.addAction(Intent.ACTION_MEDIA_MOUNTED);            //sd可用
        filter.addAction(Intent.ACTION_MEDIA_REMOVED);            //sd拔出
        filter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);          //sd卸载

        filter.addDataScheme("file");
        filter.setPriority(1000);
        registerReceiver(receiver, filter);

        IntentFilter filter_usb = new IntentFilter();
        filter_usb.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);  //usb插入
        filter_usb.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);  //usb拔出
        filter_usb.setPriority(1000);
        registerReceiver(receiver, filter_usb);
    }


    private void initFileList(final File rootDir) {
        if (!mVisitHostiry.contains(mCurrentDir))
            mVisitHostiry.push(mCurrentDir);
        mFileRecyclerView.setVisibility(View.GONE);
        mLoadingBar.setVisibility(View.VISIBLE);
        mFlyingView.setVisibility(View.GONE);
        mTvSelectFileCount.setVisibility(View.INVISIBLE);
        mTvSelectFileName.setVisibility(View.INVISIBLE);
        setMainTitle(mCurrentDir);
        new Thread() {
            @Override
            public void run() {
                if (tagList == null) {
                    initTagList();
                }
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
                            if (tagList.containsKey(subFile.getName())) {
                                item.setFileTag(tagList.get(subFile.getName()));
                            }
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

                MemHelper.printfMemInfo(getApplicationContext());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mLoadingBar.setVisibility(View.GONE);
                        if (mFileItemList != null && mFileItemList.size() > 0) {
                            setSubTitle(mFileItemList.size() + "项");
                            mFlyingView.setVisibility(View.VISIBLE);
                            mEmptyLayout.setVisibility(View.GONE);
                            mAdapter = new FileListAdapter(mCheckStatus, mViewtMode, mFileItemList, getApplicationContext());
                            mFileRecyclerView.setAdapter(mAdapter);
                            mFileRecyclerView.setVisibility(View.VISIBLE);
                            mFileRecyclerView.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mFileRecyclerView.requestFocus();
                                }
                            }, 10);
                        } else {
                            mEmptyLayout.setVisibility(View.VISIBLE);
                            mTvSelectFileCount.setVisibility(View.GONE);
                            mTvSelectFileName.setVisibility(View.GONE);
                        }
                    }
                });
            }
        }.start();
    }

    private void initTagList() {
        tagList = FileTagHelper.getTagList(this);
    }

    private void initRecyclerView() {
        initLayoutManager();

        mFileRecyclerView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView parent, View itemView, int position) {
                FileItem selectItem = mFileItemList.get(position);
                if (mCheckStatus == FileOptHelper.SELECT_STATUS_ONE) {
                    ELog.i(TAG, "click:" + selectItem.getFullPath());
                    if (selectItem.isDirectory()) {
                        mCurrentDir = selectItem.getFullPath();
                        File subRoot = new File(mCurrentDir);
                        initFileList(subRoot);
                    } else {
                        Intent intent = FileIntentHelper.getOpenFileIntent(selectItem.getFullPath());
                        startActivity(intent);
                    }
                } else {
                    boolean ischeck = selectItem.isChecked();
                    selectItem.setChecked(!ischeck);
                    mAdapter.notifyDataSetChanged();
                    if (ischeck) {
                        mSelectFileList.remove(new File(selectItem.getFullPath()));
                        mSelectFileCount--;
                    } else {
                        mSelectFileCount++;
                        mSelectFileList.add(selectItem.getFullPath());
                    }
                    setBottomView();
                }
            }
        });

        mFileRecyclerView.setOnItemFocusListener(new OnItemFocusChangeListener() {
            @Override
            public boolean onItemPreSelected(@NonNull RecyclerView parent, @NonNull View itemView, int position) {
                return false;
            }

            @Override
            public boolean onItemSelected(@NonNull RecyclerView parent, @NonNull View itemView, int position) {
                ELog.d(TAG, "onItemSelected:" + position);
                mFlyingView.onMoveTo(itemView, 1.0f, 1.0f, 0);
                mCurrentSelectPosition = position;
                setBottomView();
                return false;
            }

            @Override
            public boolean onReviseFocusFollow(@NonNull RecyclerView parent, @NonNull View itemView, int position) {
                ELog.d(TAG, "onReviseFocusFollow:" + position);
                mFlyingView.onMoveTo(itemView, 1.0f, 1.0f, 0);
                return false;
            }
        });

        mFileRecyclerView.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(RecyclerView parent, View itemView, int position) {
                FileItem selectItem = mFileItemList.get(position);
                if (mCheckStatus == FileOptHelper.SELECT_STATUS_ONE) {
                    mCheckStatus = FileOptHelper.SELECT_STATUS_MUILT;
                    selectItem.setChecked(true);
                    mSelectFileCount++;
                    if (!mSelectFileList.contains(selectItem.getFullPath()))
                        mSelectFileList.add(selectItem.getFullPath());
                    mAdapter.setCheckStatus(mCheckStatus);
                    setBottomView();
                }
                return true;
            }
        });
    }

    private void initLayoutManager() {
        if (mViewtMode == FileOptHelper.VIEW_MODE_LIST) {
            V7LinearLayoutManager mLayoutManager = new V7LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            mFileRecyclerView.setLayoutManager(mLayoutManager);
        } else {
            V7GridLayoutManager mLayoutManager = new V7GridLayoutManager(this, 6, LinearLayoutManager.VERTICAL, false);
            mFileRecyclerView.setLayoutManager(mLayoutManager);
        }
    }

    @Override
    public void onBackPressed() {
        if (mCheckStatus == FileOptHelper.SELECT_STATUS_ONE) {
            mVisitHostiry.pop();
            if (mVisitHostiry.isEmpty()) {
                super.onBackPressed();
            } else {
                mCurrentDir = mVisitHostiry.pop();
                File rootDir = new File(mCurrentDir);
                initFileList(rootDir);
            }
        } else {
            mCheckStatus = FileOptHelper.SELECT_STATUS_ONE;
            for (FileItem item : mFileItemList) {
                item.setChecked(false);
            }
            mSelectFileList.clear();
            mAdapter.setCheckStatus(mCheckStatus);
            mAdapter.notifyDataSetChanged();
            mSelectFileCount = 0;
            setBottomView();
        }
    }


    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_UP) {
            if (event.getKeyCode() == KeyEvent.KEYCODE_MENU) {
                mDialogFragment = null;
                showMenuDialog();
                return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    private void showMenuDialog() {
        mDialogFragment = new FileOptDialogFragment();
        mDialogFragment.setViewtMode(mViewtMode);
        mDialogFragment.setSortType(mSortType);
        mDialogFragment.setmCurrentDir(new File(mCurrentDir));
        mDialogFragment.setmSelectStatus(mCheckStatus);
        mDialogFragment.setOnFileOptItemClickListener(new FileOptDialogFragment.OnFileOptItemClickListener() {
            @Override
            public void onExecItem(FileOptItem fileOptItem) {
                ELog.d(TAG, fileOptItem.getCmd());
                execCmd(fileOptItem.getCmd());

            }
        });
        mDialogFragment.show(getSupportFragmentManager(), DLG_FILE_OPT_TAG);
    }

    private void execCmd(String cmd) {
        if ("copy".equals(cmd)) {
            if (mCheckStatus == FileOptHelper.SELECT_STATUS_ONE) {
                FileItem selectItem = mFileItemList.get(mCurrentSelectPosition);
                FileClipboard.copyFile(selectItem.getFullPath());
            } else {
                FileClipboard.copyFile(mSelectFileList);
            }
        } else if ("delete".equals(cmd)) {
            showDeleteDialog();
        } else if ("cut".equals(cmd)) {
            if (mCheckStatus == FileOptHelper.SELECT_STATUS_ONE) {
                FileItem selectItem = mFileItemList.get(mCurrentSelectPosition);
                FileClipboard.cutFile(selectItem.getFullPath());
            } else {
                FileClipboard.cutFile(mSelectFileList);
            }
        } else if ("paste".equals(cmd)) {
            File currentFile = new File(mCurrentDir);
            if (FileClipboard.checkFileExits(currentFile)) {
                showFileExitDialog();
            } else {
                FileClipboard.pasteFile(currentFile);
                initFileList(currentFile);
            }
        } else if ("list_view".equals(cmd)) {
            mViewtMode = FileOptHelper.VIEW_MODE_LIST;
            initLayoutManager();
            mAdapter = new FileListAdapter(mCheckStatus, mViewtMode, mFileItemList, getApplicationContext());
            mFileRecyclerView.setAdapter(mAdapter);
            mFileRecyclerView.post(new Runnable() {
                @Override
                public void run() {
                    mFileRecyclerView.requestFocus();
                }
            });
        } else if ("grid_view".equals(cmd)) {
            mViewtMode = FileOptHelper.VIEW_MODE_GRID;
            initLayoutManager();
            mAdapter = new FileListAdapter(mCheckStatus, mViewtMode, mFileItemList, getApplicationContext());
            mFileRecyclerView.setAdapter(mAdapter);
            mFileRecyclerView.post(new Runnable() {
                @Override
                public void run() {
                    mFileRecyclerView.requestFocus();
                }
            });
        } else if ("rename".equals(cmd)) {
            showRenameDialog();
        } else if ("file_prop".equals(cmd)) {
            showPropertyDialog();
        } else if ("sort_bydate".equals(cmd)) {
            mSortType = FileOptHelper.SORT_BYDATE;
            FileOptHelper.orderByDate(mFileItemList, false);
            mAdapter.notifyDataSetChanged();
        } else if ("sort_byname".equals(cmd)) {
            mSortType = FileOptHelper.SORT_BYNAME;
            FileOptHelper.orderByName(mFileItemList, true);
            mAdapter.notifyDataSetChanged();
        } else if ("rescan".equals(cmd)) {

        } else if ("new_file".equals(cmd)) {
            File newFile = FileOptHelper.newDir(mCurrentDir);
            if (newFile != null && newFile.exists()) {
                FileItem newItem = new FileItem();
                newItem.setFullPath(newFile.getAbsolutePath());
                newItem.setLastModifyDate(newFile.lastModified());
                newItem.setIsDirectory(true);
                newItem.setFileName(newFile.getName());
                if (tagList.containsKey(newFile.getName())) {
                    newItem.setFileTag(tagList.get(newFile.getName()));
                }
                newItem.setEmpty(true);
                mFileItemList.add(newItem);
                if (mFileItemList.size() == 1) {
                    mFlyingView.setVisibility(View.VISIBLE);
                    mEmptyLayout.setVisibility(View.GONE);
                    mAdapter = new FileListAdapter(mCheckStatus, mViewtMode, mFileItemList, getApplicationContext());
                    mFileRecyclerView.setAdapter(mAdapter);
                    mFileRecyclerView.setVisibility(View.VISIBLE);
                    mFileRecyclerView.requestFocus();
                } else {
                    mAdapter.notifyItemInserted(mFileItemList.size() - 1);
                    mFileRecyclerView.smoothScrollToPosition(mFileItemList.size() - 1);
                }
                new FMToast(this).text("新建文件夹成功").show();
            } else {
                new FMToast(this).text("新建文件夹失败").show();
            }
        } else if ("all_select".equals(cmd)) {
            setAllFile(true);
            mCheckStatus = FileOptHelper.SELECT_STATUS_ALL;
            mAdapter.notifyDataSetChanged();
        } else if ("exit_muilt_select".equals(cmd)) {
            showExitMuiltDialog();
        } else if ("select_none".equals(cmd)) {
            setAllFile(false);
            mCheckStatus = FileOptHelper.SELECT_STATUS_NONE;
            mAdapter.setCheckStatus(mCheckStatus);
        } else if ("muilt_select".equals(cmd)) {
            mCheckStatus = FileOptHelper.SELECT_STATUS_MUILT;
            mAdapter.setCheckStatus(mCheckStatus);
        }
    }

    private void setAllFile(boolean checked) {
        for (FileItem item : mFileItemList) {
            item.setChecked(checked);
            if (!mSelectFileList.contains(item.getFullPath()))
                mSelectFileList.add(item.getFullPath());
        }
    }


    @SuppressLint("StringFormatMatches")
    private void setBottomView() {
        if (mViewtMode == FileOptHelper.VIEW_MODE_GRID) {
            mTvSelectFileCount.setVisibility(View.INVISIBLE);
            mTvSelectFileName.setVisibility(View.VISIBLE);
            String name = mFileItemList.get(mCurrentSelectPosition).getFileName();
            ELog.d(TAG, "fileName:" + name);
            mTvSelectFileName.setText(name);
        } else {
            mTvSelectFileName.setVisibility(View.INVISIBLE);
        }

        if (mCheckStatus == FileOptHelper.SELECT_STATUS_ONE) {
            mTvSelectFileCount.setVisibility(View.INVISIBLE);
        } else {
            mTvSelectFileCount.setVisibility(View.VISIBLE);
            mTvSelectFileCount.setText(getString(R.string.has_select_files, mSelectFileCount));
        }
    }

    private void showDeleteDialog() {
        FileCommonDialogFragment dialogFragment = new FileCommonDialogFragment();
        dialogFragment.show(getSupportFragmentManager(), DLG_FILE_COMMON_TAG);
        dialogFragment.setTitle("确定要删除吗?");
        dialogFragment.setOptText("删除");
        dialogFragment.setCmd("delete");
        dialogFragment.setOnOptClickListener(this);


    }

    private void showExitMuiltDialog() {
        FileCommonDialogFragment dialogFragment = new FileCommonDialogFragment();
        dialogFragment.setTitle("是否退出多选模式?");
        dialogFragment.setOptText("确定");
        dialogFragment.setCmd("exit_muilt_select");
        dialogFragment.setOnOptClickListener(this);
        dialogFragment.show(getSupportFragmentManager(), DLG_FILE_COMMON_TAG);
    }


    private void showFileExitDialog() {
        FileCommonDialogFragment dialogFragment = new FileCommonDialogFragment();
        dialogFragment.setTitle("文件已存在,是否覆盖");
        dialogFragment.setOptText("确定");
        dialogFragment.setCmd("paste");
        dialogFragment.setOnOptClickListener(this);
        dialogFragment.show(getSupportFragmentManager(), DLG_FILE_COMMON_TAG);
    }


    private void showRenameDialog() {
        FileRenameDialogFragment dialogFragment = new FileRenameDialogFragment();
        dialogFragment.setCmd(FileOptHelper.FILE_CMD_RENAME);
        dialogFragment.setOldFileName(mFileItemList.get(mCurrentSelectPosition).getFileName());
        dialogFragment.setOnOptClickListener(this);
        dialogFragment.show(getSupportFragmentManager(), DLG_RENAME_COMMON_TAG);
    }

    private void showPropertyDialog() {
        FilePropertyDialogFragment dialogFragment = new FilePropertyDialogFragment();
        dialogFragment.setCmd(FileOptHelper.FILE_CMD_FILEPROP);
        dialogFragment.setSelectFileItem(mFileItemList.get(mCurrentSelectPosition));
        dialogFragment.setOnOptClickListener(this);
        dialogFragment.show(getSupportFragmentManager(), DLG_FILEPROP_COMMON_TAG);
    }

    @Override
    public void onExecOpt(View view, String cmd, Object result) {
        if ("delete".equals(cmd)) {
            if (mCheckStatus == FileOptHelper.SELECT_STATUS_ONE) {
                FileItem selectItem = mFileItemList.get(mCurrentSelectPosition);
                FileOptHelper.deleteFile(new File(selectItem.getFullPath()));
                mFileItemList.remove(mCurrentSelectPosition);
                mAdapter.notifyDataSetChanged();
                if (mFileItemList.size() == 0) {
                    setSubTitle(mFileItemList.size() + "项");
                    mEmptyLayout.setVisibility(View.VISIBLE);
                    mTvSelectFileCount.setVisibility(View.GONE);
                    mTvSelectFileName.setVisibility(View.GONE);
                    mFlyingView.setVisibility(View.GONE);
                }
            } else {
                FileOptHelper.deleteFileList(mSelectFileList);//
                mSelectFileList.clear();
                mSelectFileCount = 0;
                mCheckStatus = FileOptHelper.SELECT_STATUS_ONE;
                initFileList(new File(mCurrentDir));
            }
        } else if ("exit_muilt_select".equals(cmd)) {
            setAllFile(false);
            mCheckStatus = FileOptHelper.SELECT_STATUS_ONE;
            mAdapter.setCheckStatus(mCheckStatus);
        } else if (FileOptHelper.FILE_CMD_RENAME.equals(cmd)) {
            String newFileName = (String) result;
            FileItem selectFileItem = mFileItemList.get(mCurrentSelectPosition);

            File file = new File(selectFileItem.getFullPath());
            File tagFile = new File(mCurrentDir, newFileName);
            boolean renameResult = file.renameTo(tagFile);
            if (renameResult) {
                ELog.d(TAG, "rename 成功!");
                selectFileItem.setFileName(tagFile.getName());
                selectFileItem.setFullPath(tagFile.getAbsolutePath());
                selectFileItem.setLastModifyDate(tagFile.lastModified());
                if (tagList.containsKey(selectFileItem.getFileName())) {
                    selectFileItem.setFileTag(tagList.get(selectFileItem.getFileName()));
                }
                mAdapter.notifyDataSetChanged();

            } else {
                ELog.e(TAG, String.format("重命名失败,目标文件已%d经存在", tagFile.getAbsolutePath()));
            }
        } else if (FileOptHelper.FILE_CMD_PASTE.equals(cmd)) {
            File currentFile = new File(mCurrentDir);
            FileClipboard.pasteFile(currentFile);
            initFileList(currentFile);
        }
        setBottomView();
    }


    @Override
    public void onDestroy() {
        FileSettingSharePref.getInstance().setFileSortType(this, mSortType);
        FileSettingSharePref.getInstance().setViewMode(this, mViewtMode);
        unregisterReceiver(receiver);
        super.onDestroy();
    }

    @Override
    public void onCancelOpt(View view, String cmd) {

    }


    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_MEDIA_EJECT)) {
                ELog.i(TAG, "===================外置sd开拔出");
            } else if (action.equals(Intent.ACTION_MEDIA_UNMOUNTED)) {
                ELog.i(TAG, "===================SD卡被卸载");
            } else if (action.equals(Intent.ACTION_MEDIA_MOUNTED)) {
                ELog.i(TAG, "===================外置sd开插入");
                getExtPathList();
            } else if (action.equals(UsbManager.ACTION_USB_DEVICE_ATTACHED)) {
                ELog.i(TAG, "===================U盘插入");
            } else if (action.equals(UsbManager.ACTION_USB_DEVICE_DETACHED)) {
                ELog.i(TAG, "===================U盘被拔出");
            }
        }
    };

    private void getExtPathList() {
        List<String> pathList = ExtFileHelper.getStoragePath(this, ExtFileHelper.KEY_SD);

    }


}
