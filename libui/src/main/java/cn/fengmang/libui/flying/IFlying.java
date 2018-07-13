package cn.fengmang.libui.flying;

import android.support.annotation.NonNull;
import android.view.View;

/**
 * Created by Administrator on 2018/7/10.
 */

public interface IFlying {
    void onMoveTo(@NonNull View focusView, float scaleX, float scaleY, float raduis);
}
