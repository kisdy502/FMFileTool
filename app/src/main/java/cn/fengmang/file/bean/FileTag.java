package cn.fengmang.file.bean;

/**
 * Created by Administrator on 2018/6/28.
 */

public class FileTag {

    private String fileName;
    private  String tagName;

    public FileTag(String fileName, String tagName) {
        this.fileName = fileName;
        this.tagName = tagName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }
}
