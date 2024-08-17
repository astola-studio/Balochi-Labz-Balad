package dd.astolastudio.balochidictionary.data.appdata;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class SubdictBookmarksProvider extends ContentProvider {
	public static final String AUTHORITY = "dd.astolastudio.balochidictionary.data.appdata.SubdictBookmarksProvider";
	public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.astolastudio.balochi.dictionarypro";
	public static final Uri CONTENT_URI = Uri.parse("content://dd.astolastudio.balochidictionary.data.appdata.SubdictBookmarksProvider/appData");
	public static final String CONTENT_WORD_TYPE = "vnd.android.cursor.itemvnd.astolastudio.balochi.dictionarypro";
	private static final int SECTIONS = 0;
	private static final int SECTION_ID = 1;
	private static final UriMatcher sMatcher = buildUriMatcher();
	private SQLiteDatabase mDatabase;

	private static UriMatcher buildUriMatcher() {
		UriMatcher matcher = new UriMatcher(-1);
		matcher.addURI(AUTHORITY, "appData", SECTIONS);
		matcher.addURI(AUTHORITY, "appData/#", SECTION_ID);
		return matcher;
	}

	public boolean onCreate() {
		this.mDatabase = new AppDataDbHelper(getContext()).getWritableDatabase();
		return true;
	}

	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		switch (sMatcher.match(uri)) {
			case SECTIONS /*0*/:
				return searchSections(uri, projection, selection, selectionArgs, sortOrder);
			case SECTION_ID /*1*/:
				return getSection(uri);
			default:
				throw new IllegalArgumentException("Unknown URI: " + uri);
		}
	}

	private Cursor searchSections(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		queryBuilder.setTables("subdict_bookmarks");
		Cursor cursor = queryBuilder.query(this.mDatabase, projection, selection, selectionArgs, null, null, sortOrder);
		cursor.setNotificationUri(getContext().getContentResolver(), uri);
		return cursor;
	}

	private Cursor getSection(Uri uri) {
		String[] projection = new String[]{"_id", "subdict_id", "subdict_section"};
		String[] selectionArgs = new String[SECTION_ID];
		selectionArgs[SECTIONS] = uri.getLastPathSegment();
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		queryBuilder.setTables("subdict_bookmarks");
		return queryBuilder.query(this.mDatabase, projection, "subdict_id = ?", selectionArgs, null, null, null);
	}

	public String getType(Uri uri) {
		switch (sMatcher.match(uri)) {
			case SECTIONS /*0*/:
				return CONTENT_TYPE;
			case SECTION_ID /*1*/:
				return CONTENT_WORD_TYPE;
			default:
				throw new IllegalArgumentException("Unknown URI: " + uri);
		}
	}

	public Uri insert(Uri uri, ContentValues values) {
		Uri resultUri = ContentUris.withAppendedId(CONTENT_URI, this.mDatabase.insert("subdict_bookmarks", "subdict_id", values));
		getContext().getContentResolver().notifyChange(resultUri, null);
		return resultUri;
	}

	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		throw new UnsupportedOperationException();
	}

	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int affected;
		switch (sMatcher.match(uri)) {
			case SECTIONS /*0*/:
				affected = this.mDatabase.delete("subdict_bookmarks", selection, selectionArgs);
				break;
			case SECTION_ID /*1*/:
				affected = this.mDatabase.delete("subdict_bookmarks", "_id=" + ContentUris.parseId(uri) + " AND (" + selection + ")", selectionArgs);
				break;
			default:
				throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return affected;
	}
}
