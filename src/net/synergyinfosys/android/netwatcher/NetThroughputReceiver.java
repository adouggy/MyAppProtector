package net.synergyinfosys.android.netwatcher;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

public class NetThroughputReceiver extends BroadcastReceiver{
	public static final String TAG = "NetThroughputReceiver";
	
	private EditText mTextView = null;
	
	public NetThroughputReceiver(EditText txtView){
		this.mTextView = txtView;
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d( TAG, "on receiving...");
		Bundle bundle = intent.getExtras();
		String status = bundle.getString("status");
		mTextView.setText(status);
	}

}
