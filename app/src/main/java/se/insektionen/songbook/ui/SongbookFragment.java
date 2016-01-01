package se.insektionen.songbook.ui;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import se.insektionen.songbook.R;
import se.insektionen.songbook.model.Song;
import se.insektionen.songbook.model.Songbook;
import se.insektionen.songbook.services.Repository;
import se.insektionen.songbook.services.RepositoryResultHandler;
import se.insektionen.songbook.utils.AndroidUtils;

/**
 * Fragment which displays the list of songs.
 */
public final class SongbookFragment extends ListFragment implements MainActivity.HasNavigationItem, MainActivity.HasMenu {
	private static final int INTERNAL_LIST_CONTAINER_ID = 0x00ff0003; // from android.support.v4.app.ListFragment
	private static final String STATE_LIST_VIEW = "songbookListViewState";
	private static final String STATE_SEARCH_QUERY = "songbookSearchQueryState";
	private static final String TAG = SongbookFragment.class.getSimpleName();
	private final Handler mHandler = new Handler();
	private ImageButton mClearSearchButton;
	private boolean mIsLoaded;
	private SongbookListAdapter mListAdapter;
	private Parcelable mListState;
	private String mSearchQuery;
	private EditText mSearchText;
	private Songbook mSongbook;

	@Override
	public int getItemId() {
		return R.id.nav_list_songs;
	}

	@Override
	public int getMenu() {
		return R.menu.songbook;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (savedInstanceState != null) {
			mListState = savedInstanceState.getParcelable(STATE_LIST_VIEW);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		ViewGroup root = (ViewGroup) super.onCreateView(inflater, container, savedInstanceState);
		assert (root != null);

		// Extract the internal list container from the root view
		@SuppressWarnings("ResourceType")
		View listContainer = root.findViewById(INTERNAL_LIST_CONTAINER_ID);
		root.removeView(listContainer);

		// Put the internal list container inside our custom container
		View outerContainer = inflater.inflate(R.layout.fragment_songbook, root, false);
		FrameLayout innerContainer = (FrameLayout) outerContainer.findViewById(R.id.list_container);
		innerContainer.addView(listContainer);

		// Put the custom container inside the root
		root.addView(outerContainer, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

		mSearchText = (EditText) outerContainer.findViewById(R.id.songbook_search);
		mSearchText.addTextChangedListener(new SearchChangedListener());
		mClearSearchButton = (ImageButton) outerContainer.findViewById(R.id.songbook_search_clear);
		mClearSearchButton.setOnClickListener(new ClearSearchClickedListener());

		if (null != savedInstanceState) {
			mSearchQuery = savedInstanceState.getString(STATE_SEARCH_QUERY);
		}

		return root;
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		if (null == mListAdapter || position < 0 || position >= mListAdapter.getCount()) {
			return;
		}

		Song song = (Song) mListAdapter.getItem(position);
		SongFragment fragment = SongFragment.createInstance(song);

		Activity activity = getActivity();
		if (activity instanceof MainActivity) {
			AndroidUtils.hideSoftKeyboard(getContext(), getView());
			saveInstanceState();
			((MainActivity) activity).openFragment(fragment);
		} else {
			Log.e(TAG, "Activity holding fragment is not MainActivity!");
		}
	}

	@Override
	public boolean onMenuItemSelected(MenuItem item) {
		return false;
	}

	@Override
	public void onResume() {
		super.onResume();
		setEmptyText(getString(R.string.songbook_list_empty));

		mSearchText.setEnabled(false);
		mClearSearchButton.setEnabled(false);

		if (!mIsLoaded) {
			Repository repository = new Repository();
			repository.getSongbook(new SongbookLoadedHandler(), false);
		} else {
			initializeList();
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (null != getView()) {
			mListState = getListView().onSaveInstanceState();
			outState.putParcelable(STATE_LIST_VIEW, mListState);
		}
		if (null != mSearchQuery) {
			outState.putString(STATE_SEARCH_QUERY, mSearchQuery);
		}
	}

	private void initializeList() {
		mSearchText.setEnabled(true);
		mClearSearchButton.setEnabled(true);

		mListAdapter = new SongbookListAdapter(getContext(), mSongbook.getSongs());
		setListAdapter(mListAdapter);

		if (null != mListState) {
			getListView().onRestoreInstanceState(mListState);
			mListState = null;
		}

		mListAdapter.getFilter().filter(mSearchQuery);
	}

	private void saveInstanceState() {
		mListState = getListView().onSaveInstanceState();
	}

	private final class ClearSearchClickedListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			mSearchText.setText("");
		}
	}

	private final class SearchChangedListener implements TextWatcher {
		@Override
		public void afterTextChanged(Editable s) {
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			mSearchQuery = s.toString().toLowerCase();
			if (null != mListAdapter) {
				mListAdapter.getFilter().filter(mSearchQuery);
			}
		}
	}

	private final class SongbookLoadedHandler implements RepositoryResultHandler<Songbook> {
		@Override
		public void onError(final int errorMessage) {
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					Toast toast = Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG);
					toast.show();
				}
			});
		}

		@Override
		public void onSuccess(Songbook songbook) {
			mSongbook = songbook;
			mIsLoaded = true;

			mHandler.post(new Runnable() {
				@Override
				public void run() {
					if (null != getActivity()) {
						initializeList();
					}
				}
			});
		}
	}
}
