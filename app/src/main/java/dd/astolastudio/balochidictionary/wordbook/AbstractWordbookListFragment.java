package dd.astolastudio.balochidictionary.wordbook;

import android.database.Cursor;
import dd.astolastudio.balochidictionary.AbstractListFragment;
import dd.astolastudio.balochidictionary.data.appdata.AppDataContract.WordbookFavorites;

public abstract class AbstractWordbookListFragment extends AbstractListFragment {
	private static final int NO_SELECTION = -1;
	protected int mSelectedWordbookId = NO_SELECTION;

	protected abstract void setSelectedWordbookItemId(int i);

	public boolean nothingIsSelected() {
		return NO_SELECTION == this.mSelectedWordbookId;
	}

	public boolean selectedWordIsFavorite() {
		String[] columns = new String[]{"_id"};
		String[] selectionArgs = new String[]{Integer.toString(this.mSelectedWordbookId)};
		Cursor cursor = getActivity().getContentResolver().query(WordbookFavorites.CONTENT_URI, columns, "wordbookID = ?", selectionArgs, null);
		boolean result = false;
		if (cursor.getCount() > 0) {
			result = true;
		}
		cursor.close();
		return result;
	}

	public int getSelectedWordbookId() {
		return this.mSelectedWordbookId;
	}
}
