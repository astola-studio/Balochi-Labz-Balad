package dd.astolastudio.balochidictionary.data.subdict;

import android.content.Context;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

public class SubdictHelper extends SQLiteAssetHelper {
	private static final int DB_VERSION = 1;

	public SubdictHelper(Context context) {
		super(context, "subdict", null, DB_VERSION);
		setForcedUpgrade(DB_VERSION);
	}
}
