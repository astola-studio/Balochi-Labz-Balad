package dd.astolastudio.balochidictionary.navigationdrawer;

public abstract class AbstractNavigationDrawerItem {
	private int mId;
	private String mLabel;

	public abstract int getType();

	public abstract boolean isCat();

	public abstract boolean isEnabled();

	public abstract boolean isRow();

	public AbstractNavigationDrawerItem(int id, String label) {
		this.mId = id;
		this.mLabel = label;
	}

	public int getId() {
		return this.mId;
	}

	public void setId(int id) {
		this.mId = id;
	}

	public String getLabel() {
		return this.mLabel;
	}

	public void setLabel(String mLabel) {
		this.mLabel = mLabel;
	}
}
