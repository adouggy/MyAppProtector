package net.synergyinfosys.android.myappprotector.activity;

import java.util.List;

import net.synergyinfosys.android.myappprotector.R;
import net.synergyinfosys.android.myappprotector.util.MyUtil;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class SwitchHomeActivity extends Activity implements OnClickListener, OnItemClickListener {

	public static final String TAG = "MyHomeSwitcher.MainActivity";

	Button mBtnClearDefault;
	ListView mListView;

	ActivityManager mActivityManager = null;
	PackageManager mPackageManager = null;
	List<ResolveInfo> mLauncherList = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_switchhome);
		
		mActivityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		mPackageManager = getPackageManager();
		mLauncherList = MyUtil.getAllLauncher(this.getApplicationContext());

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
			MyUtil.clearDefaultLauncer(this.getApplicationContext());
			break;
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
			String extraStr = "";
			if( MyUtil.isLauncherDefault(getApplicationContext(), pkgName)){
				extraStr = "(默认)";
			}
			holder.appName.setText(appName + extraStr);

			holder.switchButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
//					Log.i(TAG, appName);
//					Log.i(TAG, pkgName);
//					Log.i(TAG, clsName);
//					Log.i(TAG, ">>>>>>>>>>");
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
