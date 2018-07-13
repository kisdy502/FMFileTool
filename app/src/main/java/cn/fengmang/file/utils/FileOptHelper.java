package cn.fengmang.file.utils;

import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cn.fengmang.baselib.IOUtil;
import cn.fengmang.file.bean.FileItem;

/**
 * Created by Administrator on 2018/6/29.
 */

public class FileOptHelper {

    public final static int SORT_BYDATE = 0;
    public final static int SORT_BYNAME = 1;

    public final static int VIEW_MODE_LIST = 0;
    public final static int VIEW_MODE_GRID = 1;

    public final static int SELECT_STATUS_ONE = 0;
    public final static int SELECT_STATUS_MUILT = 1;
    public final static int SELECT_STATUS_ALL = 2;
    public final static int SELECT_STATUS_NONE = 3;
    private final static int BUFFER_SIZE = 4096;

    public final static String FILE_CMD_DELETE = "delete";
    public final static String FILE_CMD_CUT = "cut";
    public final static String FILE_CMD_COPY = "copy";
    public final static String FILE_CMD_PASTE = "paste";
    public final static String FILE_CMD_LISTVIEW = "list_view";
    public final static String FILE_CMD_GRIDVIEW = "grid_view";
    public final static String FILE_CMD_RENAME = "rename";
    public final static String FILE_CMD_FILEPROP = "file_prop";
    public final static String FILE_CMD_SORT_BYDATE = "sort_bydate";
    public final static String FILE_CMD_SORT_BYNAME = "sort_byname";
    public final static String FILE_CMD_RESCAN = "rescan";
    public final static String FILE_CMD_NEW_FILE = "new_file";
    public final static String FILE_CMD_ALL_SELECT = "all_select";
    public final static String FILE_CMD_EXIT_MUILT_SELECT = "exit_muilt_select";
    public final static String FILE_CMD_EXIT_SELECT_NONE = "select_none";
    public final static String FILE_CMD_MUILT_SELECT = "muilt_select";


    public static void deleteFileList(List<String> fileList) {
        for (String strfile : fileList) {
            deleteFile(new File(strfile));
        }
    }

    public static void deleteFile(File file) {
        if (file.exists()) {
            if (file.isFile()) {
                file.delete();
            } else if (file.isDirectory()) {
                deleteDir(file);
            }
        }
    }

    public static boolean deleteDir(File dir) {
        return deleteDir(dir, true);
    }

    public static boolean deleteDir(String dir) {
        return deleteDir(new File(dir), true);
    }

    /**
     * 递归删除目录下的所有文件及子目录下所有文件
     *
     * @param dir     将要删除的文件目录
     * @param delRoot 是否需要将根目录页删除掉
     * @return boolean Returns "true" if all deletions were successful.
     * If a deletion fails, the method stops attempting to
     * delete and returns "false".
     */
    public static boolean deleteDir(File dir, boolean delRoot) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]), true);
                if (!success) {
                    return false;
                }
            }
        }
        // 目录此时为空，可以删除
        if (delRoot) {
            return dir.delete();
        }
        return true;
    }


    /**
     * 文件拷贝
     *
     * @param fileIn
     * @param fileOut
     */
    public static void copyFile(File fileIn, File fileOut) {
        if (!fileIn.exists()) {
            return;
        }
        FileInputStream fileInputStream = null;
        FileOutputStream fileOutputStream = null;
        try {
            fileInputStream = new FileInputStream(fileIn);
            fileOutputStream = new FileOutputStream(fileOut);
            byte[] buffer = new byte[BUFFER_SIZE];
            int len = fileInputStream.read(buffer);
            while (len > 0) {
                fileOutputStream.write(buffer, 0, len);
                len = fileInputStream.read(buffer);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtil.closeQuietly(fileInputStream);
            IOUtil.closeQuietly(fileOutputStream);
        }
    }


    public static void copyDir(String sourceDir, String tagPath) {
        File sourceFile = new File(sourceDir);
        String[] filePath = sourceFile.list();

        File tagFile = new File(tagPath);

        if (!tagFile.exists()) {
            tagFile.mkdir();
        }
        for (int i = 0; i < filePath.length; i++) {
            String strFile = sourceDir.concat(File.separator).concat(filePath[i]);
            File subFile = new File(strFile);
            if (subFile.isDirectory()) {
                copyDir(sourceDir.concat(File.separator).concat(filePath[i]), tagPath.concat(File.separator).concat(filePath[i]));
            }

            if (subFile.isFile()) {
                FileOptHelper.copyFile(subFile, new File(tagFile, filePath[i]));
            }
        }

    }


    public static void orderByName(List<FileItem> fileList, final boolean asc) {
        Collections.sort(fileList, new Comparator<FileItem>() {
            @Override
            public int compare(FileItem o1, FileItem o2) {
                if (o1.isDirectory() && o2.isFile())
                    return asc ? -1 : 1;
                if (o1.isFile() && o2.isDirectory())
                    return asc ? 1 : -1;
                return asc ? o1.getFileName().compareTo(o2.getFileName())
                        : o2.getFileName().compareTo(o1.getFileName());
            }
        });

    }

    public static void orderByDate(List<FileItem> fileList, final boolean asc) {
        Collections.sort(fileList, new Comparator<FileItem>() {
            public int compare(FileItem f1, FileItem f2) {
                long diff = f1.getLastModifyDate() - f2.getLastModifyDate();
                if (diff > 0)
                    return asc ? 1 : -1;
                else if (diff == 0)
                    return 0;
                else
                    return asc ? -1 : 1;
            }
        });
    }

    public static String convertStorage(long size) {
        long kb = 1024;
        long mb = kb * 1024;
        long gb = mb * 1024;

        if (size >= gb) {
            return String.format("%.1f GB", (float) size / gb);
        } else if (size >= mb) {
            float f = (float) size / mb;
            return String.format(f > 100 ? "%.0f MB" : "%.1f MB", f);
        } else if (size >= kb) {
            float f = (float) size / kb;
            return String.format(f > 100 ? "%.0f KB" : "%.1f KB", f);
        } else
            return String.format("%d B", size);
    }


    public static File newDir(String tagDir) {
        for (int i = 0; i < 128; i++) {
            File file = new File(tagDir, NEWFILES.get(i));
            if (!file.exists()) {
                file.mkdir();
                return file;
            }
        }
        return null;
    }


    private final static List<String> NEWFILES = new ArrayList<>(128);
    private final static String FILE_DEFALUT_NEW = "新建文件夹";

    static {
        NEWFILES.add(FILE_DEFALUT_NEW);
        for (int i = 1; i < 128; i++) {
            NEWFILES.add(FILE_DEFALUT_NEW.concat("(").concat(String.valueOf(i)).concat(")"));
        }
    }

}
