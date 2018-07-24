package cn.fengmang.file;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;


import cn.fengmang.libui.FMProgressBar;

/**
 * Created by Administrator on 2018/6/27.
 */

public abstract class FMBaseActivity extends FragmentActivity {
    protected ImageView mImgBackground;
    protected FMProgressBar mLoadingBar;


    protected void initUI() {
        mImgBackground = (ImageView) findViewById(R.id.fm_bg);
        mLoadingBar = (FMProgressBar) findViewById(R.id.fm_loadingbar);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        initUI();
    }

    @Override
    public <T extends View> T findViewById(int id) {
        return super.findViewById(id);
    }



    @Override
    public ClassLoader getClassLoader() {
        return super.getClassLoader();
    }

    @NonNull
    @Override
    public LayoutInflater getLayoutInflater() {
        return super.getLayoutInflater();
    }

    @Override
    public WindowManager getWindowManager() {
        return super.getWindowManager();
    }

    @Override
    public Window getWindow() {
        return super.getWindow();
    }

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

}
