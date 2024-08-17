package dd.astolastudio.balochidictionary.navigationdrawer;

import android.content.Context;

public class NavigationDrawerRow extends AbstractNavigationDrawerItem {
	private static final int TYPE = 1;
	private int mCurrentIcon;
	private boolean highlighted;

	public NavigationDrawerRow(int id, String label, int icon, Context context) {
		super(id, label);
		this.mCurrentIcon=icon;
	}

	public boolean isEnabled() {
		return true;
	}

	public boolean isRow() {
		return true;
	}

	public boolean isCat() {
		return false;
	}

	public int getIcon() {
		return this.mCurrentIcon;
	}

	public void setIconHighlighted(boolean highlighted) {
		this.highlighted=highlighted;
	}

	public int getType() {
		return TYPE;
	}
}
