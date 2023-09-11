package se.insektionen.songbook.services

import android.content.Context
import android.content.SharedPreferences

/**
 * Wrapper for Android shared preferences. Used to store application data between launches.
 */
class Preferences(context: Context) {
    private val prefs: SharedPreferences

    init {
        prefs = context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE)
    }

    var songScaleFactor: Float
        get() = prefs.getFloat(SONG_SCALE_FACTOR, 1.0f)
        set(scaleFactor) {
            prefs.edit().putFloat(SONG_SCALE_FACTOR, scaleFactor).apply()
        }

    var songbookSelection: SongbookSelection
        get() = SongbookSelection.fromInt(
            prefs.getInt(SONGBOOK_SELECTION, SongbookSelection.DEFAULT.value)
        )
        set(selection) {
            prefs.edit().putInt(SONGBOOK_SELECTION, selection.value).apply()
        }

    companion object {
        private const val PREFERENCES_FILE = "SongbookPreferences"
        private const val SONG_SCALE_FACTOR = "songScaleFactor"
        private const val SONGBOOK_SELECTION = "songbookSelection"
    }
}
