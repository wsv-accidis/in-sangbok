package se.insektionen.songbook.model;

import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.auto.value.AutoValue;

import java.util.ArrayList;
import java.util.List;

/**
 * Data model for a song.
 */
@AutoValue
public abstract class Song {
	public static Song create(String author, String category, String composer, String melody, String name, List<SongPart> parts) {
		final String searchText = createSearchText(name, melody, parts);
		return new AutoValue_Song(author, category, composer, melody, name, parts, searchText);
	}

	public static Song fromBundle(Bundle bundle) {
		String author = bundle.getString(Keys.AUTHOR);
		String category = bundle.getString(Keys.CATEGORY);
		String composer = bundle.getString(Keys.COMPOSER);
		String melody = bundle.getString(Keys.MELODY);
		String name = bundle.getString(Keys.NAME);

		int[] partsTypes = bundle.getIntArray(Keys.PARTS_TYPES);
		String[] partsTexts = bundle.getStringArray(Keys.PARTS_TEXTS);
		if (null == partsTypes || null == partsTexts || partsTypes.length != partsTexts.length) {
			throw new IllegalArgumentException("Bundle contains invalid song parts.");
		}

		List<SongPart> parts = new ArrayList<>(partsTypes.length);
		for (int i = 0; i < partsTypes.length; i++) {
			SongPart songPart = SongPart.create(partsTypes[i], partsTexts[i]);
			parts.add(songPart);
		}

		return Song.create(author, category, composer, melody, name, parts);
	}

	@Nullable
	public abstract String author();

	public abstract String category();

	@Nullable
	public abstract String composer();

	public String firstLineOfSong() {
		for (SongPart songPart : parts()) {
			if (SongPart.TYPE_PARAGRAPH == songPart.type()) {
				String firstParagraph = songPart.text();
				int firstLineBreak = firstParagraph.indexOf('\n');
				return (-1 == firstLineBreak ? firstParagraph : firstParagraph.substring(0, firstLineBreak));
			}
		}

		return "";
	}

	public boolean matches(String search) {
		return searchText().contains(search.toLowerCase());
	}

	@Nullable
	public abstract String melody();

	public abstract String name();

	public abstract List<SongPart> parts();

	abstract String searchText();

	public Bundle toBundle() {
		Bundle bundle = new Bundle();
		bundle.putString(Keys.AUTHOR, author());
		bundle.putString(Keys.CATEGORY, category());
		bundle.putString(Keys.COMPOSER, composer());
		bundle.putString(Keys.MELODY, melody());
		bundle.putString(Keys.NAME, name());

		int numParts = parts().size();
		int[] partsTypes = new int[numParts];
		String[] partsTexts = new String[numParts];

		for (int i = 0; i < numParts; i++) {
			SongPart part = parts().get(i);
			partsTypes[i] = part.type();
			partsTexts[i] = part.text();
		}

		bundle.putIntArray(Keys.PARTS_TYPES, partsTypes);
		bundle.putStringArray(Keys.PARTS_TEXTS, partsTexts);
		return bundle;
	}

	@NonNull
	@Override
	public String toString() {
		return name();
	}

	private static String createSearchText(String name, String melody, List<SongPart> parts) {
		StringBuilder builder = new StringBuilder();
		builder.append(name.toLowerCase());

		if (!TextUtils.isEmpty(melody)) {
			builder.append(' ');
			builder.append(melody.toLowerCase());
		}

		for (SongPart songPart : parts) {
			if (SongPart.TYPE_PARAGRAPH == songPart.type()) {
				builder.append(' ');
				builder.append(songPart.text().toLowerCase());
			}
		}

		return builder.toString();
	}

	private static class Keys {
		static final String AUTHOR = "author";
		static final String CATEGORY = "category";
		static final String COMPOSER = "composer";
		static final String MELODY = "melody";
		static final String NAME = "name";
		static final String PARTS_TEXTS = "partsTexts";
		static final String PARTS_TYPES = "partsTypes";

		private Keys() {
		}
	}
}
