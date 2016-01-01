package se.insektionen.songbook.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import se.insektionen.songbook.R;

/**
 * Fragment which displays information about the app and the current songbook.
 */
public final class AboutFragment extends Fragment implements MainActivity.HasNavigationItem {
    @Override
    public int getItemId() {
        return R.id.nav_about;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_about, container, false);
    }
}
