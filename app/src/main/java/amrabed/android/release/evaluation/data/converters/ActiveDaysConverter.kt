package amrabed.android.release.evaluation.data.converters

import androidx.room.TypeConverter

class ActiveDaysConverter {
    @TypeConverter
    fun getActiveDays(activeDays: BooleanArray): Byte {
        var result = 0
        for (i in activeDays.indices) {
            if (activeDays[i]) {
                result = result or (1 shl i)
            }
        }
        return result.toByte()
    }

    @TypeConverter
    fun setActiveDays(activeDays: Byte): BooleanArray {
        var days :Int = activeDays.toInt()
        val activeDaysArray = BooleanArray(7)
        for (i in activeDaysArray.indices) {
            activeDaysArray[i] = days and 0x01 == 0x01
            days = days ushr 1
        }
        return activeDaysArray
    }
}