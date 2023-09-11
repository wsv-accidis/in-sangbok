package se.insektionen.songbook.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import se.insektionen.songbook.BuildConfig
import se.insektionen.songbook.R
import se.insektionen.songbook.services.Preferences
import se.insektionen.songbook.services.Repository
import se.insektionen.songbook.ui.MainActivity.HasNavigationItem
import se.insektionen.songbook.utils.hideSoftKeyboard

/**
 * Fragment which displays information about the app and the current songbook.
 */
class AboutFragment : Fragment(), HasNavigationItem {
    private val handler = Handler(Looper.getMainLooper())

    override val itemId = R.id.nav_about

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_about, container, false)

        intArrayOf(R.id.about_para1, R.id.about_para2, R.id.about_para3).forEach {
            view.findViewById<TextView>(it).movementMethod = LinkMovementMethod.getInstance()
        }

        val appInfoView = view.findViewById<TextView>(R.id.about_app_info)
        appInfoView.text =
            String.format(getString(R.string.about_app_info), BuildConfig.VERSION_NAME)

        val prefs = Preferences(requireContext())
        view.findViewById<TextView>(R.id.about_songbook_source).text =
            String.format(
                getString(R.string.about_songbook_source), prefs.songbookSelection.url
            )

        val songbookInfoView = view.findViewById<TextView>(R.id.about_songbook_info)
        Repository.getSongbook(prefs.songbookSelection, { result ->
            result.onSuccess {
                handler.post {
                    songbookInfoView.text = String.format(
                        getString(R.string.about_songbook_info),
                        it.description,
                        it.updated
                    )
                }
            }
        }, false)

        return view
    }

    override fun onResume() {
        super.onResume()
        requireContext().hideSoftKeyboard(view)
    }
}
