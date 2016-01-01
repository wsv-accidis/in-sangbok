package se.insektionen.songbook.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.widget.Toast;

import se.insektionen.songbook.R;
import se.insektionen.songbook.model.Songbook;
import se.insektionen.songbook.services.Repository;
import se.insektionen.songbook.services.RepositoryResultHandler;

/**
 * Fragment which displays the list of songs.
 */
public final class SongbookFragment extends ListFragment {
    private static final String STATE_LIST_VIEW = "songbookListViewState";
    private final Handler mHandler = new Handler();
    private Parcelable mListState;
    private boolean mIsLoaded;
    private Songbook mSongbook;
    private SongbookListAdapter mListAdapter;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            mListState = savedInstanceState.getParcelable(STATE_LIST_VIEW);
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

    private void initializeList() {
        mListAdapter = new SongbookListAdapter(getContext(), mSongbook.getSongs());
        setListAdapter(mListAdapter);

        if (null != mListState) {
            getListView().onRestoreInstanceState(mListState);
            mListState = null;
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
