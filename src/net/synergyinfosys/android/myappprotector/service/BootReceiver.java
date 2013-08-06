package net.synergyinfosys.android.myappprotector.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {
	
	public static final String TAG = "BootReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
			Log.i( TAG, "Boot receiving...prepare to start alarm..");
			registerAlarmStart(context);
		}
	}
	
	public static void registerAlarmStart(Context context){
		// 启动完成
		Intent i = new Intent(context, AlarmReceiver.class);
		i.setAction( LongLiveService.LONGLIVESERVICE_BROADCAST_START_SERVICE );
		PendingIntent sender = PendingIntent.getBroadcast(context, 0, i, 0);
		
		// long firstime = SystemClock.elapsedRealtime();
		AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

		am.setRepeating(AlarmManager.RTC_WAKEUP/*.ELAPSED_REALTIME_WAKEUP*/, System.currentTimeMillis(), 5 * 1000, sender);
	}

}
