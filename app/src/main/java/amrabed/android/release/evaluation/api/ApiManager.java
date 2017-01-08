package amrabed.android.release.evaluation.api;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.drive.DriveScopes;

import java.util.Arrays;
import java.util.Collections;

/**
 * API helper to handle connection to Google APIs
 *
 * @author AmrAbed
 */

public class ApiManager implements GoogleApiClient.ConnectionCallbacks,
		GoogleApiClient.OnConnectionFailedListener
{
	private static final String TAG = ApiManager.class.getName();
	public static final int RESOLVE_CONNECTION_REQUEST_CODE = 1000;

	private final Context context;
	private final GoogleAccountCredential credential;
	private final GoogleApiClient client;

	public ApiManager(Context context)
	{
		this.context = context;

		client = new GoogleApiClient.Builder(context)
				.addApi(Drive.API)
				.addScope(Drive.SCOPE_APPFOLDER)
				.addScope(Drive.SCOPE_FILE)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.build();

		credential = GoogleAccountCredential.usingOAuth2(context.getApplicationContext(),
				Collections.singletonList(DriveScopes.DRIVE))
				.setBackOff(new ExponentialBackOff());

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


	public GoogleAccountCredential getCredential()
	{
		return credential;
	}

//	public void getApiResults()
//	{
//		if (!isGooglePlayServicesAvailable())
//		{
//			acquireGooglePlayServices();
//		}
//		else if (credential.getSelectedAccountName() == null)
//		{
//			listener.chooseAccount();
//		}
//		else if (!isDeviceOnline())
//		{
//			listener.onConnectionError();
//		}
//		else
//		{
//			listener.onApiReady(credential);
//		}
//	}

	private boolean isDeviceOnline()
	{
		final ConnectivityManager manager =
				(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		final NetworkInfo networkInfo = manager.getActiveNetworkInfo();
		return (networkInfo != null && networkInfo.isConnected());
	}

	private boolean isGooglePlayServicesAvailable()
	{
		final GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
		final int connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(context);
		return connectionStatusCode == ConnectionResult.SUCCESS;
	}


//	private void acquireGooglePlayServices()
//	{
//		final GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
//		final int connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(context);
//		if (apiAvailability.isUserResolvableError(connectionStatusCode))
//		{
//			listener.onApiError(connectionStatusCode);
//		}
//	}

	public void setAccountName(String accountName)
	{
		credential.setSelectedAccountName(accountName);
	}

	public interface Listener
	{
		void onConnectionError();

		void onApiError(int connectionStatusCode);

		void onApiReady(GoogleAccountCredential credential);

		void chooseAccount();
	}

}
