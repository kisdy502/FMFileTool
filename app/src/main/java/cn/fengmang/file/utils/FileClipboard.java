package cn.fengmang.file.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.fengmang.baselib.ELog;
import cn.fengmang.file.bean.FileItem;

/**
 * 文件剪贴板
 * Created by Administrator on 2018/7/2.
 */

public class FileClipboard {
    private final static String TAG = "Clipboard";
    private static boolean isCut = false;  //是剪切还是复制

    public final static List<String> ClipboardList = new ArrayList<>();

    public static boolean hasContent() {
        return ClipboardList.size() != 0;
    }

    public static int getClipboardSize() {
        return ClipboardList.size();
    }


    public static List<String> getClipboardContent() {
        return ClipboardList;
    }

    public static boolean isCut() {
        return isCut;
    }

    public static void cutFile(String file) {
        save(file, true);
    }

    public static void cutFile(List<String> fileList) {
        save(fileList, true);
    }

    public static void copyFile(List<String> fileList) {
        save(fileList, false);
    }

    public static void copyFile(String file) {
        save(file, false);
    }

    private static void save(String file, boolean iscut) {
        ELog.w(TAG, iscut + ",file:" + file);
        ClipboardList.clear();
        ClipboardList.add(file);
        isCut = iscut;
        printClipboard();
    }

    private static void save(List<String> fileList, boolean iscut) {
        if (fileList != null && fileList.size() > 0) {
            ClipboardList.clear();
            ClipboardList.addAll(fileList);
            isCut = iscut;
        }
        printClipboard();
    }

    public static boolean checkFileExits(File tagDir) {
        if (tagDir.isDirectory()) {
            File[] subFiles = tagDir.listFiles();
            if (subFiles == null || subFiles.length == 0) {
                return false;
            } else {
                for (File file : subFiles) {
                    if (ClipboardList.contains(file.getAbsolutePath())) {
                        return true;
                    }
                }
                return false;
            }
        }
        return false;
    }

    public static void pasteFile(File tagDir) {
        if (tagDir.isDirectory()) {
            for (String strfile : ClipboardList) {
                File sourceFile = new File(strfile);
                File tagFile = new File(tagDir, sourceFile.getName());
                ELog.d(TAG, "tagFile:" + tagFile.getAbsolutePath());
                if (sourceFile.isFile()) {
                    FileOptHelper.copyFile(sourceFile, tagFile);
                } else if (sourceFile.isDirectory()) {
                    FileOptHelper.copyDir(sourceFile.getAbsolutePath(), tagFile.getAbsolutePath());
                }
            }
        }

        if (isCut) {
            for (String strfile : ClipboardList) {
                FileOptHelper.deleteFile(new File(strfile));
            }
        }
        ClipboardList.clear();
        isCut = false;
    }

    private static void printClipboard() {
        ELog.d("剪切板内容:");
        for (String str : ClipboardList) {
            ELog.i("path:" + str);
        }
        ELog.d("打印剪贴板结束......");
    }

}
