package se.insektionen.songbook.model

/**
 * Data model for part of a song.
 */
data class SongPart(
    val type: Int,
    val text: String
) {
    companion object {
        const val TYPE_COMMENT = 1
        const val TYPE_HEADER = 2
        const val TYPE_PARAGRAPH = 0
    }
}
