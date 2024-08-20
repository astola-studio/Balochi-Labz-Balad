package com.readystatesoftware.sqliteasset;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.zip.ZipInputStream;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper.SQLiteAssetException;

public class SQLiteAssetHelper extends SQLiteOpenHelper {
	private static final String ASSET_DB_PATH = "databases";
	private static final String TAG = SQLiteAssetHelper.class.getSimpleName();
	private String mAssetPath;
	private final Context mContext;
	private SQLiteDatabase mDatabase;
	private String mDatabasePath;
	private final CursorFactory mFactory;
	private int mForcedUpgradeVersion;
	private boolean mIsInitializing;
	private final String mName;
	private final int mNewVersion;
	private String mUpgradePathFormat;

	public static class SQLiteAssetException extends SQLiteException {
		public SQLiteAssetException(String error) {
			super(error);
		}
	}

	public SQLiteAssetHelper(Context context, String name, String storageDirectory, CursorFactory factory, int version) {
		super(context, name, factory, version);
		this.mDatabase = null;
		this.mIsInitializing = false;
		this.mForcedUpgradeVersion = 0;
		if (version < 1) {
			throw new IllegalArgumentException("Version must be >= 1, was " + version);
		} else if (name == null) {
			throw new IllegalArgumentException("Database name cannot be null");
		} else {
			this.mContext = context;
			this.mName = name;
			this.mFactory = factory;
			this.mNewVersion = version;
			this.mAssetPath = "databases/" + name;
			if (storageDirectory != null) {
				this.mDatabasePath = storageDirectory;
			} else {
				this.mDatabasePath = context.getApplicationInfo().dataDir + "/databases";
			}
			this.mUpgradePathFormat = "databases/" + name + "_upgrade_%s-%s.sql";
		}
	}

	public SQLiteAssetHelper(Context context, String name, CursorFactory factory, int version) {
		this(context, name, null, factory, version);
	}

	public synchronized SQLiteDatabase getWritableDatabase() {
		SQLiteDatabase sQLiteDatabase;
		if (this.mDatabase != null && this.mDatabase.isOpen() && !this.mDatabase.isReadOnly()) {
			sQLiteDatabase = this.mDatabase;
		} else if (this.mIsInitializing) {
			throw new IllegalStateException("getWritableDatabase called recursively");
		} else {
			sQLiteDatabase = null;
			try {
				this.mIsInitializing = true;
				sQLiteDatabase = createOrOpenDatabase(false);
				int version = sQLiteDatabase.getVersion();
				if (version != 0 && version < this.mForcedUpgradeVersion) {
					sQLiteDatabase = createOrOpenDatabase(true);
					sQLiteDatabase.setVersion(this.mNewVersion);
					version = sQLiteDatabase.getVersion();
				}
				if (version != this.mNewVersion) {
					sQLiteDatabase.beginTransaction();
					if (version == 0) {
						onCreate(sQLiteDatabase);
					} else {
						if (version > this.mNewVersion) {
							Log.w(TAG, "Can't downgrade read-only database from version " + version + " to " + this.mNewVersion + ": " + sQLiteDatabase.getPath());
						}
						onUpgrade(sQLiteDatabase, version, this.mNewVersion);
					}
					sQLiteDatabase.setVersion(this.mNewVersion);
					sQLiteDatabase.setTransactionSuccessful();
					sQLiteDatabase.endTransaction();
				}
				onOpen(sQLiteDatabase);
				this.mIsInitializing = false;
				if (true) {
					if (this.mDatabase != null) {
						try {
							this.mDatabase.close();
						} catch (Exception e) {
						}
					}
					this.mDatabase = sQLiteDatabase;
				} else if (sQLiteDatabase != null) {
					sQLiteDatabase.close();
				}
			} catch (Throwable th) {
				this.mIsInitializing = false;
				if (false) {
					if (this.mDatabase != null) {
						try {
							this.mDatabase.close();
						} catch (Exception e2) {
						}
					}
					this.mDatabase = sQLiteDatabase;
				} else if (sQLiteDatabase != null) {
					sQLiteDatabase.close();
				}
			}
		}
		return sQLiteDatabase;
	}

	@Override
	public void onOpen(SQLiteDatabase db) {
		Log.d(TAG, "onOpen("+db+")");
		super.onOpen(db);
	}

	public synchronized SQLiteDatabase getReadableDatabase() {
		SQLiteDatabase sQLiteDatabase;
		SQLiteDatabase db = null;
		
		if (this.mDatabase != null && this.mDatabase.isOpen()) {
			sQLiteDatabase = this.mDatabase;
		} else if (this.mIsInitializing) {
			throw new IllegalStateException("getReadableDatabase called recursively");
		} else {
			try {
				sQLiteDatabase = getWritableDatabase();
			} catch (SQLiteException e) {
				if (this.mName == null) {
					throw e;
				}
				
				Log.e(TAG, "Couldn't open " + this.mName + " for writing (will try read-only):", e);
				db = null;
				this.mIsInitializing = true;
				String path = this.mContext.getDatabasePath(this.mName).getPath();
				db = SQLiteDatabase.openDatabase(path, this.mFactory, 1);
				if (db.getVersion() != this.mNewVersion) {
					throw new SQLiteException("Can't upgrade read-only database from version " + db.getVersion() + " to " + this.mNewVersion + ": " + path);
				}
				onOpen(db);
				String str = "Opened " + this.mName + " in read-only mode";
				Log.w(TAG, str);
				this.mDatabase = db;
				sQLiteDatabase = this.mDatabase;
				this.mIsInitializing = false;
				if (!(db == null || db == this.mDatabase)) {
					db.close();
				}
			} finally {
				this.mIsInitializing = false;
				if (!(db == null || db == this.mDatabase)) {
					db.close();
				}
			}
		}
		return sQLiteDatabase;
	}

	public synchronized void close() {
		if (this.mIsInitializing) {
			throw new IllegalStateException("Closed during initialization");
		} else if (this.mDatabase != null && this.mDatabase.isOpen()) {
			this.mDatabase.close();
			this.mDatabase = null;
		}
	}

	public final void onConfigure(SQLiteDatabase db) {
	}

	public final void onCreate(SQLiteDatabase db) {
	}

	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(TAG, "Upgrading database " + this.mName + " from version " + oldVersion + " to " + newVersion + "...");
		ArrayList<String> paths = new ArrayList();
		getUpgradeFilePaths(oldVersion, newVersion - 1, newVersion, paths);
		if (paths.isEmpty()) {
			Log.e(TAG, "no upgrade script path from " + oldVersion + " to " + newVersion);
			throw new SQLiteAssetException("no upgrade script path from " + oldVersion + " to " + newVersion);
		}
		Collections.sort(paths, new VersionComparator());
		Iterator it = paths.iterator();
		while (it.hasNext()) {
			String path = (String) it.next();
			try {
				Log.w(TAG, "processing upgrade: " + path);
				String sql = Utils.convertStreamToString(this.mContext.getAssets().open(path));
				if (sql != null) {
					for (String cmd : Utils.splitSqlScript(sql, ';')) {
						if (cmd.trim().length() > 0) {
							db.execSQL(cmd);
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		Log.w(TAG, "Successfully upgraded database " + this.mName + " from version " + oldVersion + " to " + newVersion);
	}

	public final void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

	@Deprecated
	public void setForcedUpgradeVersion(int version) {
		setForcedUpgrade(version);
	}

	public void setForcedUpgrade(int version) {
		this.mForcedUpgradeVersion = version;
	}

	public void setForcedUpgrade() {
		setForcedUpgrade(this.mNewVersion);
	}

	private SQLiteDatabase createOrOpenDatabase(boolean force) throws SQLiteAssetException {
		SQLiteDatabase db = null;
		if (new File(this.mDatabasePath + "/" + this.mName).exists()) {
			if (force) {
				Log.w(TAG, "forcing database upgrade!");
				copyDatabaseFromAssets();
			}
			db = returnDatabase();
		}
		if (db == null) {
			copyDatabaseFromAssets();
			return returnDatabase();
		}
		return db;
	}

	private SQLiteDatabase returnDatabase() {
		try {
			SQLiteDatabase db = SQLiteDatabase.openDatabase(this.mDatabasePath + "/" + this.mName, this.mFactory, 0);
			Log.i(TAG, "successfully opened database " + this.mName);
			return db;
		} catch (SQLiteException e) {
			Log.w(TAG, "could not open database " + this.mName + " - " + e.getMessage());
			return null;
		}
	}

	private void copyDatabaseFromAssets() throws SQLiteAssetException {
		InputStream is;
		Log.w(TAG, "copying database from assets...");
		String path = this.mAssetPath;
		String dest = this.mDatabasePath + "/" + this.mName;
		if(new File(dest).exists()){
			Log.d(TAG, mName+" exists, so skipping extraction!");
			return;
		}
		boolean isZip = false;
		try {
			is = this.mContext.getAssets().open(path);
		} catch (IOException e) {
			try {
				is = this.mContext.getAssets().open(path + ".zip");
				isZip = true;
			} catch (IOException e2) {
				try {
					is = this.mContext.getAssets().open(path + ".gz");
				} catch (IOException e3) {
					SQLiteAssetException se = new SQLiteAssetException("Missing " + this.mAssetPath + " file (or .zip, .gz archive) in assets, or target folder not writable");
					se.setStackTrace(e3.getStackTrace());
					throw se;
				}
			}
		}
		try {
			File f = new File(this.mDatabasePath + "/");
			if (!f.exists()) {
				f.mkdir();
			}
			if (isZip) {
				ZipInputStream zis = Utils.getFileFromZip(is);
				if (zis == null) {
					throw new SQLiteAssetException("Archive is missing a SQLite database file");
				}
				Utils.writeExtractedFileToDisk(zis, new FileOutputStream(dest));
			} else {
				Utils.writeExtractedFileToDisk(is, new FileOutputStream(dest));
			}
			Log.w(TAG, "database copy complete");
		} catch (IOException e4) {
			SQLiteAssetHelper.SQLiteAssetException se = new SQLiteAssetException("Unable to write " + dest + " to data directory");
			se.setStackTrace(e4.getStackTrace());
			throw se;
		}
	}

	private InputStream getUpgradeSQLStream(int oldVersion, int newVersion) {
		String path = String.format(this.mUpgradePathFormat, new Object[]{Integer.valueOf(oldVersion), Integer.valueOf(newVersion)});
		try {
			return this.mContext.getAssets().open(path);
		} catch (IOException e) {
			Log.w(TAG, "missing database upgrade script: " + path);
			return null;
		}
	}

	private void getUpgradeFilePaths(int baseVersion, int start, int end, ArrayList<String> paths) {
		int a;
		int b;
		if (getUpgradeSQLStream(start, end) != null) {
			paths.add(String.format(this.mUpgradePathFormat, new Object[]{Integer.valueOf(start), Integer.valueOf(end)}));
			a = start - 1;
			b = start;
		} else {
			a = start - 1;
			b = end;
		}
		if (a >= baseVersion) {
			getUpgradeFilePaths(baseVersion, a, b, paths);
		}
	}
}
