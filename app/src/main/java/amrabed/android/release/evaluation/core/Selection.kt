package amrabed.android.release.evaluation.core

import amrabed.android.release.evaluation.R

class Selection(var value: Byte) {
    operator fun next(): Selection {
        value = ((value + 1) % 4).toByte()
        return this
    }

    val icon: Int
        get() = ICONS[value.toInt()]

    companion object {
        const val NONE: Byte = 0
        const val GOOD: Byte = 1
        const val OK: Byte = 2
        const val BAD: Byte = 3
        val colors = intArrayOf(R.color.none, R.color.good, R.color.ok, R.color.bad)
        private val ICONS = intArrayOf(0, R.drawable.ic_check, R.drawable.ic_neutral, R.drawable.ic_clear)

        fun getIcon(selection: Byte): Int {
            return ICONS[selection.toInt()]
        }
    }
}