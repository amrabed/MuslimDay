package amrabed.android.release.evaluation.main.guide

import amrabed.android.release.evaluation.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import kotlinx.android.synthetic.main.guide_item.view.*
import java.io.InputStream
import java.util.*

/**
 * Guide section
 */
class GuideSection : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup?, state: Bundle?): View {
        val view = inflater.inflate(R.layout.list, parent, false) as RecyclerView
        view.adapter = Adapter(resources.getStringArray(R.array.titles))
        view.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))
        return view
    }

    inner class Adapter(val list: Array<String>) : RecyclerView.Adapter<Adapter.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(LayoutInflater.from(context).inflate(R.layout.guide_item, parent, false))
        override fun getItemCount() = list.size
        override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(position)

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            fun bind(position: Int) {
                itemView.title.text = list[position]
                itemView.setOnClickListener { findNavController().navigate(R.id.guideEntry, bundleOf(Pair(POSITION, position))) }
            }
        }
    }
}

class EntryFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup?, state: Bundle?): View {
        return inflater.inflate(R.layout.guide_container, parent, false).apply {
            val pager = findViewById<ViewPager2>(R.id.pager)
            pager.adapter = PagerAdapter()
            pager.setCurrentItem(requireArguments().getInt(POSITION, 0), false)
        }
    }

    inner class PagerAdapter : FragmentStateAdapter(requireActivity()) {
        override fun getItemCount() = ENTRIES.size
        override fun createFragment(position: Int) = Page().apply { arguments = bundleOf(Pair(POSITION, position)) }
    }
}

/**
 * Fragment to show content of the selected guide entry
 */
class Page : Fragment() {
    private val index by lazy { requireArguments().getInt(POSITION, 0) }

    override fun onResume() {
        super.onResume()
        activity?.title = resources.getStringArray(R.array.titles)[index]
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, state: Bundle?): View {
        return inflater.inflate(R.layout.guide_entry, container, false).apply {
            findViewById<TextView>(R.id.text).text = readText(resources.openRawResource(ENTRIES[index]))
        }
    }

    private fun readText(inputStream: InputStream) = Scanner(inputStream).useDelimiter("\\A").next()
}

private val ENTRIES = intArrayOf(R.raw.wakeup, R.raw.brush, R.raw.night, R.raw.fasting,
        R.raw.sunna, R.raw.fajr, R.raw.quran, R.raw.memorize, R.raw.morning, R.raw.duha,
        R.raw.sports, R.raw.friday, R.raw.work, R.raw.cong, R.raw.fajr_azkar, R.raw.rawateb,
        R.raw.evening, R.raw.isha, R.raw.wetr, R.raw.diet, R.raw.manners, R.raw.honesty,
        R.raw.backbiting, R.raw.gaze, R.raw.wudu, R.raw.sleep)

const val POSITION = "position"