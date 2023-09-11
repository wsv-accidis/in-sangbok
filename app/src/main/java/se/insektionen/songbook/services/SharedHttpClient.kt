package se.insektionen.songbook.services

import android.content.Context
import android.util.Log
import okhttp3.Cache
import okhttp3.OkHttpClient
import se.insektionen.songbook.utils.TAG
import java.io.File

/**
 * Static wrapper for the HTTP client. This is in accordance with documented
 * best practices for OkHttpClient, which suggests using the same instance everywhere.
 */
object SharedHttpClient {
    private const val CACHE_SIZE = 1024 * 1024

    lateinit var instance: OkHttpClient

    fun initialize(context: Context) = with(OkHttpClient.Builder()) {
        initializeCache(context).onSuccess { cache(it) }
        instance = build()
    }

    private fun initializeCache(context: Context): Result<Cache> = try {
        val cacheDirectory = File(context.cacheDir.absolutePath, "HttpCache")
        val cache = Cache(cacheDirectory, CACHE_SIZE.toLong())
        Log.d(TAG, "HTTP response cache was initialized.")
        Result.success(cache)
    } catch (ex: Exception) {
        Log.w(TAG, "Failed to initialize HTTP response cache.", ex)
        Result.failure(ex)
    }
}
