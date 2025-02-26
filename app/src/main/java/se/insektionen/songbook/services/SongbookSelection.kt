package se.insektionen.songbook.services

/**
 * Default songbook which is hosted and curated in the same repo as the app.
 */
private const val SONGBOOK_DEFAULT_URL =
    "https://raw.githubusercontent.com/wsv-accidis/in-sangbok/master/sangbok/main.xml"

/**
 * Songbook from itsektionen/songlist, this is hosted in a repo in the chapter's GitHub org.
 */
private const val SONGBOOK_ITSEKTIONEN_URL =
    "https://raw.githubusercontent.com/itsektionen/songlist/master/dist/songs.xml"

/**
 * Holds possible selections for which songbook to use.
 */
enum class SongbookSelection(val value: Int, val url: String) {
    DEFAULT(0, SONGBOOK_DEFAULT_URL),
    ITSEKTIONEN(1, SONGBOOK_ITSEKTIONEN_URL);

    companion object {
        fun fromInt(value: Int): SongbookSelection = when (value) {
            DEFAULT.value -> DEFAULT
            ITSEKTIONEN.value -> ITSEKTIONEN
            else -> DEFAULT
        }
    }
}
