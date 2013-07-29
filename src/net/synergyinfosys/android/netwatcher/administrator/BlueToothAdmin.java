package net.synergyinfosys.android.netwatcher.administrator;

import android.bluetooth.BluetoothAdapter;
import android.util.Log;

public class BlueToothAdmin {
	public static final String TAG = "BlueToothAdmin";
	private BluetoothAdapter adapter = null;
	public BlueToothAdmin(){
		adapter = BluetoothAdapter.getDefaultAdapter();
	}
	
	public boolean isEnable(){
		if( adapter == null )
			return false;
		
		if( adapter.isEnabled() )
			return true;
		
		return false;
	}
	
	public void makeClose(){
		if( adapter != null ){
			Log.d( TAG, "make bluetooth close.");
			adapter.disable();
		}
	}
}
