package dd.astolastudio.balochidictionary.data.appdata;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class CategoryProvider extends ContentProvider {
	private SQLiteDatabase mDatabase;

	public boolean onCreate() {
		this.mDatabase = new AppDataDbHelper(getContext()).getWritableDatabase();
		return true;
	}

	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		return getCat();
	}

	public String getType(Uri uri) {
		return null;
	}

	public Uri insert(Uri uri, ContentValues values) {
		return null;
	}

	public int delete(Uri uri, String selection, String[] selectionArgs) {
		return 0;
	}

	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		return 0;
	}

	private Cursor getCat() {
		String[] projection = new String[]{"name"};
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		queryBuilder.setTables("category");
		return queryBuilder.query(this.mDatabase, projection, null, null, null, null, null);
	}
}
