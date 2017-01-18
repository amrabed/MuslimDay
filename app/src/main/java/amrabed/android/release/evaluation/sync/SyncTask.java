package amrabed.android.release.evaluation.sync;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.MetadataBuffer;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;
import com.google.api.client.http.FileContent;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;

import amrabed.android.release.evaluation.EditSection;
import amrabed.android.release.evaluation.db.Database;

/**
 * AsyncTask for syncing files and shredPreference to Google Drive
 *
 * @author AmrAbed
 */
//ToDo: Use SyncAdapter
public class SyncTask extends AsyncTask<GoogleApiClient, Void, Void>
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
    protected Void doInBackground(GoogleApiClient... clients)
    {
        final GoogleApiClient client = clients[0];
        saveDatabaseFile(client);
        saveListFile(client);
        return null;
    }

    private void saveListFile(GoogleApiClient client)
    {
        final FileContent content = new FileContent("text/plain", new java.io.File(
                context.getFilesDir().getAbsoluteFile() + "/" + EditSection.LIST_FILE));

        if (content.getLength() != 0)
        {
            saveFile(client, EditSection.LIST_FILE, content, "LAST_LIST_UPDATE");
        }
    }

    private void saveDatabaseFile(GoogleApiClient client)
    {
        final FileContent content = new FileContent("text/plain",
                new java.io.File(context.getDatabasePath(
                        Database.DATABASE_NAME).getAbsolutePath()));

        saveFile(client, Database.DATABASE_NAME, content, "LAST_UPDATE");
    }

    private void saveFile(final GoogleApiClient client, final String title,
                          final FileContent content, final String preferenceKey)
    {
        final long lastUpdate = getPreferences().getLong(preferenceKey, 0);
        final DriveFolder appFolder = Drive.DriveApi.getAppFolder(client);

        // Check to see if the file exists
        final Query query = new Query.Builder()
                .addFilter(Filters.eq(SearchableField.TITLE, title)).build();
        final MetadataBuffer buffer = appFolder.queryChildren(client, query).await()
                .getMetadataBuffer();

        DriveFile file;
        long lastRemoteUpdate = 0;
        if (buffer == null || buffer.getCount() == 0)
        {
            // The file does not exist, create it
            isSaved = true;

            final MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                    .setTitle(title)
                    .setDescription(String.valueOf(lastUpdate))
                    .setLastViewedByMeDate(new Date())
                    .setMimeType("text/plain")
                    .build();

            final DriveContents contents = Drive.DriveApi.newDriveContents(client).await()
                    .getDriveContents();

            file = appFolder.createFile(client, changeSet, contents).await().getDriveFile();
        }
        else
        {
            // File already exists, read
            file = buffer.get(0).getDriveId().asDriveFile();

            lastRemoteUpdate = Long.valueOf(buffer.get(0).getDescription());//.getModifiedByMeDate().getTime();
        }

        if (lastUpdate > lastRemoteUpdate)
        {
            // Local copy is newer, replace Drive copy
            isSaved = true;
            file.updateMetadata(client, new MetadataChangeSet.Builder()
                    .setDescription(String.valueOf(lastUpdate)).build());
            try
            {

                content.writeTo(file.open(client,
                        DriveFile.MODE_WRITE_ONLY, null).await()
                        .getDriveContents().getOutputStream());
            }
            catch (IOException io)
            {
                Log.wtf(TAG, io);
                listener.onSyncError(io);
            }
        }
        else if (lastUpdate < lastRemoteUpdate)
        {
            // Drive copy is newer, download
            isUpdated = true;
            final DriveContents contents = file.open(client, DriveFile.MODE_READ_ONLY, null).await()
                    .getDriveContents();
            if (contents == null)
            {
                return;
            }

            final BufferedReader in = new BufferedReader(new InputStreamReader(
                    contents.getInputStream()));

            FileOutputStream out;
            try
            {
                out = context.openFileOutput(title, Context.MODE_PRIVATE);
                int c;
                while ((c = in.read()) != -1)
                {
                    out.write(c);
                }
                out.close();
            }
            catch (IOException e)
            {
                Log.wtf(TAG, e);
                listener.onSyncError(e);
            }
            getPreferences().edit().putLong(preferenceKey, lastRemoteUpdate).apply();
        }
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