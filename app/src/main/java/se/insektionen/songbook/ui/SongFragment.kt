package se.insektionen.songbook.ui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import se.insektionen.songbook.R
import se.insektionen.songbook.model.Song
import se.insektionen.songbook.model.SongPart
import se.insektionen.songbook.services.Preferences
import se.insektionen.songbook.ui.MainActivity.HasNavigationItem
import se.insektionen.songbook.utils.TAG

/**
 * Fragment which displays a single song.
 */
class SongFragment : Fragment(), HasNavigationItem {
    private var currentTextSize = 0f
    private var initialTextSize = 0f
    private lateinit var prefs: Preferences
    private var scaleFactor = 1.0
    private val songPartViews: MutableList<TextView> = ArrayList()

    override val itemId = R.id.nav_list_songs

    override fun onAttach(context: Context) {
        super.onAttach(context)
        prefs = Preferences(context)
        initialTextSize = resources.getDimension(R.dimen.song_part_text_size)
        scaleFactor = prefs.songScaleFactor.toDouble()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_song, container, false)

        with(Song.fromBundle(requireArguments())) {
            setTextIfNotEmpty(view, R.id.song_category, category)
            setTextIfNotEmpty(view, R.id.song_name, name)
            setTextIfNotEmpty(view, R.id.song_author, author)

            val melodyComposer = when {
                melody.isEmpty() -> ""
                composer.isEmpty() -> melody
                else -> String.format(
                    getString(R.string.song_melody_composer_format), melody, composer
                )
            }
            setTextWithPrefixIfNotEmpty(
                view, R.id.song_melody, R.string.song_melody, melodyComposer
            )

            val songLayout = view.findViewById<LinearLayout>(R.id.song_layout)
            populateSongLayout(songLayout, parts)
        }

        view.setOnTouchListener(ViewTouchListener(requireContext()))
        return view
    }

    override fun onPause() {
        super.onPause()
        prefs.songScaleFactor = scaleFactor.toFloat()
    }

    private fun populateSongLayout(songLayout: LinearLayout, parts: List<SongPart>) {
        songLayout.removeAllViews()
        songPartViews.clear()

        val context = requireContext()
        val defaultColor = ContextCompat.getColor(context, R.color.black)
        val commentColor = ContextCompat.getColor(context, R.color.insektionen)
        val topMargin = context.resources.getDimensionPixelSize(R.dimen.song_part_top_margin)

        for ((type, text) in parts) {
            val textView = TextView(context)
            textView.text = text
            textView.setTextSize(
                TypedValue.COMPLEX_UNIT_PX, (initialTextSize * scaleFactor).toFloat()
            )
            if (SongPart.TYPE_COMMENT == type) {
                textView.setTypeface(null, Typeface.ITALIC)
                textView.setTextColor(commentColor)
            } else if (SongPart.TYPE_HEADER == type) {
                textView.setTypeface(null, Typeface.BOLD)
                textView.setTextColor(defaultColor)
            } else {
                textView.setTextColor(defaultColor)
            }
            val layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
            )
            layoutParams.setMargins(0, topMargin, 0, 0)
            songLayout.addView(textView, layoutParams)
            songPartViews.add(textView)
        }
    }

    private fun refreshTextSize() {
        val newTextSize = (initialTextSize * scaleFactor).toFloat()
        if (newTextSize != currentTextSize) {
            currentTextSize = newTextSize
            Log.d(
                TAG, "Scaling view, scale factor = $scaleFactor, actual size = $currentTextSize px."
            )
            songPartViews.forEach { it.setTextSize(TypedValue.COMPLEX_UNIT_PX, currentTextSize) }
        }
    }

    private fun setTextIfNotEmpty(view: View, textViewId: Int, str: String) =
        with(view.findViewById<TextView>(textViewId)) {
            if (str.isNotEmpty()) {
                text = str
                visibility = View.VISIBLE
            } else {
                visibility = View.GONE
            }
        }

    private fun setTextWithPrefix(textView: TextView, prefixResId: Int, str: String) {
        val prefixStr = getString(prefixResId)
        val spanStr = SpannableString("$prefixStr $str")
        val foregroundColor = ContextCompat.getColor(requireContext(), R.color.darkgray)
        spanStr.setSpan(
            ForegroundColorSpan(foregroundColor),
            0,
            prefixStr.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        textView.text = spanStr
    }

    private fun setTextWithPrefixIfNotEmpty(
        view: View,
        textViewId: Int,
        prefixResId: Int,
        str: String
    ) = with(view.findViewById<TextView>(textViewId)) {
        if (str.isNotEmpty()) {
            setTextWithPrefix(this, prefixResId, str)
            visibility = View.VISIBLE
        } else {
            visibility = View.GONE
        }
    }

    private inner class ScaleGestureListener : SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            scaleFactor *= detector.scaleFactor.toDouble()
            if (scaleFactor < MIN_SCALE_FACTOR) {
                scaleFactor = MIN_SCALE_FACTOR
            } else if (scaleFactor > MAX_SCALE_FACTOR) {
                scaleFactor = MAX_SCALE_FACTOR
            }
            refreshTextSize()
            return true
        }
    }

    private inner class ViewTouchListener(context: Context) : OnTouchListener {
        private val gestureDetector: ScaleGestureDetector =
            ScaleGestureDetector(context, ScaleGestureListener())

        @SuppressLint("ClickableViewAccessibility")
        override fun onTouch(v: View, event: MotionEvent): Boolean {
            gestureDetector.onTouchEvent(event)
            return false
        }
    }

    companion object {
        private const val MIN_SCALE_FACTOR = .8
        private const val MAX_SCALE_FACTOR = 5.0

        fun createInstance(song: Song): SongFragment =
            SongFragment().also { it.arguments = song.toBundle() }
    }
}
