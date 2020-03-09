package amrabed.android.release.evaluation.guide

import amrabed.android.release.evaluation.R
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.fragment.app.ListFragment

/**
 * Guide section
 */
class GuideSection : ListFragment() {
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (context != null) {
            listAdapter = ArrayAdapter(context!!,
                    R.layout.guide_item,
                    resources.getStringArray(R.array.titles))
        }
    }

    override fun onListItemClick(listView: ListView, view: View, position: Int, id: Long) {
        showDetails(position)
    }

    private fun showDetails(index: Int) {
        val fragment: Fragment = DetailsFragment.newInstance(ENTRIES[index], resources.getStringArray(R.array.titles)[index])
        val fragmentManager = parentFragmentManager
        fragmentManager.beginTransaction().addToBackStack(null)
                .replace(R.id.content, fragment).commit()
    }

    companion object {
        private val ENTRIES = intArrayOf(R.raw.wakeup, R.raw.brush, R.raw.night, R.raw.fasting,
                R.raw.sunna, R.raw.fajr, R.raw.quran, R.raw.memorize, R.raw.morning, R.raw.duha,
                R.raw.sports, R.raw.friday, R.raw.work, R.raw.cong, R.raw.fajr_azkar, R.raw.rawateb,
                R.raw.evening, R.raw.isha, R.raw.wetr, R.raw.diet, R.raw.manners, R.raw.honesty,
                R.raw.backbiting, R.raw.gaze, R.raw.wudu, R.raw.sleep)
    }
}