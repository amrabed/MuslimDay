package amrabed.android.release.evaluation.guide

import amrabed.android.release.evaluation.R
import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RawRes
import androidx.fragment.app.Fragment
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream

/**
 * Fragment to show details of guide entry
 */
class DetailsFragment : Fragment() {
    override fun onResume() {
        super.onResume()
        val args = arguments
        val activity: Activity? = activity
        if (args != null && activity != null) {
            activity.title = args.getString(TITLE, null)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.guide_entry, container, false)
        val text = view.findViewById<TextView>(R.id.text)
        if (arguments != null) {
            text.text = readText(resources.openRawResource(arguments!!.getInt(ENTRY, 0)))
        }
        return view
    }

    companion object {
        private const val ENTRY = "entry"
        private const val TITLE = "title"
        private val TAG = DetailsFragment::class.java.canonicalName
        fun newInstance(@RawRes entry: Int, title: String?): DetailsFragment {
            val detailsFragment = DetailsFragment()
            val args = Bundle()
            args.putInt(ENTRY, entry)
            args.putString(TITLE, title)
            detailsFragment.arguments = args
            return detailsFragment
        }

        /**
         * Read text from raw input file
         *
         * @param inputStream input file stream
         * @return Text content of file
         */
        private fun readText(inputStream: InputStream): String {
            val byteArrayOutputStream = ByteArrayOutputStream()
            var i: Int
            try {
                i = inputStream.read()
                while (i != -1) {
                    byteArrayOutputStream.write(i)
                    i = inputStream.read()
                }
                inputStream.close()
            } catch (e: IOException) {
                Log.e(TAG, e.toString())
            }
            return byteArrayOutputStream.toString()
        }
    }
}