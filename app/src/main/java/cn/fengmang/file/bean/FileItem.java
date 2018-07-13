package cn.fengmang.file.bean;

/**
 * Created by Administrator on 2018/6/27.
 */

public class FileItem {

    private String fileName;
    private long lastModifyDate;
    private boolean isDirectory;
    private boolean isFile;
    private String md5;
    private String sha1;
    private boolean isEmpty;
    private String fullPath;
    private String fileTag;

    private boolean isChecked;

    private int childDirCount;
    private int childFileCount;
    private long fileSize;

    public FileItem() {

    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getLastModifyDate() {
        return lastModifyDate;
    }

    public void setLastModifyDate(long lastModifyDate) {
        this.lastModifyDate = lastModifyDate;
    }

    public boolean isDirectory() {
        return isDirectory;
    }

    public void setIsDirectory(boolean dir) {
        isDirectory = dir;
        isFile = !dir;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getSha1() {
        return sha1;
    }

    public void setSha1(String sha1) {
        this.sha1 = sha1;
    }

    public boolean isEmpty() {
        return isEmpty;
    }

    public void setEmpty(boolean empty) {
        isEmpty = empty;
    }

    public String getFullPath() {
        return fullPath;
    }

    public void setFullPath(String fullPath) {
        this.fullPath = fullPath;
    }

    public String getFileTag() {
        return fileTag;
    }

    public void setFileTag(String fileTag) {
        this.fileTag = fileTag;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public boolean isFile() {
        return isFile;
    }

    public int getChildDirCount() {
        return childDirCount;
    }

    public void setChildDirCount(int childDirCount) {
        this.childDirCount = childDirCount;
    }

    public int getChildFileCount() {
        return childFileCount;
    }

    public void setChildFileCount(int childFileCount) {
        this.childFileCount = childFileCount;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }
}
