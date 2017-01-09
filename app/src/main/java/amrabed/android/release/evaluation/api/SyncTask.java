package amrabed.android.release.evaluation.api;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.ParentReference;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;

import amrabed.android.release.evaluation.EditSection;
import amrabed.android.release.evaluation.R;
import amrabed.android.release.evaluation.db.Database;

/**
 * AsyncTask for syncing files and shredPreference to Google Drive
 *
 * @author AmrAbed
 */
public class SyncTask extends AsyncTask<Drive, Void, Void>
{

	private static final String TAG = SyncTask.class.getName();
	private final Context context;
	private final Listener listener;

	private boolean isUpdated = false;
	private boolean isSaved = false;

	public SyncTask(Context context, Listener listener)
	{
		this.context = context;
		this.listener = listener;
	}

	@Override
	protected Void doInBackground(Drive... drives)
	{
		final Drive service = drives[0];
		try
		{

			File folder = new File();
			folder.setMimeType("application/vnd.google-apps.folder");
			folder.setTitle(context.getString(R.string.app_name));
			folder.setEditable(false);

			List<File> files = new ArrayList<File>();
			files.addAll(
					service.files().list().setQ("title = '" + folder.getTitle() + "'").execute()
							.getItems());
			if (files.isEmpty())
			{
				folder = service.files().insert(folder).execute();
			}
			else
			{
				folder = service.files().update(files.get(0).getId(), folder).execute();
			}

			ParentReference p = new ParentReference();
			p.setId(folder.getId());
			List<ParentReference> list = new ArrayList<>();
			list.add(p);

			saveListFile(service, list);
			saveDatabaseFile(service, list);
		}
		catch (ConnectException e)
		{
			listener.onSyncError(e);
		}
		catch (UserRecoverableAuthIOException e)
		{
			listener.onSyncError(e);
		}
		catch (Exception e)
		{
			Log.e(TAG, e.toString());
		}
		return null;
	}

	private long saveFile(Drive service, long lastUpdate, FileContent content, String title,
						  List<ParentReference> parents)
	{
		try
		{
			File file = new File();
			file.setTitle(title);
			file.setEditable(false);
			file.setDescription(String.valueOf(lastUpdate));
			file.setParents(parents);

			final List<File> files = new ArrayList<>();
			files.addAll(
					service.files().list().setQ("title = '" + file.getTitle() + "'").execute()
							.getItems());
			if (files.isEmpty())
			{
				isSaved = true;
				file = service.files().insert(file, content).execute();
			}
			else
			{
				long remotLastUpdate = Long.parseLong(files.get(0).getDescription());
				if (lastUpdate > remotLastUpdate)
				{
					isSaved = true;
					// My copy is newer, replace server copy
					file = service.files()
							.update(files.get(0).getId(), file, content).execute();
				}
				else if (lastUpdate < remotLastUpdate)
				{
					isUpdated = true;
					file = files.get(0);
					// My copy is obsolete, download server's
					if (file.getDownloadUrl() != null && file.getDownloadUrl().length() > 0)
					{
						final InputStream in = service.getRequestFactory()
								.buildGetRequest(new GenericUrl(file.getDownloadUrl()))
								.execute().getContent();
						FileOutputStream out = context.openFileOutput(title, Context.MODE_PRIVATE);
						int c;
						while ((c = in.read()) != -1)
						{
							out.write(c);
						}
						out.close();
						return remotLastUpdate;
//						PreferenceManager.getDefaultSharedPreferences(context)
//								.edit().putLong("LAST_LIST_UPDATE", remotLastUpdate).apply();
					}
				}
			}
		}
		catch (IOException e)
		{
			Log.e(TAG, e.toString());
		}
		return 0;
	}

	private void saveListFile(Drive service, List<ParentReference> parents)
	{
		final long lastUpdate = getPreferences().getLong("LAST_LIST_UPDATE", 0);
		final FileContent content = new FileContent("text/plain", new java.io.File(
				context.getFilesDir().getAbsoluteFile() + "/" + EditSection.LIST_FILE));

		final long lastRemoteUpdate = saveFile(service, lastUpdate, content, EditSection.LIST_FILE,
				parents);
		if (lastRemoteUpdate > lastUpdate)
		{
			getPreferences().edit().putLong("LAST_LIST_UPDATE", lastRemoteUpdate).apply();
		}
//			File listFile = new File();
//			listFile.setTitle(EditSection.LIST_FILE);
//			listFile.setEditable(false);
//			listFile.setDescription(String.valueOf(lastUpdate));
//			listFile.setParents(parents);
//
//			List<File> files = new ArrayList<>();
//			files.addAll(
//					service.files().list().setQ("title = '" + listFile.getTitle() + "'").execute()
//							.getItems());
//			if (files.isEmpty())
//			{
//				isSaved = true;
//				listFile = service.files().insert(listFile, listFileContent).execute();
//			}
//			else
//			{
//				long remotLastModify = Long.parseLong(files.get(0).getDescription());
//				if (lastUpdate > remotLastModify)
//				{
//					isSaved = true;
//					// My copy is newer, replace server copy
//					listFile = service.files()
//							.update(files.get(0).getId(), listFile, listFileContent).execute();
//				}
//				else if (lastUpdate < remotLastModify)
//				{
//					isUpdated = true;
//					listFile = files.get(0);
//					// My copy is obsolete, download server's
//					if (listFile.getDownloadUrl() != null && listFile.getDownloadUrl().length() > 0)
//					{
//						final InputStream in = service.getRequestFactory()
//								.buildGetRequest(new GenericUrl(listFile.getDownloadUrl()))
//								.execute().getContent();
//						FileOutputStream out = context.openFileOutput(EditSection.LIST_FILE,
//								Context.MODE_PRIVATE);
//						int c;
//						while ((c = in.read()) != -1)
//						{
//							out.write(c);
//						}
//						out.close();
//						PreferenceManager.getDefaultSharedPreferences(context)
//								.edit().putLong("LAST_LIST_UPDATE", remotLastModify).apply();
//					}
//				}
//			}
//		}
//		catch (IOException e)
//		{
//			Log.e(TAG, e.toString());
//		}
	}

	private void saveDatabaseFile(Drive service, List<ParentReference> parents)
	{
		final long lastUpdate = getPreferences().getLong("LAST_UPDATE", 0);
		final FileContent content = new FileContent("text/plain",
				new java.io.File(context.getDatabasePath(
						Database.DATABASE_NAME).getAbsolutePath()));

		final long lastRemoteUpdate = saveFile(service, lastUpdate, content, Database.DATABASE_NAME,
				parents);
		if (lastRemoteUpdate > lastUpdate)
		{
			getPreferences().edit().putLong("LAST_UPDATE", lastRemoteUpdate).apply();
		}
//			File databaseFile = new File();
//			databaseFile.setTitle(Database.DATABASE_NAME);
//			databaseFile.setEditable(false);
//			databaseFile.setDescription(String.valueOf(lastUpdate));
//			// PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
//			// .getLong("LAST_UPDATE", 0)));
//			databaseFile.setParents(parents);
//
//			List<File> files = new ArrayList<>();
//			files.addAll(service.files().list().setQ("title = '" + databaseFile.getTitle() + "'")
//					.execute().getItems());
//			if (files.isEmpty())
//			{
//				isSaved = true;
//				databaseFile = service.files().insert(databaseFile, content).execute();
//			}
//			else
//			{
//				long remotLastModify = Long.parseLong(files.get(0).getDescription());
//
//				if (lastUpdate > remotLastModify)
//				{
//
//					// My copy is newer, replace server copy
//					databaseFile = service.files()
//							.update(files.get(0).getId(), databaseFile, content).execute();
//					isSaved = true;
//				}
//				else if (lastUpdate < remotLastModify)
//				{
//					isUpdated = true;
//					databaseFile = files.get(0);
//					// My copy is obsolete, download server copy
//					if (databaseFile.getDownloadUrl() != null && databaseFile.getDownloadUrl()
//							.length() > 0)
//					{
//						InputStream in = service.getRequestFactory()
//								.buildGetRequest(new GenericUrl(databaseFile.getDownloadUrl()))
//								.execute().getContent();
//						FileOutputStream out = new FileOutputStream(
//								context.getDatabasePath(Database.DATABASE_NAME).getAbsoluteFile());
//						int c;
//						while ((c = in.read()) != -1)
//						{
//							out.write(c);
//						}
//						out.close();
//						PreferenceManager.getDefaultSharedPreferences(context)
//								.edit().putLong("LAST_UPDATE", remotLastModify).apply();
//					}
//				}
//			}
//		}
//		catch (IOException e)
//		{
//			Log.e(TAG, e.toString());
//		}
	}

	private SharedPreferences getPreferences()
	{
		return PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
	}

	@Override
	protected void onPostExecute(Void aVoid)
	{
		super.onPostExecute(aVoid);
		listener.onSyncSuccess(isSaved, isUpdated);
	}

	public interface Listener
	{
		void onSyncSuccess(boolean isSaved, boolean isUpdated);

		void onSyncError(Exception e);
	}
}
