package net.synergyinfosys.android.myappprotector.activity.holder;

import net.synergyinfosys.android.myappprotector.R;
import net.synergyinfosys.android.myappprotector.util.MyUtil;
import net.synergyinfosys.android.netwatcher.WatcherService;
import net.synergyinfosys.android.netwatcher.receiver.NetThroughputReceiver;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class NetWatcherViewHolder {
	
	public static final String TAG = "NetWatcherViewHolder";

	private Activity mRootActivity;
	private Context mContext;
	
	private Switch sNetWatcher;
	private static EditText txtStatus;
	

	public NetWatcherViewHolder( Activity act) {
		this.mRootActivity = act;
		this.mContext = act.getApplicationContext();
		
		sNetWatcher = (Switch) mRootActivity.findViewById(R.id.switch_netwatcher);
		txtStatus = (EditText) mRootActivity.findViewById(R.id.editText1);
		
		sNetWatcher.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if( isChecked ){
					Log.d(TAG, "\tstart netwatcher clicked.");
					mContext.startService(new Intent(mContext, WatcherService.class));
				}else{
					Log.d(TAG, "\tstop netwatcher clicked.");
					mContext.stopService(new Intent(mContext, WatcherService.class));
				}
			}
		});

		IntentFilter throughputFilter = new IntentFilter(WatcherService.INTENT_ACTION);
		BroadcastReceiver throughtputReciever = new NetThroughputReceiver(txtStatus);
		mContext.registerReceiver(throughtputReciever, throughputFilter);
		
		if( MyUtil.isServiceRunning(this.mContext, WatcherService.class.getName()) ){
			this.sNetWatcher.setChecked(true);
		}else{
			this.sNetWatcher.setChecked(false);
		}
	}
}
