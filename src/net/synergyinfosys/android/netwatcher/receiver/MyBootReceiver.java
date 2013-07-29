package net.synergyinfosys.android.netwatcher.receiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

public class MyBootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent i) {
		if (Intent.ACTION_BOOT_COMPLETED.equals(i.getAction())) {
			// 启动完成
			Intent intent = new Intent(context, AlarmReceiver.class);
			intent.setAction( AlarmReceiver.INTENT_ACTION );
			PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
			long firstime = SystemClock.elapsedRealtime();
			AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

			// 10秒一个周期，不停的发送广播
			am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstime, 10 * 1000, sender);
		}
	}

}
