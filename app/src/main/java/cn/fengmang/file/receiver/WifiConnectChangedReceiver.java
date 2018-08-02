package cn.fengmang.file.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Parcelable;

import cn.fengmang.baselib.ELog;
import cn.fengmang.file.widget.FMToast;

/**
 * Created by Administrator on 2018/7/29.
 */

public class WifiConnectChangedReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())) {
            Parcelable parcelableExtra = intent
                    .getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            if (null != parcelableExtra) {
                NetworkInfo networkInfo = (NetworkInfo) parcelableExtra;
                if (networkInfo != null) {
                    String text = "state:" + networkInfo.getState() + ",isConnected:" + networkInfo.isConnected() + ",isAvailable:" + networkInfo.isAvailable();
                    ELog.e(text);
                    new FMToast(context).text(text).show();
                } else {
                    new FMToast(context).text("networkInfo is null").show();
                }
            } else {
                new FMToast(context).text("parcelableExtra is null").show();
            }
        }
    }
}
