package net.synergyinfosys.android.myappprotector.util;

import java.util.ArrayList;
import java.util.List;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

public class MyUtil {
	/**
	 * 用来判断服务是否运行.
	 * 
	 * @param context
	 * @param className
	 *            判断的服务名字
	 * @return true 在运行 false 不在运行
	 */
	public static boolean isServiceRunning(Context mContext, String className) {
		boolean isRunning = false;
		ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningServiceInfo> serviceList = activityManager.getRunningServices(30);
		if (!(serviceList.size() > 0)) {
			return false;
		}
		for (int i = 0; i < serviceList.size(); i++) {
			if (serviceList.get(i).service.getClassName().equals(className) == true) {
				isRunning = true;
				break;
			}
		}
		return isRunning;
	}
	
	/**
	 * 
	 * get running launcher package name
	 * 
	 * @param context
	 * @return
	 */
	public static String whichLauncherIsRunning(Context context) {
		final Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		final ResolveInfo res = context.getPackageManager().resolveActivity(intent, 0);
		if (res.activityInfo == null) {
			// should not happen. A home is always installed, isn't it?
			return "N/A";
		}
		if (res.activityInfo.packageName.equals("android")) {
			// No default selected
			return "No default";
		} else {
			// res.activityInfo.packageName and res.activityInfo.name gives you
			// the default app
			String pkgName = res.activityInfo.packageName;
//			String actName = res.activityInfo.name;
			return pkgName;
		}
	}
	
	/**
	 * find if the launcher is the default
	 * 
	 * @param context
	 * @param pkgName
	 * @return
	 */
	public static boolean isLauncherDefault(Context context, String pkgName) {
		if( pkgName == null ){
			return false;
		}
		
		final PackageManager packageManager = context.getPackageManager();
		final IntentFilter filter = new IntentFilter(Intent.ACTION_MAIN);
		filter.addCategory(Intent.CATEGORY_HOME);
		List<IntentFilter> filters = new ArrayList<IntentFilter>();
		filters.add(filter);

		List<ComponentName> activities = new ArrayList<ComponentName>();

		// You can use name of your package here as third argument
		packageManager.getPreferredActivities(filters, activities, null);

		for (ComponentName activity : activities) {
			if (pkgName.equals(activity.getPackageName())) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * per sdk
	 * 
	 * @param img
	 * @param d
	 */
	@SuppressWarnings("deprecation")
	public static void setBackground(View img, Drawable d){
	    int sysVersion = android.os.Build.VERSION.SDK_INT;
	    if( img instanceof ImageView ){
	    	((ImageView) img).setImageDrawable(d);
	    }else{
	    	 if( sysVersion <= 16){
	 	    	img.setBackgroundDrawable(d);
	 	    	
	 	    }else{
	 	    	img.setBackground( d );
	 	    }
	    }
	}
}
