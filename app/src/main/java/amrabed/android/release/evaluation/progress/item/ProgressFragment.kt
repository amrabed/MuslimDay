package amrabed.android.release.evaluation.progress.item

import amrabed.android.release.evaluation.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager

class ProgressFragment : Fragment() {
    private var id: String? = null
    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.progress, parent, false)
        val pager: ViewPager = view.findViewById(R.id.pager)
        val args = arguments
        if (args != null) {
            id = args.getString(ID)
            activity?.title = args.getString(TITLE)
        }
        pager.adapter = PagerAdapter(childFragmentManager)
        return view
    }

    private inner class PagerAdapter internal constructor(manager: FragmentManager?) : FragmentPagerAdapter(manager!!, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        override fun getItem(position: Int): Fragment {
            return PieFragment.newInstance(id, position)
        }

        override fun getCount(): Int {
            return 4
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return resources.getStringArray(R.array.progress)[position]
        }
    }

    companion object {
        private const val ID = "ID"
        private const val TITLE = "TITLE"
        fun newInstance(taskId: String?, title: String?): Fragment {
            val fragment = ProgressFragment()
            val args = Bundle()
            args.putString(ID, taskId)
            args.putString(TITLE, title)
            fragment.arguments = args
            return fragment
        }
    }
}