package cn.fengmang.file.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import cn.fengmang.file.R;

/**
 * Created by Administrator on 2018/7/3.
 */

public class FileCommonDialogFragment extends BaseDialogFragment implements View.OnClickListener {

    private Button mBtnCancel;
    private Button mBtnOpt;
    private TextView mTvTitle;
    private String cmd;
    private String title;
    private String optText;

    private OnOptClickListener onOptClickListener;

    public void setOnOptClickListener(OnOptClickListener onOptClickListener) {
        this.onOptClickListener = onOptClickListener;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setOptText(String optext) {
        this.optText = optext;
    }

    @Override
    protected String getUmengTag() {
        return "FileCommonDialogFragment";
    }

    @Override
    protected int setContentView() {
        return R.layout.fm_file_common_fragment_layout;
    }

    @Override
    protected void initUI() {
        mBtnOpt = mRootView.findViewById(R.id.btn_opt);
        mBtnCancel = mRootView.findViewById(R.id.btn_cancel);
        mTvTitle = mRootView.findViewById(R.id.title);
        mBtnOpt.setOnClickListener(this);
        mBtnCancel.setOnClickListener(this);
        mTvTitle.setText(title);
        mBtnOpt.setText(optText);
    }

    @Override
    protected void fillData() {
        mBtnOpt.requestFocus();
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
