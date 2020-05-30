package amrabed.android.release.evaluation.tools.graphs

import com.github.mikephil.charting.charts.Chart

interface Plot {
    fun show(chart: Chart<*>)
}