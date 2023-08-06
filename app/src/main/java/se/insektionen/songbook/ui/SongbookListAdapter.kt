package se.insektionen.songbook.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.core.content.getSystemService
import se.insektionen.songbook.R
import se.insektionen.songbook.model.Song
import java.util.Locale

/**
 * List adapter for the list of songs in a songbook.
 */
class SongbookListAdapter(
    context: Context,
    initialList: List<Song>
) : BaseAdapter(), Filterable {
    private val filter: Filter = SongbookListFilter()
    private var filteredList: List<Song> = initialList
    private val inflater: LayoutInflater = context.getSystemService()!!
    private val list: List<Song> = initialList

    override fun getCount(): Int = filteredList.size

    override fun getFilter(): Filter = filter

    override fun getItem(position: Int): Any = filteredList[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = convertView ?: inflater.inflate(R.layout.list_item_song, parent, false)
        val song = filteredList[position]
        view.findViewById<TextView>(R.id.song_list_primary).apply { text = song.name }
        view.findViewById<TextView>(R.id.song_list_secondary)
            .apply { text = formatFirstLineOfSong(song.firstLineOfSong()) }
        view.findViewById<TextView>(R.id.song_list_tertiary).apply { text = song.category }
        return view
    }

    private fun formatFirstLineOfSong(line: String): String {
        var result = line
        TRIM_START.forEach {
            if (result.startsWith(it)) {
                result = result.substring(it.length)
            }
        }
        TRIM_END.forEach {
            if (result.endsWith(it)) {
                result = result.substring(0, result.length - it.length)
            }
        }
        return result.trim() + ELLIPSIS
    }

    private inner class SongbookListFilter : Filter() {
        override fun performFiltering(constraint: CharSequence): FilterResults {
            val resultList =
                if (constraint.isNotEmpty()) {
                    /*
                     * The query is of the format {textFilter}?category={categoryFilter}
                     * Both parts are optional.
                     */
                    val query = constraint.toString()
                    val categoryFilter: String
                    val textFilter: String

                    if (query.contains(CATEGORY_QUERY)) {
                        val idx = query.indexOf(CATEGORY_QUERY)
                        categoryFilter = query.substring(idx + CATEGORY_QUERY.length)
                        textFilter = query.substring(0, idx)
                    } else {
                        categoryFilter = ""
                        textFilter = query
                    }
                    list.filter { filter(it, textFilter, categoryFilter) }
                } else {
                    list
                }

            return FilterResults().also {
                it.values = resultList
                it.count = resultList.size
            }
        }

        @Suppress("UNCHECKED_CAST")
        override fun publishResults(constraint: CharSequence, results: FilterResults) {
            filteredList = results.values as List<Song>
            if (results.count > 0) {
                notifyDataSetChanged()
            } else {
                notifyDataSetInvalidated()
            }
        }

        private fun filter(song: Song, textFilter: String, categoryFilter: String): Boolean {
            if (categoryFilter.isNotEmpty() && song.category != categoryFilter) {
                // Drop any song that does not match the selected category
                return false
            }
            if (textFilter.isEmpty()) {
                // Accept any song if the text filter is empty
                return true
            }
            // Otherwise, apply the text filter
            return textFilter.isNotEmpty() && song.matches(textFilter.lowercase(Locale.getDefault()))
        }
    }

    companion object {
        const val CATEGORY_QUERY = "?category="
        private const val ELLIPSIS = "..."
        private val TRIM_END = arrayOf(".", ",", "!", "?")
        private val TRIM_START = arrayOf("*", "//:")
    }
}
