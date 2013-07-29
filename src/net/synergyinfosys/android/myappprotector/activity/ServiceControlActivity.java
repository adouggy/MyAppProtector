package net.synergyinfosys.android.myappprotector.activity;

import net.synergyinfosys.android.myappprotector.R;
import net.synergyinfosys.android.myappprotector.service.LongLiveService;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ServiceControlActivity extends Activity implements OnClickListener {
	public static final String TAG = "MainActivity";
	private Button buttonStart, buttonStop, buttonLockAllInList, buttonClearMyLauncherDefault;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		buttonStart = (Button) findViewById(R.id.button_start);
		buttonStop = (Button) findViewById(R.id.button_stop);
		buttonLockAllInList = (Button) findViewById(R.id.button_lockall);
		buttonClearMyLauncherDefault = (Button) findViewById(R.id.button_clear_default);

		buttonStart.setOnClickListener(this);
		buttonStop.setOnClickListener(this);
		buttonLockAllInList.setOnClickListener(this);
		buttonClearMyLauncherDefault.setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onClick(View src) {
		switch (src.getId()) {
		case R.id.button_start:
			Log.i(TAG, "onClick: starting service");
			startService(new Intent(this, LongLiveService.class));
			break;
		case R.id.button_stop:
			Log.i(TAG, "onClick: stopping service");
			stopService(new Intent(this, LongLiveService.class));
			break;
		case R.id.button_lockall:
			Intent broadcastIntent = new Intent(LongLiveService.LONGLIVESERVICE_BROADCAST_LOCKALL_ACTION);
			sendBroadcast(broadcastIntent);
			break;
		case R.id.button_clear_default:
			getPackageManager().clearPackagePreferredActivities(this.getPackageName());
			break;
		}
	}

}
