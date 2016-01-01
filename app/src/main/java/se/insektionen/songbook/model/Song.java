package se.insektionen.songbook.model;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

/**
 * Data model for a song.
 */
public final class Song {
	private final String mAuthor;
	private final String mCategory;
	private final String mComposer;
	private final String mMelody;
	private final String mName;
	private final List<SongPart> mParts;

	public Song(String author, String category, String composer, String melody, String name, List<SongPart> parts) {
		mAuthor = author;
		mCategory = category;
		mComposer = composer;
		mMelody = melody;
		mName = name;
		mParts = parts;
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
			SongPart songPart = new SongPart(partsTypes[i], partsTexts[i]);
			parts.add(songPart);
		}

		return new Song(author, category, composer, melody, name, parts);
	}

	public String getAuthor() {
		return mAuthor;
	}

	public String getCategory() {
		return mCategory;
	}

	public String getComposer() {
		return mComposer;
	}

	public String getFirstLineOfSong() {
		for (SongPart songPart : mParts) {
			if (SongPart.TYPE_PARAGRAPH == songPart.getType()) {
				String firstParagraph = songPart.getText();
				int firstLineBreak = firstParagraph.indexOf('\n');
				return (-1 == firstLineBreak ? firstParagraph : firstParagraph.substring(0, firstLineBreak));
			}
		}

		return "";
	}

	public String getMelody() {
		return mMelody;
	}

	public String getName() {
		return mName;
	}

	public List<SongPart> getParts() {
		return mParts;
	}

	public Bundle toBundle() {
		Bundle bundle = new Bundle();
		bundle.putString(Keys.AUTHOR, mAuthor);
		bundle.putString(Keys.CATEGORY, mCategory);
		bundle.putString(Keys.COMPOSER, mComposer);
		bundle.putString(Keys.MELODY, mMelody);
		bundle.putString(Keys.NAME, mName);

		int numParts = mParts.size();
		int[] partsTypes = new int[numParts];
		String[] partsTexts = new String[numParts];

		for (int i = 0; i < numParts; i++) {
			SongPart part = mParts.get(i);
			partsTypes[i] = part.getType();
			partsTexts[i] = part.getText();
		}

		bundle.putIntArray(Keys.PARTS_TYPES, partsTypes);
		bundle.putStringArray(Keys.PARTS_TEXTS, partsTexts);
		return bundle;
	}

	@Override
	public String toString() {
		return mName;
	}

	public static class Keys {
		public static final String AUTHOR = "author";
		public static final String CATEGORY = "category";
		public static final String COMPOSER = "composer";
		public static final String MELODY = "melody";
		public static final String NAME = "name";
		public static final String PARTS_TEXTS = "partsTexts";
		public static final String PARTS_TYPES = "partsTypes";

		private Keys() {
		}
	}
}
