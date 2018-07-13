package cn.fengmang.file.bean;

import android.support.annotation.StringRes;

/**
 * Created by Administrator on 2018/6/29.
 */

public class FileOptItem {

    private String cmd;
    private String optName;
    @StringRes
    private int resId;

    public FileOptItem(String cmd, String optName, int resId) {
        this.cmd = cmd;
        this.optName = optName;
        this.resId = resId;
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public String getOptName() {
        return optName;
    }

    public void setOptName(String optName) {
        this.optName = optName;
    }

    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }
}
