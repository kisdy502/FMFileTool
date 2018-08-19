package cn.fengmang.file.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import cn.fengmang.file.R;
import cn.fengmang.file.widget.FMToast;

/**
 * Created by Administrator on 2018/7/3.
 */

public class FileRenameDialogFragment extends BaseDialogFragment implements View.OnClickListener {

    private Button mBtnCancel;
    private Button mBtnOpt;
    private TextView mTvTitle;
    private EditText mEdtNewFileName;
    private String cmd;
    private String oldFileName;

    private OnOptClickListener onOptClickListener;

    public void setOnOptClickListener(OnOptClickListener onOptClickListener) {
        this.onOptClickListener = onOptClickListener;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public void setOldFileName(String oldFileName) {
        this.oldFileName = oldFileName;
    }

    @Override
    protected String getUmengTag() {
        return "FileCommonDialogFragment";
    }

    @Override
    protected int setContentView() {
        return R.layout.fm_file_rename_fragment_layout;
    }

    @Override
    protected void initUI() {
        mBtnOpt = mRootView.findViewById(R.id.btn_opt);
        mBtnCancel = mRootView.findViewById(R.id.btn_cancel);
        mTvTitle = mRootView.findViewById(R.id.title);
        mEdtNewFileName = mRootView.findViewById(R.id.new_name);
        mBtnOpt.setOnClickListener(this);
        mBtnCancel.setOnClickListener(this);
    }

    @Override
    protected void fillData() {
        mEdtNewFileName.setText(oldFileName);
        mEdtNewFileName.setSelection(oldFileName.length());
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
                String newName = mEdtNewFileName.getText().toString();
                if (TextUtils.isEmpty(newName)) {
                    new FMToast(getContext()).text("重命名的文件名字不能为空").show();
                } else {
                    if (onOptClickListener != null) {
                        onOptClickListener.onExecOpt(v, cmd, newName);
                    }
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
