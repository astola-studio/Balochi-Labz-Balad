package dd.astolastudio.balochidictionary;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;

public abstract class AbstractDetailActivity extends ActionBarActivity {
	protected CharSequence mTitle;

	protected abstract void restoreActionBar();

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_item_detail);
		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
		if (VERSION.SDK_INT >= 21) {
			getWindow().setStatusBarColor(getResources().getColor(R.color.green_accent_dark));
		}
		setSupportActionBar((Toolbar) findViewById(R.id.toolbar_actionbar));
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_settings:
				startActivity(new Intent(this, SettingsActivity.class));
				return true;
			case R.id.action_help:
				displayHelp();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	private void sendFeedback() {
		Intent intent = new Intent("android.intent.action.SENDTO", Uri.fromParts("mailto", getString(R.string.feedback_email), null));
		intent.putExtra("android.intent.extra.SUBJECT", getString(R.string.feedback_subject));
		startActivity(Intent.createChooser(intent, getString(R.string.feedback_intent_chooser)));
	}

	private void displayHelp() {
		new MainActivity.HelpDialogFragment().show(getFragmentManager(), "help");
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (82 == keyCode && Build.BRAND.equalsIgnoreCase("LGE")) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (82 != keyCode || !Build.BRAND.equalsIgnoreCase("LGE")) {
			return super.onKeyUp(keyCode, event);
		}
		openOptionsMenu();
		return true;
	}
}
