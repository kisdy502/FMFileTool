package cn.fengmang.file.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Build;

import cn.fengmang.baselib.ELog;
import cn.fengmang.file.widget.FMToast;

/**
 * Created by Administrator on 2018/7/31.
 */

public class NetListenerHelper {

    public static void initListener(final Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            // 请注意这里会有一个版本适配bug，所以请在这里添加非空判断
            if (connectivityManager != null) {
                connectivityManager.requestNetwork(new NetworkRequest.Builder().build(), new ConnectivityManager.NetworkCallback() {
                    @Override
                    public void onAvailable(Network network) {
                        super.onAvailable(network);
                        ELog.e("onAvailable");
                        new FMToast(context).text("onAvailable").show();
                    }

                    @Override
                    public void onLosing(Network network, int maxMsToLive) {
                        super.onLosing(network, maxMsToLive);
                        new FMToast(context).text("onLosing:" + maxMsToLive).show();
                    }

                    @Override
                    public void onLost(Network network) {
                        super.onLost(network);
                        new FMToast(context).text("onLost:").show();
                    }

                    @Override
                    public void onUnavailable() {
                        super.onUnavailable();
                        new FMToast(context).text("onUnavailable:").show();
                    }

                    @Override
                    public void onCapabilitiesChanged(Network network, NetworkCapabilities networkCapabilities) {
                        super.onCapabilitiesChanged(network, networkCapabilities);
                        new FMToast(context).text("onCapabilitiesChanged:").show();
                    }

                    @Override
                    public void onLinkPropertiesChanged(Network network, LinkProperties linkProperties) {
                        super.onLinkPropertiesChanged(network, linkProperties);
                        new FMToast(context).text("onLinkPropertiesChanged:").show();
                    }
                });
            }
        }
    }
}
