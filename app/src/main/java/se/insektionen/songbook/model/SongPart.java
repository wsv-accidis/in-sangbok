package se.insektionen.songbook.model;

/**
 * Data model for part of a song.
 */
public final class SongPart {
    public static final int TYPE_COMMENT = 1;
    public static final int TYPE_HEADER = 2;
    public static final int TYPE_PARAGRAPH = 0;
    private final String mText;
    private final int mType;

    public SongPart(int type, String text) {
        mType = type;
        mText = text;
    }

    public String getText() {
        return mText;
    }

    public int getType() {
        return mType;
    }
}
