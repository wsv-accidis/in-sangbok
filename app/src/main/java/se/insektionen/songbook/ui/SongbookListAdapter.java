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

        TextView titleText = (TextView) view.findViewById(R.id.song_title);
        titleText.setText(song.getName());

        return view;
    }
}
