package amrabed.android.release.evaluation.eval

import amrabed.android.release.evaluation.R
import amrabed.android.release.evaluation.data.entities.Day
import amrabed.android.release.evaluation.data.models.DayViewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.viewpager.widget.ViewPager
import org.joda.time.LocalDate

/**
 * Evaluation section
 */
class EvaluationSection : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.day_view, parent, false)
        val pager: ViewPager = view.findViewById(R.id.pager)
        ViewModelProvider(activity as ViewModelStoreOwner).get(DayViewModel::class.java).dayList
                ?.observe(viewLifecycleOwner, Observer { dayList: List<Day?>? ->
                    pager.adapter = SectionPagerAdapter(dayList)
                    pager.currentItem = dayList!!.size - 1
                })
        return view
    }

    private inner class SectionPagerAdapter(private val dayList: List<Day?>?) : FragmentPagerAdapter(childFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        override fun getItem(position: Int): Fragment {
            ViewModelProvider(activity as ViewModelStoreOwner)
                    .get(position.toString(), DayViewModel::class.java)
                    .select(dayList!![position])
            return DayFragment.newInstance(position)
        }

        override fun getCount(): Int {
            return dayList!!.size
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return LocalDate(dayList!![position]!!.date)
                    .toString(getString(R.string.datetimeShortFormatPattern))
        }

    }
}