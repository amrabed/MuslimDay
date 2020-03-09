package amrabed.android.release.evaluation.about

import amrabed.android.release.evaluation.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment

class AboutSection : DialogFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.about, container, false) as TextView
        view.setText(R.string.about_content1)
        return view
    }
}