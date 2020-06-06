package amrabed.android.release.evaluation.core

import amrabed.android.release.evaluation.R

enum class Status(var value: Byte) {
    NONE(0), DONE(1), EXCUSE(2), MISSED(3), PARTIAL(4);

    val title get() = TITLES[ordinal]
    val icon get() = ICONS[ordinal]

    companion object {
        val COLORS = intArrayOf(R.color.none, R.color.done, R.color.excuse, R.color.missed, R.color.partial)
        private val ICONS = intArrayOf(0, R.drawable.ic_done, R.drawable.ic_excuse, R.drawable.ic_missed, R.drawable.ic_partial)
        private val TITLES = intArrayOf(R.string.later, R.string.done, R.string.excuse, R.string.missed, R.string.partial)

        fun of(x: Byte) = values()[x.toInt()]
    }
}