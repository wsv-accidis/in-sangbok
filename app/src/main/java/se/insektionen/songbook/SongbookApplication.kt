package se.insektionen.songbook

import android.app.Application
import se.insektionen.songbook.services.SharedHttpClient
import java.util.Locale

/**
 * Main application class.
 */
class SongbookApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Locale.setDefault(Locale("sv", "SE"))
        SharedHttpClient.initialize(applicationContext)
    }
}
