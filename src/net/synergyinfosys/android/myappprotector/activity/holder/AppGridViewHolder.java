package net.synergyinfosys.android.myappprotector.activity.holder;

import java.util.List;

import net.synergyinfosys.android.myappprotector.R;
import net.synergyinfosys.android.myappprotector.bean.RunningAppInfo;
import net.synergyinfosys.android.myappprotector.util.MyUtil;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class AppGridViewHolder {

	public static final String TAG = "AppListViewHolder";

	private GridView mGrid;

	private Activity mRootActivity = null;
	private Context mContext = null;

	private List<ResolveInfo> mApps;

	private List<RunningAppInfo> mHideList;

	public AppGridViewHolder(Activity act) {
		this.mRootActivity = act;
		this.mContext = mRootActivity.getApplicationContext();

		loadApps();

		mGrid = (GridView) mRootActivity.findViewById(R.id.app_list);
		mGrid.setAdapter(new AppAdapter());
		mGrid.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				ResolveInfo info = mApps.get(position);

				String pkg = info.activityInfo.packageName;
				String cls = info.activityInfo.name;

				if (!isLocked(pkg)) {
					Toast.makeText(mContext, "未被保护", Toast.LENGTH_SHORT).show();
				} else {
					// 启动
					ComponentName component = new ComponentName(pkg, cls);
					Intent i = new Intent();
					i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					i.setComponent(component);
					mContext.startActivity(i);
				}
			}

		});
	}

	private void loadApps() {
		Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
		mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		mApps = mContext.getPackageManager().queryIntentActivities(mainIntent, 0);
		// Log.i(TAG, "Got apps:");
		// for (ResolveInfo r : mApps) {
		// Log.i(TAG, r.activityInfo.name);
		// }
	}

	public void hideApps(List<RunningAppInfo> list) {
		if (mApps == null || list == null)
			return;

		this.mHideList = list;
		((AppAdapter)this.mGrid.getAdapter()).notifyDataSetChanged();
	}

	private boolean isLocked(String pkgName) {
		for (RunningAppInfo info : mHideList) {
			if (info.isLocked() && info.getPkgName().compareTo(pkgName) == 0) {
				return true;
			}
		}

		return false;
	}

	class AppAdapter extends BaseAdapter {
		private PackageManager mPM = null;

		private class GridHolder {
			ImageView appImage;
			TextView appName;
		}

		private LayoutInflater mInflater;

		public AppAdapter() {
			super();
			mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			mPM = mContext.getPackageManager();
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
				convertView = mInflater.inflate(R.layout.activity_home_grid_item, null);
				holder = new GridHolder();
				holder.appImage = (ImageView) convertView.findViewById(R.id.ItemImage);
				holder.appName = (TextView) convertView.findViewById(R.id.ItemText);
				convertView.setTag(holder);
			} else {
				holder = (GridHolder) convertView.getTag();
			}

			ResolveInfo info = mApps.get(position);
			String pkgName = info.activityInfo.packageName;

			Drawable d = info.activityInfo.loadIcon(mPM);
//			holder.appImage.setBackground(d);
			MyUtil.setBackground(holder.appImage, d);
			if ( mHideList != null && !isLocked(pkgName)) {
//				d.setAlpha(50);
			}

			holder.appName.setText(info.activityInfo.loadLabel(mPM));
			return convertView;
		}
	}
}
