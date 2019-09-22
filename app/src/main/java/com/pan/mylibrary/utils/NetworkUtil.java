package com.pan.mylibrary.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

/**
 * Created by panchenhuan on 2018/6/18 20:24.
 * 网络工具
 */
public class NetworkUtil {
    /**
     * 判断WIFI网络是否可用
     *
     * @param context 上下文
     * @return true 连接
     */
    public static boolean isWifiConnected(Context context) {
        if (context != null) {
            ConnectivityManager cManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            assert cManager != null;
            NetworkInfo mWiFiNetworkInfo = cManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (mWiFiNetworkInfo != null && mWiFiNetworkInfo.isAvailable()) {
                return mWiFiNetworkInfo.isConnected();
            }
        }
        return false;
    }

    /**
     * 判断WIFI网络是否打开
     *
     * @param context 上下文
     * @return true 打开
     */
    public static boolean isWifiOpened(Context context) {
        WifiManager mWifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        assert mWifiManager != null;
        return mWifiManager.isWifiEnabled();
    }

    /**
     * 获取当前ss_id
     *
     * @param context 上下文
     * @return wifi名称
     */
    public static String getSSID(Context context) {
        if (isWifiConnected(context)) {
            WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            assert wifiManager != null;
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            String ss_id = wifiInfo.getSSID();
            if (ss_id != null) {
                KLog.d("Current wifi ss_id is " + ss_id);
                ss_id = ss_id.replaceAll("\"", "");
                if ("0x".equals(ss_id) || "<unknown ssid>".equalsIgnoreCase(ss_id)) {
                    ss_id = null;
                }
            }
            return ss_id;
        }
        return "";
    }

    /**
     * 判断网络是否可用
     *
     * @param context 上下文
     * @return true 可用
     */
    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            assert mConnectivityManager != null;
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null && mNetworkInfo.isAvailable()) {
                return mNetworkInfo.isConnected();
            }
        }
        return false;
    }

    /**
     * 判断网络是否可用,不可用弹Toast
     *
     * @param act 上下文
     */
//    public static boolean isNetworkConnectedWithToast(BaseActivity act) {
//        boolean flag = isNetworkConnected(act);
//        if (!flag) act.showToast(act.getString(R.string.tip_net_disconnect));
//        return flag;
//    }
}
