package dd.astolastudio.balochidictionary.wordbook;

import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import dd.astolastudio.balochidictionary.R;
import dd.astolastudio.balochidictionary.AbstractListFragment.Callbacks;
import dd.astolastudio.balochidictionary.data.wordbook.WordbookContract;

public class WordbookBrowseListFragment extends AbstractWordbookListFragment implements LoaderCallbacks<Cursor> {
	public static final String NAME = "wordbook_browse";
	private static final String ORDER_BY = "balochi ASC";
	private static final String[] PROJECTION = new String[]{"_id", "balochi", "english", "urdu", "pronunciation"};
	private static final String SELECTION = "";
	private static final String[] SELECTION_ARGS = new String[0];
	private static final String TAG = "WordbookBrowseListFragment";
	private static final Callbacks sDummyCallbacks = new Callbacks() {
		public void onItemSelected(String fragmentName) {
		}
	};
	private int mActivatedPosition = -1;
	private SimpleCursorAdapter mAdapter;
	private Callbacks mCallbacks = sDummyCallbacks;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.mAdapter = new SimpleCursorAdapter(getActivity(), R.layout.lang_simple_list_item_activated_1, null, new String[]{"balochi"}, new int[]{android.R.id.text1}, 0);
		setListAdapter(this.mAdapter);
		getLoaderManager().initLoader(0, null, this);
	}

	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		return new CursorLoader(getActivity(), WordbookContract.CONTENT_URI, PROJECTION, SELECTION, SELECTION_ARGS, ORDER_BY);
	}

	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		this.mAdapter.swapCursor(data);
	}

	public void onLoaderReset(Loader<Cursor> loader) {
		this.mAdapter.swapCursor(null);
	}

	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		getListView().setFastScrollEnabled(true);
		getListView().setFastScrollAlwaysVisible(true);
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
		setSelectedWordbookItemId(((Cursor) this.mAdapter.getItem(position)).getInt(0));
		this.mCallbacks.onItemSelected(NAME);
	}

	protected void setSelectedWordbookItemId(int id) {
		this.mSelectedWordbookId = id;
	}

	public void selectItem(int id) {
		this.mSelectedWordbookId = id;
		int position = id - 1;
		setActivatedPosition(position);
		getListView().setSelection(position);
		this.mCallbacks.onItemSelected(NAME);
	}
}
