package net.synergyinfosys.android.myappprotector.bean;

import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

public class RunningAppInfo implements Parcelable {
	private String appLabel;
	private Drawable appIcon;
	private String pkgName;
	private int pid;
	private String processName;
	private int uid;
	private long rxkb;
	private long txkb;
	private boolean isLocked;
	
	public boolean isLocked() {
		return isLocked;
	}

	public void setLocked(boolean isLocked) {
		this.isLocked = isLocked;
	}

	public long getRxkb() {
		return rxkb;
	}

	public void setRxkb(long rxkb) {
		this.rxkb = rxkb;
	}

	public long getTxkb() {
		return txkb;
	}

	public void setTxkb(long txkb) {
		this.txkb = txkb;
	}

	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}

	public RunningAppInfo() {
	}

	public String getAppLabel() {
		return appLabel;
	}

	public void setAppLabel(String appName) {
		this.appLabel = appName;
	}

	public Drawable getAppIcon() {
		return appIcon;
	}

	public void setAppIcon(Drawable appIcon) {
		this.appIcon = appIcon;
	}

	public String getPkgName() {
		return pkgName;
	}

	public void setPkgName(String pkgName) {
		this.pkgName = pkgName;
	}

	public int getPid() {
		return pid;
	}

	public void setPid(int pid) {
		this.pid = pid;
	}

	public String getProcessName() {
		return processName;
	}

	public void setProcessName(String processName) {
		this.processName = processName;
	}

	// to implement parcelable

	public static final Parcelable.Creator<RunningAppInfo> CREATOR = new Parcelable.Creator<RunningAppInfo>() {
		public RunningAppInfo createFromParcel(Parcel in) {
			RunningAppInfo info = new RunningAppInfo();
			info.setAppLabel(in.readString());
			info.setRxkb(in.readLong());
			info.setTxkb(in.readLong());
			info.setPkgName(in.readString());
			info.setLocked(in.readByte() == 1);
			return info;
		}

		public RunningAppInfo[] newArray(int size) {
			return new RunningAppInfo[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.appLabel);
		dest.writeLong(this.rxkb);
		dest.writeLong(this.txkb);
		dest.writeString(this.pkgName);
		dest.writeByte((byte) (isLocked ? 1 : 0));
	}
}
