package amrabed.android.release.evaluation.api;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;

import java.util.ArrayList;
import java.util.List;

import amrabed.android.release.evaluation.R;

/**
 * AsyncTask for creating app folder on Google Drive
 *
 * @author AmrAbed
 */
public class CreateFolderTask extends AsyncTask<Drive, Void, Void>
{
	private static final String TAG = CreateFolderTask.class.getName();


	private final Context context;
	private final Listener listener;

	public CreateFolderTask(Context context, Listener listener)
	{
		this.context = context;
		this.listener = listener;
	}

	@Override
	protected Void doInBackground(Drive... services)
	{
		final Drive service = services[0];
		try
		{
			// if (isOnline())
			{

				final File folder = new File();
				folder.setMimeType("application/vnd.google-apps.folder");
				folder.setTitle(context.getString(R.string.app_name));
				folder.setEditable(false);

				final List<File> files = new ArrayList<>();
				files.addAll(service.files().list().setQ("title = '" + folder.getTitle() + "'")
								.execute().getItems());
				if (files.isEmpty())
				{
					service.files().insert(folder).execute();
				}
			}
		}
		catch (UserRecoverableAuthIOException e)
		{
			listener.onCreateFolderError(e);
		}
		catch (Exception e)
		{
			Log.e(TAG, e.toString());
		}
		return null;
	}

	@Override
	protected void onPostExecute(Void aVoid)
	{
		super.onPostExecute(aVoid);
		listener.onCreateFolderSuccess();
	}

	public interface Listener
	{
		void onCreateFolderError(Exception e);

		void onCreateFolderSuccess();
	}
}
