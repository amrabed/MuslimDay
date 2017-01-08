package amrabed.android.release.evaluation;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class DatabaseTimer extends BroadcastReceiver
{
	Context context;

	@Override
	public void onReceive(Context context, Intent intent)
	{
		context.startService(new Intent(context,DatabaseUpdater.class));
	}

}
