package dd.astolastudio.balochidictionary.subdict;

import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import dd.astolastudio.balochidictionary.R;
import android.widget.SimpleCursorAdapter;
import dd.astolastudio.balochidictionary.AbstractListFragment.Callbacks;
import dd.astolastudio.balochidictionary.data.appdata.AppDataContract.SubdictBookmarks;

public class SubdictBookmarksListFragment extends AbstractSubdictListFragment implements LoaderCallbacks<Cursor> {
	public static final String NAME = "subdict_bookmarks";
	private static final String[] PROJECTION = new String[]{"_id", "subdict_section", "subdict_id"};
	private static final String SELECTION = "";
	private static final String[] SELECTION_ARGS = new String[0];
	private static final String SORT_ORDER = "subdict_id ASC";
	private static final String STATE_ACTIVATED_POSITION = "activated_position";
	private static final Callbacks sDummyCallbacks = new Callbacks() {
		public void onItemSelected(String fragmentName) {
		}
	};
	private int mActivatedPosition = -1;
	private SimpleCursorAdapter mAdapter;
	private Callbacks mCallbacks = sDummyCallbacks;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.mAdapter = new SimpleCursorAdapter(getActivity(), android.R.layout.simple_list_item_activated_1, null, new String[]{"subdict_section"}, new int[]{android.R.id.text1}, 0);
		setListAdapter(this.mAdapter);
		getLoaderManager().initLoader(0, null, this);
	}

	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		return new CursorLoader(getActivity(), SubdictBookmarks.CONTENT_URI, PROJECTION, SELECTION, SELECTION_ARGS, SORT_ORDER);
	}

	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		this.mAdapter.swapCursor(data);
		setNoItemsView(R.string.subdict_bookmarks_empty_view);
	}

	public void onLoaderReset(Loader<Cursor> loader) {
		this.mAdapter.swapCursor(null);
	}

	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		if (savedInstanceState != null && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
			setActivatedPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION));
		}
	}

	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (activity instanceof Callbacks) {
			this.mCallbacks = (Callbacks) activity;
			return;
		}
		throw new IllegalStateException("Activity must implement fragment's callbacks.");
	}

	public void onDetach() {
		super.onDetach();
		this.mCallbacks = sDummyCallbacks;
	}

	public void onListItemClick(ListView listView, View view, int position, long id) {
		super.onListItemClick(listView, view, position, id);
		setSelectedSubdictItemId(((Cursor) this.mAdapter.getItem(position)).getInt(0));
		this.mCallbacks.onItemSelected(NAME);
	}

	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (this.mActivatedPosition != -1) {
			outState.putInt(STATE_ACTIVATED_POSITION, this.mActivatedPosition);
		}
	}

	protected void setSelectedSubdictItemId(int id) {
		String[] columns = new String[]{"subdict_id"};
		String[] selectionArgs = new String[]{Integer.toString(id)};
		Cursor cursor = getActivity().getContentResolver().query(SubdictBookmarks.CONTENT_URI, columns, "_id = ?", selectionArgs, null);
		if (cursor.moveToFirst()) {
			this.mSelectedSubdictId = cursor.getInt(0);
			return;
		}
		throw new IllegalArgumentException("Invalid ID: " + id);
	}
}
