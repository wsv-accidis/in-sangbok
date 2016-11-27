package se.insektionen.songbook.model;

import com.google.auto.value.AutoValue;

import java.util.ArrayList;
import java.util.List;

/**
 * Data model for a songbook.
 */
@AutoValue
public abstract class Songbook {
	public static Songbook create(String description, String updated, List<Song> songs) {
		return new AutoValue_Songbook(findCategories(songs), description, songs, updated);
	}

	public abstract List<String> categories();

	public abstract String description();

	public abstract List<Song> songs();

	public abstract String updated();

	private static List<String> findCategories(List<Song> songs) {
		List<String> categories = new ArrayList<>();
		for (Song song : songs) {
			if (!categories.contains(song.category())) {
				categories.add(song.category());
			}
		}

		return categories;
	}
}
