package net.synergyinfosys.android.myappprotector.activity.holder;

import net.synergyinfosys.android.myappprotector.R;
import net.synergyinfosys.android.myappprotector.service.LongLiveService;
import net.synergyinfosys.android.myappprotector.util.MyUtil;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;

public class AppLockViewHolder {
	
	public static final String TAG = "AppLockViewHolder";

	private Activity mRootActivity = null;
	private Context mContext = null;
	
	private Switch sAppLock;
	private Button buttonLockAllInList;
	
	public AppLockViewHolder(Activity act){
		this.mRootActivity = act;
		this.mContext = act.getApplicationContext();

		sAppLock = (Switch) mRootActivity.findViewById(R.id.switch_applock);
		buttonLockAllInList = (Button) mRootActivity.findViewById(R.id.button_applock_lockall);

		OnClickListener appLockListener = new OnClickListener() {
			@Override
			public void onClick(View src) {
				switch (src.getId()) {
				case R.id.button_applock_lockall:
					Intent broadcastIntent = new Intent(LongLiveService.LONGLIVESERVICE_BROADCAST_LOCKALL_ACTION);
					mContext.sendBroadcast(broadcastIntent);
					break;
				}
			}
		};
		
		sAppLock.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if( isChecked ){
					Log.i(TAG, "onClick: starting service");
					mContext.startService(new Intent(mContext, LongLiveService.class));
				}else{
					Log.i(TAG, "onClick: stopping service");
					mContext.stopService(new Intent(mContext, LongLiveService.class));
				}
			}
			
		});
		
		buttonLockAllInList.setOnClickListener(appLockListener);
		
		if( MyUtil.isServiceRunning(this.mContext, LongLiveService.class.getName()) ){
			this.sAppLock.setChecked(true);
		}else{
			this.sAppLock.setChecked(false);
		}
	}
}
