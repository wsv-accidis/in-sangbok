package se.insektionen.songbook.model;

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

    public String getAuthor() {
        return mAuthor;
    }

    public String getCategory() {
        return mCategory;
    }

    public String getComposer() {
        return mComposer;
    }

    public String getMelody() {
        return mMelody;
    }

    public String getName() {
        return mName;
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

    public List<SongPart> getParts() {
        return mParts;
    }

    @Override
    public String toString() {
        return mName;
    }
}
