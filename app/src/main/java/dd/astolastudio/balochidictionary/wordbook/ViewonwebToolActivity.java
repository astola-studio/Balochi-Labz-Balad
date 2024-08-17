package dd.astolastudio.balochidictionary.wordbook;

import android.annotation.SuppressLint;
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
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import dd.astolastudio.balochidictionary.R;
import dd.astolastudio.balochidictionary.ActionBarActivity;
import dd.astolastudio.balochidictionary.SettingsActivity;
import dd.astolastudio.balochidictionary.MainActivity;

public class ViewonwebToolActivity extends ActionBarActivity {
	private static final String URL_START = "http://www.thefreedictionary.com/";
	private WebView mWebView;

	@SuppressLint({"SetJavaScriptEnabled"})
	protected void onCreate(Bundle savedInstanceState) {
		getWindow().requestFeature(2);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_viewonweb_tool);
		if (VERSION.SDK_INT >= 21) {
			getWindow().setStatusBarColor(getResources().getColor(R.color.green_accent_dark));
		}
		setSupportActionBar((Toolbar) findViewById(R.id.toolbar_actionbar));
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle(getString(R.string.title_wordbook));
		actionBar.setSubtitle(getString(R.string.subtitle_viewonweb_tool));
		String url = URL_START + getIntent().getStringExtra("dd.astolastudio.balochidictionary.wordbook.ViewonwebToolExtraKey") + "#content";
		this.mWebView = (WebView) findViewById(R.id.viewonweb_tool_webview);
		WebSettings settings = this.mWebView.getSettings();
		settings.setJavaScriptEnabled(true);
		final ActionBarActivity activity = this;
		this.mWebView.setWebChromeClient(new WebChromeClient() {
			public void onProgressChanged(WebView view, int progress) {
				if (100 == progress) {
					activity.setSupportProgressBarVisibility(false);
				}
				activity.setProgress(progress * 1000);
			}
		});
		this.mWebView.setWebViewClient(new WebViewClient() {
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}

			public void onPageFinished(WebView view, String url) {
				view.loadUrl("javascript:(function(){var style=document.createElement('style');style.innerHTML='<style>@font-face{font-family:NotoSerif;src: url(\"fonts/NotoSerif-Regular.ttf\");}.lang{font-family:NotoSerif, Gentium, Cardo, serif;}</style>';document.getElementsByTagName('head')[0].appendChild(style);})();");
			}

			public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
				Toast.makeText(activity, ViewonwebToolActivity.this.getString(R.string.webview_error) + description, 0).show();
			}
		});
		settings.setBuiltInZoomControls(true);
		settings.setDisplayZoomControls(false);
		this.mWebView.loadUrl(url);
	}

	public boolean onKeyUp(int keyCode, @NonNull KeyEvent event) {
		if (82 != keyCode || !Build.BRAND.equalsIgnoreCase("LGE")) {
			return super.onKeyUp(keyCode, event);
		}
		openOptionsMenu();
		return true;
	}

	public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
		if (82 == keyCode && Build.BRAND.equalsIgnoreCase("LGE")) {
			return true;
		}
		if (keyCode != 4 || !this.mWebView.canGoBack()) {
			return super.onKeyDown(keyCode, event);
		}
		this.mWebView.goBack();
		return true;
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.global_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				finish();
				return true;
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
}
