package se.insektionen.songbook.services;

import android.content.Context;
import android.util.Log;

import com.squareup.okhttp.Cache;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.File;
import java.io.IOException;

/**
 * Static wrapper for the HTTP client. This is in accordance with documented
 * best practices for OkHttpClient, which suggests using the same instance everywhere.
 */
public final class SharedHttpClient extends OkHttpClient {
    private static final int CACHE_SIZE = 1024 * 1024;
    private static final String TAG = SharedHttpClient.class.getSimpleName();
    private static final SharedHttpClient mInstance;

    private SharedHttpClient() {
    }

    static {
        mInstance = new SharedHttpClient();
    }

    public static SharedHttpClient getInstance() {
        return mInstance;
    }

    public static void initializeCache(Context context) {
        try {
            File cacheDirectory = new File(context.getCacheDir().getAbsolutePath(), "HttpCache");
            Cache cache = new Cache(cacheDirectory, CACHE_SIZE);
            mInstance.setCache(cache);
            Log.d(TAG, "HTTP response cache was initialized.");
        } catch (Exception ex) {
            Log.w(TAG, "Failed to initialize HTTP response cache.", ex);
        }
    }

    public Call enqueueRequest(Request request, Callback callback) {
        Call call = newCall(request);
        call.enqueue(callback);
        return call;
    }

    public Response executeRequest(Request request) throws IOException {
        return newCall(request).execute();
    }

    @Override
    public Call newCall(Request request) {
        Log.d(TAG, "Created a call for URL = " + request.urlString());
        return super.newCall(request);
    }
}
