package cn.fengmang.file.prefs;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Administrator on 2018/7/5.
 */

public abstract class CommonSharePref {

    protected abstract String getSharePrefName();

    protected void setData(Context context, String key, String value) {
        //1、打开Preferences，名称为setting，如果存在则打开它，否则创建新的Preferences
        SharedPreferences settings = context.getSharedPreferences(getSharePrefName(), MODE_PRIVATE);
        //2、让setting处于编辑状态
        SharedPreferences.Editor editor = settings.edit();
        //3、存放数据
        editor.putString(key, value);
        //4、完成提交
        editor.commit();
    }

    protected String getData(Context context, String key) {
        //1、获取Preferences
        SharedPreferences settings = context.getSharedPreferences(getSharePrefName(), MODE_PRIVATE);
        //2、取出数据
        String name = settings.getString(key, "");
        return name;
    }

    protected void setIntData(Context context, String key, int value) {
        //1、打开Preferences，名称为setting，如果存在则打开它，否则创建新的Preferences
        SharedPreferences settings = context.getSharedPreferences(getSharePrefName(), MODE_PRIVATE);
        //2、让setting处于编辑状态
        SharedPreferences.Editor editor = settings.edit();
        //3、存放数据
        editor.putInt(key, value);
        //4、完成提交
        editor.commit();
    }

    protected int getIntData(Context context, String key) {
        //1、获取Preferences
        SharedPreferences settings = context.getSharedPreferences(getSharePrefName(), MODE_PRIVATE);
        //2、取出数据
        int value = settings.getInt(key, -1);
        return value;
    }

    protected int getIntData(Context context, String key, int defaultValue) {
        //1、获取Preferences
        SharedPreferences settings = context.getSharedPreferences(getSharePrefName(), MODE_PRIVATE);
        //2、取出数据
        int value = settings.getInt(key, defaultValue);
        return value;
    }

    protected void setLongData(Context context, String key, long value) {
        //1、打开Preferences，名称为setting，如果存在则打开它，否则创建新的Preferences
        SharedPreferences settings = context.getSharedPreferences(getSharePrefName(), MODE_PRIVATE);
        //2、让setting处于编辑状态
        SharedPreferences.Editor editor = settings.edit();
        //3、存放数据
        editor.putLong(key, value);
        //4、完成提交
        editor.commit();
    }

    protected long getLongData(Context context, String key) {
        //1、获取Preferences
        SharedPreferences settings = context.getSharedPreferences(getSharePrefName(), MODE_PRIVATE);
        //2、取出数据
        long value = settings.getLong(key, -1L);
        return value;
    }
}
