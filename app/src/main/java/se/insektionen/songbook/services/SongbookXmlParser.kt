package se.insektionen.songbook.services

import android.util.Log
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import org.xmlpull.v1.XmlPullParserFactory
import se.insektionen.songbook.model.Song
import se.insektionen.songbook.model.SongPart
import se.insektionen.songbook.model.Songbook
import se.insektionen.songbook.utils.TAG
import java.io.Reader

/**
 * Parses XML into model objects.
 */
class SongbookXmlParser(private val xml: XmlPullParser) {
    private fun doParse(): Songbook {
        // Read the top-level <songs> element and make sure it's nothing else
        xml.next()
        xml.require(XmlPullParser.START_TAG, null, Elements.SONGS)
        val updated = xml.getAttributeValue(null, Attributes.UPDATED)
        val description = xml.getAttributeValue(null, Attributes.DESCRIPTION)
        val songs: MutableList<Song> = ArrayList()
        do {
            xml.next()

            // Skip any text inside <songs>
            while (XmlPullParser.TEXT == xml.eventType) {
                xml.next()
            }

            // Check if we skipped all the way to the end
            if (isAtEndOfDocument()) {
                break
            }

            val song = tryParseSong()
            if (null != song) {
                songs.add(song)
                Log.d(TAG, "Read a song \"${song.name}\".")
            }
            xml.next()

            // Continue reading until we reach the end
        } while (!isAtEndOfDocument())
        Log.i(TAG, "Finished parsing songbook, got ${songs.size} song(s).")
        return Songbook(description, updated, songs)
    }

    private fun doParseSongParts(): List<SongPart> {
        val songParts: MutableList<SongPart> = ArrayList()

        // Continue reading until we reach the end tag </song>
        var parsingEvent = xml.next()
        while (XmlPullParser.END_TAG != parsingEvent || Elements.SONG != xml.name) {
            var songPart: SongPart? = null
            if (XmlPullParser.TEXT == parsingEvent) {
                songPart = tryParseText(SongPart.TYPE_PARAGRAPH)
            } else {
                xml.require(XmlPullParser.START_TAG, null, null)
                when (xml.name) {
                    Elements.P -> songPart = tryParseSongPart(SongPart.TYPE_PARAGRAPH)
                    Elements.COMMENT -> songPart = tryParseSongPart(SongPart.TYPE_COMMENT)
                    Elements.HEADER -> songPart = tryParseSongPart(SongPart.TYPE_HEADER)
                    else -> {
                        Log.w(
                            TAG,
                            "Song contains unrecognized part \"${xml.name}\", reading a format that is newer than the app supports?"
                        )
                        skip()
                    }
                }
            }
            if (null != songPart) {
                songParts.add(songPart)
            }
            parsingEvent = xml.next()
        }
        return songParts
    }

    private fun isAtEndOfDocument(): Boolean =
        XmlPullParser.END_DOCUMENT == xml.eventType || XmlPullParser.END_TAG == xml.eventType && Elements.SONGS == xml.name

    private fun skip() {
        if (XmlPullParser.START_TAG != xml.eventType) {
            return
        }
        var depth = 1
        while (depth != 0) {
            when (xml.next()) {
                XmlPullParser.END_TAG -> depth--
                XmlPullParser.START_TAG -> depth++
            }
        }
    }

    private fun tryParseSong(): Song? {
        return try {
            // Ensure we are now at the start of a song
            xml.require(XmlPullParser.START_TAG, null, Elements.SONG)

            // Read and validate attributes
            val author = xml.getAttributeValue(null, Attributes.AUTHOR) ?: ""
            val category = xml.getAttributeValue(null, Attributes.CATEGORY) ?: ""
            val composer = xml.getAttributeValue(null, Attributes.COMPOSER) ?: ""
            val melody = xml.getAttributeValue(null, Attributes.MELODY) ?: ""
            val name = xml.getAttributeValue(null, Attributes.NAME) ?: ""
            if (name.isEmpty() || category.isEmpty()) {
                throw XmlPullParserException("Missing required attribute ${Attributes.NAME} and/or ${Attributes.CATEGORY}.")
            }
            val parts = doParseSongParts()
            Song(author, category, composer, melody, name, parts)
        } catch (ex: Exception) {
            Log.e(TAG, "Exception while parsing song: $ex")
            null
        }
    }

    private fun tryParseSongPart(type: Int): SongPart? {
        val name = xml.name
        return try {
            xml.next()
            if (XmlPullParser.END_TAG == xml.eventType) {
                // This song part was empty for some reason, ignore it
                return null
            }

            // Content of song part must be text only, no nested tags
            xml.require(XmlPullParser.TEXT, null, null)
            val songPart = tryParseText(type)
            xml.next()
            xml.require(XmlPullParser.END_TAG, null, name)
            songPart
        } catch (ex: Exception) {
            Log.e(TAG, "Exception while parsing song part: $ex")
            null
        }
    }

    private fun tryParseText(type: Int): SongPart? {
        var text = xml.text.trim()
        if (text.isEmpty()) {
            return null
        }
        text = if (SongPart.TYPE_PARAGRAPH == type) {
            // Eliminate whitespace caused by indentation but keep the line breaks internal to the text
            text.replace(LINE_BREAK_REGEX.toRegex(), "\n")
        } else {
            // Eliminate whitespace caused by indentation and strip linebreaks
            text.replace(LINE_BREAK_REGEX.toRegex(), " ")
        }
        return SongPart(type, text)
    }

    private object Attributes {
        const val AUTHOR = "author"
        const val CATEGORY = "category"
        const val COMPOSER = "composer"
        const val DESCRIPTION = "description"
        const val MELODY = "melody"
        const val NAME = "name"
        const val UPDATED = "updated"
    }

    private object Elements {
        const val COMMENT = "comment"
        const val HEADER = "header"
        const val P = "p"
        const val SONG = "song"
        const val SONGS = "songs"
    }

    companion object {
        private const val LINE_BREAK_REGEX = "\\s*\\r?\\n\\s*"

        fun parseSongbook(reader: Reader): Songbook {
            val xmlParser = XmlPullParserFactory.newInstance().newPullParser()
            xmlParser.setInput(reader)
            val instance = SongbookXmlParser(xmlParser)
            return instance.doParse()
        }
    }
}
