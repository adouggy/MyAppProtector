package net.synergyinfosys.android.netwatcher.receiver;

import net.synergyinfosys.android.myappprotector.service.WatcherService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReceiver extends BroadcastReceiver {
	public static final String INTENT_ACTION = "net.synergyinfosys.android.netwatcher.alarmreceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(INTENT_ACTION)) {  
            Intent i = new Intent();  
            i.setClass(context, WatcherService.class);  
            // 启动service   
            // 多次调用startService并不会启动多个service 而是会多次调用onStart  
            context.startService(i);  
        }  
	}

}
