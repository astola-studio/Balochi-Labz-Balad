package dd.astolastudio.balochidictionary;

import android.content.Context;
import android.preference.DialogPreference;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;
import androidx.annotation.NonNull;

public class AboutPreference extends DialogPreference {
	public AboutPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		setDialogLayoutResource(R.layout.about_preference_layout);
		setPositiveButtonText(R.string.ok);
		setNegativeButtonText(null);
		setDialogIcon(null);
	}

	protected void onBindDialogView(@NonNull View view) {
		super.onBindDialogView(view);
		TextView textView = (TextView) view.findViewById(R.id.aboutDialogTextView);
		textView.setText(Html.fromHtml(getContext().getString(R.string.message_about)));
		textView.setMovementMethod(LinkMovementMethod.getInstance());
		WebView webView = (WebView) view.findViewById(R.id.aboutDialogWebView);
		webView.getSettings().setUseWideViewPort(true);
		webView.getSettings().setLoadWithOverviewMode(true);
		webView.loadDataWithBaseURL(null, getContext().getString(R.string.apache_license), "text/html", null, null);
	}
}
