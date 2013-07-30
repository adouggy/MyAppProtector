package net.synergyinfosys.android.netwatcher.receiver;

import java.util.ArrayList;

import net.synergyinfosys.android.myappprotector.activity.holder.NetWatcherViewHolder;
import net.synergyinfosys.android.myappprotector.bean.RunningAppInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class NetThroughputReceiver extends BroadcastReceiver{
	public static final String TAG = "NetThroughputReceiver";
	
	NetWatcherViewHolder mNetWatcher = null;
	
	public NetThroughputReceiver(NetWatcherViewHolder netWatcher){
		this.mNetWatcher = netWatcher;
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d( TAG, "on receiving...");
		ArrayList<RunningAppInfo> list = intent.getParcelableArrayListExtra("status");
		mNetWatcher.setNetInfo(list);
	}

}
