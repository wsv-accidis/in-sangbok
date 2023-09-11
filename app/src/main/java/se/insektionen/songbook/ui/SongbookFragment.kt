package se.insektionen.songbook.ui

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Parcelable
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.SubMenu
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.ListFragment
import se.insektionen.songbook.R
import se.insektionen.songbook.model.Song
import se.insektionen.songbook.model.Songbook
import se.insektionen.songbook.services.Preferences
import se.insektionen.songbook.services.Repository
import se.insektionen.songbook.services.RepositoryException
import se.insektionen.songbook.ui.MainActivity.HasMenu
import se.insektionen.songbook.ui.MainActivity.HasNavigationItem
import se.insektionen.songbook.utils.TAG
import se.insektionen.songbook.utils.hideSoftKeyboard
import java.util.Locale

/**
 * Fragment which displays the list of songs.
 */
class SongbookFragment : ListFragment(), HasNavigationItem, HasMenu {
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var clearSearchButton: ImageButton
    private var filterCategory: String = ""
    private var isLoaded = false
    private var listAdapter: SongbookListAdapter? = null
    private var listState: Parcelable? = null
    private var searchQuery: String = ""
    private lateinit var searchText: EditText
    private var songbook: Songbook? = null

    override val itemId = R.id.nav_list_songs

    override val menu = R.menu.songbook

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        val filterItem = menu.findItem(R.id.songbook_filter_category)
        if (null != filterItem && null != songbook) {
            val subMenu: SubMenu = filterItem.subMenu!!
            subMenu.clear()
            subMenu.add(Menu.NONE, MENU_CLEAR_FILTER_ID, Menu.NONE, R.string.songbook_show_all)
            songbook!!.categories.forEachIndexed { idx, category ->
                subMenu.add(
                    MENU_CATEGORIES_ID,
                    MENU_CATEGORIES_ID + idx,
                    Menu.NONE,
                    category
                )
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root = super.onCreateView(inflater, container, savedInstanceState) as ViewGroup

        // Extract the internal list container from the root view
        val listContainer = root.findViewById<View>(INTERNAL_LIST_CONTAINER_ID)
        root.removeView(listContainer)

        // Put the internal list container inside our custom container
        val outerContainer = inflater.inflate(R.layout.fragment_songbook, root, false)
        val innerContainer = outerContainer.findViewById<FrameLayout>(R.id.list_container)
        innerContainer.addView(listContainer)

        // Put the custom container inside the root
        root.addView(
            outerContainer,
            FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )

        searchText = outerContainer.findViewById(R.id.songbook_search)
        searchText.addTextChangedListener(SearchChangedListener())
        clearSearchButton = outerContainer.findViewById(R.id.songbook_search_clear)
        clearSearchButton.setOnClickListener(ClearSearchClickedListener())

        if (null != savedInstanceState) {
            searchQuery = savedInstanceState.getString(STATE_SEARCH_QUERY, "")
            filterCategory = savedInstanceState.getString(STATE_FILTER_CATEGORY, "")
            if (filterCategory.isNotEmpty()) {
                searchText.hint = String.format(
                    getString(R.string.songbook_search_hint_with_category_format),
                    filterCategory
                )
            }
        }
        return root
    }

    override fun onListItemClick(l: ListView, v: View, position: Int, id: Long) {
        if (null == listAdapter || position < 0 || position >= listAdapter!!.count) {
            return
        }
        val song = listAdapter!!.getItem(position) as Song
        val fragment = SongFragment.createInstance(song)
        val activity: Activity? = activity
        if (activity is MainActivity) {
            activity.hideSoftKeyboard(view)
            listState = listView.onSaveInstanceState()
            activity.openFragment(fragment)
        } else {
            Log.e(TAG, "Activity holding fragment is not MainActivity!")
        }
    }

    override fun onMenuItemSelected(item: MenuItem): Boolean =
        when {
            MENU_CLEAR_FILTER_ID == item.itemId -> {
                onClearFilterSelected()
                true
            }

            MENU_CATEGORIES_ID == item.groupId -> {
                onFilterCategorySelected(item)
                true
            }

            else -> false
        }

    override fun onResume() {
        super.onResume()
        setEmptyText(getString(R.string.songbook_list_empty))
        searchText.isEnabled = false
        clearSearchButton.isEnabled = false
        if (!isLoaded) {
            val prefs = Preferences(requireContext())
            Repository.getSongbook(prefs.songbookSelection, this::onSongbookLoaded, false)
        } else {
            initializeList()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (null != view) {
            listState = listView.onSaveInstanceState()
            outState.putParcelable(STATE_LIST_VIEW, listState)
        }
        if (searchQuery.isNotEmpty()) {
            outState.putString(STATE_SEARCH_QUERY, searchQuery)
        }
        if (filterCategory.isNotEmpty()) {
            outState.putString(STATE_FILTER_CATEGORY, filterCategory)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (savedInstanceState != null) {
            @Suppress("DEPRECATION")
            listState = savedInstanceState.getParcelable(STATE_LIST_VIEW)
        }
    }

    private fun initializeList() {
        searchText.isEnabled = true
        clearSearchButton.isEnabled = true

        listAdapter = SongbookListAdapter(requireContext(), songbook!!.songs)
        setListAdapter(listAdapter)

        if (null != listState) {
            listView.onRestoreInstanceState(listState)
            listState = null
        }

        refreshFilter()
        requireActivity().invalidateOptionsMenu()
    }

    private fun onClearFilterSelected() {
        filterCategory = ""
        searchText.setHint(R.string.songbook_search_hint)
        refreshFilter()
    }

    private fun onFilterCategorySelected(item: MenuItem) {
        if (null == songbook) {
            return
        }
        val categories = songbook!!.categories
        val categoryIdx = item.itemId - MENU_CATEGORIES_ID
        if (categoryIdx >= 0 && categoryIdx < categories.size) {
            filterCategory = categories[categoryIdx]
            searchText.hint = String.format(
                getString(R.string.songbook_search_hint_with_category_format),
                filterCategory
            )
            refreshFilter()
        } else {
            Log.e(TAG, "Menu item outside of range for category index.")
        }
    }

    private fun onSongbookLoaded(result: Result<Songbook>) = result
        .onSuccess {
            songbook = it
            isLoaded = true
            handler.post {
                if (null != activity) {
                    initializeList()
                }
            }
        }.onFailure {
            if (it is RepositoryException) {
                handler.post {
                    val toast = Toast.makeText(context, it.errorResId, Toast.LENGTH_LONG)
                    toast.show()
                }
            }
        }

    private fun refreshFilter() {
        if (null == listAdapter) {
            return
        }
        var query = searchQuery
        if (filterCategory.isNotEmpty()) {
            query = query + SongbookListAdapter.CATEGORY_QUERY + filterCategory
        }
        listAdapter!!.filter.filter(query)
    }

    private inner class ClearSearchClickedListener : View.OnClickListener {
        override fun onClick(v: View) {
            searchText.setText("")
        }
    }

    private inner class SearchChangedListener : TextWatcher {
        override fun afterTextChanged(s: Editable) {}
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            searchQuery = s.toString().lowercase(Locale.getDefault())
            refreshFilter()
        }
    }

    companion object {
        private const val INTERNAL_LIST_CONTAINER_ID =
            0x00ff0003 // from android.support.v4.app.ListFragment
        private const val MENU_CATEGORIES_ID = 10000
        private const val MENU_CLEAR_FILTER_ID = 1
        private const val STATE_FILTER_CATEGORY = "songbookFilterCategoryState"
        private const val STATE_LIST_VIEW = "songbookListViewState"
        private const val STATE_SEARCH_QUERY = "songbookSearchQueryState"
    }
}
