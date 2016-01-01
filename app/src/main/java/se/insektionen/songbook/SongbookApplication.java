package se.insektionen.songbook;

import android.app.Application;

import java.util.Locale;

import se.insektionen.songbook.services.SharedHttpClient;

/**
 * Main application class.
 */
public final class SongbookApplication extends Application {
	@Override
	public void onCreate() {
		super.onCreate();
		Locale.setDefault(new Locale("sv", "SE"));
		SharedHttpClient.initializeCache(getApplicationContext());
	}
}
