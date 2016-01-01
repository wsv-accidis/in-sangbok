package se.insektionen.songbook.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import se.insektionen.songbook.R;
import se.insektionen.songbook.model.Song;

/**
 * List adapter for the list of songs in a songbook.
 */
public final class SongbookListAdapter extends BaseAdapter {
    private static final String ELLIPISIS = "...";
    private static final String[] TRIM_END = new String[]{".", ",", "!", "?"};
    private static final String[] TRIM_START = new String[]{"*", "//:"};
    private final LayoutInflater mInflater;
    private final List<Song> mList;

    public SongbookListAdapter(Context context, List<Song> list) {
        mList = list;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if (null == convertView) {
            view = mInflater.inflate(R.layout.list_item_song, parent, false);
        } else {
            view = convertView;
        }

        Song song = mList.get(position);

        TextView nameText = (TextView) view.findViewById(R.id.song_list_primary);
        nameText.setText(song.getName());

        TextView firstLineText = (TextView) view.findViewById(R.id.song_list_secondary);
        firstLineText.setText(formatFirstLineOfSong(song.getFirstLineOfSong()));

        TextView categoryText = (TextView) view.findViewById(R.id.song_list_tertiary);
        categoryText.setText(song.getCategory());

        return view;
    }

    private String formatFirstLineOfSong(String line) {
        for (String trimStart : TRIM_START) {
            if (line.startsWith(trimStart)) {
                line = line.substring(trimStart.length());
            }
        }
        for (String trimEnd : TRIM_END) {
            if (line.endsWith(trimEnd)) {
                line = line.substring(0, line.length() - trimEnd.length());
            }
        }

        return line.trim().concat(ELLIPISIS);
    }
}
