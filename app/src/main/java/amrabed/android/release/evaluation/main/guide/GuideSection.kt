package amrabed.android.release.evaluation.main.guide

import amrabed.android.release.evaluation.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.ListFragment
import androidx.navigation.fragment.findNavController
import java.io.InputStream
import java.util.*

/**
 * Guide section
 */
class GuideSection : ListFragment() {
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        listAdapter = ArrayAdapter(requireContext(), R.layout.guide_item, resources.getStringArray(R.array.titles))
    }

    override fun onListItemClick(listView: ListView, view: View, position: Int, id: Long) {
        findNavController().navigate(R.id.guideEntry, bundleOf(Pair(POSITION, position)))
    }
}

/**
 * Fragment to show content of the selected guide entry
 */
class EntryFragment : Fragment() {
    private val index by lazy { requireArguments().getInt(POSITION, 0) }

    override fun onResume() {
        super.onResume()
        activity?.title = resources.getStringArray(R.array.titles)[index]

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, state: Bundle?): View? {
        val view = inflater.inflate(R.layout.guide_entry, container, false)
        view.findViewById<TextView>(R.id.text).text = readText(resources.openRawResource(ENTRIES[index]))
        return view
    }

    private fun readText(inputStream: InputStream): String? {
        return Scanner(inputStream).useDelimiter("\\A").next()
    }

    companion object {
        private val ENTRIES = intArrayOf(R.raw.wakeup, R.raw.brush, R.raw.night, R.raw.fasting,
                R.raw.sunna, R.raw.fajr, R.raw.quran, R.raw.memorize, R.raw.morning, R.raw.duha,
                R.raw.sports, R.raw.friday, R.raw.work, R.raw.cong, R.raw.fajr_azkar, R.raw.rawateb,
                R.raw.evening, R.raw.isha, R.raw.wetr, R.raw.diet, R.raw.manners, R.raw.honesty,
                R.raw.backbiting, R.raw.gaze, R.raw.wudu, R.raw.sleep)
    }
}

const val POSITION = "position"