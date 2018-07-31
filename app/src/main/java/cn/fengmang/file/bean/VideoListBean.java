package cn.fengmang.file.bean;

import java.util.List;

/**
 * Created by Administrator on 2018/7/30.
 */

public class VideoListBean {

    List<VideoInfo> videoInfoList;
    List<Performer> performerList;

    public List<VideoInfo> getVideoInfoList() {
        return videoInfoList;
    }

    public void setVideoInfoList(List<VideoInfo> videoInfoList) {
        this.videoInfoList = videoInfoList;
    }

    public List<Performer> getPerformerList() {
        return performerList;
    }

    public void setPerformerList(List<Performer> performerList) {
        this.performerList = performerList;
    }

    public static class VideoInfo {

        private String videoName;
        private long videoDate;
        private String director;
        private String videoPosterUrl;

        public String getVideoName() {
            return videoName;
        }

        public void setVideoName(String videoName) {
            this.videoName = videoName;
        }

        public long getVideoDate() {
            return videoDate;
        }

        public void setVideoDate(long videoDate) {
            this.videoDate = videoDate;
        }

        public String getDirector() {
            return director;
        }

        public void setDirector(String director) {
            this.director = director;
        }

        public String getVideoPosterUrl() {
            return videoPosterUrl;
        }

        public void setVideoPosterUrl(String videoPosterUrl) {
            this.videoPosterUrl = videoPosterUrl;
        }
    }

    public static class Performer {

        private String name;
        private String sex;
        private int age;
        private String city;
        private String performUrl;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getSex() {
            return sex;
        }

        public void setSex(String sex) {
            this.sex = sex;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getPerformUrl() {
            return performUrl;
        }

        public void setPerformUrl(String performUrl) {
            this.performUrl = performUrl;
        }
    }
}
