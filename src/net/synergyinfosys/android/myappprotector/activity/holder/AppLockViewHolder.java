package net.synergyinfosys.android.myappprotector.activity.holder;

import java.util.ArrayList;
import java.util.List;

import net.synergyinfosys.android.myappprotector.R;
import net.synergyinfosys.android.myappprotector.bean.RunningAppInfo;
import net.synergyinfosys.android.myappprotector.service.LongLiveService;
import net.synergyinfosys.android.myappprotector.util.MyUtil;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
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
			Intent broadcastIntent = new Intent(LongLiveService.LONGLIVESERVICE_BROADCAST_LOCKALL_ACTION);
			if (isChecked) {
				// prepare to lock all in list..
				// stay what it is..
			} else {
				// uncheck all
				for( RunningAppInfo info: mApps ){
					info.setLocked(false);
				}
			}
			broadcastIntent.putParcelableArrayListExtra("lockList", mApps);
			mContext.sendBroadcast(broadcastIntent);
			
			if( mListView != null ){
				((AppListAdapter)mListView.getAdapter()).notifyDataSetChanged();
			}
		}
	};

	public AppLockViewHolder(Activity act) {
		this.mRootActivity = act;
		this.mContext = act.getApplicationContext();

		sAppLock = (Switch) mRootActivity.findViewById(R.id.switch_applock);
		sAppLockAll = (Switch) mRootActivity.findViewById(R.id.applock_lockall);

		sAppLockAll.setOnCheckedChangeListener(lockAllListener);
		sAppLock.setOnCheckedChangeListener(lockListener);

		if (MyUtil.isServiceRunning(this.mContext, LongLiveService.class.getName())) {
			this.sAppLock.setChecked(true);
		} else {
			this.sAppLock.setChecked(false);
		}

		loadApps();
		mListView = (ListView) mRootActivity.findViewById(R.id.applock_list);
		mListView.setAdapter(new AppListAdapter());
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
					info.setLocked(isChecked);
					mApps.set(position, info);
					notifyDataSetChanged();
				}
			});

			holder.appName.setText( info.getAppLabel() );
			holder.appIcon.setBackground( info.getAppIcon() );
			holder.appCheckBox.setChecked( info.isLocked() );
			return convertView;
		}
	}

	private void loadApps() {
		Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
		mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		List<ResolveInfo> list = mContext.getPackageManager().queryIntentActivities(mainIntent, 0);
		mApps = new ArrayList<RunningAppInfo>(); 
		for( ResolveInfo info : list ){
			RunningAppInfo a = new RunningAppInfo();
			a.setAppIcon( info.activityInfo.loadIcon(mContext.getPackageManager()) );
			a.setAppLabel( info.activityInfo.loadLabel(mContext.getPackageManager()).toString() );
			a.setLocked( false );
			a.setPkgName(info.activityInfo.packageName);
			mApps.add(a);
		}
	}
}
