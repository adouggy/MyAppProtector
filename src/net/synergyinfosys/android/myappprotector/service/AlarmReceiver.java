package net.synergyinfosys.android.myappprotector.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {
	public static final String TAG = "AlarmReceiver";
	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals( LongLiveService.LONGLIVESERVICE_BROADCAST_START_SERVICE)) {  
			Log.i(TAG, "boot service alarm received...");
//            Intent i = new Intent();  
//            i.setClass(context, LongLiveService.class);  
//            context.startService(i);  
        } 
	}

}
