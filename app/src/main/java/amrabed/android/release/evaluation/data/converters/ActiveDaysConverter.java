package amrabed.android.release.evaluation.data.converters;

import androidx.room.TypeConverter;

public class ActiveDaysConverter {
    @TypeConverter
    public byte getActiveDays(boolean [] activeDays) {
        byte result = 0;
        for (int i = 0; i < activeDays.length; i++)
        {
            if (activeDays[i])
            {
                result |= (1 << i);
            }

        }
        return result;
    }

    @TypeConverter
    public boolean[] setActiveDays(byte activeDays) {
        boolean [] activeDaysArray = new boolean[7];
        for (int i = 0; i < activeDaysArray.length; i++)
        {
            activeDaysArray[i] = (activeDays & 0x01) == 0x01;
            activeDays >>>= 1;
        }
        return activeDaysArray;
    }
}
