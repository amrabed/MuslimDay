package amrabed.android.release.evaluation.core;

import java.util.List;

import amrabed.android.release.evaluation.ApplicationEvaluation;

/**
 * DayList to be read from the database on app start
 */

public class DayList {
    private static List<DayEntry> list = ApplicationEvaluation.getDatabase().loadDayList();

    private DayList() {
    }

    public static List<DayEntry> get() {
        if (list == null) {
            list = ApplicationEvaluation.getDatabase().loadDayList();
        }
        return list;
    }

    public static List<DayEntry> getRange(int rangeSize) {
        return getRange(rangeSize, list.size());
    }

    public static List<DayEntry> getRange(int rangeSize, int end) {
        final int start = end - rangeSize;
        return list.subList(start >= 0 ? start : 0, end > 0 ? end : 0);
    }

    public static int size() {
        return list.size();
	}
}
