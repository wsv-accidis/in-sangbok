package se.insektionen.songbook.model;

import java.util.List;

/**
 * Data model for a songbook.
 */
public final class Songbook {
	private final String mDescription;
	private final List<Song> mSongs;
	private final String mUpdated;

	public Songbook(String description, String updated, List<Song> songs) {
		mDescription = description;
		mUpdated = updated;
		mSongs = songs;
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
}
