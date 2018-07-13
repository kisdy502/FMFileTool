package cn.fengmang.file;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import cn.fengmang.libui.FMProgressBar;
import cn.fengmang.libui.effect.BaseEffect;
import cn.fengmang.libui.effect.FocusFrameView;

/**
 * Created by Administrator on 2018/6/27.
 */

public abstract class FMBaseActivity extends AppCompatActivity {

    protected ImageView mImgBackground;
    protected FMProgressBar mLoadingBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        initUI();
    }

    protected void initUI() {
        mImgBackground = (ImageView) findViewById(R.id.fm_bg);
        mLoadingBar = (FMProgressBar) findViewById(R.id.fm_loadingbar);
    }

}
