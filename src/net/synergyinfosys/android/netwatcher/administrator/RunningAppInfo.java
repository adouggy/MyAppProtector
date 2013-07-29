package net.synergyinfosys.android.netwatcher.administrator;

import android.graphics.drawable.Drawable;

public class RunningAppInfo {
	private String appLabel;
	private Drawable appIcon;
	private String pkgName;

	private int pid;
	private String processName;
	
	private int uid;
	
	private long rxkb;
	private long txkb;
	
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
}
