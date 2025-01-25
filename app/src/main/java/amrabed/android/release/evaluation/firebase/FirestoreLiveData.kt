package amrabed.android.release.evaluation.firebase

import amrabed.android.release.evaluation.core.Task
import android.util.Log
import androidx.lifecycle.LiveData
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ListenerRegistration

class FirestoreLiveData(private val collection: CollectionReference?) : LiveData<MutableList<Task>>() {

    private var listener: ListenerRegistration? = null

    override fun onActive() {
        super.onActive()
        listener = collection?.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.i(TAG, e.message, e.cause)
            } else {
                value = arrayListOf<Task>().also {
                    snapshot?.documents?.forEach { document ->
                        val item = document.toObject(Task::class.java)
                        if (item != null) {
                            it.add(item)
                        }
                    }
                }
            }
        }
    }

    override fun onInactive() {
        super.onInactive()
        if (!hasActiveObservers()) {
            listener?.remove()
            listener = null
        }
    }

    companion object {
        private val TAG = FirestoreLiveData::class.java.simpleName
    }
}