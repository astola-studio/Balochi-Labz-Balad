package dd.astolastudio.balochidictionary.navigationdrawer;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import dd.astolastudio.balochidictionary.R;
import java.util.ArrayList;
import android.graphics.Typeface;

public class NavigationDrawerAdapter extends ArrayAdapter<AbstractNavigationDrawerItem> {
	private final Context mContext;
	private final LayoutInflater mInflater;
	OnCategoryChange onCategoryChange;

	private static class NavigationDrawerCategoryHolder {
		private Spinner spinCat;

		private NavigationDrawerCategoryHolder() {
		}
	}

	private static class NavigationDrawerHeadingHolder {
		private TextView textView;

		private NavigationDrawerHeadingHolder() {
		}
	}

	private static class NavigationDrawerRowHolder {
		private ImageView imageView;
		private TextView textView;

		private NavigationDrawerRowHolder() {
		}
	}

	public interface OnCategoryChange {
		void onChange(String cat, int index);

		void onNothing();
	}

	public NavigationDrawerAdapter(Context context, int id, AbstractNavigationDrawerItem[] objects) {
		super(context, id, objects);
		this.mInflater = LayoutInflater.from(context);
		this.mContext = context;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		AbstractNavigationDrawerItem item = (AbstractNavigationDrawerItem) getItem(position);
		if (item.isRow()) {
			return getRowView(convertView, parent, item, position);
		}
		if (item.isCat()) {
			return getViewCat(convertView, parent, item, position);
		}
		return getHeadingView(convertView, parent, item, position);
	}

	public View getRowView(View convertView, ViewGroup parentView, AbstractNavigationDrawerItem item, int position) {
		NavigationDrawerRowHolder navigationDrawerRowHolder = null;
		TextView textView;
		if (convertView == null) {
			convertView = this.mInflater.inflate(R.layout.navigation_drawer_row, parentView, false);
			textView = (TextView) convertView.findViewById(R.id.navigation_drawer_row_text);
			ImageView imageView = (ImageView) convertView.findViewById(R.id.navigation_drawer_row_icon);
			navigationDrawerRowHolder = new NavigationDrawerRowHolder();
			navigationDrawerRowHolder.textView = textView;
			navigationDrawerRowHolder.imageView = imageView;
			convertView.setTag(navigationDrawerRowHolder);
		}
		if (navigationDrawerRowHolder == null) {
			navigationDrawerRowHolder = (NavigationDrawerRowHolder) convertView.getTag();
		}
		navigationDrawerRowHolder.textView.setText(item.getLabel());
		int textDark;
		if (((ListView) parentView).isItemChecked(position)) {
			((NavigationDrawerRow) item).setIconHighlighted(true);
			textView = navigationDrawerRowHolder.textView;
			Resources resources = this.mContext.getResources();
			textDark = resources.getColor(R.color.green_accent_light);
			TypedValue typedValue = new TypedValue();
			getContext().getTheme().resolveAttribute(android.R.attr.colorControlHighlight, typedValue, true);
			int backgroundLight = typedValue.data;
			textView.setTextColor(textDark);
			textView.setTypeface(null, Typeface.BOLD);
			navigationDrawerRowHolder.imageView.setColorFilter(textDark);
			convertView.setBackgroundColor(backgroundLight);
		} else {
			((NavigationDrawerRow) item).setIconHighlighted(false);
			textView = navigationDrawerRowHolder.textView;
			textDark = this.mContext.getResources().getColor(R.color.primary_text_default_material);
			int backgroundWhite = this.mContext.getResources().getColor(R.color.background);
			textView.setTextColor(textDark);
			textView.setTypeface(null, Typeface.NORMAL);
			navigationDrawerRowHolder.imageView.setColorFilter(textDark);
			convertView.setBackgroundColor(backgroundWhite);
		}
		navigationDrawerRowHolder.imageView.setImageResource(((NavigationDrawerRow) item).getIcon());
		return convertView;
	}

	public View getHeadingView(View convertView, ViewGroup parentView, AbstractNavigationDrawerItem item, int position) {
		NavigationDrawerHeadingHolder navigationDrawerHeadingHolder = null;
		if (convertView == null) {
			if (position == 0) {
				convertView = this.mInflater.inflate(R.layout.navigation_drawer_heading_first, parentView, false);
			} else {
				convertView = this.mInflater.inflate(R.layout.navigation_drawer_heading, parentView, false);
			}
			TextView textView = (TextView) convertView.findViewById(R.id.navigation_drawer_heading_text);
			navigationDrawerHeadingHolder = new NavigationDrawerHeadingHolder();
			navigationDrawerHeadingHolder.textView = textView;
			convertView.setTag(navigationDrawerHeadingHolder);
		}
		if (navigationDrawerHeadingHolder == null) {
			navigationDrawerHeadingHolder = (NavigationDrawerHeadingHolder) convertView.getTag();
		}
		navigationDrawerHeadingHolder.textView.setText(item.getLabel());
		return convertView;
	}

	public View getViewCat(View convertView, ViewGroup parentView, AbstractNavigationDrawerItem item, int position) {
		NavigationDrawerCategoryHolder navigationDrawerCategoryHolder = null;
		if (convertView == null) {
			convertView = this.mInflater.inflate(R.layout.navigation_drawer_cat, parentView, false);
			Spinner spinner = (Spinner) convertView.findViewById(R.id.spinner_cat);
			navigationDrawerCategoryHolder = new NavigationDrawerCategoryHolder();
			navigationDrawerCategoryHolder.spinCat = spinner;
			convertView.setTag(navigationDrawerCategoryHolder);
		}
		if (navigationDrawerCategoryHolder == null) {
			navigationDrawerCategoryHolder = (NavigationDrawerCategoryHolder) convertView.getTag();
		}
		final ArrayList<String> cats = ((NavigationDrawerCat) item).getCats();
		ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this.mContext, android.R.layout.simple_spinner_item, cats);
		spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		navigationDrawerCategoryHolder.spinCat.setAdapter(spinnerArrayAdapter);
		navigationDrawerCategoryHolder.spinCat.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
				if (NavigationDrawerAdapter.this.onCategoryChange != null) {
					NavigationDrawerAdapter.this.onCategoryChange.onChange((String) cats.get(position), position);
				}
			}

			public void onNothingSelected(AdapterView<?> adapterView) {
				if (NavigationDrawerAdapter.this.onCategoryChange != null) {
					NavigationDrawerAdapter.this.onCategoryChange.onNothing();
				}
			}
		});
		return convertView;
	}

	public int getViewTypeCount() {
		return 3;
	}

	public int getItemViewType(int position) {
		return ((AbstractNavigationDrawerItem) getItem(position)).getType();
	}

	public boolean isEnabled(int position) {
		return ((AbstractNavigationDrawerItem) getItem(position)).isEnabled();
	}

	public void setOnCategoryChange(OnCategoryChange onCategoryChange) {
		this.onCategoryChange = onCategoryChange;
	}

	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
	}
}
