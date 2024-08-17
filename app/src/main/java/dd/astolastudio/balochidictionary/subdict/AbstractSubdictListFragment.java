package dd.astolastudio.balochidictionary.subdict;

import android.database.Cursor;
import dd.astolastudio.balochidictionary.AbstractListFragment;
import dd.astolastudio.balochidictionary.data.appdata.AppDataContract.SubdictBookmarks;

public abstract class AbstractSubdictListFragment extends AbstractListFragment {
	private static final int NO_SELECTION = -1;
	protected int mSelectedSubdictId = NO_SELECTION;

	protected abstract void setSelectedSubdictItemId(int i);

	public boolean nothingIsSelected() {
		return NO_SELECTION == this.mSelectedSubdictId;
	}

	public boolean selectedSectionIsBookmarked() {
		String[] columns = new String[]{"_id"};
		String[] selectionArgs = new String[]{Integer.toString(this.mSelectedSubdictId)};
		Cursor cursor = getActivity().getContentResolver().query(SubdictBookmarks.CONTENT_URI, columns, "subdict_id = ?", selectionArgs, null);
		boolean result = false;
		if (cursor.getCount() > 0) {
			result = true;
		}
		cursor.close();
		return result;
	}

	public int getSelectedSubdictId() {
		return this.mSelectedSubdictId;
	}
}
