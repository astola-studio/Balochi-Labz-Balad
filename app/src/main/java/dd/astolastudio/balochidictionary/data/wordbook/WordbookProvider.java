package dd.astolastudio.balochidictionary.data.wordbook;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

public class WordbookProvider extends ContentProvider {
	public static final String AUTHORITY = "dd.astolastudio.balochidictionary.data.wordbook.WordbookProvider";
	public static final Uri CONTENT_URI = Uri.parse("content://dd.astolastudio.balochidictionary.data.wordbook.WordbookProvider/wordbook");
	public static final String ENTRY_MIME_TYPE = "vnd.android.cursor.item/vnd.astolastudio.balochi.dictionarypro";
	private static final int GET_WORD = 2;
	private static final String LIMIT = "20";
	private static final int SEARCH = 0;
	private static final int SEARCH_SUGGEST = 1;
	public static final String WORDS_MIME_TYPE = "vnd.android.cursor.dir/vnd.astolastudio.balochi.dictionarypro";
	private static final UriMatcher sMatcher = buildUriMatcher();
	private SQLiteDatabase mDatabase = null;
	private WordbookHelper mHelper;

	public static String fixQuery(String str) {
		return !str.contains("%") ? str.concat("%") : str;
	}

	private static UriMatcher buildUriMatcher() {
		UriMatcher matcher = new UriMatcher(-1);
		matcher.addURI(AUTHORITY, "wordbook", SEARCH);
		matcher.addURI(AUTHORITY, "wordbook/#", GET_WORD);
		matcher.addURI(AUTHORITY, "search_suggest_query", SEARCH_SUGGEST);
		matcher.addURI(AUTHORITY, "search_suggest_query/*", SEARCH_SUGGEST);
		return matcher;
	}

	public boolean onCreate() {
		this.mHelper = new WordbookHelper(getContext());
		return true;
	}

	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		getReadableDatabase();
		switch (sMatcher.match(uri)) {
			case SEARCH /*0*/:
				if (selectionArgs != null) {
					return search(uri, projection, selection, selectionArgs, sortOrder);
				}
				throw new IllegalArgumentException("selectionArgs must be provided for the URI: " + uri);
			case SEARCH_SUGGEST /*1*/:
				if (selectionArgs != null) {
					return getSuggestions(selectionArgs[SEARCH]);
				}
				throw new IllegalArgumentException("selectionArgs must be provided for the URI: " + uri);
			case GET_WORD /*2*/:
				return getWord(uri);
			default:
				throw new IllegalArgumentException("Unknown URI: " + uri);
		}
	}

	private Cursor search(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		queryBuilder.setTables("wordbook");
		Cursor cursor = queryBuilder.query(this.mDatabase, projection, selection, selectionArgs, null, null, sortOrder);
		cursor.setNotificationUri(getContext().getContentResolver(), uri);
		return cursor;
	}

	
	private Cursor getSuggestions(String query) {
		query = fixQuery(query);
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		queryBuilder.setTables("wordbook");
		return queryBuilder.query(this.mDatabase, new String[]{"_id as _id", "langFullWord AS suggest_text_1", "langLowercase", "_id AS suggest_intent_data_id"}, "betaSymbols LIKE ? OR betaNoSymbols LIKE ? OR langFullWord LIKE ? OR langLowercase LIKE ?", new String[]{query.toLowerCase(), query.toLowerCase(), query, query.toLowerCase()}, (String)null, (String)null, "langFullWord ASC", "20");
	}
	
	private Cursor getWord(Uri uri) {
		SQLiteQueryBuilder queruBuilder = new SQLiteQueryBuilder();
		queruBuilder.setTables("wordbook");
		return queruBuilder.query(this.mDatabase, new String[]{"_id", "entry", "langNoSymbols", "soundName"}, "_id = ?", new String[]{uri.getLastPathSegment()}, (String)null, (String)null, (String)null);
	}
	public String getType(Uri uri) {
		switch (sMatcher.match(uri)) {
			case SEARCH /*0*/:
				return WORDS_MIME_TYPE;
			case SEARCH_SUGGEST /*1*/:
				return "vnd.android.cursor.dir/vnd.android.search.suggest";
			case GET_WORD /*2*/:
				return ENTRY_MIME_TYPE;
			default:
				throw new IllegalArgumentException("Unknown URI: " + uri);
		}
	}

	public Uri insert(Uri uri, ContentValues values) {
		throw new UnsupportedOperationException();
	}

	public int delete(Uri uri, String selection, String[] selectionArgs) {
		throw new UnsupportedOperationException();
	}

	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		throw new UnsupportedOperationException();
	}

	private void getReadableDatabase() {
		if (this.mDatabase == null) {
			this.mDatabase = this.mHelper.getReadableDatabase();
		}
	}
}
