package dd.astolastudio.balochidictionary.data.wordbook;

import android.content.Context;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

public class WordbookHelper extends SQLiteAssetHelper {
	private static final int DB_VERSION = 3;

	public WordbookHelper(Context context) {
		super(context, "wordbook", null, DB_VERSION);
		setForcedUpgrade(DB_VERSION);
	}
}
