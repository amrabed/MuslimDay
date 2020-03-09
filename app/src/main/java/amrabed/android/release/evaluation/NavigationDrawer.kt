package amrabed.android.release.evaluation

import amrabed.android.release.evaluation.about.AboutSection
import amrabed.android.release.evaluation.about.HelpSection
import amrabed.android.release.evaluation.eval.EvaluationSection
import amrabed.android.release.evaluation.guide.GuideSection
import amrabed.android.release.evaluation.preferences.PreferenceSection
import amrabed.android.release.evaluation.preferences.SettingsSection
import amrabed.android.release.evaluation.progress.ProgressSection
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.navigation.NavigationView
import java.util.*

/**
 * Navigation Drawer
 */
// ToDo: Simplify
class NavigationDrawer internal constructor(private val activity: MainActivity) : NavigationView.OnNavigationItemSelectedListener, FragmentManager.OnBackStackChangedListener {
    private var drawer: DrawerLayout? = null
    private var navigationView: NavigationView? = null
    private var fragment: Fragment? = null
    fun create(savedInstanceState: Bundle?, toolbar: Toolbar?): NavigationDrawer {
        drawer = activity.findViewById(R.id.drawer_layout)
        navigationView = activity.findViewById(R.id.navigation)
        navigationView?.setNavigationItemSelectedListener(this)
        val toggle = ActionBarDrawerToggle(activity, drawer,
                toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer?.addDrawerListener(toggle)
        toggle.syncState()
        if (savedInstanceState != null) {
            fragment = fragmentManager.getFragment(savedInstanceState, FRAGMENT_KEY)
            selectItem(fragment)
        } else {
            loadFragment(R.id.nav_eval)
        }
        fragmentManager.addOnBackStackChangedListener(this)
        return this
    }

    val isOpen: Boolean
        get() = drawer!!.isDrawerOpen(navigationView!!)

    fun saveState(outState: Bundle?) {
        fragmentManager.putFragment(outState!!, FRAGMENT_KEY, fragment!!)
    }

    fun close() {
        drawer!!.closeDrawers()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        loadFragment(item.itemId)
        close()
        return true
    }

    override fun onBackStackChanged() {
        selectItem(fragmentManager.findFragmentById(R.id.content))
        activity.invalidateOptionsMenu()
    }

    private fun loadFragment(id: Int) {
        when (id) {
            R.id.nav_eval -> {
                activity.setTitle(R.string.evaluation)
                fragmentManager.popBackStack()
                fragmentManager.beginTransaction().replace(R.id.content, EvaluationSection()).commit()
                return
            }
            R.id.nav_progress -> fragment = ProgressSection()
            R.id.nav_edit -> {
                drawer!!.closeDrawer(navigationView!!)
                activity.startEditorActivity()
                return
            }
            R.id.nav_preferences -> fragment = PreferenceSection()
            R.id.nav_Settings -> fragment = SettingsSection()
            R.id.nav_guide -> fragment = GuideSection()
            R.id.nav_help -> fragment = HelpSection()
            R.id.nav_about -> fragment = AboutSection()
            R.id.nav_sign_out -> {
                activity.signOut()
                return
            }
            else -> {
            }
        }
        selectItem(fragment)
        fragmentManager.beginTransaction().addToBackStack(null)
                .replace(R.id.content, fragment!!).commit()
    }

    private val fragmentManager: FragmentManager
        get() = activity.supportFragmentManager

    private fun selectItem(fragment: Fragment?) {
        if (fragment != null) {
            val index = map[fragment.javaClass]
            if (index != null) {
                navigationView!!.menu.getItem(index).isChecked = true
                activity.setTitle(TITLES[index])
            }
        }
    }

    companion object {
        private const val FRAGMENT_KEY = "Current fragment"
        private val map = HashMap<Class<out Fragment>, Int>()
        private val TITLES = intArrayOf(R.string.evaluation, R.string.menu_progress,
                R.string.menu_edit, R.string.menu_preferences,
                R.string.menu_settings, R.string.menu_guide, R.string.menu_help, R.string.menu_about)

        init {
            map[EvaluationSection::class.java] = 0
            map[ProgressSection::class.java] = 1
            map[PreferenceSection::class.java] = 3
            map[SettingsSection::class.java] = 4
            map[GuideSection::class.java] = 5
            map[HelpSection::class.java] = 6
            map[AboutSection::class.java] = 7
        }
    }

}