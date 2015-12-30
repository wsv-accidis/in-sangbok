package se.insektionen.songbook.services;

import android.util.Log;

import com.squareup.okhttp.CacheControl;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.Reader;
import java.util.List;

import se.insektionen.songbook.R;
import se.insektionen.songbook.model.Song;

/**
 * Repository which provides an abstraction layer against the underlying web requests.
 */
public final class Repository {
	private static final String TAG = Repository.class.getSimpleName();

	public void getSongs(RepositoryResultHandler<List<Song>> resultHandler, boolean noCache) {
		Request.Builder requestBuilder = new Request.Builder()
				.url(SongbookConfig.SONGBOOK_DEFAULT_URL);
		if (noCache) {
			requestBuilder.cacheControl(CacheControl.FORCE_NETWORK);
		}
		SharedHttpClient.getInstance().enqueueRequest(requestBuilder.build(), new GetSongsCallback(resultHandler));
	}

	private final class GetSongsCallback extends GetResultCallback<List<Song>> {
		protected GetSongsCallback(RepositoryResultHandler<List<Song>> resultHandler) {
			super(resultHandler);
		}

		@Override
		protected List<Song> getResult(ResponseBody responseBody) throws Exception {
			Reader streamReader = null;
			try {
				XmlPullParserFactory xmlParserFactory = XmlPullParserFactory.newInstance();
				XmlPullParser xmlParser = xmlParserFactory.newPullParser();
				streamReader = responseBody.charStream();
				xmlParser.setInput(streamReader);

return null;
			} finally {
				if (null != streamReader) {
					streamReader.close();
				}
			}
		}
	}

	private abstract class GetResultCallback<TResult> implements Callback {
		protected final RepositoryResultHandler<TResult> mResultHandler;

		protected GetResultCallback(RepositoryResultHandler<TResult> resultHandler) {
			mResultHandler = resultHandler;
		}

		@Override
		public void onFailure(Request request, IOException e) {
			Log.e(TAG, "Downloading data failed due to an IO error.", e);
			mResultHandler.onError(R.string.repository_error_unspecified_network);
		}

		@Override
		public void onResponse(Response response) throws IOException {
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
}
