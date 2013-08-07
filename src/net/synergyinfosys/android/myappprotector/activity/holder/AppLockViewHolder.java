package net.synergyinfosys.android.myappprotector.activity.holder;

import java.util.ArrayList;

import net.synergyinfosys.android.myappprotector.R;
import net.synergyinfosys.android.myappprotector.bean.RunningAppInfo;
import net.synergyinfosys.android.myappprotector.service.LongLiveService;
import net.synergyinfosys.android.myappprotector.service.WatcherService;
import net.synergyinfosys.android.myappprotector.util.MyUtil;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

public class AppLockViewHolder {
	public static final String TAG = "AppLockViewHolder";

	private Activity mRootActivity = null;
	private Context mContext = null;

	private Switch sAppLock;
	private Switch sAppLockAll;
	private Switch sNetWatcher;
	
	private ArrayList<RunningAppInfo> mApps = null;

	private ListView mListView = null;

	private OnCheckedChangeListener lockListener = new OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			if (isChecked) {
				Log.i(TAG, "onClick: starting service");
				mContext.startService(new Intent(mContext, LongLiveService.class));
			} else {
				Log.i(TAG, "onClick: stopping service");
				mContext.stopService(new Intent(mContext, LongLiveService.class));
			}
		}
	};

	private OnCheckedChangeListener lockAllListener = new OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			if (isChecked) {
				// prepare to lock all in list..
				// stay what it is..
			} else {
				// uncheck all
				for( RunningAppInfo info: mApps ){
					if( !info.isLauncher() )
						info.setLocked(false);
				}
				if( mListView != null ){
					((AppListAdapter)mListView.getAdapter()).notifyDataSetChanged();
				}
			}
			
			sendLockBroadcast();
			
		}
	};

	public AppLockViewHolder(Activity act) {
		this.mRootActivity = act;
		this.mContext = act.getApplicationContext();

		sAppLock = (Switch) mRootActivity.findViewById(R.id.switch_applock);
		sAppLockAll = (Switch) mRootActivity.findViewById(R.id.applock_lockall);

		sAppLockAll.setOnCheckedChangeListener(lockAllListener);
		sAppLock.setOnCheckedChangeListener(lockListener);

		this.mApps = MyUtil.loadApps(this.mContext);
		
		if (MyUtil.isServiceRunning(this.mContext, LongLiveService.class.getName())) {
			this.sAppLock.setChecked(true);
		} else {
			this.sAppLock.setChecked(false);
		}

		mListView = (ListView) mRootActivity.findViewById(R.id.applock_list);
		mListView.setAdapter(new AppListAdapter());
		
		sNetWatcher = (Switch) mRootActivity.findViewById(R.id.switch_netwatcher);
		
		
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

		if( MyUtil.isServiceRunning(this.mContext, WatcherService.class.getName()) ){
			this.sNetWatcher.setChecked(true);
		}else{
			this.sNetWatcher.setChecked(false);
		}
	}
	
	private void sendLockBroadcast(){
		Intent broadcastIntent = new Intent(LongLiveService.LONGLIVESERVICE_BROADCAST_LOCKALL_ACTION);
		broadcastIntent.putParcelableArrayListExtra("lockList", mApps);
		mContext.sendBroadcast(broadcastIntent);
	}

	class AppListAdapter extends BaseAdapter {
		private class ItemHolder {
			ImageView appIcon;
			TextView appName;
			CheckBox appCheckBox;
		}

		private LayoutInflater mInflater;

		public AppListAdapter() {
			super();
			mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			return mApps.size();
		}

		@Override
		public Object getItem(int idx) {
			return mApps.get(idx);
		}

		@Override
		public long getItemId(int idx) {
			return idx;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			ItemHolder holder = null;

			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.activity_applock_list_item, null);
				holder = new ItemHolder();
				holder.appName = (TextView) convertView.findViewById(R.id.applock_list_label);
				holder.appIcon = (ImageView) convertView.findViewById(R.id.applock_list_img);
				holder.appCheckBox = (CheckBox) convertView.findViewById(R.id.applock_list_checkbox);
				convertView.setTag(holder);
			} else {
				holder = (ItemHolder) convertView.getTag();
			}

			RunningAppInfo info = mApps.get(position);
			holder.appCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener(){
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					Log.i(TAG, position + " changed to " + isChecked);
					RunningAppInfo info = mApps.get(position);
					if( !info.isLauncher() )
						info.setLocked(isChecked);
					mApps.set(position, info);
					notifyDataSetChanged();
					sendLockBroadcast();
					
					if( info.isLocked() ){
						sAppLockAll.setChecked(true);
					}
				}
			});
			
			String extraStr = "";
			if( info.isLauncher() ){
				extraStr = "(桌面程序)";
			}
			holder.appName.setText( info.getAppLabel() + extraStr );
//			holder.appIcon.setBackground( info.getAppIcon() );
			MyUtil.setBackground( holder.appIcon, info.getAppIcon());
			holder.appCheckBox.setChecked( info.isLocked() );
			return convertView;
		}
	}
}
