package amrabed.android.release.evaluation.core

import amrabed.android.release.evaluation.R

enum class Selection(var value: Byte) {
    NONE(0), GOOD(1), OK(2), BAD(3);

    fun next(): Selection {
        return of(((value + 1) % 4).toByte())
    }

    val title: Int get() = TITLES[ordinal]
    val icon: Int get() = ICONS[ordinal]

    companion object {
        val colors = intArrayOf(R.color.none, R.color.good, R.color.neutral, R.color.bad)
        private val ICONS = intArrayOf(0, R.drawable.ic_check, R.drawable.ic_neutral, R.drawable.ic_clear)
        private val TITLES = intArrayOf(R.string.notYet, R.string.done, R.string.excuse, R.string.missed)

        fun of(x: Byte?): Selection {
            return if (x != null) values()[x.toInt()] else NONE
        }
    }
}