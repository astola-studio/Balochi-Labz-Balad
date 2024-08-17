package dd.astolastudio.balochidictionary;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.TextView;

public abstract class AbstractListFragment extends ListFragment {
	private static final int EMPTY_LIST_SIDE_PADDING = 100;
	private static final String STATE_ACTIVATED_POSITION = "activated_position";
	private int mActivatedPosition = -1;
	protected TextView mEmptyView;

	public interface Callbacks {
		void onItemSelected(String str);
	}

	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		if (((MainActivity) getActivity()).isTwoPane()) {
			setActivateOnItemClick(true);
		}
		if (savedInstanceState != null && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
			setActivatedPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION));
		}
		ProgressBar progressBar = new ProgressBar(getActivity());
		progressBar.setLayoutParams(new LayoutParams(-2, -2, 17));
		progressBar.setIndeterminate(true);
		getListView().setEmptyView(progressBar);
		((ViewGroup) view).addView(progressBar);
		this.mEmptyView = new TextView(getActivity());
		this.mEmptyView.setGravity(17);
		this.mEmptyView.setTextSize(25.0f);
		((ViewGroup) view).addView(this.mEmptyView);
	}

	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (this.mActivatedPosition != -1) {
			outState.putInt(STATE_ACTIVATED_POSITION, this.mActivatedPosition);
		}
	}

	protected void setActivateOnItemClick(boolean activateOnItemClick) {
		int choiceMode;
		if (activateOnItemClick) {
			choiceMode = 1;
		} else {
			choiceMode = 0;
		}
		getListView().setChoiceMode(choiceMode);
	}

	protected void setActivatedPosition(int position) {
		if (position == -1) {
			getListView().setItemChecked(this.mActivatedPosition, false);
		} else {
			getListView().setItemChecked(position, true);
		}
		this.mActivatedPosition = position;
	}

	protected void setNoItemsView(int stringId) {
		getListView().getEmptyView().setVisibility(View.INVISIBLE);
		getListView().setEmptyView(this.mEmptyView);
		this.mEmptyView.setText(stringId);
		this.mEmptyView.setPadding(EMPTY_LIST_SIDE_PADDING, 0, EMPTY_LIST_SIDE_PADDING, 0);
	}
}
