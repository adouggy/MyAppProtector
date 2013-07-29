package net.synergyinfosys.android.myappprotector.service;

import java.util.Iterator;
import java.util.Map;

import net.synergyinfosys.android.myappprotector.LockList;
import net.synergyinfosys.android.myappprotector.R;
import net.synergyinfosys.android.myappprotector.activity.PasswordActivity;
import net.synergyinfosys.android.myappprotector.activity.SwitchHomeActivity;
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
	public static final String LONGLIVESERVICE_BROADCAST_UNLOCK_ACTION = "LongLiveService.broadcast.unlock";
	public static final String LONGLIVESERVICE_BROADCAST_LOCKALL_ACTION = "LongLiveService.broadcast.lockall";
	public static final String LONGLIVESERVICE_BROADCAST_START_SAFE = "LongLiveService.broadcast.start.safe";

	ActivityManager mActivityManager = null;
	long threadInterval = 1000;
	Context mContext = null;

	Map<String, Boolean> lockList = null; // package list

	private String safePkgName = "", safeClsName = "";

	private final BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context ctx, Intent intent) {
			Bundle bundle = intent.getExtras();
			String pkgName = bundle.getString("unlockPkgName");
			String clsName = bundle.getString("unlockClsName");
			Log.i(TAG, "Prepare to unlock:" + pkgName + "/" + clsName);
			LockList.INSTANCE.setList(pkgName, false);
			lockList = LockList.INSTANCE.loadList();

			Intent startPackageIntent = new Intent(Intent.ACTION_MAIN);
			startPackageIntent.addCategory(Intent.CATEGORY_LAUNCHER);
			ComponentName cn = new ComponentName(pkgName, clsName);
			intent.setComponent(cn);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			ctx.startActivity(intent);
		}
	};

	private final BroadcastReceiver lockAllReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context ctx, Intent intent) {
			Log.i(TAG, "Prepare to lock all");
			lockList = LockList.INSTANCE.loadList();
			Iterator<String> iter = lockList.keySet().iterator();
			while (iter.hasNext()) {
				String key = iter.next();
				lockList.put(key, true);
			}
			LockList.INSTANCE.saveList(lockList);
		}
	};

	private final BroadcastReceiver isStartFromMyLauncherReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context ctx, Intent intent) {
			Log.i(TAG, "isStartFromMyLauncherReceiver");

			Bundle bundle = intent.getExtras();
			safePkgName = bundle.getString("safePkgName");
			safeClsName = bundle.getString("safeClsName");

			Log.i(TAG, ">>>>>>>>>>safe start " + safePkgName + "," + safeClsName);
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
		lockList = LockList.INSTANCE.loadList();

		IntentFilter filter = new IntentFilter();
		filter.addAction(LONGLIVESERVICE_BROADCAST_UNLOCK_ACTION);
		registerReceiver(receiver, filter);

		IntentFilter lockAllFilter = new IntentFilter();
		lockAllFilter.addAction(LONGLIVESERVICE_BROADCAST_LOCKALL_ACTION);
		registerReceiver(lockAllReceiver, lockAllFilter);

		IntentFilter safeStartFilter = new IntentFilter();
		lockAllFilter.addAction(LONGLIVESERVICE_BROADCAST_START_SAFE);
		registerReceiver(isStartFromMyLauncherReceiver, safeStartFilter);
	}

	@Override
	public void onDestroy() {
		Log.i(TAG, "onDestroy");

		unregisterReceiver(receiver);
		unregisterReceiver(lockAllReceiver);
		unregisterReceiver(isStartFromMyLauncherReceiver);
		
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
	
	public void handleStart(){
		Resources r = this.getResources();
		Notification notification = NotificationHelper.genNotification(
											this.mContext, 
											0, 
											R.drawable.ic_launcher, 
											"安全桌面已启动..", 
											0, 
											r.getString(R.string.notification_forground_service_title ), 
											r.getString(R.string.notification_forground_service_text ), 
											SwitchHomeActivity.class,
											Notification.FLAG_FOREGROUND_SERVICE);
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
							Log.d(TAG, "sleeping.." + sleepingTime + " ms.");
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
		// 获取目前最顶层的Activity
		ComponentName cn = mActivityManager.getRunningTasks(1).get(0).topActivity;
		String pkgName = cn.getPackageName();
		String className = cn.getClassName();
		Log.d(TAG, "pkg:" + pkgName);
		Log.d(TAG, "cls:" + className);

		// app换了，说明有新的启动的app了！
		Log.d(TAG, "Running app is " + pkgName);

		boolean isInTheList = false;
		String s = null;
		lockList = LockList.INSTANCE.loadList();
		Iterator<String> iter = lockList.keySet().iterator();
		while (iter.hasNext()) {
			s = iter.next();
			if (s.compareTo(pkgName) == 0) {
				isInTheList = true;
				break;
			}
		}

		// 如果从安全桌面启动，则跳过锁定
		if (pkgName.compareTo(safePkgName) == 0 && className.compareTo(safeClsName) == 0) {
			return;
		}

		// 如果再block list中并且没输入过密码，则尝试锁定。
		if (isInTheList && s != null && lockList.get(s)) {
			// show the desktop for killing the app
			Intent i = new Intent(Intent.ACTION_MAIN);
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // 如果是服务里调用，必须加入new
														// task标识
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
//			mActivityManager.restartPackage(pkgName);
		}
	}

}
