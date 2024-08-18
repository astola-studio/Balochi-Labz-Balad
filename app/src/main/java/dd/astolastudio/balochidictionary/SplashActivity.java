package dd.astolastudio.balochidictionary;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import java.util.Timer;
import java.util.TimerTask;
import android.view.Window;
import android.view.WindowManager;

public class SplashActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.wordbook_splash);
		
		new Timer().schedule(new TimerTask(){

				@Override
				public void run() {
					startActivity(new Intent(SplashActivity.this, MainActivity.class));
					finish();
				}
			}, 3000);
	}
	
	@Override
	public void onBackPressed() {
		// Disallow Back Press
	}
}
