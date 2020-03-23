package amrabed.android.release.evaluation.data.converters

import android.util.Log
import androidx.room.TypeConverter
import java.io.*
import java.util.*

class SelectionsConverter {
    @TypeConverter
    fun deserialize(data: ByteArray?): HashMap<String, Byte>? {
        try {
            val byteArrayIS = ByteArrayInputStream(data)
            val objectIS = ObjectInputStream(byteArrayIS)
            return objectIS.readObject() as HashMap<String, Byte>
        } catch (e: IOException) {
            Log.e(SelectionsConverter::class.java.canonicalName, e.toString())
        } catch (e: ClassNotFoundException) {
            Log.e(SelectionsConverter::class.java.canonicalName, e.toString())
        }
        return null
    }

    @TypeConverter
    fun serialize(selections: HashMap<String?, Byte?>?): ByteArray? {
        try {
            val byteArrayOutputStream = ByteArrayOutputStream()
            val objectOutputStream = ObjectOutputStream(byteArrayOutputStream)
            objectOutputStream.writeObject(selections)
            objectOutputStream.flush()
            return byteArrayOutputStream.toByteArray()
        } catch (e: IOException) {
            Log.e(SelectionsConverter::class.java.canonicalName, e.toString())
        }
        return null
    }
}