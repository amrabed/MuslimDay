package amrabed.android.release.evaluation.edit

import amrabed.android.release.evaluation.data.entities.Task

class Modification internal constructor(val task: Task?, val operation: Int) {

    companion object {
        const val ADD = 0
        const val UPDATE = 1
        const val DELETE = 2
    }

}