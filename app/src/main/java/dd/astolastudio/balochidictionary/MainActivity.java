package dd.astolastudio.balochidictionary;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.SearchManager;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import dd.astolastudio.balochidictionary.AbstractListFragment.Callbacks;
import dd.astolastudio.balochidictionary.data.appdata.AppDataContract.SubdictBookmarks;
import dd.astolastudio.balochidictionary.data.appdata.AppDataContract.WordbookFavorites;
import dd.astolastudio.balochidictionary.data.appdata.AppDataContract.WordbookHistory;
import dd.astolastudio.balochidictionary.data.appdata.WordbookHistoryProvider;
import dd.astolastudio.balochidictionary.data.subdict.SubdictContract;
import dd.astolastudio.balochidictionary.data.wordbook.WordbookContract;
import dd.astolastudio.balochidictionary.data.wordbook.WordbookProvider;
import dd.astolastudio.balochidictionary.navigationdrawer.NavigationDrawerFragment;
import dd.astolastudio.balochidictionary.navigationdrawer.NavigationDrawerFragment.NavigationDrawerCallbacks;
import dd.astolastudio.balochidictionary.subdict.AbstractSubdictListFragment;
import dd.astolastudio.balochidictionary.subdict.SubdictBookmarksListFragment;
import dd.astolastudio.balochidictionary.subdict.SubdictBrowseListFragment;
import dd.astolastudio.balochidictionary.subdict.SubdictDetailActivity;
import dd.astolastudio.balochidictionary.subdict.SubdictDetailFragment;
import dd.astolastudio.balochidictionary.wordbook.AbstractWordbookListFragment;
import dd.astolastudio.balochidictionary.wordbook.WordbookBrowseListFragment;
import dd.astolastudio.balochidictionary.wordbook.WordbookDetailActivity;
import dd.astolastudio.balochidictionary.wordbook.WordbookDetailFragment;
import dd.astolastudio.balochidictionary.wordbook.WordbookFavoritesListFragment;
import dd.astolastudio.balochidictionary.wordbook.WordbookHistoryListFragment;
import androidx.appcompat.widget.PopupMenu;

public class MainActivity extends ActionBarActivity implements NavigationDrawerCallbacks, Callbacks {
	public static final String ACTION_SET_MODE = "dd.astolastudio.balochidictionary.SET_MODE";
	public static final String KEY_MODE = "mode";
	private static final String KEY_SUBTITLE = "action_bar_subtitle";
	private static final String KEY_TITLE = "action_bar_title";
	private static final String TAG = "MainActivity";

	private Mode mMode;
	private NavigationDrawerFragment mNavigationDrawerFragment;
	private CharSequence mSubtitle;
	private CharSequence mTitle;
	private boolean mTwoPane;

	public static class ClearSubdictBookmarksDialogFragment extends DialogFragment {
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			Builder builder = new Builder(getActivity());
			builder.setMessage(R.string.clear_subdict_bookmarks_dialog_message);
			builder.setPositiveButton(R.string.ok, new OnClickListener() {
					public void onClick(DialogInterface dialogInterface, int i) {
						MainActivity activity = (MainActivity) ClearSubdictBookmarksDialogFragment.this.getActivity();
						activity.getContentResolver().delete(SubdictBookmarks.CONTENT_URI, null, null);
						Toast.makeText(activity.getApplicationContext(), ClearSubdictBookmarksDialogFragment.this.getString(R.string.toast_clear_subdict_bookmarks), 0).show();
					}
				});
			builder.setNegativeButton(R.string.cancel, new OnClickListener() {
					public void onClick(DialogInterface dialogInterface, int i) {
						dialogInterface.dismiss();
					}
				});
			return builder.create();
		}
	}

	public static class ClearWordbookFavoritesDialogFragment extends DialogFragment {
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			Builder builder = new Builder(getActivity());
			builder.setMessage(R.string.clear_wordbook_favorites_dialog_message);
			builder.setPositiveButton(R.string.ok, new OnClickListener() {
					public void onClick(DialogInterface dialogInterface, int i) {
						ClearWordbookFavoritesDialogFragment.this.getActivity().getContentResolver().delete(WordbookFavorites.CONTENT_URI, null, null);
						Toast.makeText(ClearWordbookFavoritesDialogFragment.this.getActivity(), ClearWordbookFavoritesDialogFragment.this.getString(R.string.toast_clear_wordbook_favorites), 0).show();
					}
				});
			builder.setNegativeButton(R.string.cancel, new OnClickListener() {
					public void onClick(DialogInterface dialogInterface, int i) {
						dialogInterface.dismiss();
					}
				});
			return builder.create();
		}
	}

	public static class HelpDialogFragment extends DialogFragment {
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			Builder builder = new Builder(getActivity());
			builder.setTitle(R.string.title_help);
			TextView textView = new TextView(getActivity());
			textView.setTextAppearance(getActivity(), android.R.style.TextAppearance_Medium);
			//textView.setTextColor(getResources().getColor(android.R.color.black));
			textView.setPadding(25, 25, 25, 25);
			textView.setText(Html.fromHtml(getString(R.string.message_help)));
			textView.setMovementMethod(LinkMovementMethod.getInstance());
			ScrollView scrollView = new ScrollView(getActivity());
			scrollView.addView(textView);
			builder.setView(scrollView);
			builder.setPositiveButton(R.string.ok, new OnClickListener() {
					public void onClick(DialogInterface dialogInterface, int i) {
						dialogInterface.dismiss();
					}
				});
			return builder.create();
		}
	}

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_item_list);
		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
		setSupportActionBar((Toolbar) findViewById(R.id.toolbar_actionbar));
		if (savedInstanceState == null) {
			this.mTitle = getString(R.string.title_wordbook);
			this.mSubtitle = getString(R.string.title_wordbook_browse);
		} else {
			this.mTitle = savedInstanceState.getString(KEY_TITLE);
			this.mSubtitle = savedInstanceState.getString(KEY_SUBTITLE);
			this.mMode = Mode.getModeFromName(savedInstanceState.getString(KEY_MODE));
		}
		((DrawerLayout) findViewById(R.id.drawer_layout)).setStatusBarBackgroundColor(getResources().getColor(R.color.green_accent_dark));
		this.mNavigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager().findFragmentById(R.id.navigation_drawer_fragment);
		this.mNavigationDrawerFragment.setUp(R.id.navigation_drawer_fragment_container, (DrawerLayout) findViewById(R.id.drawer_layout));
		if (findViewById(R.id.item_detail_container) != null) {
			this.mTwoPane = true;
		}
		restoreActionBar();
		checkTabletDisplayMode();
		handleIntent(getIntent());
	}

	protected void onResume() {
		super.onResume();
		checkTabletDisplayMode();
	}

	private void checkTabletDisplayMode() {
		if (this.mTwoPane) {
			View leftPane = findViewById(R.id.item_list_container);
			if (onePaneModeSelected() && this.mMode.equals(Mode.WORDBOOK_BROWSE)) {
				leftPane.setVisibility(View.GONE);
			} else {
				leftPane.setVisibility(View.VISIBLE);
			}
		}
	}

	private boolean onePaneModeSelected() {
		return PreferenceManager.getDefaultSharedPreferences(this).getBoolean(getString(R.string.pref_onePane_key), false);
	}

	protected void onSaveInstanceState(@NonNull Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString(KEY_TITLE, (String) this.mTitle);
		outState.putString(KEY_SUBTITLE, (String) this.mSubtitle);
		outState.putString(KEY_MODE, this.mMode.getName());
	}

	protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		this.mTitle = savedInstanceState.getString(KEY_TITLE);
		this.mSubtitle = savedInstanceState.getString(KEY_SUBTITLE);
		this.mMode = Mode.getModeFromName(savedInstanceState.getString(KEY_MODE));
		restoreActionBar();
	}

	protected void onNewIntent(Intent intent) {
		handleIntent(intent);
	}

	void handleIntent(Intent intent) {
		if ("android.intent.action.SEARCH".equals(intent.getAction())) {
			search(intent.getStringExtra("query"));
		} else if ("android.intent.action.VIEW".equals(intent.getAction())) {
			getWordbookEntry(intent.getData());
		} else if (ACTION_SET_MODE.equals(intent.getAction())) {
			switchToMode(Mode.getModeFromName(intent.getStringExtra(KEY_MODE)));
		}
	}

	public void onItemSelected(String fragmentName) {
		if (fragmentName.startsWith("wordbook_") || fragmentName.startsWith("dictionary_")) {
			wordbookItemSelected();
		} else if (fragmentName.startsWith("subdict_")) {
			subdictItemSelected();
		} else {
			throw new IllegalArgumentException("Invalid fragment name");
		}
		invalidateOptionsMenu();
	}

	private void wordbookItemSelected() {
		String[] columns = new String[]{"balochi","english","urdu","pronunciation"};
		String id = Integer.toString(((AbstractWordbookListFragment) getFragmentManager().findFragmentById(R.id.item_list_container)).getSelectedWordbookId());
		String[] selectionArgs = new String[]{id};
		Uri uri = WordbookContract.CONTENT_URI;
		Cursor cursor = getContentResolver().query(uri, columns, "_id = ?", selectionArgs, null);
		if (cursor.moveToFirst()) {
			displayWordbookEntry(id, cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3));
			return;
		}
		throw new IllegalStateException("Failed to retrieve wordbook entry");
	}

	private void subdictItemSelected() {
		AbstractSubdictListFragment fragment = (AbstractSubdictListFragment) getFragmentManager().findFragmentById(R.id.item_list_container);
		String[] columns = new String[]{"xml", "section"};
		String[] selectionArgs = new String[]{Integer.toString(fragment.getSelectedSubdictId())};
		Uri uri = SubdictContract.CONTENT_URI;
		Cursor cursor = getContentResolver().query(uri, columns, "_id = ?", selectionArgs, null);
		if (cursor.moveToFirst()) {
			String xml = cursor.getString(0);
			String section = cursor.getString(1);
			Log.w("Subdict item selected", section + ": " + xml);
			displaySubdictSection(section, xml);
			return;
		}
		throw new IllegalStateException("Failed to retrieve subdict section");
	}

	void displayWordbookEntry(String id, String balochi, String english, String urdu, String pronunciation) {
		if (!(this.mMode.equals(Mode.WORDBOOK_BROWSE) || this.mMode.equals(Mode.WORDBOOK_FAVORITES) || this.mMode.equals(Mode.WORDBOOK_HISTORY))) {
			switchToWordbookBrowse();
		}
		if (!this.mMode.equals(Mode.WORDBOOK_HISTORY)) {
			addHistory(id, balochi);
		}
		if (this.mTwoPane) {
			Bundle arguments = new Bundle();
			arguments.putString("balochi", balochi);
			arguments.putString("english", english);
			arguments.putString("urdu", urdu);
			arguments.putString("pronunciation", pronunciation);
			arguments.putInt("wordbook_id", Integer.parseInt(id));
			WordbookDetailFragment fragment = new WordbookDetailFragment();
			fragment.setArguments(arguments);
			FragmentTransaction transaction = getFragmentManager().beginTransaction();
			transaction.replace(R.id.item_detail_container, fragment);
			transaction.commitAllowingStateLoss();
			return;
		}
		AbstractWordbookListFragment fragment2 = (AbstractWordbookListFragment) getFragmentManager().findFragmentById(R.id.item_list_container);
		Intent intent = new Intent(this, WordbookDetailActivity.class);
		intent.putExtra("balochi", balochi);
		intent.putExtra("english", english);
		intent.putExtra("urdu", urdu);
		intent.putExtra("pronunciation", pronunciation);
		intent.putExtra("wordbook_id", fragment2.getSelectedWordbookId());
		startActivity(intent);
	}

	void displaySubdictSection(String section, String xml) {
		if (this.mTwoPane) {
			Bundle arguments = new Bundle();
			arguments.putString("xml", xml);
			SubdictDetailFragment fragment = new SubdictDetailFragment();
			fragment.setArguments(arguments);
			FragmentTransaction transaction = getFragmentManager().beginTransaction();
			transaction.replace(R.id.item_detail_container, fragment);
			transaction.commitAllowingStateLoss();
			return;
		}
		AbstractSubdictListFragment fragment2 = (AbstractSubdictListFragment) getFragmentManager().findFragmentById(R.id.item_list_container);
		Intent intent = new Intent(this, SubdictDetailActivity.class);
		intent.putExtra("xml", xml);
		intent.putExtra("subdict_id", fragment2.getSelectedSubdictId());
		intent.putExtra("section", section);
		startActivity(intent);
	}

	void addHistory(String id, String word) {
		String[] selectionArgs = new String[]{id};
		getContentResolver().delete(WordbookHistoryProvider.CONTENT_URI, "wordbookID = ?", selectionArgs);
		ContentValues values = new ContentValues();
		values.put("wordbookID", id);
		values.put("balochi", word);
		getContentResolver().insert(WordbookHistoryProvider.CONTENT_URI, values);
	}

	public void onNavigationDrawerItemSelected(int position, String cat) {
		switchToMode(Mode.getModeFromName(cat));
		switchToMode(Mode.getModeFromPosition(position));
	}

	public void restoreActionBar() {
		ActionBar actionBar = getSupportActionBar();
		if (actionBar != null) {
			actionBar.setDisplayShowTitleEnabled(true);
			actionBar.setTitle(this.mTitle);
			actionBar.setSubtitle(this.mSubtitle);
		}
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		if (this.mNavigationDrawerFragment.isDrawerOpen()) {
			return super.onCreateOptionsMenu(menu);
		}
		if (this.mMode.isWordbookMode()) {
			getMenuInflater().inflate(R.menu.wordbook_menu, menu);
			setWordbookFavoriteIcon(menu);
			((SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.menu_search)))
				.setSearchableInfo(((SearchManager) getSystemService("search")).getSearchableInfo(getComponentName()));
			restoreActionBar();
			return super.onCreateOptionsMenu(menu);
		} else if (this.mMode.isCateMode()) {
			return super.onCreateOptionsMenu(menu);
		} else {
			if (this.mMode.isSubdictMode()) {
				getMenuInflater().inflate(R.menu.subdict_menu, menu);
				setSubdictBookmarkIcon(menu);
				restoreActionBar();
				return super.onCreateOptionsMenu(menu);
			}
			throw new IllegalStateException("Invalid mode");
		}
	}

	public boolean onPrepareOptionsMenu(Menu menu) {
		if (this.mNavigationDrawerFragment.isDrawerOpen()) {
			return super.onCreateOptionsMenu(menu);
		}
		if (this.mMode.isWordbookMode()) {
			setWordbookFavoriteIcon(menu);
		} else if (this.mMode.isSubdictMode()) {
			setSubdictBookmarkIcon(menu);
		} else if (!this.mMode.isCateMode()) {
			throw new IllegalStateException("Invalid mode");
		}
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		FragmentManager mgr = getFragmentManager();
		switch (item.getItemId()) {
			case R.id.action_settings:
				startActivity(new Intent(this, SettingsActivity.class));
				return true;
			case R.id.action_help:
				displayHelp();
				return true;
			case R.id.action_add_bookmark:
				((SubdictDetailFragment) mgr.findFragmentById(R.id.item_detail_container)).addSubdictBookmark();
				return true;
			case R.id.action_remove_bookmark:
				((SubdictDetailFragment) mgr.findFragmentById(R.id.item_detail_container)).removeSubdictBookmark();
				return true;
			case R.id.action_clear_bookmarks:
				clearSubdictBookmarks();
				return true;
			case R.id.action_sound:
				((WordbookDetailFragment) mgr.findFragmentById(R.id.item_detail_container)).playSound();
				return true;
			case R.id.action_add_favorite:
				((WordbookDetailFragment) mgr.findFragmentById(R.id.item_detail_container)).addWordbookFavorite();
				return true;
			case R.id.action_remove_favorite:
				((WordbookDetailFragment) mgr.findFragmentById(R.id.item_detail_container)).removeWordbookFavorite();
				return true;
			case R.id.action_clear_favorites:
				clearWordbookFavorites();
				return true;
			case R.id.action_clear_history:
				clearHistory();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	private void clearHistory() {
		getContentResolver().delete(WordbookHistory.CONTENT_URI, null, null);
		Toast.makeText(getApplicationContext(), getString(R.string.toast_clear_history), 0).show();
	}

	private void clearWordbookFavorites() {
		new ClearWordbookFavoritesDialogFragment().show(getFragmentManager(), "clearFavorites");
	}

	private void clearSubdictBookmarks() {
		new ClearSubdictBookmarksDialogFragment().show(getFragmentManager(), "clearBookmarks");
	}

	private void getWordbookEntry(Uri data) {
		ensureModeIsWordbookBrowse();
		Cursor cursor = getContentResolver().query(data, null, null, null, null);
		cursor.moveToFirst();
		try {
			((WordbookBrowseListFragment) getFragmentManager().findFragmentById(R.id.item_list_container)).selectItem(Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow("_id"))));
		} catch (IllegalArgumentException e) {
			Log.e(TAG, "Failed to retrieve result from database.");
			throw e;
		}
	}

	void search(String query) {
		String[] columns = new String[]{"_id"};
		String[] selectionArgs = new String[]{query, query, query, query};
		Log.d("ThangTB", "searching..." + query);
		Cursor cursor = getContentResolver().query(WordbookProvider.CONTENT_URI, columns, "LOWER(balochi) LIKE LOWER(?) OR LOWER(urdu) LIKE LOWER(?) OR LOWER(english) LIKE LOWER(?) OR LOWER(pronunciation) LIKE LOWER(?)", selectionArgs, "_id ASC");
		if (cursor.moveToFirst()) {
			String id = cursor.getString(0);
			ensureModeIsWordbookBrowse();
			((WordbookBrowseListFragment) getFragmentManager().findFragmentById(R.id.item_list_container)).selectItem(Integer.parseInt(id));
		} else {
			Toast.makeText(getApplicationContext(), getString(R.string.toast_search_no_results), 1).show();
		}
		cursor.close();
	}

	private void ensureModeIsWordbookBrowse() {
		if (!this.mMode.equals(Mode.WORDBOOK_BROWSE)) {
			switchToWordbookBrowse();
			getFragmentManager().executePendingTransactions();
		}
	}

	private void switchToMode(Mode mode) {
		switch (mode.ordinal()) {
			case 1:
				switchToWordbookBrowse();
				break;
			case 2:
				switchToWordbookFavorites();
				break;
			case 3:
				switchToWordbookHistory();
				break;
			case 4:
				switchToSubdictBrowse();
				break;
			case 5:
				switchToSubdictBookmarks();
				break;
			default:
				throw new IllegalArgumentException("Invalid mode");
		}
		checkTabletDisplayMode();
	}

	private void switchToWordbookBrowse() {
		this.mMode = Mode.WORDBOOK_BROWSE;
		this.mTitle = getString(R.string.title_wordbook);
		this.mSubtitle = getString(R.string.title_wordbook_browse);
		restoreActionBar();
		swapInFragments(new WordbookBrowseListFragment(), new WordbookDetailFragment());
		ensureNavDrawerSelection(Mode.WORDBOOK_BROWSE);
	}

	private void switchToWordbookFavorites() {
		this.mMode = Mode.WORDBOOK_FAVORITES;
		this.mTitle = getString(R.string.title_wordbook);
		this.mSubtitle = getString(R.string.title_wordbook_favorites);
		restoreActionBar();
		swapInFragments(new WordbookFavoritesListFragment(), new WordbookDetailFragment());
		ensureNavDrawerSelection(Mode.WORDBOOK_FAVORITES);
	}

	private void switchToWordbookHistory() {
		this.mMode = Mode.WORDBOOK_HISTORY;
		this.mTitle = getString(R.string.title_wordbook);
		this.mSubtitle = getString(R.string.title_wordbook_history);
		restoreActionBar();
		swapInFragments(new WordbookHistoryListFragment(), new WordbookDetailFragment());
		ensureNavDrawerSelection(Mode.WORDBOOK_HISTORY);
	}

	private void switchToSubdictBrowse() {
		this.mMode = Mode.SYNTAX_BROWSE;
		this.mTitle = getString(R.string.title_subdict);
		restoreActionBar();
		swapInFragments(new SubdictBrowseListFragment(), new SubdictDetailFragment());
		ensureNavDrawerSelection(Mode.SYNTAX_BROWSE);
	}

	private void switchToSubdictBookmarks() {
		this.mMode = Mode.SYNTAX_BOOKMARKS;
		this.mTitle = getString(R.string.title_subdict);
		restoreActionBar();
		swapInFragments(new SubdictBookmarksListFragment(), new SubdictDetailFragment());
		ensureNavDrawerSelection(Mode.SYNTAX_BOOKMARKS);
	}

	private void ensureNavDrawerSelection(Mode mode) {
		if (this.mNavigationDrawerFragment != null) {
			this.mNavigationDrawerFragment.setCurrentSelectedPosition(mode.getPosition());
		}
	}

	private void swapInFragments(Fragment listFragment, Fragment detailFragment) {
		if (this.mTwoPane) {
			FragmentTransaction transaction = getFragmentManager().beginTransaction();
			transaction.replace(R.id.item_list_container, listFragment);
			transaction.replace(R.id.item_detail_container, detailFragment);
			transaction.commit();
			return;
		}
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		transaction.replace(R.id.item_list_container, listFragment);
		transaction.commit();
	}

	public boolean isTwoPane() {
		return this.mTwoPane;
	}

	private void setWordbookFavoriteIcon(Menu menu) {
		AbstractWordbookListFragment fragment = (AbstractWordbookListFragment) getFragmentManager().findFragmentById(R.id.item_list_container);
		MenuItem addFavorite = menu.findItem(R.id.action_add_favorite);
		MenuItem sound = menu.findItem(R.id.action_sound);
		MenuItem removeFavorite = menu.findItem(R.id.action_remove_favorite);
		if (this.mTwoPane) {
			sound.setVisible(true);
		} else {
			sound.setVisible(false);
		}
		if (fragment.nothingIsSelected() || !this.mTwoPane) {
			addFavorite.setVisible(false);
			removeFavorite.setVisible(false);
		} else if (fragment.selectedWordIsFavorite()) {
			addFavorite.setVisible(false);
			removeFavorite.setVisible(true);
		} else {
			addFavorite.setVisible(true);
			removeFavorite.setVisible(false);
		}
	}

	private void setSubdictBookmarkIcon(Menu menu) {
		AbstractSubdictListFragment fragment = (AbstractSubdictListFragment) getFragmentManager().findFragmentById(R.id.item_list_container);
		MenuItem addBookmark = menu.findItem(R.id.action_add_bookmark);
		MenuItem removeBookmark = menu.findItem(R.id.action_remove_bookmark);
		if (fragment.nothingIsSelected() || !this.mTwoPane) {
			addBookmark.setVisible(false);
			removeBookmark.setVisible(false);
		} else if (fragment.selectedSectionIsBookmarked()) {
			addBookmark.setVisible(false);
			removeBookmark.setVisible(true);
		} else {
			addBookmark.setVisible(true);
			removeBookmark.setVisible(false);
		}
	}

	private void sendFeedback() {
		Intent intent = new Intent("android.intent.action.SENDTO", Uri.fromParts("mailto", getString(R.string.feedback_email), null));
		intent.putExtra("android.intent.extra.SUBJECT", getString(R.string.feedback_subject));
		startActivity(Intent.createChooser(intent, getString(R.string.feedback_intent_chooser)));
	}

	private void displayHelp() {
		new HelpDialogFragment().show(getFragmentManager(), "help");
	}

	public Mode getMode() {
		return this.mMode;
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
