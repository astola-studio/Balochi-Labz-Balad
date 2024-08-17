package dd.astolastudio.balochidictionary;

public enum Mode {
	CAT(0, "category"),
	WORDBOOK_BROWSE(1, "wordbook_browse"),
	WORDBOOK_FAVORITES(2, "wordbook_favorites"),
	WORDBOOK_HISTORY(3, "wordbook_history"),
	SYNTAX_BROWSE(5, "subdict_browse"),
	SYNTAX_BOOKMARKS(6, "subdict_bookmarks");
	
	private final String mName;
	private final int mPosition;

	private Mode(int position, String name) {
		this.mPosition = position;
		this.mName = name;
	}

	public String toString() {
		return this.mName;
	}

	public String getName() {
		return this.mName;
	}

	public int getPosition() {
		return this.mPosition;
	}

	public static Mode getModeFromPosition(int position) {
		for (Mode m : values()) {
			if (m.mPosition == position) {
				return m;
			}
		}
		return WORDBOOK_BROWSE;
	}

	public static Mode getModeFromName(String name) {
		for (Mode m : values()) {
			if (m.mName.equals(name)) {
				return m;
			}
		}
		return WORDBOOK_BROWSE;
	}

	public boolean isWordbookMode() {
		return equals(WORDBOOK_BROWSE) || equals(WORDBOOK_FAVORITES) || equals(WORDBOOK_HISTORY);
	}

	public boolean isSubdictMode() {
		return equals(SYNTAX_BROWSE) || equals(SYNTAX_BOOKMARKS);
	}

	public boolean isCateMode() {
		return equals(CAT);
	}
}
