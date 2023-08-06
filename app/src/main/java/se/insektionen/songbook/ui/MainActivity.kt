package se.insektionen.songbook.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.drawerlayout.widget.DrawerLayout.DrawerListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.navigation.NavigationView
import se.insektionen.songbook.R
import se.insektionen.songbook.utils.hideSoftKeyboard
import java.util.Random

/**
 * The main activity.
 */
class MainActivity : AppCompatActivity() {
    private lateinit var navigationDrawer: DrawerLayout
    private lateinit var navigationView: NavigationView
    private var optionsMenu: HasMenu? = null

    override fun onBackPressed() {
        if (navigationDrawer.isDrawerOpen(GravityCompat.START)) {
            navigationDrawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean =
        if (null != optionsMenu) {
            menuInflater.inflate(optionsMenu!!.menu, menu)
            true
        } else {
            false
        }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        null != optionsMenu && optionsMenu!!.onMenuItemSelected(item)
                || super.onOptionsItemSelected(item)

    fun openFragment(fragment: Fragment, addToBackStack: Boolean = true) {
        val oldFragment = supportFragmentManager.findFragmentById(R.id.container)
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        if (addToBackStack && !isSameFragment(oldFragment, fragment)) {
            transaction.addToBackStack(null)
        }
        transaction.commit()
        updateViewFromFragment(fragment)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<Toolbar>(R.id.toolbar).apply {
            setSupportActionBar(this)
            setTitle(R.string.app_name)
        }

        navigationDrawer = findViewById(R.id.drawer_layout)
        val toggle = ActionBarDrawerToggle(
            this@MainActivity,
            navigationDrawer,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        navigationDrawer.addDrawerListener(toggle)
        navigationDrawer.addDrawerListener(NavigationDrawerListener())
        toggle.syncState()

        navigationView = findViewById<NavigationView>(R.id.nav_view).apply {
            setNavigationItemSelectedListener(NavigationListener())
            val navigationHeader = getHeaderView(0)
            setRandomQuoteInHeader(navigationHeader)
        }

        supportFragmentManager.addOnBackStackChangedListener(BackStackChangedListener())
        if (null != savedInstanceState) {
            val lastFragment =
                supportFragmentManager.getFragment(savedInstanceState, STATE_LAST_OPENED_FRAGMENT)
            openFragment(lastFragment!!, false)
        } else {
            openFragment(getFragmentByNavigationItem(R.id.nav_list_songs), false)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        supportFragmentManager.findFragmentById(R.id.container)?.apply {
            supportFragmentManager.putFragment(outState, STATE_LAST_OPENED_FRAGMENT, this)
        }
    }

    private fun getFragmentByNavigationItem(navId: Int): Fragment =
        if (navId == R.id.nav_list_songs) {
            SongbookFragment()
        } else if (navId == R.id.nav_about) {
            AboutFragment()
        } else {
            throw IllegalArgumentException("Unknown navigation ID.")
        }

    private fun isSameFragment(oldFragment: Fragment?, newFragment: Fragment): Boolean =
        null != oldFragment && oldFragment::class == newFragment::class

    private fun setRandomQuoteInHeader(headerView: View) {
        val line1Text = headerView.findViewById<TextView>(R.id.navigation_header_qotd1)
        val line2Text = headerView.findViewById<TextView>(R.id.navigation_header_qotd2)
        val quotes = resources.getStringArray(R.array.quotes_list)
        val quote = quotes[Random().nextInt(quotes.size)]
        val separatorIdx = quote.indexOf('|')
        if (-1 != separatorIdx) {
            line1Text.text = quote.substring(0, separatorIdx)
            line2Text.text = "- " + quote.substring(separatorIdx + 1)
        } else {
            line1Text.text = quote
            line2Text.text = ""
        }
    }

    private fun updateViewFromFragment(fragment: Fragment) {
        if (fragment is HasNavigationItem) {
            val fragmentWithNavItem = fragment as HasNavigationItem
            navigationView.setCheckedItem(fragmentWithNavItem.itemId)
        }
        if (fragment is HasMenu) {
            optionsMenu = fragment
            fragment.setHasOptionsMenu(true)
        } else {
            optionsMenu = null
            fragment.setHasOptionsMenu(false)
        }
    }

    interface HasMenu {
        val menu: Int
        fun onMenuItemSelected(item: MenuItem): Boolean
    }

    interface HasNavigationItem {
        val itemId: Int
    }

    private inner class BackStackChangedListener : FragmentManager.OnBackStackChangedListener {
        override fun onBackStackChanged() {
            val fragment = supportFragmentManager.findFragmentById(R.id.container)
            fragment?.let { updateViewFromFragment(it) }
        }
    }

    private inner class NavigationDrawerListener : DrawerListener {
        override fun onDrawerOpened(drawerView: View) {
            hideSoftKeyboard(currentFocus)
        }

        override fun onDrawerClosed(drawerView: View) {}
        override fun onDrawerSlide(drawerView: View, slideOffset: Float) {}
        override fun onDrawerStateChanged(newState: Int) {}
    }

    private inner class NavigationListener : NavigationView.OnNavigationItemSelectedListener {
        override fun onNavigationItemSelected(item: MenuItem): Boolean {
            openFragment(getFragmentByNavigationItem(item.itemId))
            findViewById<DrawerLayout>(R.id.drawer_layout).closeDrawer(GravityCompat.START)
            return true
        }
    }

    companion object {
        private const val STATE_LAST_OPENED_FRAGMENT = "openMainActivityFragment"
    }
}