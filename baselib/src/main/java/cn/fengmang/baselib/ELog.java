package cn.fengmang.baselib;

import android.text.TextUtils;
import android.util.Log;

/**
 * Created by SDT13292 on 2017/4/27.
 */
public class ELog {

    private static final String prefix = "MT:";
    public final static int LEVEL_V = 1;
    public final static int LEVEL_D = 2;
    public final static int LEVEL_I = 3;
    public final static int LEVEL_W = 4;
    public final static int LEVEL_E = 5;

    private static int tagLevel = LEVEL_D;
    private static boolean isDebug = true;  //默认开启

    public static void enableDebug() {
        isDebug = true;
    }

    public static void setTagLevel(int level) {
        tagLevel = level;
    }


    public static void v(String msg) {
        if (isDebug && tagLevel <= LEVEL_V)
            Log.v(generateTag(), msg);

    }

    public static void v(String tag, String msg) {
        if (isDebug && tagLevel <= LEVEL_V) {
            String newTag = generateTag(tag);
            Log.v(newTag, msg);
        }
    }

    public static void d(String msg) {
        if (isDebug && tagLevel <= LEVEL_D)
            Log.d(generateTag(), msg);

    }

    public static void d(String tag, String msg) {
        if (isDebug && tagLevel <= LEVEL_D) {
            String newTag = generateTag(tag);
            Log.d(newTag, msg);
        }
    }

    public static void i(String msg) {
        if (isDebug && tagLevel <= LEVEL_I) {
            Log.i(generateTag(), msg);
        }
    }

    public static void i(String tag, String msg) {
        if (isDebug && tagLevel <= LEVEL_I) {
            String newTag = generateTag(tag);
            Log.i(newTag, msg);
        }
    }

    public static void w(String msg) {
        if (isDebug && tagLevel <= LEVEL_W) {
            Log.w(generateTag(), msg);
        }
    }


    public static void w(String tag, String msg) {
        if (isDebug && tagLevel <= LEVEL_W) {
            String newTag = generateTag(tag);
            Log.w(newTag, msg);
        }
    }

    public static void e(String msg) {
        if (isDebug && tagLevel <= LEVEL_E) {
            Log.e(generateTag(), msg);
        }
    }

    public static void e(String tag, String msg) {
        if (isDebug && tagLevel <= LEVEL_E) {
            String newTag = generateTag(tag);
            Log.e(newTag, msg);
        }
    }

    private static String generateTag() {
        StackTraceElement caller = new Throwable().getStackTrace()[2];
        String tag = "%s.%s[%d]";
        String callerClazzName = caller.getClassName();
        callerClazzName = callerClazzName.substring(callerClazzName.lastIndexOf(".") + 1);
        tag = String.format(tag, callerClazzName, caller.getMethodName(), caller.getLineNumber());
        tag = TextUtils.isEmpty(prefix) ? tag : prefix.concat(tag);
        return tag;
    }

    private static String generateTag(String mTag) {
        if (!TextUtils.isEmpty(mTag)) {
            return prefix.concat(mTag);
        } else {
            return generateTag();
        }
    }

}
