package se.insektionen.songbook.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Data model for a songbook.
 */
public final class Songbook {
	private final List<String> mCategories;
	private final String mDescription;
	private final List<Song> mSongs;
	private final String mUpdated;

	public Songbook(String description, String updated, List<Song> songs) {
		mDescription = description;
		mUpdated = updated;
		mSongs = songs;
		mCategories = findCategories(songs);
	}

	public List<String> getCategories() {
		return mCategories;
	}

	public String getDescription() {
		return mDescription;
	}

	public List<Song> getSongs() {
		return mSongs;
	}

	public String getUpdated() {
		return mUpdated;
	}

	private static List<String> findCategories(List<Song> songs) {
		List<String> categories = new ArrayList<>();
		for (Song song : songs) {
			if (!categories.contains(song.getCategory())) {
				categories.add(song.getCategory());
			}
		}

		return categories;
	}
}
