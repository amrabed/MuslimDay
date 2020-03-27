package amrabed.android.release.evaluation

import amrabed.android.release.evaluation.eval.EvaluationSection
import amrabed.android.release.evaluation.guide.GuideSection
import amrabed.android.release.evaluation.progress.ProgressSection
import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomnavigation.BottomNavigationView

/**
 * Navigation Menu
 */
class NavigationMenu internal constructor(private val activity: MainActivity) :
        BottomNavigationView.OnNavigationItemSelectedListener,
        FragmentManager.OnBackStackChangedListener {
    private val navigationView = activity.findViewById<BottomNavigationView>(R.id.navigation)

    init {
        navigationView.setOnNavigationItemSelectedListener(this)
        fragmentManager.addOnBackStackChangedListener(this)
        loadFragment(R.id.nav_eval)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        loadFragment(item.itemId)
        return true
    }

    override fun onBackStackChanged() {
        selectMenuItem(fragmentManager.findFragmentById(R.id.content))
        activity.invalidateOptionsMenu()
    }

    private fun loadFragment(id: Int) {
        when (id) {
            R.id.nav_eval -> {
                activity.setTitle(R.string.evaluation)
                fragmentManager.popBackStack()
                fragmentManager.beginTransaction().replace(R.id.content, EvaluationSection()).commit()
            }
            R.id.nav_progress -> {
                activity.setTitle(R.string.progress)
                fragmentManager.beginTransaction().addToBackStack(null)
                        .replace(R.id.content, ProgressSection()).commit()

            }
            R.id.nav_guide -> {
                activity.setTitle(R.string.guide)
                fragmentManager.beginTransaction().addToBackStack(null)
                        .replace(R.id.content, GuideSection()).commit()

            }
        }
    }

    private val fragmentManager: FragmentManager
        get() = activity.supportFragmentManager

    private fun selectMenuItem(fragment: Fragment?) {
        if (fragment != null) {
            val index = when (fragment.javaClass) {
                EvaluationSection::class.java -> 0
                ProgressSection::class.java -> 1
                GuideSection::class.java -> 2
                else -> null
            }
            if (index != null) {
                navigationView!!.menu.getItem(index).isChecked = true
                activity.setTitle(TITLES[index])
            }
        }
    }

    companion object {
        private val TITLES = intArrayOf(R.string.evaluation, R.string.progress, R.string.guide)
    }
}