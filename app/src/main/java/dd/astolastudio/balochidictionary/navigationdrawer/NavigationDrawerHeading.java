package dd.astolastudio.balochidictionary.navigationdrawer;

public class NavigationDrawerHeading extends AbstractNavigationDrawerItem {
	private static final int TYPE = 0;

	public NavigationDrawerHeading(int id, String label) {
		super(id, label);
	}

	public boolean isEnabled() {
		return false;
	}

	public boolean isRow() {
		return false;
	}

	public boolean isCat() {
		return false;
	}

	public int getType() {
		return 0;
	}
}
