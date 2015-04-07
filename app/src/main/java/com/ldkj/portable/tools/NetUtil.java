package com.ldkj.portable.tools;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import org.apache.http.conn.util.InetAddressUtils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.content.*;

public class NetUtil {
	/**
	 * 检查当前手机网络
	 */
	public static boolean checkNetConnectde(Context context) {
		boolean mobileConnectde = isMOBILEConnectde(context);
		boolean wifiConnectde = isWIFIConnectde(context);
		if (mobileConnectde == false && wifiConnectde == false) {
			return false;
		} else if (mobileConnectde) {
		}
		return true;
	}

	/**
	 * 判断手机是否采用手机默认联网方式
	 */
	public static boolean isMOBILEConnectde(Context context) {
		ConnectivityManager manager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = manager
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		if (networkInfo != null && networkInfo.isConnected()) {
			return true;
		}
		return false;
	}

	/**
	 * 判断手机是否采用 WIFI 联网
	 */
	public static boolean isWIFIConnectde(Context context) {
		ConnectivityManager manager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = manager
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if (networkInfo != null && networkInfo.isConnected()) {
			return true;
		}
		return false;
	}

	/**
	 *   判断wifi是否打开
	 * @param context
	 * @return
	 */
	public boolean isWifiActive(Context context) {
		ConnectivityManager mConnectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (mConnectivity != null) {
			NetworkInfo[] infos = mConnectivity.getAllNetworkInfo();

			if (infos != null) {
				for (NetworkInfo ni : infos) {
					if ("WIFI".equals(ni.getTypeName()) && ni.isConnected())
						return true;
				}
			}
		}

		return false;
	}

	/**
	 * 得到本机IP地址
	 * @return
	 */
	public static String getLocalIpAddress() {
		try {
			Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces();
			while (en.hasMoreElements()) {
				NetworkInterface nif = en.nextElement();
				Enumeration<InetAddress> enumIpAddr = nif.getInetAddresses();
				while (enumIpAddr.hasMoreElements()) {
					InetAddress mInetAddress = enumIpAddr.nextElement();
					if (!mInetAddress.isLoopbackAddress()
							&& InetAddressUtils.isIPv4Address(mInetAddress
									.getHostAddress())) {
						return mInetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (SocketException ex) {
			Log.e("MyFeiGeActivity", "获取本地IP地址失败");
		}

		return null;
	}

	/**
	 * 获取本机MAC地址
	 * @param context
	 * @return
	 */
	public static String getLocalMacAddress(Context context) {
		WifiManager wifi = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifi.getConnectionInfo();
		return info.getMacAddress();
	}
}
