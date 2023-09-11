package se.insektionen.songbook.services

/**
 * Default songbook which is hosted and curated in the same repo as the app.
 */
private const val SONGBOOK_DEFAULT_URL =
    "https://raw.githubusercontent.com/wsv-accidis/in-sangbok/master/sangbok/main.xml"

/**
 * Songbook from insektionen/songlist, this is hosted in a repo in the chapter's GitHub org.
 */
private const val SONGBOOK_INSEKTIONEN_URL =
    "https://raw.githubusercontent.com/insektionen/songlist/master/dist/songs.xml"

/**
 * Holds possible selections for which songbook to use.
 */
enum class SongbookSelection(val value: Int, val url: String) {
    DEFAULT(0, SONGBOOK_DEFAULT_URL),
    INSEKTIONEN(1, SONGBOOK_INSEKTIONEN_URL);

    companion object {
        fun fromInt(value: Int): SongbookSelection = when (value) {
            DEFAULT.value -> DEFAULT
            INSEKTIONEN.value -> INSEKTIONEN
            else -> DEFAULT
        }
    }
}
