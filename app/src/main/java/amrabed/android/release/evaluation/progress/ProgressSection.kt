package amrabed.android.release.evaluation.progress

import amrabed.android.release.evaluation.R
import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager

/**
 * Progress Section
 */
class ProgressSection : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.progress, parent, false)
        val pager: ViewPager = view.findViewById(R.id.pager)
        pager.adapter = PagerAdapter(childFragmentManager)
        return view
    }

    override fun onResume() {
        super.onResume()
        val activity: Activity? = activity
        activity?.setTitle(R.string.menu_progress)
    }

    private inner class PagerAdapter internal constructor(manager: FragmentManager?) : FragmentPagerAdapter(manager!!, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        override fun getItem(position: Int): Fragment {
            return BarFragment.newInstance(position)
        }

        override fun getCount(): Int {
            return 3
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return resources.getStringArray(R.array.progress)[position]
        }
    }
}