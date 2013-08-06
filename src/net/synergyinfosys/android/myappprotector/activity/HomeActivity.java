package net.synergyinfosys.android.myappprotector.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.synergyinfosys.android.myappprotector.LockList;
import net.synergyinfosys.android.myappprotector.R;
import net.synergyinfosys.android.myappprotector.activity.holder.AppGridViewHolder;
import net.synergyinfosys.android.myappprotector.activity.holder.AppLockViewHolder;
import net.synergyinfosys.android.myappprotector.activity.holder.NetWatcherViewHolder;
import net.synergyinfosys.android.myappprotector.bean.RunningAppInfo;
import net.synergyinfosys.android.myappprotector.service.BootReceiver;
import net.synergyinfosys.android.myappprotector.service.LongLiveService;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

public class HomeActivity extends Activity implements OnClickListener {
	public static final String TAG = "HomeActivity";

	private Context mContext = null;

	private ArrayList<View> mViewList = null;

	private ViewPager mViewPager;

	private HashMap<String, Boolean> lockList = null;
	
	//for dock
	private ResolveInfo mDialApp, mSMSApp, mEmailApp, mWebApp;
	private ImageView imgForDial = null, imgForSMS = null, imgForEmail = null, imgForWeb = null;
	AppGridViewHolder mAppListHolder = null;
	
	//for net watcher
	NetWatcherViewHolder mNetWatcherHolder = null;

	// for app lock control
	AppLockViewHolder mAppLockHolder = null;
	
	ArrayList<RunningAppInfo> mLockList = null;
	
	private final BroadcastReceiver lockAllReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context ctx, Intent intent) {
			Log.i(TAG, "Prepare to lock the selected");
			mLockList = intent.getParcelableArrayListExtra("lockList");
			mAppListHolder.hideApps(mLockList);
		}
	};

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		Log.i(TAG, ">>>>OnNewIntent");
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);

		mContext = getApplicationContext();

		lockList = new HashMap<String, Boolean>();
		lockList.put("net.synergyinfosys.xmppclient_test_12", true);
		lockList.put("net.synergyinfosys.xmppclient_test_13", true);

		LockList.INSTANCE.setContext(mContext);
		LockList.INSTANCE.saveList(lockList);
		
		loadDockApp();
		
		imgForDial = (ImageView) findViewById(R.id.imageView_forDial);
		imgForDial.setOnClickListener(this);
		if (mDialApp != null) {
			imgForDial.setBackground(mDialApp.activityInfo.loadIcon(getPackageManager()));
			imgForDial.invalidate();
		}

		imgForSMS = (ImageView) findViewById(R.id.imageView_forSMS);
		imgForSMS.setOnClickListener(this);
		if (mSMSApp != null) {
			imgForSMS.setBackground(mSMSApp.activityInfo.loadIcon(getPackageManager()));
			imgForSMS.invalidate();
		}
		
		imgForEmail = (ImageView) findViewById( R.id.imageView_forEmail );
		imgForEmail.setOnClickListener(this);
		if( mEmailApp != null ){
			imgForEmail.setBackground( mEmailApp.activityInfo.loadIcon(getPackageManager()) );
			imgForEmail.invalidate();
		}
		
		imgForWeb = (ImageView) findViewById( R.id.imageView_forWeb );
		imgForWeb.setOnClickListener(this);
		if( mWebApp != null ){
			imgForWeb.setBackground( mWebApp.activityInfo.loadIcon(getPackageManager()) );
			imgForWeb.invalidate();
		}

		LayoutInflater lf = LayoutInflater.from(this);
		View viewGrid = lf.inflate(R.layout.activity_home_grid, null);
		View viewIntroduction = lf.inflate(R.layout.activity_home_introduction, null);
		View viewNetWatcher = lf.inflate(R.layout.activity_netwatcher_main, null);
		View viewAppLock = lf.inflate(R.layout.activity_applock_control, null);
		mViewList = new ArrayList<View>();
		mViewList.add(viewIntroduction);
		mViewList.add(viewGrid);
		mViewList.add(viewNetWatcher);
		mViewList.add(viewAppLock);

		mViewPager = (ViewPager) findViewById(R.id.myViewPager);
		mViewPager.setAdapter(this.pagerAdapter);
		mViewPager.setCurrentItem(1);


		View rootView = imgForDial.getRootView();
		rootView.setBackground(this.mContext.getResources().getDrawable(R.drawable.synergy));
		
		IntentFilter lockAllFilter = new IntentFilter();
		lockAllFilter.addAction(LongLiveService.LONGLIVESERVICE_BROADCAST_LOCKALL_ACTION);
		registerReceiver(lockAllReceiver, lockAllFilter);
		
		//start the SERVICE!!
		
		startService(new Intent(this, LongLiveService.class));
//		BootReceiver.registerAlarmStart(this.getApplicationContext())
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(lockAllReceiver);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.home, menu);
		return true;
	}

	private void loadDockApp() {
		Intent dialIntent = new Intent(Intent.ACTION_DIAL, null);
		List<ResolveInfo> list = mContext.getPackageManager().queryIntentActivities(dialIntent, 0);
		if (list != null && list.size() > 0) {
			mDialApp = list.get(0);
		}

		Intent smsIntent = new Intent(Intent.ACTION_VIEW);
		smsIntent.setType("vnd.android-dir/mms-sms");
		list = mContext.getPackageManager().queryIntentActivities(smsIntent, 0);
		if (list != null && list.size() > 0) {
			mSMSApp = list.get(0);
		}
		
		Intent emailIntent = new Intent( Intent.ACTION_SENDTO );
		emailIntent.setData(Uri.parse("mailto:"));
		list = mContext.getPackageManager().queryIntentActivities(emailIntent, 0);
		if (list != null && list.size() > 0) {
			mEmailApp = list.get(0);
		}
		
		Intent webIntent = new Intent( Intent.ACTION_WEB_SEARCH );
		list = mContext.getPackageManager().queryIntentActivities(webIntent, 0);
		if (list != null && list.size() > 0) {
			mWebApp = list.get(0);
		}
	}
	
	/**
	 * 这里先偷懒，在安全桌面下后退就是回到桌面本身 Laucher pro后退建貌似也是自己阻塞了，
	 * 会打印一堆奇怪的log <-- seems a problem under api 17
	 */
	@Override
	public void onBackPressed() {
		Intent intent = new Intent();
		ComponentName cn = new ComponentName("net.synergyinfosys.android.myappprotector", "net.synergyinfosys.android.myappprotector.activity.HomeActivity");
		intent.setComponent(cn);
		startActivity(intent);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.imageView_forDial:
			Intent i = new Intent(Intent.ACTION_DIAL);
			startActivity(i);
			break;
		case R.id.imageView_forSMS:
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setType("vnd.android-dir/mms-sms");
			startActivity(intent);
			break;
		case R.id.imageView_forEmail:
			Intent emailIntent = new Intent( Intent.ACTION_SENDTO );
			emailIntent.setData(Uri.parse("mailto:"));
			startActivity( emailIntent );
			break;
			
		case R.id.imageView_forWeb:
			Intent webIntent = new Intent( Intent.ACTION_WEB_SEARCH );
			startActivity( webIntent );
			break;
		}
	}

	private PagerAdapter pagerAdapter = new PagerAdapter() {
		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public int getCount() {
			return mViewList.size();
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView(mViewList.get(position));
		}

		@Override
		public int getItemPosition(Object object) {
			return super.getItemPosition(object);
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return /* titleList.get(position) */"blah";
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			container.addView(mViewList.get(position));
			switch (position) {
			case 1:
				Log.i(TAG, "initial app list view..");
				if( mAppListHolder == null )
					mAppListHolder = new AppGridViewHolder(HomeActivity.this);
				break;
			case 2:
				Log.i(TAG, "initial net watcher view..");
				if( mNetWatcherHolder == null )
					mNetWatcherHolder = new NetWatcherViewHolder(HomeActivity.this);
				break;
			case 3:
				Log.i(TAG, "initial app lock view..");
				if( mAppLockHolder == null )
					mAppLockHolder = new AppLockViewHolder(HomeActivity.this);
				break;
			}

			return mViewList.get(position);
		}
	};

}
