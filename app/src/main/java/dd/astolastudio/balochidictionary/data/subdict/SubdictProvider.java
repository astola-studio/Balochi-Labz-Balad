package dd.astolastudio.balochidictionary.data.subdict;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class SubdictProvider extends ContentProvider {
	
	public static final String AUTHORITY = "dd.astolastudio.balochidictionary.data.subdict.SubdictProvider";
	public static final String CONTENT_SECTION_TYPE = "vnd.android.cursor.itemvnd.astolastudio.balochi.dictionarypro";
	public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.astolastudio.balochi.dictionarypro";
	public static final Uri CONTENT_URI = Uri.parse("content://dd.astolastudio.balochidictionary.data.subdict.SubdictProvider/subdict");
	private static final int SECTIONS = 0;
	private static final int SECTION_ID = 1;
	private static final UriMatcher sMatcher = buildUriMatcher();
	private SQLiteDatabase mDatabase = null;
	private SubdictHelper mHelper;

	private static UriMatcher buildUriMatcher() {
		UriMatcher matcher = new UriMatcher(-1);
		matcher.addURI(AUTHORITY, "subdict", SECTIONS);
		matcher.addURI(AUTHORITY, "subdict/#", SECTION_ID);
		return matcher;
	}

	public boolean onCreate() {
		this.mHelper = new SubdictHelper(getContext());
		return true;
	}

	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		getReadableDatabase();
		switch (sMatcher.match(uri)) {
			case SECTIONS /*0*/:
				return searchSections(uri, projection, selection, selectionArgs);
			case SECTION_ID /*1*/:
				return getSection(uri);
			default:
				throw new IllegalArgumentException("Unkown URI: " + uri);
		}
	}

	private Cursor searchSections(Uri uri, String[] projection, String selection, String[] selectionArgs) {
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		queryBuilder.setTables("subdict");
		Cursor cursor = queryBuilder.query(this.mDatabase, projection, selection, selectionArgs, null, null, null);
		cursor.setNotificationUri(getContext().getContentResolver(), uri);
		return cursor;
	}

	private Cursor getSection(Uri uri) {
		String[] projection = new String[]{"_id", "chapter", "section", "xml"};
		String[] selectionArgs = new String[SECTION_ID];
		selectionArgs[SECTIONS] = uri.getLastPathSegment();
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		queryBuilder.setTables("subdict");
		Cursor cursor = queryBuilder.query(this.mDatabase, projection, "_id = ?", selectionArgs, null, null, null);
		return cursor;
	}

	public String getType(Uri uri) {
		switch (sMatcher.match(uri)) {
			case SECTIONS /*0*/:
				return CONTENT_TYPE;
			case SECTION_ID /*1*/:
				return CONTENT_SECTION_TYPE;
			default:
				throw new IllegalArgumentException("Unknown URI: " + uri);
		}
	}

	public Uri insert(Uri uri, ContentValues values) {
		throw new UnsupportedOperationException();
	}

	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		throw new UnsupportedOperationException();
	}

	public int delete(Uri uri, String selection, String[] selectionArgs) {
		throw new UnsupportedOperationException();
	}

	public void getReadableDatabase() {
		if (this.mDatabase == null) {
			this.mDatabase = this.mHelper.getReadableDatabase();
		}
	}
}
