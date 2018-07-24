package cn.fengmang.file.widget;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import cn.fengmang.baselib.ELog;
import cn.fengmang.file.R;

/**
 * Created by Administrator on 2018/7/23.
 */

public class StatusView extends LinearLayout implements View.OnClickListener {

    public final static int STATUS_SYSTEM = 1;
    public final static int STATUS_USER = 0;
    private int mStatus = STATUS_USER;
    private StatusItemView mStausItemSystem;
    private StatusItemView mStausItemUser;
    private OnFocusChangeListener onFocusChangeListener;

    public StatusView(Context context) {
        this(context, null);
    }

    public StatusView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StatusView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOrientation(HORIZONTAL);
        inflate(getContext(), R.layout.fm_status_view_layout, this);
        setDescendantFocusability(FOCUS_AFTER_DESCENDANTS);
        onFocusChangeListener = new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                ELog.d("onFocusChange");
                if (v.hasFocus()) {
                    if (onItemFocusChangeListener != null) {
                        onItemFocusChangeListener.onItemFocusChange((StatusItemView) v);
                    }
                }
            }
        };

        mStausItemSystem = findViewById(R.id.statusItemSystem);
        mStausItemUser = findViewById(R.id.statusItemUser);
        mStausItemSystem.setOnClickListener(this);
        mStausItemUser.setOnClickListener(this);
        mStausItemSystem.setOnFocusChangeListener(onFocusChangeListener);
        mStausItemUser.setOnFocusChangeListener(onFocusChangeListener);
        mStausItemSystem.setStatus(STATUS_SYSTEM);
        mStausItemUser.setStatus(STATUS_USER);
        notifyStatusChange();
    }


    @Override
    public void onClick(View v) {
        if (v instanceof StatusItemView) {
            final StatusItemView statusItemView = (StatusItemView) v;
            if (statusItemView.getStatus() != mStatus) {
                mStatus = statusItemView.getStatus();
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClickListener(statusItemView, mStatus);
                    notifyStatusChange();
                }
            }
        }
    }


    private OnItemFocusChangeListener onItemFocusChangeListener;

    public void setOnItemFocusChangeListener(OnItemFocusChangeListener onItemFocusChangeListener) {
        this.onItemFocusChangeListener = onItemFocusChangeListener;
    }

    public interface OnItemFocusChangeListener {
        void onItemFocusChange(StatusItemView itemView);
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClickListener(StatusItemView itemView, int status);
    }

    private void notifyStatusChange() {
        mStausItemSystem.onStatusChange(mStatus);
        mStausItemUser.onStatusChange(mStatus);
    }

}
