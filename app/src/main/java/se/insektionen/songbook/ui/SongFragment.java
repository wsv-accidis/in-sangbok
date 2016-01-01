package se.insektionen.songbook.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import se.insektionen.songbook.R;
import se.insektionen.songbook.model.Song;

/**
 * Fragment which displays a single song.
 */
public final class SongFragment extends Fragment {
    public static SongFragment createInstance(Song song) {
        Bundle args = new Bundle();

        SongFragment fragment = new SongFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_song, container, false);

        Bundle args = getArguments();
        return view;
    }
}
