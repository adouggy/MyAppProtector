package net.synergyinfosys.android.myappprotector.activity.holder;

import java.util.ArrayList;

import net.synergyinfosys.android.myappprotector.R;
import net.synergyinfosys.android.myappprotector.bean.RunningAppInfo;
import net.synergyinfosys.android.myappprotector.service.WatcherService;
import net.synergyinfosys.android.myappprotector.util.MyUtil;
import net.synergyinfosys.android.netwatcher.receiver.NetThroughputReceiver;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

public class NetWatcherViewHolder {
	
	public static final String TAG = "NetWatcherViewHolder";

	private Activity mRootActivity;
	private Context mContext;
	
	private Switch sNetWatcher;
	private ListView mList;
	private ArrayList<RunningAppInfo> mInfoList = new ArrayList<RunningAppInfo>();
	
	public void setNetInfo(ArrayList<RunningAppInfo> info){
		this.mInfoList = info;
		if( this.mList != null ){
			Log.d(TAG, "refreshing list");
			((NetWatcherAdapter)this.mList.getAdapter()).notifyDataSetChanged();
		}
	}
	
	public NetWatcherViewHolder( Activity act) {
		this.mRootActivity = act;
		this.mContext = act.getApplicationContext();
		
		sNetWatcher = (Switch) mRootActivity.findViewById(R.id.switch_netwatcher);
		mList = (ListView) mRootActivity.findViewById(R.id.listView_net_status);
		mList.setAdapter( new  NetWatcherAdapter());
		
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
		BroadcastReceiver throughtputReciever = new NetThroughputReceiver(this);
		mContext.registerReceiver(throughtputReciever, throughputFilter);
		
		if( MyUtil.isServiceRunning(this.mContext, WatcherService.class.getName()) ){
			this.sNetWatcher.setChecked(true);
		}else{
			this.sNetWatcher.setChecked(false);
		}
	}
	
	class NetWatcherAdapter extends BaseAdapter {
		private class ItemHolder {
			TextView appName;
			TextView download;
			TextView upload;
		}

		private LayoutInflater mInflater;

		public NetWatcherAdapter() {
			super();
			mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			return mInfoList.size();
		}

		@Override
		public Object getItem(int idx) {
			return mInfoList.get(idx);
		}

		@Override
		public long getItemId(int idx) {
			return idx;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ItemHolder holder = null;

			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.activity_netwatcher_list_item, null);
				holder = new ItemHolder();
				holder.appName = (TextView) convertView.findViewById(R.id.netwatcher_label);
				holder.download = (TextView) convertView.findViewById( R.id.netwatcher_download_status );
				holder.upload = (TextView) convertView.findViewById( R.id.netwatcher_upload_status);
				convertView.setTag(holder);
			} else {
				holder = (ItemHolder) convertView.getTag();
			}

			RunningAppInfo info = mInfoList.get(position);
			
			holder.appName.setText(info.getAppLabel());
			holder.download.setText(String.valueOf( info.getRxkb() ) + "kb");
			holder.upload.setText(String.valueOf( info.getTxkb() ) + "kb");
			
			return convertView;
		}
	}
}
