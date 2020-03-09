package amrabed.android.release.evaluation.edit

import amrabed.android.release.evaluation.R
import amrabed.android.release.evaluation.data.models.TaskViewModel
import amrabed.android.release.evaluation.edit.drag.DragListener
import amrabed.android.release.evaluation.edit.drag.ItemTouchHandler
import amrabed.android.release.evaluation.locale.LocaleManager
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.editor.*

/**
 * Editor fragment used to edit list items
 */
class EditActivity : AppCompatActivity(), DragListener, View.OnClickListener {
    private val model : TaskViewModel by lazy {
        ViewModelProvider(this).get(TaskViewModel::class.java)
    }

    private var touchHelper: ItemTouchHelper? = null
    private lateinit var adapter: EditListAdapter

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LocaleManager.setLocale(this)
        setContentView(R.layout.editor)

        listView?.addItemDecoration(DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL))
        model.taskList?.observe(this, Observer { list ->
            adapter = EditListAdapter(this, this, list)
            listView?.adapter = adapter
        })

        touchHelper = ItemTouchHelper(ItemTouchHandler(this, this))
        touchHelper!!.attachToRecyclerView(listView)

        fab.setOnClickListener(this)
    }

    override fun onResume() {
        super.onResume()
        LocaleManager.setLocale(this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_edit, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_add) {
            adapter.addNewItem()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        checkSaved()
    }

    override fun onDrag(holder: RecyclerView.ViewHolder?) {
        touchHelper!!.startDrag(holder!!)
    }

    override fun onItemMoved(source: RecyclerView.ViewHolder, destination: RecyclerView.ViewHolder) {
        adapter.moveItem(source, destination)
    }

    override fun onItemRemoved(holder: RecyclerView.ViewHolder) {
        val position = holder.adapterPosition
        val item = adapter.list!![position]
        adapter.removeItem(holder)
        val view = currentFocus
        if (view != null) {
            Snackbar.make(view, R.string.deleted, Snackbar.LENGTH_LONG)
                    .setAction(R.string.undo) { adapter.putBack(position, item) }
                    .show()
        }
    }

    override fun onItemHidden(holder: RecyclerView.ViewHolder) {
        adapter.hide(holder)
        val view = currentFocus
        if (view != null) {
            Snackbar.make(view, R.string.hidden, Snackbar.LENGTH_LONG).show()
        }
    }

    override fun onItemAdded(position: Int) {
        val view = currentFocus
        if (view != null) {
            Snackbar.make(view, R.string.added, Snackbar.LENGTH_LONG).show()
        }
        listView!!.smoothScrollToPosition(position)
    }

    /**
     * Handles FAB click by saving the modified list
     *
     * @param view the clicked FAB
     */
    override fun onClick(view: View) {
        checkSaved()
    }

    private fun checkSaved() {
        if (adapter.isChanged) {
            AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setMessage(R.string.confirm_save)
                    .setCancelable(false)
                    .setPositiveButton(R.string.save
                    ) { _: DialogInterface?, _: Int ->
                        adapter.commit()
                        setResult(Activity.RESULT_OK)
                        finish()
                    }
                    .setNegativeButton(R.string.discard) { dialog: DialogInterface, _: Int ->
                        dialog.cancel()
                        finish()
                    }
                    .create().show()
        } else {
            finish()
        }
    }
}