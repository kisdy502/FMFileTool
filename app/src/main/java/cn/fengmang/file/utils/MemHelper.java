package cn.fengmang.file.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Debug;
import android.os.StatFs;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import java.util.List;

import cn.fengmang.baselib.ELog;

/**
 * Created by Administrator on 2018/7/12.
 */

public class MemHelper {


    //运行内存大小
    public static void printfMemInfo(Context context) {
        ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        mActivityManager.getMemoryInfo(memoryInfo);
        long availMem = memoryInfo.availMem;
        long threshold = memoryInfo.threshold;
        long totalMem = memoryInfo.totalMem;

        StringBuilder sb = new StringBuilder();
        sb.append("Memory Info:\n").append("可用内存: ").append(availMem / (1024 * 1024)).append("MB\n")
                .append("内存临界值:").append(threshold / (1024 * 1024)).append("MB\n")
                .append("总内存:").append(totalMem / (1024 * 1024)).append("MB\n");
        ELog.d(sb.toString());

        // 通过调用ActivityManager的getRunningAppProcesses()方法获得系统里所有正在运行的进程
        List<ActivityManager.RunningAppProcessInfo> appProcessList = mActivityManager
                .getRunningAppProcesses();
        final String pkgName = context.getPackageName();
        StringBuilder stringBuilder = new StringBuilder();
        for (ActivityManager.RunningAppProcessInfo appProcessInfo : appProcessList) {
            if (pkgName.equals(appProcessInfo.processName)) {
                stringBuilder.append("进程信息:\n")
                        .append("PID: ").append(appProcessInfo.pid).append("\n")
                        .append("UID:").append(appProcessInfo.uid).append("\n")
                        .append("进程名称:").append(appProcessInfo.processName).append("\n");
                int[] mempids = new int[]{appProcessInfo.pid};
                Debug.MemoryInfo[] mInfo = mActivityManager.getProcessMemoryInfo(mempids);
                // 获取进程占内存用信息 kb单位
                int memSize = mInfo[0].dalvikPrivateDirty;
                stringBuilder.append("占用内存:").append(memSize).append("KB\n");
                break;
            }
        }
        ELog.d(stringBuilder.toString());
    }


    public static void printfTvInfo(Context context) {
        DisplayMetrics metric = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(metric);
        // 屏幕宽度（像素）
        int width = metric.widthPixels;
        // 屏幕高度（像素）
        int height = metric.heightPixels;
        // 屏幕密度（1.0 / 1.5 / 2.0）
        float density = metric.density;
        // 屏幕密度DPI（160 / 240 / 320）
        int densityDpi = metric.densityDpi;
        StringBuilder sb = new StringBuilder();
        sb.append("System Info\n").append("机顶盒型号: ").append(android.os.Build.MODEL).append("\n")
                .append("SDK版本:").append(android.os.Build.VERSION.SDK_INT).append("\n")
                .append("系统版本:").append(android.os.Build.VERSION.RELEASE).append("\n")
                .append("屏幕宽度").append(width).append("px\n")
                .append("屏幕高度").append(height).append("px\n")
                .append("屏幕密度").append(densityDpi).append("DPI\n");
        ELog.d(sb.toString());
    }

    public static void getExtInfo(String path) {
        final StatFs statFs = new StatFs(path);
        long totalCount = statFs.getBlockCount();                //总共的block数
        long availableCount = statFs.getAvailableBlocks();       //获取可用的block数
        long size = statFs.getBlockSize();                       //每格所占的大小，一般是4KB==
        long availROMSize = availableCount * size;               //可用存储大小
        long totalROMSize = totalCount * size;                   //总大小
        ELog.d(path + ",可用内存大小" + FileOptHelper.convertStorage(availROMSize) + ",总内存" + FileOptHelper.convertStorage(totalROMSize));
        ;

    }
}
