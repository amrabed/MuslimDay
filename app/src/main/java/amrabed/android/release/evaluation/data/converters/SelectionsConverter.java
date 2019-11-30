package amrabed.android.release.evaluation.data.converters;


import android.util.Log;

import androidx.room.TypeConverter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

public class SelectionsConverter {

    @TypeConverter
    public static HashMap<String, Byte> desrialize(byte [] data) {
        try {
            ByteArrayInputStream byteArrayIS = new ByteArrayInputStream(data);
            ObjectInputStream objectIS = new ObjectInputStream(byteArrayIS);
            return (HashMap<String, Byte>) objectIS.readObject();
        } catch (IOException | ClassNotFoundException e) {
            Log.e(SelectionsConverter.class.getCanonicalName(), e.toString());
        }
        return null;
    }

    @TypeConverter
    public static byte[] serialize(HashMap<String, Byte> selections) {
        try {
            final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            final ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(selections);
            objectOutputStream.flush();
            return byteArrayOutputStream.toByteArray();
        }catch (IOException e) {
            Log.e(SelectionsConverter.class.getCanonicalName(), e.toString());
        }
        return null;
    }
}
