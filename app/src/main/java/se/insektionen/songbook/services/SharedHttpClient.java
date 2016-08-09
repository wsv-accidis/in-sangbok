package se.insektionen.songbook.services;

import android.content.Context;
import android.util.Log;

import java.io.File;

import okhttp3.Cache;
import okhttp3.OkHttpClient;

/**
 * Static wrapper for the HTTP client. This is in accordance with documented
 * best practices for OkHttpClient, which suggests using the same instance everywhere.
 */
public final class SharedHttpClient extends OkHttpClient {
	private static final int CACHE_SIZE = 1024 * 1024;
	private static final String TAG = SharedHttpClient.class.getSimpleName();
	private static OkHttpClient mInstance;

	private SharedHttpClient() {
	}

	public static OkHttpClient getInstance() {
		return mInstance;
	}

	public static void initialize(Context context) {
		OkHttpClient.Builder builder = new OkHttpClient.Builder();
		Cache cache = initializeCache(context);
		if (null != cache) {
			builder.cache(cache);
		}

		mInstance = builder.build();
	}

	private static Cache initializeCache(Context context) {
		try {
			File cacheDirectory = new File(context.getCacheDir().getAbsolutePath(), "HttpCache");
			Cache cache = new Cache(cacheDirectory, CACHE_SIZE);
			Log.d(TAG, "HTTP response cache was initialized.");
			return cache;
		} catch (Exception ex) {
			Log.w(TAG, "Failed to initialize HTTP response cache.", ex);
			return null;
		}
	}
}
