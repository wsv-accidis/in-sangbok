package se.insektionen.songbook.model;

import com.google.auto.value.AutoValue;

/**
 * Data model for part of a song.
 */
@AutoValue
public abstract class SongPart {
	public static final int TYPE_COMMENT = 1;
	public static final int TYPE_HEADER = 2;
	public static final int TYPE_PARAGRAPH = 0;

	public static SongPart create(int type, String text) {
		return new AutoValue_SongPart(text, type);
	}

	public abstract String text();

	public abstract int type();
}
