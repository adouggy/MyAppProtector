package net.synergyinfosys.android.netwatcher.administrator;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.net.TrafficStats;
import android.util.Log;

public class TrafficAdmin {

	public static final String TAG = "TrafficAdmin";

	public TrafficAdmin() {

	}

	public long getTotalRxBytes() { // 获取总的接受字节数，包含Mobile和WiFi等
		return TrafficStats.getTotalRxBytes() == TrafficStats.UNSUPPORTED ? 0 : (TrafficStats.getTotalRxBytes() / 1024);
	}

	public long getTotalTxBytes() { // 总的发送字节数，包含Mobile和WiFi等
		return TrafficStats.getTotalTxBytes() == TrafficStats.UNSUPPORTED ? 0 : (TrafficStats.getTotalTxBytes() / 1024);
	}

	public long getMobileRxBytes() { // 获取通过Mobile连接收到的字节总数，不包含WiFi
		return TrafficStats.getMobileRxBytes() == TrafficStats.UNSUPPORTED ? 0 : (TrafficStats.getMobileRxBytes() / 1024);
	}

	public long getMobileRxBytes(int uid) {
		return TrafficStats.getMobileRxBytes() == TrafficStats.UNSUPPORTED ? 0 : (TrafficStats.getUidRxBytes(uid) / 1024);
	}

	public long getMobileTxBytes(int uid) {
		return TrafficStats.getMobileRxBytes() == TrafficStats.UNSUPPORTED ? 0 : (TrafficStats.getUidTxBytes(uid) / 1024);
	}

	public String getStatus() {
		StringBuilder sb = new StringBuilder();
		sb.append("获取总的接受字节数:" + getTotalRxBytes() + "KB\n");
		sb.append("总的发送字节数:" + getTotalTxBytes() + "KB\n");
		sb.append("3g/2g接受字节数:" + getMobileRxBytes() + "KB\n");
		return sb.toString();
	}

	public String getStatusForRunningApp(AppAdmin appAdmin) {
		StringBuilder sb = new StringBuilder();
		
		List<RunningAppInfo> list = appAdmin.queryAllRunningAppInfo();
		for (RunningAppInfo i : list) {
			long rx = getMobileRxBytes(i.getUid());
			long tx = getMobileTxBytes(i.getUid());
			i.setRxkb( rx );
			i.setTxkb( tx );
		}
		
		Collections.sort( list, new Comparator<RunningAppInfo>(){
			@Override
			public int compare(RunningAppInfo lhs, RunningAppInfo rhs) {
				return -(int)(lhs.getTxkb()-rhs.getTxkb());
			}} 
		);
		
		for( RunningAppInfo i: list ){
			sb.append(i.getAppLabel() + /*"\t\t[p:" + i.getPid() + ",u:" + i.getUid() + "]\t" +*/ "  (down:" + i.getRxkb() + "kb,\tup:" + i.getTxkb() + "kb)");
			sb.append("\n");
		}
		
		return sb.toString();
	}
}
