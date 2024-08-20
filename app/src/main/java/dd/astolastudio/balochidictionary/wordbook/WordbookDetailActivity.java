package dd.astolastudio.balochidictionary.wordbook;

import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import androidx.appcompat.app.ActionBar;
import androidx.core.app.NavUtils;
import dd.astolastudio.balochidictionary.R;
import dd.astolastudio.balochidictionary.AbstractDetailActivity;
import dd.astolastudio.balochidictionary.MainActivity;
import dd.astolastudio.balochidictionary.SoundPlayer;
import dd.astolastudio.balochidictionary.data.appdata.AppDataContract.WordbookFavorites;

public class WordbookDetailActivity extends AbstractDetailActivity {
	
	public static final String ARG_SOUND = "sound";
	public static final String ARG_WORD = "balochi";
	public static final String ARG_WORDBOOK_ID = "wordbook_id";
	private String mSound;
	private String mWord;
	private int mWordbookId;
	SoundPlayer soundPlayer;

	private WordbookDetailFragment fragment;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		this.mWordbookId = intent.getIntExtra(ARG_WORDBOOK_ID, -1);
		this.mWord = intent.getStringExtra(ARG_WORD);
		this.mSound = intent.getStringExtra(ARG_SOUND);
		this.mTitle = getString(R.string.title_wordbook);
		this.soundPlayer = new SoundPlayer(getApplicationContext());
		if (savedInstanceState == null) {
			Bundle arguments = new Bundle();
			for(String s:new String[]{
				"balochi", "english", "urdu", "pronunciation"
			}){
				arguments.putString(s, getIntent().getStringExtra(s));
			}
			arguments.putInt(ARG_WORDBOOK_ID, mWordbookId);
			fragment = new WordbookDetailFragment();
			fragment.setArguments(arguments);
			getFragmentManager().beginTransaction().replace(R.id.item_detail_container, fragment).commitAllowingStateLoss();
		}else{
			fragment = (WordbookDetailFragment) getFragmentManager()
				.findFragmentById(R.id.item_detail_container);
		}
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.wordbook_detail_activity_menu, menu);
		setWordbookFavoriteIcon(menu);
		restoreActionBar();
		return super.onCreateOptionsMenu(menu);
	}

	public boolean onPrepareOptionsMenu(Menu menu) {
		setWordbookFavoriteIcon(menu);
		restoreActionBar();
		return super.onPrepareOptionsMenu(menu);
	}

	private void setWordbookFavoriteIcon(Menu menu) {
		MenuItem addFavorite = menu.findItem(R.id.action_add_favorite);
		MenuItem removeFavorite = menu.findItem(R.id.action_remove_favorite);
		if (isFavorite(this.mWordbookId)) {
			addFavorite.setVisible(false);
			removeFavorite.setVisible(true);
			return;
		}
		addFavorite.setVisible(true);
		removeFavorite.setVisible(false);
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				NavUtils.navigateUpTo(this, new Intent(this, MainActivity.class));
				return true;
			case R.id.action_sound:
				if (fragment == null) {
					return true;
				}
				fragment.playSound();
				return true;
			case R.id.action_add_favorite:
				((WordbookDetailFragment) getFragmentManager().findFragmentById(R.id.item_detail_container)).addWordbookFavorite(this.mWordbookId, this.mWord);
				return true;
			case R.id.action_remove_favorite:
				((WordbookDetailFragment) getFragmentManager().findFragmentById(R.id.item_detail_container)).removeWordbookFavorite(this.mWordbookId);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	private boolean isFavorite(int wordbookId) {
		String[] columns = new String[]{"_id"};
		String[] selectionArgs = new String[]{Integer.toString(wordbookId)};
		Cursor cursor = getContentResolver().query(WordbookFavorites.CONTENT_URI, columns, "wordbookID = ?", selectionArgs, null);
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
