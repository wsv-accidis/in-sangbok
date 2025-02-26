package se.insektionen.songbook.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import se.insektionen.songbook.R
import se.insektionen.songbook.services.Preferences
import se.insektionen.songbook.services.SongbookSelection
import se.insektionen.songbook.utils.TAG
import se.insektionen.songbook.utils.hideSoftKeyboard

class SelectionFragment : Fragment(), MainActivity.HasNavigationItem {
    private lateinit var prefs: Preferences

    override val itemId = R.id.nav_selection

    override fun onAttach(context: Context) {
        super.onAttach(context)
        prefs = Preferences(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_selection, container, false)

        val radioGroup = view.findViewById<RadioGroup>(R.id.selection_radiogroup)
        radioGroup.setOnCheckedChangeListener { _, id ->
            prefs.songbookSelection = selectionIdFromRadioButton(id)
        }
        radioGroup.check(radioButtonFromSelectionId(prefs.songbookSelection))

        return view
    }

    override fun onResume() {
        super.onResume()
        requireContext().hideSoftKeyboard(view)
    }

    private fun radioButtonFromSelectionId(selection: SongbookSelection) =
        if (SongbookSelection.DEFAULT == selection) {
            R.id.selection_radio_default
        } else if (SongbookSelection.ITSEKTIONEN == selection) {
            R.id.selection_radio_insektionen
        } else {
            Log.e(TAG, "No radio button for current songbook selection.")
            R.id.selection_radio_default
        }

    private fun selectionIdFromRadioButton(@IdRes id: Int) =
        if (R.id.selection_radio_default == id) {
            SongbookSelection.DEFAULT
        } else if (R.id.selection_radio_insektionen == id) {
            SongbookSelection.ITSEKTIONEN
        } else {
            Log.e(TAG, "Unknown radio button selected.")
            SongbookSelection.DEFAULT
        }

}