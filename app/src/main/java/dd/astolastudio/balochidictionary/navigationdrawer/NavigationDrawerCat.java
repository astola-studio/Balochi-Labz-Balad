package dd.astolastudio.balochidictionary.navigationdrawer;

import java.util.ArrayList;

public class NavigationDrawerCat extends AbstractNavigationDrawerItem {
	private static final int TYPE = 2;
	private ArrayList<String> mCats;

	public NavigationDrawerCat(int id, String label, ArrayList<String> cats) {
		super(id, label);
		this.mCats = cats;
	}

	public boolean isEnabled() {
		return false;
	}

	public boolean isRow() {
		return false;
	}

	public boolean isCat() {
		return true;
	}

	public int getType() {
		return TYPE;
	}

	public ArrayList<String> getCats() {
		return this.mCats;
	}

	public void setCats(ArrayList<String> mCats) {
		this.mCats = mCats;
	}
}
