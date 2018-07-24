package cn.fengmang.file.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cn.fengmang.file.bean.AppInfo;

/**
 * Created by Administrator on 2018/7/6.
 */

public class AppHelper {

    public static List<AppInfo> getUserAppList(Context context) {
        List<PackageInfo> allList = getAllApkList(context);
        PackageInfo packageInfo;
        List<AppInfo> appInfoList = new ArrayList<>();
        for (int i = 0, size = allList.size(); i < size; i++) {
            packageInfo = allList.get(i);
            if (((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0)) {
                appInfoList.add(new AppInfo(packageInfo));
            }
        }
        return appInfoList;
    }

    public static List<PackageInfo> getAllApkList(Context context) {
        PackageManager pm = context.getPackageManager(); // 获得PackageManager对象
        List<PackageInfo> packs = pm.getInstalledPackages(0);
        return packs;

    }

    public static List<AppInfo> getAppInfoList(Context context, int flag) {
        List<PackageInfo> allList = getAllApkList(context);
        PackageInfo packageInfo;
        List<AppInfo> appInfoList = new ArrayList<>();
        for (int i = 0, size = allList.size(); i < size; i++) {
            packageInfo = allList.get(i);
            if (((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == flag)) {
                appInfoList.add(new AppInfo(packageInfo));
            }
        }
        return appInfoList;
    }

    public static List<AppInfo> getSystemAppList(Context context) {
        List<PackageInfo> allList = getAllApkList(context);
        PackageInfo packageInfo;
        List<AppInfo> appInfoList = new ArrayList<>();
        for (int i = 0, size = allList.size(); i < size; i++) {
            packageInfo = allList.get(i);
            if (((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) > 0)) {
                appInfoList.add(new AppInfo(packageInfo));
            }
        }
        return appInfoList;
    }

    public static boolean isUserApp(@NonNull final String packageName, Context context) {
        PackageInfo packageInfo = AppHelper.getPackageInfo(packageName, context);
        if (packageInfo != null)
            return isUserApp(packageInfo);
        return false;
    }

    public static boolean isUserApp(@NonNull PackageInfo packageInfo) {
        return (packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0;
    }


    public static boolean isSystemApp(@NonNull final String packageName, Context context) {
        PackageInfo packageInfo = AppHelper.getPackageInfo(packageName, context);
        if (packageInfo != null)
            return isSystemApp(packageInfo);
        return false;
    }


    public static boolean isSystemApp(@NonNull PackageInfo packageInfo) {
        return (packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 1;
    }

    public static void startAppLaunch(Context context, PackageInfo packageInfo) {
        if (packageInfo == null) {
            return;
        }
        Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(packageInfo.packageName);
        if (launchIntent == null) {
            Toast.makeText(context, "没有主界面，无法启动", Toast.LENGTH_SHORT).show();
            return;
        }
        context.startActivity(launchIntent);
    }


    public static PackageInfo getPackageInfo(String packageName, Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(packageName, 0);
            return packageInfo;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void uninstall(String packageName, Context context) {
        boolean isExist = checkApplication(packageName, context);
        if (isExist) {
            Uri packageURI = Uri.parse("package:".concat(packageName));
            Intent intent = new Intent(Intent.ACTION_DELETE);
            intent.setData(packageURI);
            context.startActivity(intent);
        }
    }

    /**
     * 判断该包名的应用是否安装
     *
     * @param packageName
     * @return
     */
    private static boolean checkApplication(String packageName, Context context) {
        if (packageName == null || "".equals(packageName)) {
            return false;
        }
        try {
            context.getPackageManager().getApplicationInfo(packageName,
                    PackageManager.MATCH_UNINSTALLED_PACKAGES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }


}
