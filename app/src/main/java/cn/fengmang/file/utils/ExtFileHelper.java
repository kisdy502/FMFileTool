package cn.fengmang.file.utils;

import android.content.Context;
import android.os.Environment;
import android.os.storage.StorageManager;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import cn.fengmang.baselib.ELog;

/**
 * Created by Administrator on 2018/7/25.
 */

public class ExtFileHelper {

    public final static String KEY_USB = "USB";
    public final static String KEY_SD = "SD";
    public final static String KEY_EXT = "EXT";

    public static List<String> getStoragePath(Context mContext, String keyword) {
        List<String> tagetList = new ArrayList<>();
        StorageManager mStorageManager = (StorageManager) mContext
                .getSystemService(Context.STORAGE_SERVICE);

        Class<?> storageVolumeClazz = null;

        try {
            storageVolumeClazz = Class.forName("android.os.storage.StorageVolume");
            Method getVolumeList = mStorageManager.getClass().getMethod("getVolumeList");
            Method getPath = storageVolumeClazz.getMethod("getPath");
            Object result = getVolumeList.invoke(mStorageManager);
            final int length = Array.getLength(result);
            Method getUserLabel = storageVolumeClazz.getMethod("getUserLabel");

            for (int i = 0; i < length; i++) {
                Object storageVolumeElement = Array.get(result, i);
                String userLabel = (String) getUserLabel.invoke(storageVolumeElement);
                String path = (String) getPath.invoke(storageVolumeElement);
                MemHelper.getExtInfo(path);
                if (userLabel.contains(keyword)) {
                    tagetList.add(path);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return tagetList;

    }
}
