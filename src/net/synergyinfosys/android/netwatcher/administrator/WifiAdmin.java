package net.synergyinfosys.android.netwatcher.administrator;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.util.Log;

public class WifiAdmin {
	public static final String TAG = "WifiAdmin";
	private WifiManager mWifiManager;

	public WifiAdmin(Context context) {
		// 取得WifiManager对象
		mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
	}

	// 打开WIFI
	public void openWifi() {
		Log.d( TAG, "open wifi");
		if ( mWifiManager!= null && !mWifiManager.isWifiEnabled()) {
			mWifiManager.setWifiEnabled(true);
		}
	}

	// 关闭WIFI
	public void closeWifi() {
		Log.d( TAG, "close wifi");
		if ( mWifiManager != null && mWifiManager.isWifiEnabled()) {
			mWifiManager.setWifiEnabled(false);
		}
	}
	
	// 检查当前WIFI状态
    public int checkState() {
            return mWifiManager.getWifiState();
    }
}
