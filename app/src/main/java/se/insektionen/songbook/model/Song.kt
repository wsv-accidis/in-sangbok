package se.insektionen.songbook.model

import android.os.Bundle
import java.util.Locale

/**
 * Data model for a song.
 */
data class Song(
    val author: String,
    val category: String,
    val composer: String,
    val melody: String,
    val name: String,
    val parts: List<SongPart>
) {
    private val searchText: String = createSearchText(name, melody, parts)

    fun firstLineOfSong(): String {
        val firstParagraph = parts.firstOrNull { SongPart.TYPE_PARAGRAPH == it.type }?.text
        if (firstParagraph.isNullOrBlank()) return ""
        val firstLineBreak = firstParagraph.indexOf('\n')
        return if (-1 == firstLineBreak) firstParagraph
        else firstParagraph.substring(0, firstLineBreak)
    }

    fun matches(search: String): Boolean =
        searchText.contains(search.lowercase(Locale.getDefault()))

    fun toBundle(): Bundle =
        with(Bundle()) {
            putString(AUTHOR, author)
            putString(CATEGORY, category)
            putString(COMPOSER, composer)
            putString(MELODY, melody)
            putString(NAME, name)
            putIntArray(PARTS_TYPES, parts.map { it.type }.toIntArray())
            putStringArray(PARTS_TEXTS, parts.map { it.text }.toTypedArray())
            return this
        }

    override fun toString(): String = name

    private fun createSearchText(name: String, melody: String, parts: List<SongPart>): String {
        val builder = StringBuilder()
        builder.append(name.lowercase(Locale.getDefault()))
        if (melody.isNotEmpty()) {
            builder.append(' ')
            builder.append(melody.lowercase(Locale.getDefault()))
        }
        parts.filter { SongPart.TYPE_PARAGRAPH == it.type }
            .forEach {
                builder.append(' ')
                builder.append(it.text.lowercase(Locale.getDefault()))
            }
        return builder.toString()
    }

    companion object {
        private const val AUTHOR = "author"
        private const val CATEGORY = "category"
        private const val COMPOSER = "composer"
        private const val MELODY = "melody"
        private const val NAME = "name"
        private const val PARTS_TEXTS = "partsTexts"
        private const val PARTS_TYPES = "partsTypes"

        fun fromBundle(bundle: Bundle): Song {
            val author = bundle.getString(AUTHOR) ?: ""
            val category = bundle.getString(CATEGORY) ?: ""
            val composer = bundle.getString(COMPOSER) ?: ""
            val melody = bundle.getString(MELODY) ?: ""
            val name = bundle.getString(NAME) ?: ""
            val types = bundle.getIntArray(PARTS_TYPES) ?: IntArray(0)
            val texts = bundle.getStringArray(PARTS_TEXTS) ?: emptyArray()
            require(types.size == texts.size)
            val parts = types.zip(texts) { type, text -> SongPart(type, text) }
            return Song(author, category, composer, melody, name, parts)
        }
    }
}
