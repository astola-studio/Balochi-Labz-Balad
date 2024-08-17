package dd.astolastudio.balochidictionary.data.subdict;

import android.net.Uri;
import android.provider.BaseColumns;

public final class SubdictContract implements BaseColumns {
	public static final String COLUMN_NAME_CHAPTER = "chapter";
	public static final String COLUMN_NAME_SECTION = "section";
	public static final String COLUMN_NAME_XML = "xml";
	public static final Uri CONTENT_URI = SubdictProvider.CONTENT_URI;
	public static final String DB_NAME = "subdict";
	public static final String TABLE_NAME = "subdict";

	private SubdictContract() {
	}
}
