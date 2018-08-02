package cn.fengmang.file;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.widget.Toast;

import cn.fengmang.baselib.ELog;
import cn.fengmang.file.utils.NetUtils;
import cn.fengmang.file.widget.FMToast;
import cn.fm.libmini.MiniService;

/**
 * Created by Administrator on 2018/6/27.
 */

public class FMApplication extends Application {

    private static FMApplication instance;

    public static FMApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        ELog.setTagLevel(ELog.LEVEL_V);
        CrashCollector.getInstance(this);
        ELog.d("Application onCreate");
        startService(new Intent(this, MiniService.class));
        new FMToast(instance).text("远程安装服务器已经启动了").show();
        new Thread() {
            @Override
            public void run() {
                String ip = NetUtils.getIpAddress(instance);
                ELog.d("本机ip地址" + ip);
            }
        }.start();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        ELog.d("Application onTerminate");
        stopService(new Intent(this, MiniService.class));


    }
}
