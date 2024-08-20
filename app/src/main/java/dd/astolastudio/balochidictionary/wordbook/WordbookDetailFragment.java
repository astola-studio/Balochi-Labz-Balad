package dd.astolastudio.balochidictionary.wordbook;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import dd.astolastudio.balochidictionary.AbstractDetailFragment;
import dd.astolastudio.balochidictionary.LangTextView;
import dd.astolastudio.balochidictionary.SoundPlayer;
import dd.astolastudio.balochidictionary.data.appdata.AppDataContract.WordbookFavorites;
import dd.astolastudio.balochidictionary.data.wordbook.WordbookContract;
import dd.astolastudio.balochidictionary.data.wordbook.WordbookEntry;
import dd.astolastudio.balochidictionary.data.wordbook.WordbookXmlParser;
import java.io.ByteArrayInputStream;
import dd.astolastudio.balochidictionary.R;

public class WordbookDetailFragment extends AbstractDetailFragment {
	public static final String TAG = "WordbookDetailFragment";
	public static final String VIEWONWEB_TOOL_EXTRA_KEY = "dd.astolastudio.balochidictionary.wordbook.ViewonwebToolExtraKey";
	private boolean mBlank = true;
	private SoundPlayer soundPlayer;
	String strHtml = "";

	private int mWordbookId;

	public static class NoNetworkConnectionDialogFragment extends DialogFragment {
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			Builder builder = new Builder(getActivity());
			builder.setMessage(R.string.dialog_no_network_connection).setPositiveButton(android.R.string.ok, new OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					NoNetworkConnectionDialogFragment.this.dismiss();
				}
			});
			return builder.create();
		}
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		this.mWordbookId=getArguments() != null? getArguments().getInt("wordbook_id",-1):-1;
		if (getArguments() != null && getArguments().containsKey("balochi")) {
			this.mBlank = false;
			Bundle arguments = getArguments();
			this.strHtml = String.format("<h1>%s</h1></br>%s</br><h1></h1>%s</br><h1></h1>%s", arguments.getString("balochi"), arguments.getString("english"), arguments.getString("urdu"), arguments.getString("pronunciation"));
		}
		this.soundPlayer = new SoundPlayer(getActivity());
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_item_detail, container, false);
		if (!this.mBlank) {
			((LangTextView) rootView.findViewById(R.id.item_detail)).setText(Html.fromHtml(this.strHtml));
		}
		return rootView;
	}

	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
	}

	public void onPrepareOptionsMenu(Menu menu) {
	}

	private boolean viewonwebToolOptionDisabled() {
		return PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean(getString(R.string.pref_viewonweb_tool_key), false);
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_viewonweb_tool:
				displayViewonwebTool();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	protected void addWordbookFavorite(int wordbookId, String word) {
		ContentValues values = new ContentValues();
		values.put("wordbookID", Integer.valueOf(wordbookId));
		values.put("balochi", word);
		getActivity().getContentResolver().insert(WordbookFavorites.CONTENT_URI, values);
		getActivity().invalidateOptionsMenu();
		displayToast(getString(R.string.toast_favorite_added));
	}

	protected void removeWordbookFavorite(int wordbookId) {
		String[] selectionArgs = new String[]{Integer.toString(wordbookId)};
		getActivity().getContentResolver().delete(WordbookFavorites.CONTENT_URI, "wordbookID = ?", selectionArgs);
		getActivity().invalidateOptionsMenu();
		displayToast(getString(R.string.toast_favorite_removed));
	}

	public void playSound() {
		String soundName = getSoundFromWordbookId(mWordbookId);
		if (!soundName.equals("")) {
			this.soundPlayer.playSound(soundName);
		}
	}

	public void addWordbookFavorite() {
		int wordbookId = ((AbstractWordbookListFragment) getActivity().getFragmentManager().findFragmentById(R.id.item_list_container)).getSelectedWordbookId();
		addWordbookFavorite(wordbookId, getWordFromWordbookId(wordbookId));
	}

	public void removeWordbookFavorite() {
		removeWordbookFavorite(((AbstractWordbookListFragment) getActivity().getFragmentManager().findFragmentById(R.id.item_list_container)).getSelectedWordbookId());
	}

	private String getWordFromWordbookId(int id) {
		String[] projection = new String[]{"balochi"};
		String[] selectionArgs = new String[]{Integer.toString(id)};
		Cursor cursor = getActivity().getContentResolver().query(WordbookContract.CONTENT_URI, projection, "_id = ?", selectionArgs, null);
		if (cursor.moveToFirst()) {
			return cursor.getString(0);
		}
		throw new IllegalArgumentException("Invalid wordbook ID: " + id);
	}

	private String getSoundFromWordbookId(int id) {
		return "dummy.mp3";
	}

	private void displayViewonwebTool() {
		if (networkConnectionAvailable()) {
			WordbookEntry mWordbookEntry = null;
			try {
				mWordbookEntry = new WordbookXmlParser().parse(new ByteArrayInputStream(this.strHtml.getBytes()));
			} catch (Exception e) {
				Log.e(TAG, "Error parsing entry: " + e);
				Log.e(TAG, Log.getStackTraceString(e));
			}
			Intent intent = new Intent(getActivity(), ViewonwebToolActivity.class);
			intent.putExtra(VIEWONWEB_TOOL_EXTRA_KEY, mWordbookEntry.getOrth());
			startActivity(intent);
			return;
		}
		displayNoNetworkConnectionError();
	}

	private boolean networkConnectionAvailable() {
		NetworkInfo networkInfo = ((ConnectivityManager) getActivity().getSystemService("connectivity")).getActiveNetworkInfo();
		return networkInfo != null && networkInfo.isConnected();
	}

	private void displayNoNetworkConnectionError() {
		new NoNetworkConnectionDialogFragment().show(getFragmentManager(), null);
	}
}
