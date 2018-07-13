package cn.fengmang.file;

import android.app.Application;

import cn.fengmang.baselib.ELog;

/**
 * Created by Administrator on 2018/6/27.
 */

public class FMApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ELog.setTagLevel(ELog.LEVEL_D);
    }
}
