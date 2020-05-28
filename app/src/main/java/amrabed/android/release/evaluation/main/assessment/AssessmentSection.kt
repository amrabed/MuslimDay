package amrabed.android.release.evaluation.main.assessment

import amrabed.android.release.evaluation.R
import amrabed.android.release.evaluation.data.entities.Day
import amrabed.android.release.evaluation.models.DayViewModel
import amrabed.android.release.evaluation.utilities.time.DateManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.preference.PreferenceManager
import kotlinx.android.synthetic.main.assessment.*

/**
 * Assessment section
 */
class AssessmentSection : Fragment() {
    private val viewModel by activityViewModels<DayViewModel>()

    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.assessment, parent, false)
        viewModel.dayList?.observe(viewLifecycleOwner, Observer { dayList: List<Day> ->
            if (PreferenceManager.getDefaultSharedPreferences(context).getString("language", "en").equals("en")) {
                pager.adapter = SectionPagerAdapter(dayList)
                pager.currentItem = dayList.lastIndex
            } else {
                pager.adapter = SectionPagerAdapter(dayList.sortedByDescending { it.date })
            }
        })
        return view
    }

    private inner class SectionPagerAdapter(private val dayList: List<Day?>) :
            FragmentPagerAdapter(childFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        override fun getItem(position: Int): Fragment {
            val date = dayList[position]!!.date
            ViewModelProvider(activity as ViewModelStoreOwner)
                    .get(date.toString(), DayViewModel::class.java)
                    .selectDay(dayList[position])
            return DayFragment().apply { arguments = bundleOf(Pair(DATE, date)) }
        }

        override fun getCount() = dayList.size

        override fun getPageTitle(position: Int) = DateManager(requireContext()).getDate(dayList[position]!!.date)
    }
}

const val DATE = "date"