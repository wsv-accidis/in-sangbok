package se.insektionen.songbook.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import se.insektionen.songbook.R;
import se.insektionen.songbook.model.Song;
import se.insektionen.songbook.model.SongPart;

/**
 * Fragment which displays a single song.
 */
public final class SongFragment extends Fragment {
    public static SongFragment createInstance(Song song) {
        SongFragment fragment = new SongFragment();
        fragment.setArguments(song.toBundle());
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_song, container, false);

        Bundle args = getArguments();
        Song song = Song.fromBundle(args);

        setTextIfNotEmpty(view, R.id.song_category, song.getCategory());
        setTextIfNotEmpty(view, R.id.song_name, song.getName());
        setTextIfNotEmpty(view, R.id.song_author, song.getAuthor());

        String melodyComposer = !TextUtils.isEmpty(song.getMelody()) ?
            (TextUtils.isEmpty(song.getComposer()) ? song.getMelody() : String.format(getString(R.string.song_melody_composer_format), song.getMelody(), song.getComposer())) : "";
        setTextWithPrefixIfNotEmpty(view, R.id.song_melody, R.string.song_melody, melodyComposer);

        LinearLayout songLayout = (LinearLayout) view.findViewById(R.id.song_layout);
        populateSongLayout(songLayout, song.getParts());

        return view;
    }

    private void populateSongLayout(LinearLayout songLayout, List<SongPart> parts) {
        songLayout.removeAllViews();

        Context context = getContext();
        int color = ContextCompat.getColor(context, R.color.black);
        int topMargin = context.getResources().getDimensionPixelSize(R.dimen.song_part_top_margin);

        for (SongPart part : parts) {
            TextView textView = new TextView(context);
            textView.setText(part.getText());
            textView.setTextColor(color);

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0, topMargin, 0, 0);

            songLayout.addView(textView, layoutParams);
        }
    }

    private void setTextIfNotEmpty(View view, int textViewId, String str) {
        TextView textView = (TextView) view.findViewById(textViewId);
        if (!TextUtils.isEmpty(str)) {
            textView.setText(str);
            textView.setVisibility(View.VISIBLE);
        } else {
            textView.setVisibility(View.GONE);
        }
    }

    private void setTextWithPrefix(TextView textView, int prefixResId, String str) {
        String prefixStr = getString(prefixResId);
        SpannableString spanStr = new SpannableString(prefixStr + " " + str);
        int foregroundColor = ContextCompat.getColor(getContext(), R.color.darkgray);
        spanStr.setSpan(new ForegroundColorSpan(foregroundColor), 0, prefixStr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(spanStr);
    }

    private void setTextWithPrefixIfNotEmpty(View view, int textViewId, int prefixResId, String str) {
        TextView textView = (TextView) view.findViewById(textViewId);

        if (!TextUtils.isEmpty(str)) {
            setTextWithPrefix(textView, prefixResId, str);
            textView.setVisibility(View.VISIBLE);
        } else {
            textView.setVisibility(View.GONE);
        }
    }
}
