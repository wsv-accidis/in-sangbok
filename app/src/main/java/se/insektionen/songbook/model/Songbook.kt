package se.insektionen.songbook.model

/**
 * Data model for a songbook.
 */
data class Songbook(
    val description: String,
    val updated: String,
    val songs: List<Song>
) {
    val categories: List<String> = songs.map { it.category }.distinct()
}
