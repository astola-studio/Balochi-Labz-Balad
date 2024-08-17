package dd.astolastudio.balochidictionary.data.appdata;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import dd.astolastudio.balochidictionary.data.wordbook.WordbookContract;

public class AppDataDbHelper extends SQLiteOpenHelper {
	public static final String DATABASE_NAME = "AppData.db";
	public static final int DATABASE_VERSION = 3;
	private static final String SQL_CREATE_SYNTAX_BOOKMARKS_TABLE = "CREATE TABLE subdict_bookmarks (_id INTEGER PRIMARY KEY, subdict_id INTEGER, subdict_section TEXT )";
	private static final String SQL_CREATE_WORDBOOK_FAVORITES_TABLE = "CREATE TABLE wordbook_favorites (_id INTEGER PRIMARY KEY, wordbookID INTEGER, word TEXT)";
	private static final String SQL_CREATE_WORDBOOK_HISTORY_TABLE = "CREATE TABLE wordbook_history (_id INTEGER PRIMARY KEY, wordbookID INTEGER, word TEXT )";
	private static final String SQL_DELETE_WORDBOOK_FAVORITES_TABLE = "DROP TABLE IF EXISTS wordbook_favorites";
	private final Context mContext;

	public AppDataDbHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.mContext = context;
	}

	public void onCreate(SQLiteDatabase db) {
		db.execSQL(SQL_CREATE_WORDBOOK_FAVORITES_TABLE);
		db.execSQL(SQL_CREATE_WORDBOOK_HISTORY_TABLE);
		db.execSQL(SQL_CREATE_SYNTAX_BOOKMARKS_TABLE);
	}

	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		SQLiteDatabase sQLiteDatabase = db;
		Cursor oldData = sQLiteDatabase.query("wordbook_favorites", new String[]{"word"}, null, null, null, null, null, null);
		db.execSQL(SQL_DELETE_WORDBOOK_FAVORITES_TABLE);
		db.execSQL(SQL_CREATE_WORDBOOK_FAVORITES_TABLE);
		ContentResolver resolver = this.mContext.getContentResolver();
		String[] projection = new String[]{"_id"};
		String selection = "langFullWord = ?";
		while (oldData.moveToNext()) {
			String word=oldData.getString(0);
			Cursor wordbookCursor = resolver.query(WordbookContract.CONTENT_URI, projection, selection, new String[]{oldData.getString(0)}, null);
			wordbookCursor.moveToFirst();
			int id = wordbookCursor.getInt(wordbookCursor.getColumnIndexOrThrow("_id"));
			ContentValues values = new ContentValues(2);
			values.put("wordbookID", Integer.valueOf(id));
			values.put("word", word);
			db.insert("wordbook_favorites", null, values);
		}
	}
}
