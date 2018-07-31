package cn.fengmang.file.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by SDT13411 on 2017/12/18.
 */

public class SharedPrefUtil {

    private final static String FILE_NAME = "jikeConfig";

    private static void setData(Context context, String key, String value) {
        //1、打开Preferences，名称为setting，如果存在则打开它，否则创建新的Preferences
        SharedPreferences settings = context.getSharedPreferences(FILE_NAME, 0);
        //2、让setting处于编辑状态
        SharedPreferences.Editor editor = settings.edit();
        //3、存放数据
        editor.putString(key, value);
        //4、完成提交
        editor.commit();
    }

    public static String getData(Context context, String key) {
        //1、获取Preferences
        SharedPreferences settings = context.getSharedPreferences(FILE_NAME, 0);
        //2、取出数据
        String name = settings.getString(key, "");
        return name;
    }


    public static void setMac(Context context, String value) {
        setData(context, "mac", value);
    }


    public static String getMac(Context context) {
        return getData(context, "mac");
    }






}
