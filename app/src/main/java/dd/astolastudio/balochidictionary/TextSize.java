package dd.astolastudio.balochidictionary;

enum TextSize {
	SMALL("Small", 14.0f),
	MEDIUM("Medium", 18.0f),
	LARGE("Large", 22.0f);
	
	private final String mName;
	private final float mSize;

	private TextSize(String name, float size) {
		this.mName = name;
		this.mSize = size;
	}

	public static float getScaledPixelSize(String name) {
		for (TextSize tx : values()) {
			if (name.equals(tx.mName)) {
				return tx.mSize;
			}
		}
		throw new IllegalArgumentException("Invalid text size name.");
	}
}
