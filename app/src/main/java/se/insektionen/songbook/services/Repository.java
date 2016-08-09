package se.insektionen.songbook.services;

import android.util.Log;

import okhttp3.CacheControl;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.Reader;

import se.insektionen.songbook.R;
import se.insektionen.songbook.model.Songbook;

/**
 * Repository which provides an abstraction layer against the underlying web requests.
 */
public final class Repository {
	private static final String TAG = Repository.class.getSimpleName();
	private static Songbook mCachedSongbook;

	public void getSongbook(RepositoryResultHandler<Songbook> resultHandler, boolean noCache) {
		if (!noCache && null != mCachedSongbook) {
			resultHandler.onSuccess(mCachedSongbook);
			return;
		}

		Request.Builder requestBuilder = new Request.Builder().url(SongbookConfig.SONGBOOK_DEFAULT_URL);
		if (noCache) {
			requestBuilder.cacheControl(CacheControl.FORCE_NETWORK);
		}
		SharedHttpClient.getInstance().newCall(requestBuilder.build()).enqueue(new GetSongbookCallback(resultHandler));
	}

	private abstract class GetResultCallback<TResult> implements Callback {
		protected final RepositoryResultHandler<TResult> mResultHandler;

		protected GetResultCallback(RepositoryResultHandler<TResult> resultHandler) {
			mResultHandler = resultHandler;
		}

		@Override
		public void onFailure(Call call, IOException e) {
			Log.e(TAG, "Downloading data failed due to an IO error.", e);
			mResultHandler.onError(R.string.repository_error_unspecified_network);
		}

		@Override
		public void onResponse(Call call, Response response) throws IOException {
			Log.i(TAG, "Download response received with HTTP status = " + response.code() + ", cached = " + (null == response.networkResponse()) + ".");

			if (!response.isSuccessful()) {
				Log.e(TAG, "Downloading data failed because an unsuccessful HTTP status code (" + response.code() + ") was returned.");
				mResultHandler.onError(R.string.repository_error_unspecified_protocol);
				return;
			}

			try {
				if (null != response.body()) {
					ResponseBody responseBody = response.body();
					TResult result = getResult(responseBody);
					mResultHandler.onSuccess(result);
					Log.e(TAG, "Downloading data completed.");
				} else {
					mResultHandler.onError(R.string.repository_error_unspecified_protocol);
				}
			} catch (XmlPullParserException e) {
				Log.e(TAG, "Downloading data failed due to an XML parsing error.", e);
				mResultHandler.onError(R.string.repository_error_xml_parse_error);
			} catch (Exception e) {
				Log.e(TAG, "Downloading data failed due to an unexpected error.", e);
				mResultHandler.onError(R.string.repository_error_unspecified_protocol);
			}
		}

		protected abstract TResult getResult(ResponseBody responseBody) throws Exception;
	}

	private final class GetSongbookCallback extends GetResultCallback<Songbook> {
		protected GetSongbookCallback(RepositoryResultHandler<Songbook> resultHandler) {
			super(resultHandler);
		}

		@Override
		protected Songbook getResult(ResponseBody responseBody) throws Exception {
			Reader streamReader = null;
			try {
				streamReader = responseBody.charStream();
				Songbook songbook = SongbookXmlParser.parseSongbook(streamReader);
				mCachedSongbook = songbook;
				return songbook;
			} finally {
				if (null != streamReader) {
					streamReader.close();
				}
			}
		}
	}
}
