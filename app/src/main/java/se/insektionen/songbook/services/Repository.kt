package se.insektionen.songbook.services

import android.util.Log
import okhttp3.CacheControl
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody
import org.xmlpull.v1.XmlPullParserException
import se.insektionen.songbook.R
import se.insektionen.songbook.model.Songbook
import se.insektionen.songbook.utils.TAG
import java.io.IOException

/**
 * Repository which provides an abstraction layer against the underlying web requests.
 */
object Repository {
    private var cachedSelection: SongbookSelection = SongbookSelection.DEFAULT
    private var cachedSongbook: Songbook? = null

    fun getSongbook(
        selection: SongbookSelection,
        resultHandler: (Result<Songbook>) -> Unit,
        noCache: Boolean
    ) {
        if (!noCache && null != cachedSongbook && selection == cachedSelection) {
            resultHandler(Result.success(cachedSongbook!!))
            return
        }

        val requestBuilder: Request.Builder = Request.Builder().url(selection.url)
        if (noCache) {
            requestBuilder.cacheControl(CacheControl.FORCE_NETWORK)
        }

        SharedHttpClient.instance.newCall(requestBuilder.build()).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e(TAG, "Downloading data failed due to an IO error.", e)
                resultHandler(RepositoryException(R.string.repository_error_unspecified_network).asResult())
            }

            override fun onResponse(call: Call, response: Response) {
                Log.i(
                    TAG,
                    "Download response received with HTTP status = ${response.code}, cached = ${null == response.networkResponse}."
                )

                if (response.isSuccessful) {
                    try {
                        if (null != response.body) {
                            val result = parseSongbook(response.body!!)
                            resultHandler(Result.success(result))
                            Log.d(TAG, "Downloading data completed.")
                        } else {
                            resultHandler(RepositoryException(R.string.repository_error_unspecified_protocol).asResult())
                        }
                    } catch (e: XmlPullParserException) {
                        Log.e(TAG, "Downloading data failed due to an XML parsing error.", e)
                        resultHandler(RepositoryException(R.string.repository_error_xml_parse_error).asResult())
                    } catch (e: Exception) {
                        Log.e(TAG, "Downloading data failed due to an unexpected error.", e)
                        resultHandler(RepositoryException(R.string.repository_error_unspecified_protocol).asResult())
                    }
                } else {
                    Log.e(
                        TAG,
                        "Downloading data failed because an unsuccessful HTTP status code (${response.code}) was returned."
                    )
                    resultHandler(RepositoryException(R.string.repository_error_unspecified_protocol).asResult())
                }
            }

            private fun parseSongbook(responseBody: ResponseBody): Songbook {
                responseBody.charStream().use { streamReader ->
                    val songbook = SongbookXmlParser.parseSongbook(streamReader)
                    cachedSongbook = songbook
                    cachedSelection = selection
                    return songbook
                }
            }
        })
    }
}
