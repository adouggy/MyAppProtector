package net.synergyinfosys.android.myappprotector.activity;

import java.util.ArrayList;
import java.util.List;

import net.synergyinfosys.android.myappprotector.R;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class SwitchHomeActivity extends Activity implements OnClickListener, OnItemClickListener {

	public static final String TAG = "MyHomeSwitcher.MainActivity";
	public static final String PKG_NAME = "net.synergyinfosys.android.myappprotector";

	Button mBtnClearDefault;
	ListView mListView;

	ActivityManager mActivityManager = null;
	PackageManager mPackageManager = null;
	List<ResolveInfo> mLauncherList = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_switchhome);
		mActivityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		mPackageManager = getPackageManager();
		mLauncherList = getAllLauncher();

		mBtnClearDefault = (Button) findViewById(R.id.button_clear_default);
		mBtnClearDefault.setOnClickListener(this);

		mListView = (ListView) findViewById(R.id.list_launcher);
		mListView.setAdapter(new LaucherListAdapter());
		mListView.setOnItemClickListener(this);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button_clear_default:
			ComponentName localComponentName = new ComponentName(PKG_NAME, FakeHome.class.getName());
			getPackageManager().setComponentEnabledSetting(localComponentName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
			Intent localIntent = new Intent("android.intent.action.MAIN");
			localIntent.addCategory("android.intent.category.HOME");
			startActivity(localIntent);
			getPackageManager().setComponentEnabledSetting(localComponentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
			break;
		}
	}

	private List<ResolveInfo> getAllLauncher() {
		Intent launcher = new Intent();
		launcher.addCategory(Intent.CATEGORY_HOME);
		launcher.setAction(Intent.ACTION_MAIN);

		List<ResolveInfo> list = mPackageManager.queryIntentActivities(launcher, 0);
		// for( ResolveInfo r : list ){
		// System.out.println( r.activityInfo.packageName );
		// System.out.println( r.activityInfo.name );
		// System.out.println();
		// }
		return list;
	}

	boolean isMyLauncherDefault() {
		final IntentFilter filter = new IntentFilter(Intent.ACTION_MAIN);
		filter.addCategory(Intent.CATEGORY_HOME);

		List<IntentFilter> filters = new ArrayList<IntentFilter>();
		filters.add(filter);

		final String myPackageName = getPackageName();
		List<ComponentName> activities = new ArrayList<ComponentName>();
		final PackageManager packageManager = (PackageManager) getPackageManager();

		// You can use name of your package here as third argument
		packageManager.getPreferredActivities(filters, activities, null);

		for (ComponentName activity : activities) {
			if (myPackageName.equals(activity.getPackageName())) {
				return true;
			}
		}
		return false;
	}

	public String whichLauncherIsRunning() {
		final Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		final ResolveInfo res = getPackageManager().resolveActivity(intent, 0);
		if (res.activityInfo == null) {
			// should not happen. A home is always installed, isn't it?
			return "N/A";
		}
		if (res.activityInfo.packageName.equals("android")) {
			// No default selected
			return "No default";
		} else {
			// res.activityInfo.packageName and res.activityInfo.name gives you
			// the default app
			String pkgName = res.activityInfo.packageName;
//			String actName = res.activityInfo.name;
			return pkgName;
		}
	}

	public class LaucherListAdapter extends BaseAdapter {

		private class ItemHolder {
			ImageView appImage;
			TextView appName;
			Button switchButton;
		}

		private LayoutInflater mInflater = null;

		public LaucherListAdapter() {
			super();
			mInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			return mLauncherList.size();
		}

		@Override
		public Object getItem(int position) {
			return mLauncherList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ItemHolder holder;

			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.activity_switchhome_list_item, null);
				holder = new ItemHolder();
				holder.appImage = (ImageView) convertView.findViewById(R.id.ItemImage);
				holder.appName = (TextView) convertView.findViewById(R.id.ItemText);
				holder.switchButton = (Button) convertView.findViewById(R.id.Button_switch);
				convertView.setTag(holder);
			} else {
				holder = (ItemHolder) convertView.getTag();
			}

			ResolveInfo info = mLauncherList.get(position);
			final String pkgName = info.activityInfo.packageName;
			final String clsName = info.activityInfo.name;

			Drawable d = info.activityInfo.loadIcon(getPackageManager());
			holder.appImage.setImageDrawable(d);

			final String appName = info.activityInfo.loadLabel(getPackageManager()).toString();
			holder.appName.setText(appName);

			holder.switchButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Log.i(TAG, appName);
					Log.i(TAG, pkgName);
					Log.i(TAG, clsName);
					Log.i(TAG, ">>>>>>>>>>");
					ComponentName cn = new ComponentName(pkgName, clsName);
					Intent i = new Intent();
					i.setComponent(cn);
					i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(i);
				}
			});
			return convertView;
		}

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// ResolveInfo info = mLauncherList.get(position);
		// String pkg = info.activityInfo.packageName;
		// String cls = info.activityInfo.name;

	}
}
