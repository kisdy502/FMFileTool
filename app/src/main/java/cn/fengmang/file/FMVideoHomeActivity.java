package cn.fengmang.file;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import cn.fengmang.baselib.ELog;
import cn.fengmang.file.adapter.HomeTitleAdapter;
import cn.fengmang.file.adapter.VideoListAdapter;
import cn.fengmang.file.bean.HotInfo;
import cn.fengmang.file.bean.TitleBean;
import cn.fengmang.file.bean.VideoListBean;
import cn.fengmang.file.service.TaskService;
import cn.fengmang.file.task.GetIpTask;
import cn.fengmang.file.utils.ImgUrlCreator;
import cn.fengmang.libui.flying.DrawableFlyingFrameView;
import cn.fengmang.libui.recycler.FMRecyclerView;
import cn.fengmang.libui.recycler.OnItemFocusChangeListener;

/**
 * Created by Administrator on 2018/7/30.
 */
public class FMVideoHomeActivity extends FMBaseActivity {

    private FMRecyclerView mTitleRecyclerView;
    private FMRecyclerView mContentRecyclerView;
    private HomeTitleAdapter mHomeTitleAdapter;
    private VideoListAdapter mVideoListAdapter;
    private DrawableFlyingFrameView mFlyingView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fm_activity_video_home);
    }

    @Override
    protected void initUI() {
        super.initUI();
        mFlyingView = DrawableFlyingFrameView.build(this);
        mFlyingView.setFlyingDrawable(getResources().getDrawable(R.drawable.hover_item));
        mTitleRecyclerView = findViewById(R.id.contentTitle);
        mContentRecyclerView = findViewById(R.id.contentList);
        LinearLayoutManager titleLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mTitleRecyclerView.setLayoutManager(titleLayoutManager);
        mTitleRecyclerView.setOnItemFocusListener(new OnItemFocusChangeListener() {
            @Override
            public boolean onItemPreSelected(@NonNull RecyclerView parent, @NonNull View itemView, int position) {
                return false;
            }

            @Override
            public boolean onItemSelected(@NonNull RecyclerView parent, @NonNull View itemView, int position) {
                mFlyingView.onMoveTo(itemView, 1.0f, 1.0f, 0);
                return false;
            }

            @Override
            public boolean onReviseFocusFollow(@NonNull RecyclerView parent, @NonNull View itemView, int position) {
                return false;
            }
        });

        LinearLayoutManager contentLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mContentRecyclerView.setLayoutManager(contentLayoutManager);


        initTitle();
        initContent();


    }


    private void initTitle() {
        TitleBean titleBean0 = new TitleBean("热推");
        TitleBean titleBean1 = new TitleBean("爱奇艺");
        TitleBean titleBean2 = new TitleBean("腾讯");
        TitleBean titleBean3 = new TitleBean("4K花园");
        TitleBean titleBean4 = new TitleBean("IPTV");
        TitleBean titleBean5 = new TitleBean("我的");
        TitleBean titleBean6 = new TitleBean("购物");
        TitleBean titleBean7 = new TitleBean("优酷网");
        TitleBean titleBean8 = new TitleBean("百视通");
        TitleBean titleBean9 = new TitleBean("36氪氢");
        TitleBean titleBean10 = new TitleBean("头条热搜");
        TitleBean titleBean11 = new TitleBean("金典影视");
        TitleBean titleBean12 = new TitleBean("TVB剧场");
        TitleBean titleBean13 = new TitleBean("颤抖美剧");
        List<TitleBean> beanList = new ArrayList<>();
        beanList.add(titleBean0);
        beanList.add(titleBean1);
        beanList.add(titleBean2);
        beanList.add(titleBean3);
        beanList.add(titleBean4);
        beanList.add(titleBean5);
        beanList.add(titleBean6);
        beanList.add(titleBean7);
        beanList.add(titleBean8);
        beanList.add(titleBean9);
        beanList.add(titleBean10);
        beanList.add(titleBean11);
        beanList.add(titleBean12);
        beanList.add(titleBean13);
        mHomeTitleAdapter = new HomeTitleAdapter(this, beanList);
        mTitleRecyclerView.setAdapter(mHomeTitleAdapter);

    }

    private void initContent() {
        List<VideoListBean.Performer> performerList = initPerformerList();
        List<VideoListBean.VideoInfo> videoInfoList = initVieoInfoList();
        List<HotInfo> hotInfoList = initHotAppList();
        VideoListBean videoListBean = new VideoListBean();
        videoListBean.setPerformerList(performerList);
        videoListBean.setVideoInfoList(videoInfoList);
        videoListBean.setHotInfoList(hotInfoList);

        mVideoListAdapter = new VideoListAdapter(this, videoListBean);
        mVideoListAdapter.setItemFocusChangeListener(new OnItemFocusChangeListener() {
            @Override
            public boolean onItemPreSelected(@NonNull RecyclerView parent, @NonNull View itemView, int position) {
                return false;
            }

            @Override
            public boolean onItemSelected(@NonNull RecyclerView parent, @NonNull View itemView, int position) {
                mFlyingView.onMoveTo(itemView, 1.0f, 1.0f, 0);
                return false;
            }

            @Override
            public boolean onReviseFocusFollow(@NonNull RecyclerView parent, @NonNull View itemView, int position) {
                return false;
            }
        });
        mContentRecyclerView.setAdapter(mVideoListAdapter);
    }

    private List<VideoListBean.Performer> initPerformerList() {
        List<VideoListBean.Performer> performerList = new ArrayList<>();

        VideoListBean.Performer performer0 = new VideoListBean.Performer();
        performer0.setName("张一山");
        performer0.setSex("男");
        performer0.setAge(29);
        performer0.setCity("北京");
        performer0.setPerformUrl(ImgUrlCreator.getUrl());
        performerList.add(performer0);

        VideoListBean.Performer performer1 = new VideoListBean.Performer();
        performer1.setName("杨紫");
        performer1.setSex("女");
        performer1.setAge(29);
        performer1.setCity("北京");
        performer1.setPerformUrl(ImgUrlCreator.getUrl());
        performerList.add(performer1);

        VideoListBean.Performer performer2 = new VideoListBean.Performer();
        performer2.setName("杨幂");
        performer2.setSex("女");
        performer2.setAge(33);
        performer2.setCity("上海");
        performer2.setPerformUrl(ImgUrlCreator.getUrl());
        performerList.add(performer2);

        VideoListBean.Performer performer3 = new VideoListBean.Performer();
        performer3.setName("黄维德");
        performer3.setSex("男");
        performer3.setAge(39);
        performer3.setCity("湖南");
        performer3.setPerformUrl(ImgUrlCreator.getUrl());
        performerList.add(performer3);


        VideoListBean.Performer performer4 = new VideoListBean.Performer();
        performer4.setName("周星驰");
        performer4.setSex("男");
        performer4.setAge(66);
        performer4.setCity("香港");
        performer4.setPerformUrl(ImgUrlCreator.getUrl());
        performerList.add(performer4);


        VideoListBean.Performer performer5 = new VideoListBean.Performer();
        performer5.setName("张一鸣");
        performer5.setSex("男");
        performer5.setAge(42);
        performer5.setCity("北京");
        performer5.setPerformUrl(ImgUrlCreator.getUrl());
        performerList.add(performer5);


        VideoListBean.Performer performer6 = new VideoListBean.Performer();
        performer6.setName("周冬雨");
        performer6.setSex("女");
        performer6.setAge(29);
        performer6.setCity("北京");
        performer6.setPerformUrl(ImgUrlCreator.getUrl());
        performerList.add(performer6);

        return performerList;
    }

    private List<VideoListBean.VideoInfo> initVieoInfoList() {
        List<VideoListBean.VideoInfo> videoInfoList = new ArrayList<>();

        VideoListBean.VideoInfo videoInfo0 = new VideoListBean.VideoInfo();
        videoInfo0.setVideoName("我不是药神");
        videoInfo0.setVideoDate(System.currentTimeMillis());
        videoInfo0.setDirector("徐峥");
        videoInfo0.setVideoPosterUrl(ImgUrlCreator.getMovieUrl());
        videoInfoList.add(videoInfo0);

        VideoListBean.VideoInfo videoInfo1 = new VideoListBean.VideoInfo();
        videoInfo1.setVideoName("开封府");
        videoInfo1.setVideoDate(System.currentTimeMillis());
        videoInfo1.setDirector("张继城");
        videoInfo1.setVideoPosterUrl(ImgUrlCreator.getMovieUrl());
        videoInfoList.add(videoInfo1);

        VideoListBean.VideoInfo videoInfo2 = new VideoListBean.VideoInfo();
        videoInfo2.setVideoName("阿修罗");
        videoInfo2.setVideoDate(System.currentTimeMillis());
        videoInfo2.setDirector("梁家辉");
        videoInfo2.setVideoPosterUrl(ImgUrlCreator.getMovieUrl());
        videoInfoList.add(videoInfo2);


        VideoListBean.VideoInfo videoInfo3 = new VideoListBean.VideoInfo();
        videoInfo3.setVideoName("人民的名义");
        videoInfo3.setVideoDate(System.currentTimeMillis());
        videoInfo3.setDirector("张逗比");
        videoInfo3.setVideoPosterUrl(ImgUrlCreator.getMovieUrl());
        videoInfoList.add(videoInfo3);


        VideoListBean.VideoInfo videoInfo4 = new VideoListBean.VideoInfo();
        videoInfo4.setVideoName("西游记");
        videoInfo4.setVideoDate(System.currentTimeMillis());
        videoInfo4.setDirector("杨洁");
        videoInfo4.setVideoPosterUrl(ImgUrlCreator.getMovieUrl());
        videoInfoList.add(videoInfo4);

        VideoListBean.VideoInfo videoInfo5 = new VideoListBean.VideoInfo();
        videoInfo5.setVideoName("三国");
        videoInfo5.setVideoDate(System.currentTimeMillis());
        videoInfo5.setDirector("马东");
        videoInfo5.setVideoPosterUrl(ImgUrlCreator.getMovieUrl());
        videoInfoList.add(videoInfo5);

        VideoListBean.VideoInfo videoInfo6 = new VideoListBean.VideoInfo();
        videoInfo6.setVideoName("国家机密");
        videoInfo6.setVideoDate(System.currentTimeMillis());
        videoInfo6.setDirector("刘璐");
        videoInfo6.setVideoPosterUrl(ImgUrlCreator.getMovieUrl());
        videoInfoList.add(videoInfo6);

        return videoInfoList;
    }

    private List<HotInfo> initHotAppList() {
        List<HotInfo> hotInfoList = new ArrayList<>();

        HotInfo hotInfo0 = new HotInfo();
        hotInfo0.setHotDesc("零点影视");
        List<String> urls0 = new ArrayList<>();
        urls0.add(ImgUrlCreator.getMovieUrl());
        urls0.add(ImgUrlCreator.getMovieUrl());
        urls0.add(ImgUrlCreator.getUrl());
        urls0.add(ImgUrlCreator.getMovieUrl());
        hotInfo0.setHotUrls(urls0);

        hotInfoList.add(hotInfo0);


        HotInfo hotInfo1 = new HotInfo();
        hotInfo0.setHotDesc("零点影视");
        List<String> urls1 = new ArrayList<>();
        urls1.add(ImgUrlCreator.getMovieUrl());
        urls1.add(ImgUrlCreator.getMovieUrl());
        urls1.add(ImgUrlCreator.getUrl());
        urls1.add(ImgUrlCreator.getMovieUrl());
        hotInfo1.setHotUrls(urls1);

        hotInfoList.add(hotInfo1);


        HotInfo hotInfo2 = new HotInfo();
        hotInfo2.setHotDesc("零点影视");
        List<String> urls2 = new ArrayList<>();
        urls2.add(ImgUrlCreator.getMovieUrl());
        urls2.add(ImgUrlCreator.getMovieUrl());
        urls2.add(ImgUrlCreator.getUrl());
        urls2.add(ImgUrlCreator.getMovieUrl());
        hotInfo2.setHotUrls(urls2);

        hotInfoList.add(hotInfo2);

        return hotInfoList;
    }
}
