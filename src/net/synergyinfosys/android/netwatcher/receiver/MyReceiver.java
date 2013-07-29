package net.synergyinfosys.android.netwatcher.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class MyReceiver extends BroadcastReceiver {
	public static final String INTENT_ACTION="net.synergyinfosys.android.netwatcher.MyReceiver";
	public static final String TAG = "MyReceiver";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d( TAG, "on receiving..");
		
        Bundle bundle = intent.getExtras();
        
        if(bundle != null){
//        	Intent it = new Intent( WatcherService.INTENT_ACTION );
//        	it.putExtras(bundle);
//            
//            ServiceOperation op = (ServiceOperation) bundle.getSerializable("operation");
//            switch( op ){
//            case STOP:
//                context.stopService(it);
//                break;
//            case START:
//            	context.startService(it);
//            	break;
//            }
            	
        }
	}

}
