package amrabed.android.release.evaluation

import amrabed.android.release.evaluation.models.TaskViewModel
import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import kotlinx.android.synthetic.main.main_activity.*

/**
 * Editor fragment used to edit list items
 */
class EditActivity : BaseActivity(), View.OnClickListener {
    private val model by viewModels<TaskViewModel>()

    private val navController by lazy {
        findNavController(R.id.fragment).apply {
            addOnDestinationChangedListener { _, destination, _ ->
                toolbar.visibility = if (destination.id == R.id.taskEditor) View.GONE else View.VISIBLE
            }
        }
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_activity)
        setSupportActionBar(toolbar.apply { setupWithNavController(navController, null) })
    }

    override fun onBackPressed() {
        if (navController.currentDestination?.id == R.id.taskEditor) {
            super.onBackPressed()
        } else {
            checkSaved()
        }
    }

    override fun onClick(view: View) {
        // Handle FAB click by saving the changes (if any)
        checkSaved()
    }

    private fun checkSaved() {
        if (model.isChanged()) {
            AlertDialog.Builder(this)
                    .setMessage(R.string.confirmSave)
                    .setCancelable(false)
                    .setPositiveButton(R.string.save
                    ) { _, _ ->
                        model.commit()
                        setResult(Activity.RESULT_OK)
                        finish()
                    }
                    .setNegativeButton(R.string.discard) { _, _ ->
                        model.discard()
                        finish()
                    }
                    .create().show()
        } else {
            finish()
        }

    }
}