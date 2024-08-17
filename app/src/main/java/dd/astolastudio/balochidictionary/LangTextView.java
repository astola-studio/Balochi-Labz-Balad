package dd.astolastudio.balochidictionary;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.Build.VERSION;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;
import androidx.collection.LruCache;

public class LangTextView extends TextView implements OnSharedPreferenceChangeListener {
	private static final String NOTO_SERIF = "NotoSerif-Regular.ttf";
	private static final int TEXT_COLOR = R.color.primary_text_default_material;
	private static final LruCache<String, Typeface> sTypefaceCache = new LruCache<>(12);
	private final boolean mAllowTextSizeChanges;

	public LangTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray attrArray = context.getTheme().obtainStyledAttributes(attrs, new int[]{
																			 R.attr.allowTextSizeChanges
		}, 0, 0);
		try {
			this.mAllowTextSizeChanges = attrArray.getBoolean(0, true);
			setFont(context);
			PreferenceManager.getDefaultSharedPreferences(context).registerOnSharedPreferenceChangeListener(this);
		} finally {
			attrArray.recycle();
		}
	}

	public void setFont(Context context) {
		setTypeface(context);
		setTextColor();
		setTextSize(context);
	}

	private String getTypefaceSetting(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context).getString(context.getString(R.string.pref_typeface_key), context.getString(R.string.pref_typeface_default));
	}

	private boolean robotoTypefacePreferenceSet(Context context) {
		if (VERSION.SDK_INT < 21) {
			return false;
		}
		return context.getString(R.string.pref_typeface_roboto).equals(getTypefaceSetting(context));
	}

	private void setTypeface(Context context) {
		if (robotoTypefacePreferenceSet(context)) {
			setTypeface(Typeface.DEFAULT);
		} else if (!isInEditMode() && !TextUtils.isEmpty(NOTO_SERIF)) {
			Typeface typeface = (Typeface) sTypefaceCache.get(NOTO_SERIF);
			if (typeface == null) {
				typeface = Typeface.createFromAsset(context.getAssets(), "fonts/NotoSerif-Regular.ttf");
				sTypefaceCache.put(NOTO_SERIF, typeface);
			}
			setTypeface(typeface);
			setPaintFlags(getPaintFlags() | 128);
		}
	}

	private void setTextColor() {
		setTextColor(getResources().getColor(TEXT_COLOR));
	}

	private void setTextSize(Context context) {
		if (this.mAllowTextSizeChanges) {
			setTextSize(TextSize.getScaledPixelSize(PreferenceManager.getDefaultSharedPreferences(context).getString(context.getString(R.string.pref_textSize_key), context.getString(R.string.pref_textSize_item_medium))));
		}
	}

	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		Resources res = getResources();
		if (key.equals(res.getString(R.string.pref_typeface_key))) {
			setTypeface(getContext());
		} else if (key.equals(res.getString(R.string.pref_textSize_key))) {
			setTextSize(getContext());
		}
	}
}
