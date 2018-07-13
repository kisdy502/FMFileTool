package cn.fengmang.file.prefs;

import android.content.Context;

/**
 * Created by Administrator on 2018/7/5.
 */

public class FileSettingSharePref extends CommonSharePref {

    public static FileSettingSharePref getInstance() {
        return FileSettingSharePrefHolder.instance;
    }

    @Override
    protected String getSharePrefName() {
        return "file_setting";
    }

    private final static class FileSettingSharePrefHolder {
        private final static FileSettingSharePref instance = new FileSettingSharePref();
    }

    public void setViewMode(Context context, int value) {
        setIntData(context, "view_mode", value);
    }

    public int getViewMode(Context context, int defaultVal) {
        return getIntData(context, "view_mode", defaultVal);
    }

    public void setFileSortType(Context context, int value) {
        setIntData(context, "file_sort_type", value);
    }

    public int getFileSortType(Context context, int defaultVal) {
        return getIntData(context, "file_sort_type", defaultVal);
    }

}
