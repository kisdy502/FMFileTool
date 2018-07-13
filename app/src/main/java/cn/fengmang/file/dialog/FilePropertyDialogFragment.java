package cn.fengmang.file.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;

import cn.fengmang.file.R;
import cn.fengmang.file.bean.FileItem;
import cn.fengmang.file.utils.FileOptHelper;
import cn.fengmang.file.utils.SignatureUtil;
import cn.fengmang.libui.FMProgressBar;

/**
 * Created by Administrator on 2018/7/3.
 */

public class FilePropertyDialogFragment extends BaseDialogFragment implements View.OnClickListener {

    private Button mBtnOpt;
    private TextView mTvTitle;
    private TextView mTvFileInfo;
    private String cmd;
    private String fileProperty;
    private FileItem mSelectFileItem;
    FMProgressBar mLoadingBar;

    private OnOptClickListener onOptClickListener;

    public void setOnOptClickListener(OnOptClickListener onOptClickListener) {
        this.onOptClickListener = onOptClickListener;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public void setSelectFileItem(FileItem mSelectFileItem) {
        this.mSelectFileItem = mSelectFileItem;
    }

    @Override
    protected String getUmengTag() {
        return "FileCommonDialogFragment";
    }

    @Override
    protected int setContentView() {
        return R.layout.fm_file_property_fragment_layout;
    }

    @Override
    protected void initUI() {
        mBtnOpt = mRootView.findViewById(R.id.btn_opt);
        mTvTitle = mRootView.findViewById(R.id.title);
        mTvFileInfo = mRootView.findViewById(R.id.tvFileInfo);
        mLoadingBar = mRootView.findViewById(R.id.fm_loadingbar);
        mBtnOpt.setOnClickListener(this);
    }

    @Override
    protected void fillData() {
        mLoadingBar.setVisibility(View.VISIBLE);
        if (mSelectFileItem == null) {
            return;
        }
        new Thread() {
            @Override
            public void run() {
                final StringBuilder stringBuilder = new StringBuilder();
                File file = new File(mSelectFileItem.getFullPath());
                File subFileArray[] = file.listFiles();
                if (mSelectFileItem.isDirectory()) {
                    if (subFileArray != null && subFileArray.length > 0) {
                        int childDirCount = 0;
                        int childFileCount = 0;
                        for (int i = 0; i < subFileArray.length; i++) {
                            if (subFileArray[i].isDirectory()) {
                                childDirCount++;
                            } else if (subFileArray[i].isFile()) {
                                childFileCount++;
                            }
                        }
                        mSelectFileItem.setChildFileCount(childFileCount);
                        mSelectFileItem.setChildDirCount(childDirCount);
                    }
                    stringBuilder.append("文件名称:").append(mSelectFileItem.getFileName()).append("\n")
                            .append("子文件夹数:" + mSelectFileItem.getChildDirCount()).append("\n")
                            .append("子文件数:" + mSelectFileItem.getChildFileCount());
                } else if (mSelectFileItem.isFile()) {
                    mSelectFileItem.setMd5(SignatureUtil.getFileMD5(mSelectFileItem.getFullPath()));
                    mSelectFileItem.setSha1(SignatureUtil.getFileSha1(mSelectFileItem.getFullPath()));
                    stringBuilder.append("文件名称:").append(mSelectFileItem.getFileName()).append("\n")
                            .append("md5:" + mSelectFileItem.getMd5()).append("\n")
                            .append("SHA1:" + mSelectFileItem.getSha1()).append("\n")
                            .append("文件大小:" + FileOptHelper.convertStorage(mSelectFileItem.getFileSize()));
                }


                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (notAlive())
                            return;
                        mTvFileInfo.setText(stringBuilder.toString());
                        mLoadingBar.setVisibility(View.GONE);
                    }
                });
            }
        }.start();


    }

    @Override
    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
        return false;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dlg = new Dialog(getActivity(), R.style.fm_file_dialog);
        dlg.setOnKeyListener(this);
        return dlg;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_opt:
                if (onOptClickListener != null) {
                    onOptClickListener.onExecOpt(v, cmd, null);
                }
                break;
            case R.id.btn_cancel:
                if (onOptClickListener != null) {
                    onOptClickListener.onCancelOpt(v, cmd);
                }
                break;
        }
        dismiss();
    }
}
