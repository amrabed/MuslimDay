package amrabed.android.release.evaluation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class ActivityIntro extends Activity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_intro);
		try
		{
			Thread.sleep(100);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	
		startActivity(new Intent(this,ActivityMain.class));
	}

}
