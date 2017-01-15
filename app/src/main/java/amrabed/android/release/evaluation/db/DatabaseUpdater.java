package amrabed.android.release.evaluation.db;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.chrono.IslamicChronology;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;

import amrabed.android.release.evaluation.app.ApplicationEvaluation;
import amrabed.android.release.evaluation.core.Day;

public class DatabaseUpdater extends Service
{
    public int onStartCommand(Intent intent, int flags, int startId) 
    {
    	insertEntry(today.getMillis());
		stopSelf();
		return START_NOT_STICKY;
    	
    }
	public static DateTime today = new DateTime().withTimeAtStartOfDay();


	void insertEntry(long d)
	{
		final Day entry = new Day(d, 0);
		setFlags(d, entry);
		if (ApplicationEvaluation.getDatabase().insert(entry) == -1)
		{
			// Entry already exists
			if (!new LocalDate(d).isBefore(new LocalDate(today)))
			{
				// If current or future date, update flags
				ApplicationEvaluation.getDatabase().update(d, entry.getFlags());
			}
		}
		else
		{
			ApplicationEvaluation.getDatabase().insert(entry);
		}
	}

	void setFlags(long date, Day e)
	{
		int reciteDays = PreferenceManager.getDefaultSharedPreferences(this).getInt("reciteDays", 0);
		int memorizeDays = PreferenceManager.getDefaultSharedPreferences(this).getInt("memorizeDays", 0);
		int dietDays = PreferenceManager.getDefaultSharedPreferences(this).getInt("dietDays", 0);
		int day = new DateTime(date).getDayOfWeek();
		e.setFastingDay(isFastingDay(date));
		e.setMemorizingDay((memorizeDays & (0x01 << day)) != 0);
		e.setRecitingDay((reciteDays & (0x01 << day)) != 0);
		e.setDietDay((dietDays & (0x01 << day)) != 0);
	}

	boolean isFastingDay(long date)
	{
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
		int fasting = settings.getInt("fastingDays", 0);
		boolean dad = ((fasting & 0x08) != 0);
		long ldof = settings.getLong("ldof", 0);
		if (dad)
		{
			DateTime start = new DateTime(ldof);
			DateTime end = new DateTime(date);
			boolean moreThanOne = Days.daysBetween(start, end).isGreaterThan(Days.ONE);
			// if (settings.getBoolean("today", true))
			// {
			// settings.edit().putBoolean("today", false).commit();
			// return true;
			// }
			// else
			if (moreThanOne)
			{
				settings.edit().putLong("ldof", date).apply();
				return true;
			}
		}
		else
		{
			settings.edit().remove("ldof").apply();
		}

		DateTime c = new DateTime(date).withChronology(IslamicChronology.getInstance());
		int month = c.monthOfYear().get();
		int dom = c.dayOfMonth().get();
		if ((month == 1) && ((dom == 9) || (dom == 10))) // Aashoraa
		{
			return true;
		}
		if ((month == 12) && (dom == 9)) // Arafaat
		{
			return true;
		}

		int dow = c.dayOfWeek().get();
		boolean mon = ((fasting & 0x01) != 0);
		boolean thu = ((fasting & 0x02) != 0);
		boolean white = ((fasting & 0x04) != 0);
		return (thu && dow == DateTimeConstants.THURSDAY) ||
				(mon && dow == DateTimeConstants.MONDAY) ||
				(white && ((dom == 13) || (dom == 14) || (dom == 15)));
	}

	@Override
	public IBinder onBind(Intent intent)
	{
		return null;
	}

//  // Binder given to clients
//  private final IBinder mBinder = new LocalBinder();
//
//  /**
//   * Class used for the client Binder.  Because we know this service always
//   * runs in the same process as its clients, we don't need to deal with IPC.
//   */
//  public class LocalBinder extends Binder {
//      DatabaseUpdater getService() {
//          // Return this instance of LocalService so clients can call public methods
//          return DatabaseUpdater.this;
//      }
//  }
//
//  @Override
//  public IBinder onBind(Intent intent) {
//     return mBinder;
//  }
//
//  /** method for clients */
//  public List<Entry> getEntries() {
//  	
//    return EvaluationApplication.db.getAllEntries();
//  }

	
}
