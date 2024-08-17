package dd.astolastudio.balochidictionary.navigationdrawer;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Checkable;
import android.widget.LinearLayout;

public class CheckableLinearLayout extends LinearLayout implements Checkable {
	private boolean mIsChecked = false;

	public CheckableLinearLayout(Context context) {
		super(context);
	}

	public CheckableLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CheckableLinearLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public void setChecked(boolean b) {
		this.mIsChecked = b;
	}

	public boolean isChecked() {
		return this.mIsChecked;
	}

	public void toggle() {
		this.mIsChecked = !this.mIsChecked;
	}
}
