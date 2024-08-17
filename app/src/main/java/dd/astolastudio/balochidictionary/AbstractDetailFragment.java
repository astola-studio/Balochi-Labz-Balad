package dd.astolastudio.balochidictionary;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public abstract class AbstractDetailFragment extends Fragment {
	private static final String KEY_SCROLL_Y = "scroll_y";
	protected Toast mToast;

	@SuppressLint({"ShowToast"})
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.mToast = Toast.makeText(getActivity(), null, 0);
	}

	public void onViewCreated(View view, Bundle savedInstanceState) {
		if (savedInstanceState != null && savedInstanceState.containsKey(KEY_SCROLL_Y)) {
			getActivity().findViewById(R.id.detail_scroll_view).setScrollY(savedInstanceState.getInt(KEY_SCROLL_Y));
		}
	}

	public void onSaveInstanceState(Bundle outState) {
		View scrollView = getActivity().findViewById(R.id.detail_scroll_view);
		if (scrollView != null) {
			outState.putInt(KEY_SCROLL_Y, scrollView.getScrollY());
		}
	}

	protected void displayToast(String message) {
		this.mToast.setText(message);
		this.mToast.show();
	}
}
