package cn.fengmang.file;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.widget.TextView;

/**
 * Created by Administrator on 2018/6/27.
 */

public abstract class FMBaseTitleActivity extends FMBaseActivity {

    private TextView mainTitle;
    private TextView subTitle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initUI() {
        super.initUI();
        mainTitle = (TextView) findViewById(R.id.mainTitle);
        subTitle = (TextView) findViewById(R.id.subTitle);
    }

    public void setMainTitle(String text){
        mainTitle.setText(text);
    }

    public void setSubTitle(String text){
        subTitle.setText(text);
    }

    public void setMainTitle(@StringRes int resId){
        mainTitle.setText(resId);
    }

    public void setSubTitle(@StringRes int resId){
        subTitle.setText(resId);
    }
}
