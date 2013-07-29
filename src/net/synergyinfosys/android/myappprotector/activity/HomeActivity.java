package net.synergyinfosys.android.myappprotector.activity;

import java.util.HashMap;
import java.util.List;

import net.synergyinfosys.android.myappprotector.LockList;
import net.synergyinfosys.android.myappprotector.R;
import net.synergyinfosys.android.myappprotector.service.LongLiveService;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class HomeActivity extends Activity implements OnItemClickListener, OnClickListener {
	public static final String TAG = "HomeActivity";

	private List<ResolveInfo> mApps;
	
	private ResolveInfo mDialApp, mSMSApp;

	private GridView mGrid;

	private Context mContext = null;

	HashMap<String, Boolean> lockList = null;
	ImageView imgForDial = null, imgForSMS = null;

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		Log.i(TAG, ">>>>OnNewIntent");
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mContext = getApplicationContext();

		lockList = new HashMap<String, Boolean>();
		lockList.put("net.synergyinfosys.xmppclient_test_12", true);
		lockList.put("net.synergyinfosys.xmppclient_test_13", true);

		LockList.INSTANCE.setContext(mContext);
		LockList.INSTANCE.saveList(lockList);

		loadApps();
		loadDockApp();

		setContentView(R.layout.activity_home);
		mGrid = (GridView) findViewById(R.id.app_list);
		mGrid.setAdapter(new AppAdapter());
		mGrid.setOnItemClickListener(this);
		
		imgForDial = (ImageView) findViewById(R.id.imageView_forDial);
		imgForDial.setOnClickListener(this);
		if( mDialApp != null ){
			imgForDial.setBackground( mDialApp.activityInfo.loadIcon(getPackageManager()) );
			imgForDial.invalidate();
		}
		
		imgForSMS = (ImageView) findViewById(R.id.imageView_forSMS);
		imgForSMS.setOnClickListener(this);
		if( mSMSApp != null ){
			imgForSMS.setBackground( mSMSApp.activityInfo.loadIcon(getPackageManager()) );
			imgForSMS.invalidate();
		}
		
		startService(new Intent(this, LongLiveService.class));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.home, menu);
		return true;
	}

	private void loadApps() {
		Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
		mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		mApps = getPackageManager().queryIntentActivities(mainIntent, 0);
		Log.i(TAG, "Got apps:");
		for (ResolveInfo r : mApps) {
			Log.i(TAG, r.activityInfo.name);
		}
	}
	
	private void loadDockApp(){
		Intent dialIntent = new Intent( Intent.ACTION_DIAL, null );
		List<ResolveInfo> list = getPackageManager().queryIntentActivities(dialIntent, 0);
		if( list != null && list.size()>0 ){
			mDialApp = list.get(0);
		}
		
		Intent smsIntent = new Intent(Intent.ACTION_VIEW);
		smsIntent.setType("vnd.android-dir/mms-sms");
		list = getPackageManager().queryIntentActivities(smsIntent, 0);
		if( list != null && list.size()>0 ){
			mSMSApp = list.get(0);
		}
	}

	/**
	 * 这里先偷懒，在安全桌面下后退就是回到桌面本身 Laucher pro后退建貌似也是自己阻塞了，会打印一堆奇怪的log
	 */
	@Override
	public void onBackPressed() {
		Intent intent = new Intent();
		ComponentName cn = new ComponentName("net.synergyinfosys.android.myappprotector", "net.synergyinfosys.android.myappprotector.HomeActivity");
		intent.setComponent(cn);
		startActivity(intent);
	}

	public class AppAdapter extends BaseAdapter {

		private class GridHolder {
			ImageView appImage;
			TextView appName;
		}

		private LayoutInflater mInflater;

		public AppAdapter() {
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
		public View getView(int position, View convertView, ViewGroup parent) {
			GridHolder holder;

			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.grid_item, null);
				holder = new GridHolder();
				holder.appImage = (ImageView) convertView.findViewById(R.id.ItemImage);
				holder.appName = (TextView) convertView.findViewById(R.id.ItemText);
				convertView.setTag(holder);
			} else {
				holder = (GridHolder) convertView.getTag();
			}

			ResolveInfo info = mApps.get(position);
			String pkgName = info.activityInfo.packageName;

			if (!isLocked(pkgName)) {
				Drawable d = info.activityInfo.loadIcon(getPackageManager());
				d.setAlpha(50);
				holder.appImage.setBackground(d);
			} else {
				Drawable d = info.activityInfo.loadIcon(getPackageManager());
				holder.appImage.setBackground(d);
			}

			holder.appName.setText(info.activityInfo.loadLabel(getPackageManager()));
			return convertView;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		ResolveInfo info = mApps.get(position);

		String pkg = info.activityInfo.packageName;
		String cls = info.activityInfo.name;

		if (!isLocked(pkg)) {
			Toast.makeText(mContext, "Already locked.", Toast.LENGTH_SHORT).show();
		} else {
			// 先给监听服务发个广播，说我启动的是安全的
			Intent broadcastIntent = new Intent(LongLiveService.LONGLIVESERVICE_BROADCAST_START_SAFE);
			Bundle b = new Bundle();
			b.putString("safePkgName", pkg);
			b.putString("safeClsName", cls);
			broadcastIntent.putExtras(b);
			mContext.sendBroadcast(broadcastIntent);

			// 再启动
			ComponentName component = new ComponentName(pkg, cls);
			Intent i = new Intent();
			i.setComponent(component);
			startActivity(i);
		}
	}

	private boolean isLocked(String pkgName) {
		return true;

		// Iterator<String> iter = lockList.keySet().iterator();
		// while( iter.hasNext() ){
		// String key = iter.next();
		// if( key.compareTo(pkgName) == 0 ){
		// return true;
		// }
		// }
		//
		// return false;
	}

	@Override
	public void onClick(View v) {
		switch( v.getId() ){
		case R.id.imageView_forDial:
			Intent i = new Intent( Intent.ACTION_DIAL );
			startActivity( i );
			break;
		case R.id.imageView_forSMS:
		    Intent intent = new Intent(Intent.ACTION_VIEW);  
            intent.setType("vnd.android-dir/mms-sms");
            startActivity(intent);  
			break;
		}
	}

}
