package se.insektionen.songbook.services;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Wrapper for Android shared preferences. Used to store application data between launches.
 */
public final class Preferences {
	private static final String PREFERENCES_FILE = "SongbookPreferences";
	private static final String TAG = Preferences.class.getSimpleName();
	private final SharedPreferences mPrefs;

	public Preferences(Context context) {
		mPrefs = context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
	}

	public float getSongScaleFactor() {
		return mPrefs.getFloat(Keys.SONG_SCALE_FACTOR, 1.0f);
	}

	public void setSongScaleFactor(float scaleFactor) {
		mPrefs.edit().putFloat(Keys.SONG_SCALE_FACTOR, scaleFactor).apply();
	}

	private static class Keys {
		public static final String SONG_SCALE_FACTOR = "songScaleFactor";

		private Keys() {
		}
	}
}