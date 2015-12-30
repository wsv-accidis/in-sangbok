package se.insektionen.songbook.services;

import android.text.TextUtils;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import se.insektionen.songbook.model.Song;
import se.insektionen.songbook.model.SongPart;
import se.insektionen.songbook.model.Songbook;

/**
 * Parses XML into model objects.
 */
public final class SongbookXmlParser {
    private static final String TAG = SongbookXmlParser.class.getSimpleName();

    private SongbookXmlParser() {
    }

    public static Songbook parseSongbook(XmlPullParser xmlParser) throws XmlPullParserException, IOException {
        SongbookXmlParser instance = new SongbookXmlParser();
        return instance.doParse(xmlParser);
    }

    static String readAttribute(XmlPullParser xmlParser, String name) {
        for (int i = 0; i < xmlParser.getAttributeCount(); i++) {
            if (name.equals(xmlParser.getAttributeName(i))) {
                return xmlParser.getAttributeValue(i).trim();
            }
        }
        return null;
    }

    private Songbook doParse(XmlPullParser xmlParser) throws XmlPullParserException, IOException {
        // Read the top-level <songs> element and make sure it's nothing else
        xmlParser.next();
        xmlParser.require(XmlPullParser.START_TAG, null, Elements.SONGS);

        String updated = readAttribute(xmlParser, Attributes.UPDATED);
        String description = readAttribute(xmlParser, Attributes.DESCRIPTION);
        List<Song> songs = new ArrayList<>();

        // Continue reading until we reach the end tag </songs>
        int parsingEvent = xmlParser.next();
        while (XmlPullParser.END_TAG != parsingEvent || !Elements.SONGS.equals(xmlParser.getName())) {

            // Skip any text inside <songs>
            while (XmlPullParser.TEXT == parsingEvent) {
                parsingEvent = xmlParser.next();
            }

            // Check if we skipped all the way to the end tag </songs>
            if (XmlPullParser.END_TAG == parsingEvent && Elements.SONGS.equals(xmlParser.getName())) {
                break;
            }

            Song song = tryParseSong(xmlParser);
            if (null != song) {
                songs.add(song);
            }

            parsingEvent = xmlParser.next();

            if (null != song) {
                Log.d(TAG, "Read a song \"" + song.getName() + "\".");
            }
        }

        Log.i(TAG, "Finished parsing songbook, got " + songs.size() + " song(s).");
        return new Songbook(description, updated, songs);
    }

    private List<SongPart> doParseSongParts(XmlPullParser xmlParser) throws XmlPullParserException, IOException {
        List<SongPart> songParts = new ArrayList<>();

        // Continue reading until we reach the end tag </song>
        int parsingEvent = xmlParser.next();
        while (XmlPullParser.END_TAG != parsingEvent || !Elements.SONG.equals(xmlParser.getName())) {
            SongPart songPart = null;
            if (XmlPullParser.TEXT == parsingEvent) {
                songPart = tryParseText(xmlParser, SongPart.TYPE_PARAGRAPH);
            } else {
                xmlParser.require(XmlPullParser.START_TAG, null, null);
                switch (xmlParser.getName()) {
                    case Elements.P:
                        songPart = tryParseSongPart(xmlParser, SongPart.TYPE_PARAGRAPH);
                        break;
                    case Elements.COMMENT:
                        songPart = tryParseSongPart(xmlParser, SongPart.TYPE_COMMENT);
                        break;
                    case Elements.HEADER:
                        songPart = tryParseSongPart(xmlParser, SongPart.TYPE_HEADER);
                        break;
                    default:
                        Log.w(TAG, "Song contains unrecognized part \"" + xmlParser.getName() + "\", reading a format that is newer than the app supports?");
                        skip(xmlParser);
                        break;
                }
            }

            if (null != songPart) {
                songParts.add(songPart);
            }

            parsingEvent = xmlParser.next();
        }

        return songParts;
    }

    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (XmlPullParser.START_TAG != parser.getEventType()) {
            return;
        }

        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }

    private Song tryParseSong(XmlPullParser xmlParser) {
        try {
            // Ensure we are now at the start of a song
            xmlParser.require(XmlPullParser.START_TAG, null, Elements.SONG);

            // Read and validate attributes
            String author = readAttribute(xmlParser, Attributes.AUTHOR);
            String category = readAttribute(xmlParser, Attributes.CATEGORY);
            String composer = readAttribute(xmlParser, Attributes.COMPOSER);
            String melody = readAttribute(xmlParser, Attributes.MELODY);
            String name = readAttribute(xmlParser, Attributes.NAME);

            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(category)) {
                throw new XmlPullParserException("Missing required attribute " + Attributes.NAME + " and/or " + Attributes.CATEGORY + ".");
            }

            List<SongPart> parts = doParseSongParts(xmlParser);
            return new Song(author, category, composer, melody, name, parts);
        } catch (Exception ex) {
            Log.e(TAG, "Exception while parsing song: " + ex);
            return null;
        }
    }

    private SongPart tryParseSongPart(XmlPullParser xmlParser, int type) {
        String name = xmlParser.getName();
        try {
            xmlParser.next();
            if (XmlPullParser.END_TAG == xmlParser.getEventType()) {
                // This song part was empty for some reason, ignore it
                return null;
            }

            // Content of song part must be text only, no nested tags
            xmlParser.require(XmlPullParser.TEXT, null, null);
            SongPart songPart = tryParseText(xmlParser, type);
            xmlParser.next();
            xmlParser.require(XmlPullParser.END_TAG, null, name);

            return songPart;
        } catch (Exception ex) {
            Log.e(TAG, "Exception while parsing song part: " + ex);
            return null;
        }
    }

    private SongPart tryParseText(XmlPullParser xmlParser, int type) {
        String text = xmlParser.getText().trim();
        if (TextUtils.isEmpty(text)) {
            return null;
        }

        // Eliminate whitespace caused by indentation but keep the line breaks internal to the text
        text = text.replaceAll("\\s*\\r?\\n\\s*", "\n");
        return new SongPart(type, text);
    }

    static class Attributes {
        public static final String AUTHOR = "author";
        public static final String CATEGORY = "category";
        public static final String COMPOSER = "composer";
        public static final String DESCRIPTION = "description";
        public static final String MELODY = "melody";
        public static final String NAME = "name";
        public static final String UPDATED = "updated";
    }

    static class Elements {
        public static final String COMMENT = "comment";
        public static final String HEADER = "header";
        public static final String P = "p";
        public static final String SONG = "song";
        public static final String SONGS = "songs";
    }
}
