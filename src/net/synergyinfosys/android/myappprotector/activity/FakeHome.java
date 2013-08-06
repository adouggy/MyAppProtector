package net.synergyinfosys.android.myappprotector.activity;

import net.synergyinfosys.android.myappprotector.R;
import android.app.Activity;
import android.os.Bundle;

public class FakeHome extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_fake_home);
		finish();
	}


}
