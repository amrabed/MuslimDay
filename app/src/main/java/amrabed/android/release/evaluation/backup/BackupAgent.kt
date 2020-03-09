package amrabed.android.release.evaluation.backup

import amrabed.android.release.evaluation.data.AppDatabase
import android.app.backup.*
import android.os.ParcelFileDescriptor
import java.io.IOException

/**
 * Backup agent
 *
 */
class BackupAgent : BackupAgentHelper() {
    override fun onCreate() {
        addHelper("files",
                FileBackupHelper(this, AppDatabase.DATABASE_NAME))
        addHelper("preferences",
                SharedPreferencesBackupHelper(this, packageName + "_preferences"))
    }

    @Throws(IOException::class)
    override fun onBackup(oldState: ParcelFileDescriptor, data: BackupDataOutput,
                          newState: ParcelFileDescriptor) {
        synchronized(fileSyncLock) { super.onBackup(oldState, data, newState) }
    }

    @Throws(IOException::class)
    override fun onRestore(data: BackupDataInput, appVersionCode: Int,
                           newState: ParcelFileDescriptor) {
        synchronized(fileSyncLock) { super.onRestore(data, appVersionCode, newState) }
    }

    companion object {
        // ToDo: use the lock when updating the database
        private val fileSyncLock = Any()
    }
}