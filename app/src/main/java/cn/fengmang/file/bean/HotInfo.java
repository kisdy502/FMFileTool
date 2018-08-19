package cn.fengmang.file.bean;

import java.util.List;

/**
 * Created by Administrator on 2018/8/7.
 */

public class HotInfo {
    private int hotId;
    private String hotDesc;
    private List<String> hotUrls;
    private String tag;

    public int getHotId() {
        return hotId;
    }

    public void setHotId(int hotId) {
        this.hotId = hotId;
    }

    public String getHotDesc() {
        return hotDesc;
    }

    public void setHotDesc(String hotDesc) {
        this.hotDesc = hotDesc;
    }

    public List<String> getHotUrls() {
        return hotUrls;
    }

    public void setHotUrls(List<String> hotUrls) {
        this.hotUrls = hotUrls;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
