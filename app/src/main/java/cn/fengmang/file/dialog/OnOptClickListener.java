package cn.fengmang.file.dialog;

import android.view.View;

/**
 * Created by Administrator on 2018/7/3.
 */

public interface OnOptClickListener {
    void onExecOpt(View view, String cmd,Object result);
    void onCancelOpt(View view,String cmd);
}
