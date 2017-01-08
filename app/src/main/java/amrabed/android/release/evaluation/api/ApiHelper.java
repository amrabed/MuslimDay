package amrabed.android.release.evaluation.api;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;

/**
 * API helper to handle connection to Google APIs
 *
 * @author AmrAbed
 */

public class ApiHelper implements GoogleApiClient.ConnectionCallbacks,
		GoogleApiClient.OnConnectionFailedListener
{
	private static final String TAG = ApiHelper.class.getName();
	public static final int RESOLVE_CONNECTION_REQUEST_CODE = 1000;

	private final GoogleApiClient client;

	public ApiHelper(Context context)
	{
		client = new GoogleApiClient.Builder(context)
				.addApi(Drive.API)
				.addScope(Drive.SCOPE_APPFOLDER)
				.addScope(Drive.SCOPE_FILE)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.build();

		connect();
	}

	public GoogleApiClient getClient()
	{
		return client;
	}

	private void connect()
	{
		if(client != null)
		{
			client.connect();
		}
	}

	public void disconnect()
	{
		if (isConnected())
		{
			client.disconnect();
		}
	}

	public boolean isConnected()
	{
		return client != null && client.isConnected();
	}

	@Override
	public void onConnected(@Nullable Bundle bundle)
	{
		Log.i(TAG, "Connection to Google API client connected successfully");
	}

	@Override
	public void onConnectionSuspended(int i)
	{
		Log.i(TAG, "Connection to Google API suspended");
		connect();
	}

	@Override
	public void onConnectionFailed(@NonNull ConnectionResult result)
	{
		Log.i(TAG, "Connection to Google API client failed - Error: " + result.getErrorMessage());
//		if (result.hasResolution())
//		{
//			try
//			{
//				result.startResolutionForResult(this, RESOLVE_CONNECTION_REQUEST_CODE);
//			}
//			catch (IntentSender.SendIntentException e)
//			{
//				// Unable to resolve, message user appropriately
//			}
//		}
//		else
//		{
//			GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this, 0).show();
//		}

	}
}
