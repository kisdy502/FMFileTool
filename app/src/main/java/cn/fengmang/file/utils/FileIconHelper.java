package cn.fengmang.file.utils;

import java.util.HashMap;

import cn.fengmang.file.R;

/**
 * 根据扩展名获取对应的图标
 * Created by Administrator on 2018/6/27.
 */

public class FileIconHelper {
    private static HashMap<String, Integer> fileExtToIcons = new HashMap<String, Integer>();

    static {
        addItem(new String[]{
                "mp3"
        }, R.drawable.file_icon_mp3);
        addItem(new String[]{
                "wma"
        }, R.drawable.file_icon_wma);
        addItem(new String[]{
                "aac"
        }, R.drawable.file_icon_aac);
        addItem(new String[]{
                "flac"
        }, R.drawable.file_icon_flac);
        addItem(new String[]{
                "wav"
        }, R.drawable.file_icon_wav);
        addItem(new String[]{
                "mid"
        }, R.drawable.file_icon_mid);

        addItem(new String[]{
                "rmvb"
        }, R.drawable.file_icon_rmvb);
        addItem(new String[]{
                "mkv"
        }, R.drawable.file_icon_mkv);
        addItem(new String[]{
                "wav"
        }, R.drawable.file_icon_wav);
        addItem(new String[]{
                "wmw"
        }, R.drawable.file_icon_wmw);
        addItem(new String[]{
                "wma"
        }, R.drawable.file_icon_wma);
        addItem(new String[]{
                "mp4", "wmv", "mpeg", "m4v", "3gp", "3gpp", "3g2", "3gpp2", "asf"
        }, R.drawable.file_icon_video);
        addItem(new String[]{
                "jpeg", "bmp", "wbmp","webp"
        }, R.drawable.file_icon_picture);
        addItem(new String[]{
                "png"
        }, R.drawable.file_icon_png);
        addItem(new String[]{
                "gif"
        }, R.drawable.file_icon_gif);
        addItem(new String[]{
                "jpg"
        }, R.drawable.file_icon_jpg);

        addItem(new String[]{
                "txt", "log", "xml", "ini", "lrc"
        }, R.drawable.file_icon_txt);
        addItem(new String[]{
                "doc", "docx"
        }, R.drawable.file_icon_word);

        addItem(new String[]{
                "xsl", "xslx"
        }, R.drawable.file_icon_excel);

        addItem(new String[]{
                "ppt", "pptx"
        }, R.drawable.file_icon_ppt);
        addItem(new String[]{
                "pdf"
        }, R.drawable.file_icon_pdf);
        addItem(new String[]{
                "zip"
        }, R.drawable.file_icon_zip);
        addItem(new String[]{
                "pdf"
        }, R.drawable.file_icon_pdf);
        addItem(new String[]{
                "mtz"
        }, R.drawable.file_icon_theme);
        addItem(new String[]{
                "rar"
        }, R.drawable.file_icon_rar);
        addItem(new String[]{
                "apk"
        }, R.drawable.file_icon_apk);
        addItem(new String[]{
                "bt"
        }, R.drawable.file_icon_bt);
    }


    private static void addItem(String[] exts, int resId) {
        if (exts != null) {
            for (String ext : exts) {
                fileExtToIcons.put(ext.toLowerCase(), resId);
            }
        }
    }

    public static int getFileIcon(String ext) {
        Integer i = fileExtToIcons.get(ext.toLowerCase());
        if (i != null) {
            return i.intValue();
        } else {
            return R.drawable.file_icon_default;
        }
    }


    public static String getExtion(String fileName) {
        int index = fileName.lastIndexOf('.');
        if (index > 0) {
            String ext = fileName.substring(index + 1, fileName.length());
            return ext;
        } else {
            return "";
        }
    }
}
