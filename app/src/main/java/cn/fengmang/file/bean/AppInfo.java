package cn.fengmang.file.bean;

import android.content.pm.PackageInfo;

/**
 * Created by Administrator on 2018/7/6.
 */

public class AppInfo {

    public AppInfo(PackageInfo mPackageInfo) {
        this.mPackageInfo = mPackageInfo;
    }

    private PackageInfo mPackageInfo;

    public PackageInfo getmPackageInfo() {
        return mPackageInfo;
    }

    public void setmPackageInfo(PackageInfo mPackageInfo) {
        this.mPackageInfo = mPackageInfo;
    }
}
