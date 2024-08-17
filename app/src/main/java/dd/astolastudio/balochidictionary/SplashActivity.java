package dd.astolastudio.balochidictionary;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;

public class SplashActivity extends Activity {
	CountDownTimer countdown = null;

	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(1);
		super.onCreate(savedInstanceState);
		getWindow().setFlags(1024, 1024);
		setContentView(R.layout.wordbook_splash);
		this.countdown = new CountDownTimer(3000*0, 500) {
			public void onTick(long millisUntilFinished) {
			}

			public void onFinish() {
				SplashActivity.this.startActivity(new Intent(SplashActivity.this, MainActivity.class));
				SplashActivity.this.countdown.cancel();
				SplashActivity.this.finish();
			}
		};
		this.countdown.start();
	}

	public void onBackPressed() {
	}
}
