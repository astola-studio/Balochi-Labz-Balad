package dd.astolastudio.balochidictionary.data.appdata;

import android.net.Uri;
import android.provider.BaseColumns;

public final class AppDataContract {

	public static abstract class Category implements BaseColumns {
		public static final String COLUMN_NAME_CAT_NAME = "name";
		public static final String TABLE_NAME = "category";
	}

	public static abstract class SubdictBookmarks implements BaseColumns {
		public static final String COLUMN_NAME_SYNTAX_ID = "subdict_id";
		public static final String COLUMN_NAME_SYNTAX_SECTION = "subdict_section";
		public static final Uri CONTENT_URI = SubdictBookmarksProvider.CONTENT_URI;
		public static final String TABLE_NAME = "subdict_bookmarks";
	}

	public static abstract class WordbookFavorites implements BaseColumns {
		public static final String COLUMN_NAME_WORD = "balochi";
		public static final String COLUMN_NAME_WORDBOOK_ID = "wordbookID";
		public static final Uri CONTENT_URI = WordbookFavoritesProvider.CONTENT_URI;
		public static final String TABLE_NAME = "wordbook_favorites";
	}

	public static abstract class WordbookHistory implements BaseColumns {
		public static final String COLUMN_NAME_WORD = "balochi";
		public static final String COLUMN_NAME_WORDBOOK_ID = "wordbookID";
		public static final Uri CONTENT_URI = WordbookHistoryProvider.CONTENT_URI;
		public static final String TABLE_NAME = "wordbook_history";
	}

	private AppDataContract() {
	}
}
