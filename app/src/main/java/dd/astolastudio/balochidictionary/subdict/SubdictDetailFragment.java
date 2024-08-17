package dd.astolastudio.balochidictionary.subdict;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import dd.astolastudio.balochidictionary.AbstractDetailFragment;
import dd.astolastudio.balochidictionary.LangTextView;
import dd.astolastudio.balochidictionary.data.appdata.AppDataContract.SubdictBookmarks;
import dd.astolastudio.balochidictionary.data.subdict.SubdictContract;
import dd.astolastudio.balochidictionary.R;

public class SubdictDetailFragment extends AbstractDetailFragment {
	public static final String ARG_XML = "xml";
	public static final String TAG = "SubdictDetailFragment";
	private boolean mBlank = false;
	String strHtml = "";

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() == null || !getArguments().containsKey(ARG_XML)) {
			this.mBlank = true;
		} else {
			this.strHtml = getArguments().getString(ARG_XML);
		}
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_item_detail, container, false);
		if (!this.mBlank) {
			Spanned html = Html.fromHtml(this.strHtml + getString(R.string.subdict_footer));
			LangTextView textView = (LangTextView) rootView.findViewById(R.id.item_detail);
			textView.setMovementMethod(LinkMovementMethod.getInstance());
			textView.setText(html);
		}
		return rootView;
	}

	private String getSectionFromSubdictId(int id) {
		String[] projection = new String[]{"section"};
		String[] selectionArgs = new String[]{Integer.toString(id)};
		Cursor cursor = getActivity().getContentResolver().query(SubdictContract.CONTENT_URI, projection, "_id = ?", selectionArgs, null);
		if (cursor.moveToFirst()) {
			return cursor.getString(0);
		}
		throw new IllegalArgumentException("Invalid subdict ID: " + id);
	}

	protected void addSubdictBookmark(int subdictId, String section) {
		ContentValues values = new ContentValues();
		values.put("subdict_id", Integer.valueOf(subdictId));
		values.put("subdict_section", section);
		getActivity().getContentResolver().insert(SubdictBookmarks.CONTENT_URI, values);
		getActivity().invalidateOptionsMenu();
		displayToast(getString(R.string.toast_bookmark_added));
	}

	protected void removeSubdictBookmark(int subdictId) {
		String[] selectionArgs = new String[]{Integer.toString(subdictId)};
		getActivity().getContentResolver().delete(SubdictBookmarks.CONTENT_URI, "subdict_id = ?", selectionArgs);
		getActivity().invalidateOptionsMenu();
		displayToast(getString(R.string.toast_bookmark_removed));
	}

	public void addSubdictBookmark() {
		int subdictId = ((AbstractSubdictListFragment) getActivity().getFragmentManager().findFragmentById(R.id.item_list_container)).getSelectedSubdictId();
		addSubdictBookmark(subdictId, getSectionFromSubdictId(subdictId));
	}

	public void removeSubdictBookmark() {
		removeSubdictBookmark(((AbstractSubdictListFragment) getActivity().getFragmentManager().findFragmentById(R.id.item_list_container)).getSelectedSubdictId());
	}
}
