package cn.fengmang.file.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.fengmang.file.R;

/**
 * Created by Administrator on 2018/7/23.
 */

public class StatusItemView extends RelativeLayout {

    private TextView mTvStatus;
    private ImageView mImgStatus;
    private CharSequence text;
    private int mStatus;

    public StatusItemView(Context context) {
        this(context, null);
    }

    public StatusItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StatusItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.StatusItemView);
        text = a.getText(R.styleable.StatusItemView_itemText);
        a.recycle();
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.fm_status_item_view_layout, this);
        setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);
        mTvStatus = findViewById(R.id.tvStatus);
        mImgStatus = findViewById(R.id.imgSelected);
        if (text != null) {
            mTvStatus.setText(text);
        }
    }

    public int getStatus() {
        return mStatus;
    }

    public void setStatus(int mStatus) {
        this.mStatus = mStatus;
    }

    public void onStatusChange(int status) {
        if (status == mStatus) {
            mImgStatus.setVisibility(View.VISIBLE);
        } else {
            mImgStatus.setVisibility(View.GONE);
        }
    }


}
