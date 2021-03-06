package net.synergyinfosys.android.myappprotector.service;

import java.util.ArrayList;
import java.util.HashMap;

import net.synergyinfosys.android.myappprotector.R;
import net.synergyinfosys.android.myappprotector.activity.PasswordActivity;
import net.synergyinfosys.android.myappprotector.activity.SwitchHomeActivity;
import net.synergyinfosys.android.myappprotector.bean.RunningAppInfo;
import net.synergyinfosys.android.myappprotector.util.MyUtil;
import net.synergyinfosys.android.myappprotector.util.NotificationHelper;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class LongLiveService extends Service {

	public static final String TAG = "LongLiveService";
	public static final String LONGLIVESERVICE_BROADCAST_LOCKALL_ACTION = "LongLiveService.broadcast.lockall";
	public static final String LONGLIVESERVICE_BROADCAST_UNLOCK_ACTION = "LongLiveService.broadcast.unlock";
	public static final String LONGLIVESERVICE_BROADCAST_START_SERVICE = "LongLiveService.broadcast.start.service";

	ActivityManager mActivityManager = null;
	public static final long threadInterval = 1000;
	Context mContext = null;

	ArrayList<RunningAppInfo> mLockList = null;

	/**
	 * 每次在安全桌面上点击一次，发一个广播，收到广播后在这里记一个时间戳 短时间内如果扫描到这个程序在运行，就让他继续运行。 否则发现在运行就杀掉
	 */
	HashMap<String, Long> mAppStartMap = new HashMap<String, Long>();

	private final BroadcastReceiver lockAllReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context ctx, Intent intent) {
			Log.i(TAG, "Prepare to lock the selected");
			mLockList = intent.getParcelableArrayListExtra("lockList");
		}
	};

	private final BroadcastReceiver unlockReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context ctx, Intent intent) {
			String pkgName = intent.getStringExtra("pkgName");
			Log.i(TAG, "Prepare to unlock the app " + pkgName);
			for (RunningAppInfo info : mLockList) {
				if (info.getPkgName().compareTo(pkgName) == 0) {
					info.setLocked(false);
					break;
				}
			}
		}
	};

	private final BroadcastReceiver startFromSafeLauncherReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String pkgName = intent.getStringExtra("pkgName");
			Log.i(TAG, "safe start:" + pkgName);
			mAppStartMap.put(pkgName, System.currentTimeMillis());
		}
	};

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		Log.i(TAG, "onCreate");
		mContext = this.getApplicationContext();

		IntentFilter lockAllFilter = new IntentFilter();
		lockAllFilter.addAction(LONGLIVESERVICE_BROADCAST_LOCKALL_ACTION);
		registerReceiver(lockAllReceiver, lockAllFilter);

		IntentFilter unlockFilter = new IntentFilter();
		unlockFilter.addAction(LONGLIVESERVICE_BROADCAST_UNLOCK_ACTION);
		registerReceiver(unlockReceiver, unlockFilter);
	}

	@Override
	public void onDestroy() {
		Log.i(TAG, "onDestroy");
		unregisterReceiver(lockAllReceiver);
		unregisterReceiver(unlockReceiver);
		unregisterReceiver(startFromSafeLauncherReceiver);
		stopForeground(true);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i(TAG, "OnStartCommand");
		handleStart();
		return START_STICKY; // for restart this service..
	}

	@Override
	public void onStart(Intent intent, int startid) {
		Log.i(TAG, "onStart");
		handleStart();
	}

	public void handleStart() {
		Resources r = this.getResources();
		Notification notification = NotificationHelper.genNotification(this.mContext, 0, R.drawable.ic_launcher, "安全桌面已启动..", 0, r.getString(R.string.notification_forground_service_title),
				r.getString(R.string.notification_forground_service_text), SwitchHomeActivity.class, Notification.FLAG_FOREGROUND_SERVICE);
		startForeground(Notification.FLAG_FOREGROUND_SERVICE, notification);

		mActivityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);

		Runnable checkingThread = new Runnable() {
			@Override
			public void run() {
				while (true) {
					long curr = System.currentTimeMillis();
					doSth();
					try {
						long interval = System.currentTimeMillis() - curr;
						long sleepingTime = (threadInterval - interval);
						if (sleepingTime > 0) {
							Log.d(TAG, "sleeping.." + sleepingTime + " ms. elapse:" + interval + " ms.");
							Thread.sleep(sleepingTime);
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		};
		new Thread(checkingThread).start();
	}

	private void doSth() {
		boolean isSafeLauncherDefault = MyUtil.isLauncherDefault(mContext, MyUtil.PKG_NAME);
		Log.d(TAG, "is default?" + isSafeLauncherDefault);

		// 获取目前最顶层的Activity
		ComponentName cn = mActivityManager.getRunningTasks(1).get(0).topActivity;
		String pkgName = cn.getPackageName();
		String className = cn.getClassName();
		Log.d(TAG, pkgName + " is on top");

		if (mLockList == null)
			return;

		boolean isInTheList = findInAppList(pkgName);

		/***
		 * 无论哪个桌面是default，只要安全桌面在运行，就锁定列表中的程序
		 */
		// 如果在block list中
		if (isInTheList) {
			Log.i(TAG, pkgName + " in the list");
			killAndLock(pkgName, className);
		}
	}

	private boolean findInAppList(String pkgName) {
		boolean isInTheList = false;
		for (RunningAppInfo app : mLockList) {
			if (app.isLocked() && app.getPkgName().compareTo(pkgName) == 0) {
				isInTheList = true;
				break;
			}
		}
		return isInTheList;
	}

	// private boolean findLauncherInAppList(String pkgName) {
	// boolean isInTheList = false;
	// for (RunningAppInfo app : mLockList) {
	// // System.out.println( app.getPkgName() + ", " + app.isLauncher() +
	// // ", " + app.isLocked() );
	// if (app.isLocked() && app.isLauncher() &&
	// app.getPkgName().compareTo(pkgName) == 0) {
	// isInTheList = true;
	// break;
	// }
	// }
	// return isInTheList;
	// }

	private void killAndLock(String pkgName, String className) {
		// show the desktop for killing the app
		Intent i = new Intent(Intent.ACTION_MAIN);
		// 如果是服务里调用，必须加入new task标识
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		i.addCategory(Intent.CATEGORY_HOME);
		startActivity(i);

		// start login activity
		Intent startLoginIntent = new Intent(mContext, PasswordActivity.class);
		Bundle b = new Bundle();
		b.putString("pkgName", pkgName);
		b.putString("clsName", className);
		startLoginIntent.putExtras(b);
		startLoginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		mContext.startActivity(startLoginIntent);

		// kill the app, maybe doesn't work, don't know why...
		Log.d(TAG, "prepare to kill:" + pkgName);
		mActivityManager.killBackgroundProcesses(pkgName);
		// mActivityManager.restartPackage(pkgName);
	}

}
