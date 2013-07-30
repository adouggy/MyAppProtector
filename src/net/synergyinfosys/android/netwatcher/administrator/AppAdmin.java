package net.synergyinfosys.android.netwatcher.administrator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.synergyinfosys.android.myappprotector.bean.RunningAppInfo;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

public class AppAdmin {
	public static final String TAG = "AppAdmin";
	
	private PackageManager mPM = null;
	private Context mContext = null;

	public AppAdmin(Context ctx) {
		this.mContext = ctx;
		this.mPM = mContext.getPackageManager();
	}

	// 查询所有正在运行的应用程序信息, 包括他们所在的进程id和进程名
	// 这儿我直接获取了系统里安装的所有应用程序，然后根据包名pkgname过滤获取所有真正运行的应用程序
	public ArrayList<RunningAppInfo> queryAllRunningAppInfo() {
		// 查询所有已经安装的应用程序
		List<ApplicationInfo> listAppcations = mPM.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
		Collections.sort(listAppcations, new ApplicationInfo.DisplayNameComparator(mPM));// 排序

		// 保存所有正在运行的包名 以及它所在的进程信息
		Map<String, ActivityManager.RunningAppProcessInfo> pgkProcessAppMap = new HashMap<String, ActivityManager.RunningAppProcessInfo>();

		ActivityManager mActivityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
		// 通过调用ActivityManager的getRunningAppProcesses()方法获得系统里所有正在运行的进程
		List<ActivityManager.RunningAppProcessInfo> appProcessList = mActivityManager.getRunningAppProcesses();

		for (ActivityManager.RunningAppProcessInfo appProcess : appProcessList) {
			int pid = appProcess.pid; // pid
			String processName = appProcess.processName; // 进程名
//			Log.i(TAG, "processName: " + processName + "  pid: " + pid);

			String[] pkgNameList = appProcess.pkgList; // 获得运行在该进程里的所有应用程序包

			// 输出所有应用程序的包名
			for (int i = 0; i < pkgNameList.length; i++) {
				String pkgName = pkgNameList[i];
//				Log.i(TAG, "packageName " + pkgName + " at index " + i + " in process " + pid);
				// 加入至map对象里
				pgkProcessAppMap.put(pkgName, appProcess);
			}
		}
		// 保存所有正在运行的应用程序信息
		ArrayList<RunningAppInfo> runningAppInfos = new ArrayList<RunningAppInfo>(); // 保存过滤查到的AppInfo

		for (ApplicationInfo app : listAppcations) {
			// 如果该包名存在 则构造一个RunningAppInfo对象
			if (pgkProcessAppMap.containsKey(app.packageName)) {
				// 获得该packageName的 pid 和 processName
				int pid = pgkProcessAppMap.get(app.packageName).pid;
				String processName = pgkProcessAppMap.get(app.packageName).processName;
				runningAppInfos.add(getAppInfo(app, pid, processName));
			}
		}

		return runningAppInfos;
	}

	// 构造一个RunningAppInfo对象 ，并赋值
	private RunningAppInfo getAppInfo(ApplicationInfo app, int pid, String processName) {
		RunningAppInfo appInfo = new RunningAppInfo();
		appInfo.setAppLabel((String) app.loadLabel(mPM));
		appInfo.setAppIcon(app.loadIcon(mPM));
		appInfo.setPkgName(app.packageName);

		appInfo.setPid(pid);
		appInfo.setProcessName(processName);
		appInfo.setUid( app.uid );

		return appInfo;
	}

}
