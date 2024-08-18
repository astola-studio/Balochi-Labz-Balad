package dd.astolastudio.balochidictionary;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends Activity {
	
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(1);
		super.onCreate(savedInstanceState);
		getWindow().setFlags(1024, 1024);
		setContentView(R.layout.wordbook_splash);
		
		new Timer().schedule(new TimerTask(){

				@Override
				public void run() {
					startActivity(new Intent(SplashActivity.this, MainActivity.class));
					finish();
				}
			}, 3000);
	}

	public void onBackPressed() {
	}
}
