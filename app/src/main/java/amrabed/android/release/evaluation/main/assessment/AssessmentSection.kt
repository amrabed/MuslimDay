package amrabed.android.release.evaluation.main.assessment

import amrabed.android.release.evaluation.R
import amrabed.android.release.evaluation.utilities.time.DateManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager

/**
 * Assessment section
 */
class AssessmentSection : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.assessment, parent, false).apply {
            findViewById<ViewPager>(R.id.pager).adapter = SectionPagerAdapter()
        }
    }

    private inner class SectionPagerAdapter : FragmentPagerAdapter(childFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        override fun getItem(position: Int) = DayFragment().apply { arguments = bundleOf(Pair(DATE, date(position))) }
        override fun getCount() = 30
        override fun getPageTitle(position: Int) = DateManager.getDate(requireContext(), date(position))
        private fun date(position: Int) = DateManager.getDatabaseKey(position)
    }
}

const val DATE = "date"