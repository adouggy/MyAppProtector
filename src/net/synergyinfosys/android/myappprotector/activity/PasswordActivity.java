package net.synergyinfosys.android.myappprotector.activity;

import net.synergyinfosys.android.myappprotector.R;
import net.synergyinfosys.android.myappprotector.service.LongLiveService;
import net.synergyinfosys.android.myappprotector.util.SystemUiHider;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
public class PasswordActivity extends Activity implements OnClickListener{
	
	public static final String TAG = "PasswordActivity";
	
	/**
	 * Whether or not the system UI should be auto-hidden after
	 * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
	 */
	private static final boolean AUTO_HIDE = false;

	/**
	 * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
	 * user interaction before hiding the system UI.
	 */
	private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

	/**
	 * If set, will toggle the system UI visibility upon interaction. Otherwise,
	 * will show the system UI visibility upon interaction.
	 */
	private static final boolean TOGGLE_ON_CLICK = false;

	/**
	 * The flags to pass to {@link SystemUiHider#getInstance}.
	 */
	private static final int HIDER_FLAGS = SystemUiHider.FLAG_FULLSCREEN;

	/**
	 * The instance of the {@link SystemUiHider} for this activity.
	 */
	private SystemUiHider mSystemUiHider;
	
	Button btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8, btn9, btn0;
	TextView passwordText;
	private final String passwordTag = "输入密码";
	private String inputPassword = "";
	private String pkgName, clsName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_password);

		final View controlsView = findViewById(R.id.fullscreen_content_controls);
		final View contentView = findViewById(R.id.fullscreen_content);

		// Set up an instance of SystemUiHider to control the system UI for
		// this activity.
		mSystemUiHider = SystemUiHider.getInstance(this, contentView, HIDER_FLAGS);
		mSystemUiHider.setup();
		mSystemUiHider.setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener() {
			// Cached values.
			int mControlsHeight;
			int mShortAnimTime;

			@Override
			@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
			public void onVisibilityChange(boolean visible) {
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
					// If the ViewPropertyAnimator API is available
					// (Honeycomb MR2 and later), use it to animate the
					// in-layout UI controls at the bottom of the
					// screen.
					if (mControlsHeight == 0) {
						mControlsHeight = controlsView.getHeight();
					}
					if (mShortAnimTime == 0) {
						mShortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
					}
					controlsView.animate().translationY(visible ? 0 : mControlsHeight).setDuration(mShortAnimTime);
				} else {
					// If the ViewPropertyAnimator APIs aren't
					// available, simply show or hide the in-layout UI
					// controls.
					controlsView.setVisibility(visible ? View.VISIBLE : View.GONE);
				}

//				if (visible && AUTO_HIDE) {
//					// Schedule a hide().
//					delayedHide(AUTO_HIDE_DELAY_MILLIS);
//				}
			}
		});

		// Set up the user interaction to manually show or hide the system UI.
		contentView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (TOGGLE_ON_CLICK) {
					mSystemUiHider.toggle();
				} else {
					mSystemUiHider.show();
				}
			}
		});

		// Upon interacting with UI controls, delay any scheduled hide()
		// operations to prevent the jarring behavior of controls going away
		// while interacting with the UI.
		findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);
		
		btn1 = (Button) findViewById( R.id.button1 );
		btn2 = (Button) findViewById( R.id.button2 );
		btn3 = (Button) findViewById( R.id.button3 );
		btn4 = (Button) findViewById( R.id.button4 );
		btn5 = (Button) findViewById( R.id.button5 );
		btn6 = (Button) findViewById( R.id.button6 );
		btn7 = (Button) findViewById( R.id.button7 );
		btn8 = (Button) findViewById( R.id.button8 );
		btn9 = (Button) findViewById( R.id.button9 );
		btn0 = (Button) findViewById( R.id.button0 );
		passwordText = (TextView) contentView;
		btn1.setOnClickListener(this);
		btn2.setOnClickListener(this);
		btn3.setOnClickListener(this);
		btn4.setOnClickListener(this);
		btn5.setOnClickListener(this);
		btn6.setOnClickListener(this);
		btn7.setOnClickListener(this);
		btn8.setOnClickListener(this);
		btn9.setOnClickListener(this);
		btn0.setOnClickListener(this);
		
		Bundle b = getIntent().getExtras();    
		pkgName = b.getString("pkgName");
		clsName = b.getString("clsName");
		
		clearPasswordShow();
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		// Trigger the initial hide() shortly after the activity has been
		// created, to briefly hint to the user that UI controls
		// are available.
//		delayedHide(100);
	}

	/**
	 * Touch listener to use for in-layout UI controls to delay hiding the
	 * system UI. This is to prevent the jarring behavior of controls going away
	 * while interacting with activity UI.
	 */
	View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
		@Override
		public boolean onTouch(View view, MotionEvent motionEvent) {
			if (AUTO_HIDE) {
				delayedHide(AUTO_HIDE_DELAY_MILLIS);
			}
			return false;
		}
	};

	Handler mHideHandler = new Handler();
	Runnable mHideRunnable = new Runnable() {
		@Override
		public void run() {
			mSystemUiHider.hide();
		}
	};

	/**
	 * Schedules a call to hide() in [delay] milliseconds, canceling any
	 * previously scheduled calls.
	 */
	private void delayedHide(int delayMillis) {
		mHideHandler.removeCallbacks(mHideRunnable);
		mHideHandler.postDelayed(mHideRunnable, delayMillis);
	}
	
	private void clearPasswordShow(){
		passwordText.setText(passwordTag);
		inputPassword = "";
	}
	
	private void setPassword(int num){
		inputPassword += String.valueOf(num);
		String s = "";
		int passwordWidth = inputPassword.length();
		for( int i=0; i<passwordWidth; i++ ){
			s += "*";
		}
		passwordText.setText(s);
		
		boolean pass = false;
		if( inputPassword.length() == 4 ){
			if( inputPassword.compareTo("4321") == 0 ){
				Log.d( TAG, "correct password!" );
				pass = true;
			}else{
				Log.d( TAG, "wrong password!" );
			}
			clearPasswordShow();
		}
		
		if( pass ){
			Intent unlockBroadcastIntent = new Intent(LongLiveService.LONGLIVESERVICE_BROADCAST_UNLOCK_ACTION);
			Bundle bundle = new Bundle();
			bundle.putString("pkgName", pkgName);
			unlockBroadcastIntent.putExtras(bundle);
			sendBroadcast(unlockBroadcastIntent); 
			
			try {
				Thread.sleep(LongLiveService.threadInterval);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			
			Intent startPackageIntent = new Intent(Intent.ACTION_MAIN);
			startPackageIntent.addCategory(Intent.CATEGORY_LAUNCHER);
			ComponentName cn = new ComponentName(pkgName, clsName);
			startPackageIntent.setComponent(cn);
			startPackageIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity( startPackageIntent );
            this.finish();
		}
	}

	@Override
	public void onClick(View v) {
		if( v == btn1 ){
			setPassword(1);
		}else if( v == btn2 ){
			setPassword(2);
		}else if( v == btn3 ){
			setPassword(3);
		}else if( v == btn4 ){
			setPassword(4);
		}else if( v == btn5 ){
			setPassword(5);
		}else if( v == btn6 ){
			setPassword(6);
		}else if( v == btn7 ){
			setPassword(7);
		}else if( v == btn8 ){
			setPassword(8);
		}else if( v == btn9 ){
			setPassword(9);
		}else if( v == btn0 ){
			setPassword(0);
		}
	}
}
