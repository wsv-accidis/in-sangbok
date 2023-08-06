package se.insektionen.songbook.ui;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import se.insektionen.songbook.R;
import se.insektionen.songbook.model.Song;

/**
 * List adapter for the list of songs in a songbook.
 */
public final class SongbookListAdapter extends BaseAdapter implements Filterable {
	public static final String CATEGORY_QUERY = "?category=";
	private static final String ELLIPSIS = "...";
	private static final String[] TRIM_END = new String[]{".", ",", "!", "?"};
	private static final String[] TRIM_START = new String[]{"*", "//:"};
	private final LayoutInflater mInflater;
	private final List<Song> mList;
	private final Filter mFilter = new SongbookListFilter();
	private List<Song> mFilteredList;

	public SongbookListAdapter(Context context, List<Song> list) {
		mList = list;
		mFilteredList = list;
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return mFilteredList.size();
	}

	@Override
	public Filter getFilter() {
		return mFilter;
	}

	@Override
	public Object getItem(int position) {
		return mFilteredList.get(position);
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

		Song song = mFilteredList.get(position);

		TextView nameText = (TextView) view.findViewById(R.id.song_list_primary);
		nameText.setText(song.getName());

		TextView firstLineText = (TextView) view.findViewById(R.id.song_list_secondary);
		firstLineText.setText(formatFirstLineOfSong(song.firstLineOfSong()));

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

		return line.trim().concat(ELLIPSIS);
	}

	private final class SongbookListFilter extends Filter {
		@Override
		protected FilterResults performFiltering(CharSequence constraint) {
			FilterResults results = new FilterResults();
			List<Song> filteredList;

			if (!TextUtils.isEmpty(constraint)) {
				String categoryFilter = null;
				String textFilter;

				/*
				 * The query is of the format {textFilter}?category={categoryFilter}
				 * Both parts are optional.
				 */

				String query = constraint.toString();
				if (query.contains(CATEGORY_QUERY)) {
					int idx = query.indexOf(CATEGORY_QUERY);
					categoryFilter = query.substring(idx + CATEGORY_QUERY.length());
					textFilter = query.substring(0, idx);
				} else {
					textFilter = query;
				}

				filteredList = new ArrayList<>();
				for (Song s : mList) {
					if (filter(s, textFilter, categoryFilter)) {
						filteredList.add(s);
					}
				}
			} else {
				filteredList = mList;
			}

			results.values = filteredList;
			results.count = filteredList.size();
			return results;
		}

		@Override
		protected void publishResults(CharSequence constraint, FilterResults results) {
			//noinspection unchecked
			mFilteredList = (List<Song>) results.values;
			if (results.count > 0) {
				notifyDataSetChanged();
			} else {
				notifyDataSetInvalidated();
			}
		}

		private boolean filter(Song song, String textFilter, String categoryFilter) {
			if (!TextUtils.isEmpty(textFilter) && !song.matches(textFilter.toLowerCase())) {
				return false;
			}
			return TextUtils.isEmpty(categoryFilter) || song.getCategory().equals(categoryFilter);
		}
	}
}
