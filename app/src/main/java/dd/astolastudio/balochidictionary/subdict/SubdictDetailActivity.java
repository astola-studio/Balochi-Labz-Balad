package dd.astolastudio.balochidictionary.subdict;

import android.app.FragmentManager;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import androidx.appcompat.app.ActionBar;
import androidx.core.app.NavUtils;
import dd.astolastudio.balochidictionary.AbstractDetailActivity;
import dd.astolastudio.balochidictionary.MainActivity;
import dd.astolastudio.balochidictionary.R;
import dd.astolastudio.balochidictionary.data.appdata.AppDataContract.SubdictBookmarks;

public class SubdictDetailActivity extends AbstractDetailActivity {
	
	public static final String ARG_SECTION = "section";
	public static final String ARG_SYNTAX_ID = "subdict_id";
	private static final String TAG = "SubdictDetailActivity";
	private String mSection;
	private int mSubdictId;
	private CharSequence mTitle;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		this.mSubdictId = intent.getIntExtra(ARG_SYNTAX_ID, -1);
		this.mSection = intent.getStringExtra(ARG_SECTION);
		this.mTitle = getString(R.string.title_subdict);
		if (savedInstanceState == null) {
			Bundle arguments = new Bundle();
			arguments.putString("xml", getIntent().getStringExtra("xml"));
			SubdictDetailFragment fragment = new SubdictDetailFragment();
			fragment.setArguments(arguments);
			getFragmentManager().beginTransaction().replace(R.id.item_detail_container, fragment).commitAllowingStateLoss();
		}
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.subdict_detail_menu, menu);
		setSubdictBookmarkIcon(menu);
		restoreActionBar();
		return super.onCreateOptionsMenu(menu);
	}

	public boolean onPrepareOptionsMenu(Menu menu) {
		setSubdictBookmarkIcon(menu);
		restoreActionBar();
		return super.onPrepareOptionsMenu(menu);
	}

	private void setSubdictBookmarkIcon(Menu menu) {
		MenuItem addBookmark = menu.findItem(R.id.action_add_bookmark);
		MenuItem removeBookmark = menu.findItem(R.id.action_remove_bookmark);
		if (isBookmark(this.mSubdictId)) {
			addBookmark.setVisible(false);
			removeBookmark.setVisible(true);
			return;
		}
		addBookmark.setVisible(true);
		removeBookmark.setVisible(false);
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		FragmentManager mgr = getFragmentManager();
		switch (item.getItemId()) {
			case android.R.id.home:
				NavUtils.navigateUpTo(this, new Intent(this, MainActivity.class));
				return true;
			case R.id.action_add_bookmark:
				((SubdictDetailFragment) mgr.findFragmentById(R.id.item_detail_container)).addSubdictBookmark(this.mSubdictId, this.mSection);
				return true;
			case R.id.action_remove_bookmark:
				((SubdictDetailFragment) mgr.findFragmentById(R.id.item_detail_container)).removeSubdictBookmark(this.mSubdictId);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	private boolean isBookmark(int subdictId) {
		Log.w(TAG, "isBookmark(); id: " + subdictId);
		String[] columns = new String[]{"_id"};
		String[] selectionArgs = new String[]{Integer.toString(subdictId)};
		Uri uri = SubdictBookmarks.CONTENT_URI;
		Cursor cursor = getContentResolver().query(uri, columns, "subdict_id = ?", selectionArgs, null);
		boolean result = false;
		if (cursor.getCount() > 0) {
			result = true;
		}
		cursor.close();
		return result;
	}

	protected void restoreActionBar() {
		ActionBar actionBar = getSupportActionBar();
		if (actionBar != null) {
			actionBar.setNavigationMode(0);
			actionBar.setDisplayShowTitleEnabled(true);
			actionBar.setTitle(this.mTitle);
			return;
		}
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
