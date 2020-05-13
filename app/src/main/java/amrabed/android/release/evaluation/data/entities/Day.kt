package amrabed.android.release.evaluation.data.entities

import amrabed.android.release.evaluation.core.Selection
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import org.joda.time.LocalDate
import java.util.*

@Entity(tableName = "days")
data class Day(@PrimaryKey var date: Long, var selections: HashMap<String, Byte>? = hashMapOf()) {

    @Ignore
    constructor(date: LocalDate) : this(date.toDateTimeAtStartOfDay().millis)

    fun getSelection(id: String): Selection {
        return Selection.of(selections?.get(id))
    }

    val ratios: FloatArray
        get() {
            return FloatArray(Selection.values().size).also {
                selections?.values?.forEach { selection -> it[selection.toInt()]++ }
            }
        }

    fun setSelectionAt(id: String, selection: Byte) {
        selections?.set(id, selection)
    }
}