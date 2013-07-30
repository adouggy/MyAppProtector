package net.synergyinfosys.android.myappprotector.service;

import java.util.ArrayList;

import net.synergyinfosys.android.myappprotector.bean.RunningAppInfo;
import net.synergyinfosys.android.netwatcher.ServiceOperation;
import net.synergyinfosys.android.netwatcher.administrator.AppAdmin;
import net.synergyinfosys.android.netwatcher.administrator.BlueToothAdmin;
import net.synergyinfosys.android.netwatcher.administrator.TrafficAdmin;
import net.synergyinfosys.android.netwatcher.administrator.WifiAdmin;
import android.app.Service;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class WatcherService extends Service {
	public static final String TAG = "WatcherService";
	public static final String INTENT_ACTION = "net.synergyinfosys.android.netwatcher.WatcherService";

	private boolean threadDisable = false;
	private WifiAdmin wifiAdmin = null;
	private BlueToothAdmin bluetoothAdmin = null;
	private TrafficAdmin trafficAdmin = null;
	private AppAdmin appAdmin = null;
	
	final long interval = 3 * 1000;

	Runnable r = new Runnable() {
		@Override
		public void run() {
			long curr = 0l;
			while (!threadDisable) {
				
				curr = System.currentTimeMillis();
				Log.d(TAG, "service is running...");
				if (wifiAdmin != null) {
					StringBuilder sb = new StringBuilder();
					String wifiStatus = "Wifi status:" + wifiAdmin.checkState();
					sb.append(wifiStatus);
					sb.append("\n");
					String bluetoothStatus = "Bluetooth status:" + bluetoothAdmin.isEnable();
					sb.append(bluetoothStatus);
					sb.append("\n");

					if (wifiAdmin.checkState() >= WifiManager.WIFI_STATE_ENABLED) {
						sb.append("正在关闭wifi\n");
						wifiAdmin.closeWifi();
					}
					if (bluetoothAdmin.isEnable()) {
						sb.append("正在关闭蓝牙\n");
						bluetoothAdmin.makeClose();
					}
					
					String trafficStatus = trafficAdmin.getStatus();
					sb.append(trafficStatus);
					sb.append("\n");
					
					Log.i( TAG, sb.toString() );
					
					ArrayList<RunningAppInfo> list = trafficAdmin.getStatusForRunningAppList(appAdmin);
					Intent intent = new Intent(INTENT_ACTION);
					intent.putParcelableArrayListExtra("status", list);
					sendBroadcast(intent);

					Log.i(TAG, sb.toString());
				}
				
				try {
					long elapse = System.currentTimeMillis() - curr;
					long timeToSleep = interval - elapse;
					Log.d(TAG, "prepare to sleep " + timeToSleep + " ms");
					if( timeToSleep > 0 )
						Thread.sleep( timeToSleep );
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	};

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "service created.");

		wifiAdmin = new WifiAdmin(this);
		bluetoothAdmin = new BlueToothAdmin();
		trafficAdmin = new TrafficAdmin();
		appAdmin = new AppAdmin(this);

		new Thread(r).start();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		Log.d(TAG, "service started..");
		if (intent != null) {
			Bundle bundle = intent.getExtras();
			if (bundle != null) {
				ServiceOperation op = (ServiceOperation) bundle.getSerializable("operation");
				switch (op) {
				case START:
					Log.d(TAG, "got operation: start service");
					break;
				case STOP:
					Log.d(TAG, "got operation: stop service");
					break;
				case BIND:
					Log.d(TAG, "got operation: bind service");
					break;
				case UNBIND:
					Log.d(TAG, "got operation: unbind service");
					break;
				default:
					Log.d(TAG, "got operation: N/A");
					break;
				}

			}
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		threadDisable = true;
		Log.d(TAG, "service destroyed..");
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
}
