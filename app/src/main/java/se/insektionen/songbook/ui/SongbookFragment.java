package se.insektionen.songbook.ui;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
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
public final class SongbookFragment extends ListFragment {
    private static final String STATE_LIST_VIEW = "songbookListViewState";
    private static final String TAG = SongbookFragment.class.getSimpleName();
    private final Handler mHandler = new Handler();
    private boolean mIsLoaded;
    private SongbookListAdapter mListAdapter;
    private Parcelable mListState;
    private Songbook mSongbook;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            mListState = savedInstanceState.getParcelable(STATE_LIST_VIEW);
        }
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
    public void onResume() {
        super.onResume();
        setEmptyText(getString(R.string.songbook_list_empty));

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
    }

    private void initializeList() {
        mListAdapter = new SongbookListAdapter(getContext(), mSongbook.getSongs());
        setListAdapter(mListAdapter);

        if (null != mListState) {
            getListView().onRestoreInstanceState(mListState);
            mListState = null;
        }
    }

    private void saveInstanceState() {
        mListState = getListView().onSaveInstanceState();
    }

    private final class SongbookLoadedHandler implements RepositoryResultHandler<Songbook> {
        @Override
        public void onError(int errorMessage) {
            Toast toast = Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG);
            toast.show();
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
