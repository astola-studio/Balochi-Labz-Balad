package dd.astolastudio.balochidictionary.navigationdrawer;

import android.app.Activity;
import android.app.Fragment;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.drawerlayout.widget.DrawerLayout;
import dd.astolastudio.balochidictionary.ActionBarActivity;
import dd.astolastudio.balochidictionary.navigationdrawer.NavigationDrawerAdapter.OnCategoryChange;
import java.util.ArrayList;
import dd.astolastudio.balochidictionary.R;
import android.util.Log;

public class NavigationDrawerFragment extends Fragment {
	private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";
	private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";
	private static final String STATE_SELECTED_NAME = "selected_navigation_drawer_name";
	private String catNameSelected = "";
	private NavigationDrawerCallbacks mCallbacks;
	private int mCurrentSelectedPosition = 1;
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerListView;
	private ActionBarDrawerToggle mDrawerToggle;
	private View mFragmentContainerView;
	private boolean mFromSavedInstanceState;
	private boolean mUserLearnedDrawer;
	private NavigationDrawerAdapter navigationDrawerAdapter;
	private AbstractNavigationDrawerItem[] navigationDrawerItems;

	public interface NavigationDrawerCallbacks {
		void onNavigationDrawerItemSelected(int index, String cat);
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.mUserLearnedDrawer = PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean(PREF_USER_LEARNED_DRAWER, false);
		if (savedInstanceState != null) {
			this.mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
			this.catNameSelected = savedInstanceState.getString(STATE_SELECTED_NAME);
			this.mFromSavedInstanceState = true;
		}
		selectItem(this.mCurrentSelectedPosition, this.catNameSelected);
		setHasOptionsMenu(true);
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		this.mDrawerListView = (ListView) inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
		this.mDrawerListView.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
					NavigationDrawerFragment.this.selectItem(position, navigationDrawerAdapter.getItem(position).getLabel());
				}
			});

		this.navigationDrawerItems = new AbstractNavigationDrawerItem[]{
			new NavigationDrawerHeading(101,
										getString(R.string.nav_drawer_heading_wordbook)),
			new NavigationDrawerRow(102,
									getString(R.string.nav_drawer_row_browse_wordbook),
									R.drawable.ic_subject_white_24dp,
									getActivity()),
			new NavigationDrawerRow(103,
									getString(R.string.nav_drawer_row_favorites),
									R.drawable.ic_favorite_white_24dp,
									getActivity()),
			new NavigationDrawerRow(104,
									getString(R.string.nav_drawer_row_history),
									R.drawable.ic_history_white_24dp,
									getActivity()),
			/* // They don’t seem to be useful anymore (atleast for now)
			new NavigationDrawerHeading(200,
										getString(R.string.nav_drawer_heading_grammar)),
			new NavigationDrawerRow(201,
									getString(R.string.nav_drawer_row_browse_grammar),
									R.drawable.ic_subject_white_24dp,
									getActivity()),
			new NavigationDrawerRow(202,
									getString(R.string.nav_drawer_row_bookmarks),
									R.drawable.ic_bookmark_white_24dp,
									getActivity())
			*/
			};
		this.navigationDrawerAdapter = new NavigationDrawerAdapter(getActivity(), android.R.layout.simple_list_item_activated_2, this.navigationDrawerItems);
		this.navigationDrawerAdapter.setOnCategoryChange(new OnCategoryChange() {
				public void onChange(String s, int i) {
					if (!NavigationDrawerFragment.this.catNameSelected.equals(s)) {
						NavigationDrawerFragment.this.catNameSelected = s;
						NavigationDrawerFragment.this.selectItem(i, s);
					}
				}

				public void onNothing() {
				}
			});
		this.mDrawerListView.setAdapter(this.navigationDrawerAdapter);
		this.mDrawerListView.setItemChecked(this.mCurrentSelectedPosition, true);
		return this.mDrawerListView;
	}

	public boolean isDrawerOpen() {
		return this.mDrawerLayout != null && this.mDrawerLayout.isDrawerOpen(this.mFragmentContainerView);
	}

	public void setUp(int fragmentId, DrawerLayout drawerLayout) {
		this.mFragmentContainerView = getActivity().findViewById(fragmentId);
		this.mDrawerLayout = drawerLayout;
		this.mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, 8388611);
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);
		this.mDrawerToggle = new ActionBarDrawerToggle(getActivity(), this.mDrawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
			public void onDrawerClosed(View drawerView) {
				super.onDrawerClosed(drawerView);
				if (NavigationDrawerFragment.this.isAdded()) {
					NavigationDrawerFragment.this.getActivity().invalidateOptionsMenu();
				}
			}

			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				if (NavigationDrawerFragment.this.isAdded()) {
					if (!NavigationDrawerFragment.this.mUserLearnedDrawer) {
						NavigationDrawerFragment.this.mUserLearnedDrawer = true;
						PreferenceManager.getDefaultSharedPreferences(NavigationDrawerFragment.this.getActivity()).edit().putBoolean(NavigationDrawerFragment.PREF_USER_LEARNED_DRAWER, true).apply();
					}
					NavigationDrawerFragment.this.getActivity().invalidateOptionsMenu();
				}
			}
		};
		if (!(this.mUserLearnedDrawer || this.mFromSavedInstanceState)) {
			this.mDrawerLayout.openDrawer(this.mFragmentContainerView);
		}
		this.mDrawerLayout.post(new Runnable() {
				public void run() {
					NavigationDrawerFragment.this.mDrawerToggle.syncState();
				}
			});
		this.mDrawerLayout.setDrawerListener(this.mDrawerToggle);
	}

	private void selectItem(int position, String catName) {
		this.mCurrentSelectedPosition = position;
		if (this.mDrawerListView != null) {
			//this.mDrawerListView.setItemChecked(position, true);
		}
		if (this.mDrawerLayout != null) {
			this.mDrawerLayout.closeDrawer(this.mFragmentContainerView);
		}
		if (this.mFromSavedInstanceState) {
			this.mFromSavedInstanceState = false;
		} else if (this.mCallbacks != null) {
			this.mCallbacks.onNavigationDrawerItemSelected(position, catName);
		}
	}

	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			this.mCallbacks = (NavigationDrawerCallbacks) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
		}
	}

	public void onDetach() {
		super.onDetach();
		this.mCallbacks = null;
	}

	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(STATE_SELECTED_POSITION, this.mCurrentSelectedPosition);
		outState.putString(STATE_SELECTED_NAME, this.catNameSelected);
	}

	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		this.mDrawerToggle.onConfigurationChanged(newConfig);
	}

	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		if (this.mDrawerLayout != null && isDrawerOpen()) {
			inflater.inflate(R.menu.global_menu, menu);
			showGlobalContextActionBar();
		}
		super.onCreateOptionsMenu(menu, inflater);
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		if (this.mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void showGlobalContextActionBar() {
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setNavigationMode(0);
		actionBar.setTitle(R.string.app_name);
		actionBar.setSubtitle(null);
	}

	private ActionBar getActionBar() {
		return ((ActionBarActivity) getActivity()).getSupportActionBar();
	}

	public void userLearnedDrawer() {
		this.mUserLearnedDrawer = true;
		PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putBoolean(PREF_USER_LEARNED_DRAWER, true).apply();
	}

	public int getCurrentSelectedPosition() {
		return this.mCurrentSelectedPosition;
	}

	public void setCurrentSelectedPosition(int position) {
		this.mCurrentSelectedPosition = position;
		this.mDrawerListView.clearChoices();
		this.mDrawerListView.setItemChecked(position, true);
	}
}
