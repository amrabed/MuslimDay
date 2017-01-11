package amrabed.android.release.evaluation.api;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.drive.DriveScopes;

import java.util.Collections;

/**
 * API helper to handle connection to Google APIs
 *
 * @author AmrAbed
 */

public class ApiManager
{
	private static final String TAG = ApiManager.class.getName();
	public static final int RESOLVE_CONNECTION_REQUEST_CODE = 1000;

	private final Context context;

	private final GoogleAccountCredential credential;
	private final GoogleApiClient client;

	public ApiManager(Context context, Listener listener)
	{
		this.context = context;

		client = new GoogleApiClient.Builder(context)
				.addApi(Drive.API)
				.addScope(Drive.SCOPE_APPFOLDER)
//				.addScope(Drive.SCOPE_FILE)
				.addConnectionCallbacks(listener)
				.addOnConnectionFailedListener(listener)
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

	public void connect()
	{
		if (client != null)
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

	public com.google.api.services.drive.Drive getDriveService()
	{
		return new com.google.api.services.drive.Drive.Builder(
				AndroidHttp.newCompatibleTransport(), new GsonFactory(),
				credential).build();
	}

	public GoogleAccountCredential getCredential()
	{
		return credential;
	}

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

	public interface Listener extends GoogleApiClient.ConnectionCallbacks,
			GoogleApiClient.OnConnectionFailedListener
	{
	}

}
