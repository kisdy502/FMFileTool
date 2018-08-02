package cn.fengmang.file.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import cn.fengmang.baselib.ELog;
import cn.fengmang.file.R;

/**
 * Created by Administrator on 2018/7/31.
 */

public class FMToast extends Toast {
    private static final String TAG = FMToast.class.getSimpleName();

    private Context mContext;
    private TextView mToastTxt;

    public FMToast(Context context) {
        this(context, 0, 0);
    }

    public FMToast(Context context, int resId, int textViewId) {
        super(context);
        mContext = context;
        setView(resId, textViewId);
    }

    public FMToast setView() {
        return setView(0, 0);
    }

    public FMToast setView(int resId, int textViewId) {
        if (0 == resId) {
            resId = R.layout.fm_toast_layout;
            textViewId = R.id.toast_txt_view;
        }
        View v = LayoutInflater.from(mContext).inflate(resId, null);
        mToastTxt = (TextView) v.findViewById(textViewId);
        setView(v);
        return this;
    }

    /**
     * 使用{@link (CharSequence)}替代
     * 不再调用super.setText(CharSequence), 会抛出"java.lang.RuntimeException: This Toast was not created with Toast.makeText()"
     */
    @Deprecated
    @Override
    public void setText(CharSequence s) {
        ELog.w(TAG, "setText(CharSequence), depreated function.");
    }

    /**
     * 使用{@link #text(int)}替代
     * 不再调用super.setText(int), 会抛出"java.lang.RuntimeException: This Toast was not created with Toast.makeText()"
     */
    @Deprecated
    @Override
    public void setText(int resId) {
        ELog.w(TAG, "setText(int), depreated function.");
    }

    public FMToast text(String txt) {
        if (mToastTxt == null) {
            throw new NullPointerException("you should call setView() first");
        }
        mToastTxt.setText(txt);
        return this;
    }

    public FMToast text(int resId) {
        if (mToastTxt == null) {
            throw new NullPointerException("you should call setView() first");
        }
        String res = mContext.getResources().getString(resId);
        mToastTxt.setText(res);
        return this;
    }

    public FMToast duration(int duration) {
        this.setDuration(duration);
        return this;
    }

    public FMToast gravity(int gravity, int xOffset, int yOffset) {
        this.setGravity(gravity, xOffset, yOffset);
        return this;
    }

}
