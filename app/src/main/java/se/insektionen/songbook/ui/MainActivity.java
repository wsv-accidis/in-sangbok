package se.insektionen.songbook.ui;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.Random;

import se.insektionen.songbook.R;

/**
 * The main activity.
 */
public final class MainActivity extends AppCompatActivity {
	private static final String STATE_LAST_OPENED_FRAGMENT = "openMainActivityFragment";
	private final static String TAG = MainActivity.class.getSimpleName();
	private DrawerLayout mNavigationDrawer;
	private NavigationView mNavigationView;
	private HasMenu mOptionsMenu;

	@Override
	public void onBackPressed() {
		if (mNavigationDrawer.isDrawerOpen(GravityCompat.START)) {
			mNavigationDrawer.closeDrawer(GravityCompat.START);
		} else {
			super.onBackPressed();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (null != mOptionsMenu) {
			getMenuInflater().inflate(mOptionsMenu.getMenu(), menu);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return (null != mOptionsMenu && mOptionsMenu.onMenuItemSelected(item)) || super.onOptionsItemSelected(item);
	}

	public void openFragment(Fragment fragment) {
		openFragment(fragment, true);
	}

	public void openFragment(Fragment fragment, boolean addToBackStack) {
		FragmentManager fragmentManager = getSupportFragmentManager();
		Fragment oldFragment = fragmentManager.findFragmentById(R.id.container);

		FragmentTransaction transaction = fragmentManager.beginTransaction();
		transaction.replace(R.id.container, fragment);
		transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

		if (addToBackStack && !isSameFragment(oldFragment, fragment)) {
			transaction.addToBackStack(null);
		}

		transaction.commit();
		updateViewFromFragment(fragment);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		if (null != getSupportActionBar()) {
			getSupportActionBar().setTitle(R.string.app_name);
		}

		mNavigationDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mNavigationDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
		mNavigationDrawer.setDrawerListener(toggle);
		toggle.syncState();

		mNavigationView = (NavigationView) findViewById(R.id.nav_view);
		mNavigationView.setNavigationItemSelectedListener(new NavigationListener());
		View navigationHeader = mNavigationView.getHeaderView(0);
		setRandomQuoteInHeader(navigationHeader);

		FragmentManager fragmentManager = getSupportFragmentManager();
		fragmentManager.addOnBackStackChangedListener(new BackStackChangedListener());

		if (null != savedInstanceState) {
			Fragment lastFragment = fragmentManager.getFragment(savedInstanceState, STATE_LAST_OPENED_FRAGMENT);
			openFragment(lastFragment, false);
		} else {
			openFragment(getFragmentByNavigationItem(R.id.nav_list_songs), false);
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		FragmentManager fragmentManager = getSupportFragmentManager();
		Fragment fragment = fragmentManager.findFragmentById(R.id.container);
		if (null != fragment) {
			fragmentManager.putFragment(outState, STATE_LAST_OPENED_FRAGMENT, fragment);
		}
	}

	private Fragment getFragmentByNavigationItem(int navId) {
		switch (navId) {
			case R.id.nav_list_songs:
				return new SongbookFragment();
			case R.id.nav_about:
				return new AboutFragment();
		}

		return null;
	}

	private boolean isSameFragment(Fragment oldFragment, Fragment newFragment) {
		return null != oldFragment && oldFragment.getClass().getName().equals(newFragment.getClass().getName());
	}

	private void setRandomQuoteInHeader(View headerView) {
		if (null == headerView) {
			return;
		}

		TextView line1Text = (TextView) headerView.findViewById(R.id.navigation_header_qotd1);
		TextView line2Text = (TextView) headerView.findViewById(R.id.navigation_header_qotd2);
		String[] quotes = getResources().getStringArray(R.array.quotes_list);
		String quote = quotes[(new Random()).nextInt(quotes.length)];

		int separatorIdx = quote.indexOf('|');
		if (-1 != separatorIdx) {
			line1Text.setText(quote.substring(0, separatorIdx));
			line2Text.setText("- ".concat(quote.substring(separatorIdx + 1)));
		} else {
			line1Text.setText(quote);
			line2Text.setText("");
		}
	}

	private void updateViewFromFragment(Fragment fragment) {
		if (fragment instanceof HasNavigationItem) {
			HasNavigationItem fragmentWithNavItem = (HasNavigationItem) fragment;
			mNavigationView.setCheckedItem(fragmentWithNavItem.getItemId());
		}

		if (fragment instanceof HasMenu) {
			mOptionsMenu = (HasMenu) fragment;
			fragment.setHasOptionsMenu(true);
		} else {
			mOptionsMenu = null;
			fragment.setHasOptionsMenu(false);
		}
	}

	public interface HasMenu {
		int getMenu();

		boolean onMenuItemSelected(MenuItem item);
	}

	public interface HasNavigationItem {
		int getItemId();
	}

	private final class BackStackChangedListener implements FragmentManager.OnBackStackChangedListener {
		@Override
		public void onBackStackChanged() {
			Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.container);
			if (null != fragment) {
				updateViewFromFragment(fragment);
			}
		}
	}

	private final class NavigationListener implements NavigationView.OnNavigationItemSelectedListener {
		@Override
		public boolean onNavigationItemSelected(MenuItem item) {
			Fragment fragment = getFragmentByNavigationItem(item.getItemId());
			if (null != fragment) {
				openFragment(fragment);
			} else {
				Log.e(TAG, "Trying to navigate to unrecognized fragment.");
			}

			DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
			drawer.closeDrawer(GravityCompat.START);
			return true;
		}
	}
}
