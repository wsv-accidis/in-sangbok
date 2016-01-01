package se.insektionen.songbook.ui;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import se.insektionen.songbook.R;
import se.insektionen.songbook.model.Song;
import se.insektionen.songbook.model.SongPart;
import se.insektionen.songbook.services.Preferences;

/**
 * Fragment which displays a single song.
 */
public final class SongFragment extends Fragment implements MainActivity.HasNavigationItem {
	private final static String TAG = SongFragment.class.getSimpleName();
	private static final double mMaxScaleFactor = 5.0;
	private static final double mMinScaleFactor = .8;
	private final List<TextView> mSongPartViews = new ArrayList<>();
	private float mCurrentTextSize;
	private float mInitialTextSize;
	private Preferences mPrefs;
	private double mScaleFactor = 1.0;

	public static SongFragment createInstance(Song song) {
		SongFragment fragment = new SongFragment();
		fragment.setArguments(song.toBundle());
		return fragment;
	}

	@Override
	public int getItemId() {
		return R.id.nav_list_songs;
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		mPrefs = new Preferences(context);
		mInitialTextSize = getResources().getDimension(R.dimen.song_part_text_size);
		mScaleFactor = mPrefs.getSongScaleFactor();
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_song, container, false);
		Song song = Song.fromBundle(getArguments());

		setTextIfNotEmpty(view, R.id.song_category, song.getCategory());
		setTextIfNotEmpty(view, R.id.song_name, song.getName());
		setTextIfNotEmpty(view, R.id.song_author, song.getAuthor());

		String melodyComposer = !TextUtils.isEmpty(song.getMelody()) ?
				(TextUtils.isEmpty(song.getComposer()) ? song.getMelody() : String.format(getString(R.string.song_melody_composer_format), song.getMelody(), song.getComposer())) : "";
		setTextWithPrefixIfNotEmpty(view, R.id.song_melody, R.string.song_melody, melodyComposer);

		LinearLayout songLayout = (LinearLayout) view.findViewById(R.id.song_layout);
		populateSongLayout(songLayout, song.getParts());

		view.setOnTouchListener(new ViewTouchListener(getContext()));
		return view;
	}

	@Override
	public void onPause() {
		super.onPause();
		mPrefs.setSongScaleFactor((float) mScaleFactor);
	}

	private void populateSongLayout(LinearLayout songLayout, List<SongPart> parts) {
		songLayout.removeAllViews();
		mSongPartViews.clear();

		Context context = getContext();
		int defaultColor = ContextCompat.getColor(context, R.color.black);
		int commentColor = ContextCompat.getColor(context, R.color.colorPrimaryDark);
		int topMargin = context.getResources().getDimensionPixelSize(R.dimen.song_part_top_margin);

		for (SongPart part : parts) {
			TextView textView = new TextView(context);
			textView.setText(part.getText());
			textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, (float) (mInitialTextSize * mScaleFactor));

			if (SongPart.TYPE_COMMENT == part.getType()) {
				textView.setTypeface(null, Typeface.ITALIC);
				textView.setTextColor(commentColor);
			} else if (SongPart.TYPE_HEADER == part.getType()) {
				textView.setTypeface(null, Typeface.BOLD);
				textView.setTextColor(defaultColor);
			} else {
				textView.setTextColor(defaultColor);
			}

			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			layoutParams.setMargins(0, topMargin, 0, 0);

			songLayout.addView(textView, layoutParams);
			mSongPartViews.add(textView);
		}
	}

	private void refreshTextSize() {
		float newTextSize = (float) (mInitialTextSize * mScaleFactor);
		if (newTextSize != mCurrentTextSize) {
			mCurrentTextSize = newTextSize;
			Log.d(TAG, "Scaling view, scale factor = " + mScaleFactor + ", actual size = " + mCurrentTextSize + " px.");
			for (TextView textView : mSongPartViews) {
				textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mCurrentTextSize);
			}
		}
	}

	private void setTextIfNotEmpty(View view, int textViewId, String str) {
		TextView textView = (TextView) view.findViewById(textViewId);
		if (!TextUtils.isEmpty(str)) {
			textView.setText(str);
			textView.setVisibility(View.VISIBLE);
		} else {
			textView.setVisibility(View.GONE);
		}
	}

	private void setTextWithPrefix(TextView textView, int prefixResId, String str) {
		String prefixStr = getString(prefixResId);
		SpannableString spanStr = new SpannableString(prefixStr + " " + str);
		int foregroundColor = ContextCompat.getColor(getContext(), R.color.darkgray);
		spanStr.setSpan(new ForegroundColorSpan(foregroundColor), 0, prefixStr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		textView.setText(spanStr);
	}

	private void setTextWithPrefixIfNotEmpty(View view, int textViewId, int prefixResId, String str) {
		TextView textView = (TextView) view.findViewById(textViewId);

		if (!TextUtils.isEmpty(str)) {
			setTextWithPrefix(textView, prefixResId, str);
			textView.setVisibility(View.VISIBLE);
		} else {
			textView.setVisibility(View.GONE);
		}
	}

	private final class ScaleGestureListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
		@Override
		public boolean onScale(ScaleGestureDetector detector) {
			mScaleFactor *= detector.getScaleFactor();
			if (mScaleFactor < mMinScaleFactor) {
				mScaleFactor = mMinScaleFactor;
			} else if (mScaleFactor > mMaxScaleFactor) {
				mScaleFactor = mMaxScaleFactor;
			}

			refreshTextSize();
			return true;
		}
	}

	private final class ViewTouchListener implements View.OnTouchListener {
		private ScaleGestureDetector mGestureDetector;

		public ViewTouchListener(Context context) {
			mGestureDetector = new ScaleGestureDetector(context, new ScaleGestureListener());
		}

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			mGestureDetector.onTouchEvent(event);
			return false;
		}
	}
}
