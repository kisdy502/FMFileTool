package cn.fengmang.file.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;

import java.io.FileInputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.UUID;

import cn.fengmang.baselib.ELog;

public class NetUtils {

    private final static String TAG = "NetUtils";

    private static final int SIZE_KB = 1024;

    private final static String NOUSE_MAC = "02:00:00:00:00:00";

    /**
     * 获取mac地址
     *
     * @param context
     * @return
     */
    @SuppressLint("MissingPermission")
    public static String getMacAddress(Context context) {
        String ret = null;
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni != null) {
            ret = getMacEth();
        }
        if (TextUtils.isEmpty(ret)) {
            WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            ret = wm.getConnectionInfo().getMacAddress();
            if (NOUSE_MAC.equalsIgnoreCase(ret)) {
                ret = getUUID();
            }
        }
        if (TextUtils.isEmpty(ret)) {
            ret = getUUID();
        }
        return ret;
    }

    private static String getMacEth() {
        String macEth = null;
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> net = intf.getInetAddresses(); net
                        .hasMoreElements(); ) {
                    InetAddress iaddr = net.nextElement();
                    if (iaddr instanceof Inet4Address) {
                        if (!iaddr.isLoopbackAddress()) {
                            byte[] data = intf.getHardwareAddress();
                            StringBuilder sb = new StringBuilder();
                            if (data != null && data.length > 1) {
                                sb.append(parseByte(data[0])).append(":")
                                        .append(parseByte(data[1])).append(":")
                                        .append(parseByte(data[2])).append(":")
                                        .append(parseByte(data[3])).append(":")
                                        .append(parseByte(data[4])).append(":")
                                        .append(parseByte(data[5]));
                            }
                            macEth = sb.toString();
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return macEth;
    }


    /**
     * 得到全局唯一UUID
     */
    public static String getUUID() {
        String uuid = UUID.randomUUID().toString().replaceAll("\\-", "");
        ELog.i("DeviceId from UUID : ", uuid);
        return uuid;
    }


    public static String getIpAddress(Context context) {

        String ip = null;
        if (isNetworkAvailable(context)) {
            if (isCablePlugin()) {
                ELog.d("网线已经插入");
                ip = getEthernetIpAddress();
            } else {
                ip = getWifiIpAddress(context);
            }
        } else {
            ip = "0.0.0.0";
        }
        return ip;
    }


    public static String getWifiIpAddress(Context context) {
        ELog.d("getWifiIpAddress");
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        //判断wifi是否开启
        if (wifiManager.isWifiEnabled()) {
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            if (wifiInfo != null && wifiInfo.getIpAddress() > 0) {
                return intToIp(wifiInfo.getIpAddress());
            }
        }
        return null;
    }


    public static String getEthernetIpAddress() {
        ELog.d("getEthernetIpAddress");
        String localIp = "0.0.0.0";
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();

                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address && !inetAddress.getHostAddress().toString().equals("0.0.0.0")) {
                        localIp = inetAddress.getHostAddress().toString();
                        if (!localIp.equals("192.168.43.1")) {
                            return localIp;
                        }

                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return localIp;
    }

    /**
     * 检查当有线是否插入
     *
     * @return
     */
    public static boolean isCablePlugin() {
        final String netFile = "/sys/class/net/eth0/operstate";
        String res = "";
        try {
            FileInputStream fin = new FileInputStream(netFile);
            int length = fin.available();
            byte[] buffer = new byte[length];
            fin.read(buffer);
            res = new String(buffer, "UTF-8");
            fin.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (null != res) {
            if ("up".equals(res.trim()) || "unknown".equals(res.trim())) {
                return true;
            }
        }
        return false;
    }

    public static boolean isNetworkAvailable(Context context) {
        ELog.d("isNetworkAvailable");
        ConnectivityManager connectivityManager = null;
        connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            ELog.d("Network is invailable 1 !");
            return false;
        } else {
            // 获取NetworkInfo对象
            NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();
            if (networkInfo != null && networkInfo.length > 0) {
                for (int i = 0; i < networkInfo.length; i++) {
                    // 判断当前网络状态是否为连接状态
                    if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED) {
                        ELog.d("Network is invailable ok !");
                        return true;
                    }
                }
            }
        }
        ELog.d("Network is invailable 2 !");
        return false;
    }

    private static String intToIp(int i) {
        return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF) + "." + ((i >> 24) & 0xFF);
    }

    private static String parseByte(byte b) {
        int intValue = 0;
        if (b >= 0) {
            intValue = b;
        } else {
            intValue = 256 + b;
        }
        return Integer.toHexString(intValue);
    }

}
