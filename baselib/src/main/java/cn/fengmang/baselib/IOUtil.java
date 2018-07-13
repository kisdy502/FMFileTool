package cn.fengmang.baselib;

import java.io.Closeable;

/**
 * Created by Administrator on 2018/7/2.
 */

public class IOUtil {

    public static void closeQuietly(Closeable... closeable) {
        if (closeable != null) {
            try {
                for (Closeable o : closeable) {
                    if (o != null) {
                        o.close();
                    }
                }
            } catch (Throwable ignored) {
            }
        }
    }
}
