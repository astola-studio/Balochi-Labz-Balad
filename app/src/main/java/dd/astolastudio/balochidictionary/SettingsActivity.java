package dd.astolastudio.balochidictionary;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

public class SettingsActivity extends ActionBarActivity {

	
	public static class SettingsFragment extends PreferenceFragment {
		private static final int TWO_PANE_SMALLEST_SCREEN_WIDTH = 600;

		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.preferences);
			PreferenceScreen prefScreen = getPreferenceScreen();
			prefScreen.setOrderingAsAdded(false);
			Context context = getActivity();
			addConditionalPreferences(prefScreen, context);
			setTextSizeSummary();
			setAboutSummary(context);
			setTypefaceSummary();
		}

		private void addConditionalPreferences(PreferenceScreen prefScreen, Context context) {
			if (VERSION.SDK_INT >= 21) {
				addTypefacePreference(prefScreen, context);
			}
			if (isTwoPaneWidth(context)) {
				addOnePanePreference(prefScreen, context);
			}
		}

		private boolean isTwoPaneWidth(Context context) {
			return context.getResources().getConfiguration().smallestScreenWidthDp >= TWO_PANE_SMALLEST_SCREEN_WIDTH;
		}

		private void addOnePanePreference(PreferenceScreen prefScreen, Context context) {
			CheckBoxPreference pref = new CheckBoxPreference(getActivity());
			pref.setKey(getString(R.string.pref_onePane_key));
			pref.setTitle(R.string.pref_onePane);
			pref.setSummary(R.string.pref_onePane_summary);
			pref.setDefaultValue(Boolean.valueOf(false));
			pref.setOrder(getResources().getInteger(R.integer.pref_one_pane));
			prefScreen.addPreference(pref);
		}

		private void addTypefacePreference(PreferenceScreen prefScreen, Context context) {
			ListPreference pref = new ListPreference(getActivity());
			pref.setKey(getString(R.string.pref_typeface_key));
			pref.setTitle(R.string.pref_typeface);
			pref.setDialogTitle(R.string.pref_typeface);
			pref.setEntries(R.array.pref_typeface_entries);
			pref.setEntryValues(R.array.pref_typeface_values);
			pref.setDefaultValue(getString(R.string.pref_typeface_default));
			pref.setOrder(getResources().getInteger(R.integer.pref_typeface));
			prefScreen.addPreference(pref);
		}

		private void setTextSizeSummary() {
			findPreference(getString(R.string.pref_textSize_key)).setSummary("%s");
		}

		private void setAboutSummary(Context context) {
			try {
				String versionName = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
			findPreference(getString(R.string.pref_about_key)).setSummary(getString(R.string.version));
		}

		private void setTypefaceSummary() {
			Preference typefacePref = findPreference(getString(R.string.pref_typeface_key));
			if (typefacePref != null) {
				typefacePref.setSummary("%s");
			}
		}
	}

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		if (VERSION.SDK_INT >= 21) {
			getWindow().setStatusBarColor(getResources().getColor(R.color.green_accent_dark));
		}
		setSupportActionBar((Toolbar) findViewById(R.id.toolbar_actionbar));
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction().add(R.id.container, new SettingsFragment()).commit();
		}
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.settings_menu, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				finish();
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

	public boolean onKeyUp(int keyCode, @NonNull KeyEvent event) {
		if (82 != keyCode || !Build.BRAND.equalsIgnoreCase("LGE")) {
			return super.onKeyUp(keyCode, event);
		}
		openOptionsMenu();
		return true;
	}
}
