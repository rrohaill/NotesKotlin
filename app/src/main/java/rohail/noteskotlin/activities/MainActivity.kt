package rohail.noteskotlin.activities

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.google.gson.Gson
import org.json.JSONArray
import org.json.JSONObject
import rohail.noteskotlin.R
import rohail.noteskotlin.adapters.NetworkCalls
import rohail.noteskotlin.adapters.TaskAdapter
import rohail.noteskotlin.interfaces.JSONCommunicationManager
import rohail.noteskotlin.models.Task
import rohail.noteskotlin.utils.Constants
import rohail.noteskotlin.utils.PopupDialogs


class MainActivity : BaseActivity() {

    protected val adapter = TaskAdapter {
        showNotesDialog(it)
    }

    private var editFlag: Boolean = false
    private var itemPosition: Int = -1
    private val SETTINGS_ACTIVITY: Int = 101

    private var layoutManager: StaggeredGridLayoutManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initView()
        fetchNotes()
    }

    private fun initView() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener { _ ->
            val intent = Intent(this, AddTaskActivity::class.java)
            startActivityForResult(intent, ADD_TASK_REQUEST)
        }

        initRecyclerView()
    }

    private fun initRecyclerView() {
        val recyclerView = findViewById<RecyclerView>(R.id.task_list)
        layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
    }

    private fun showNotesDialog(it: Int) {

        itemPosition = it

        PopupDialogs.createAlertDialog(adapter.tasks.get(it).title, this,
                DialogInterface.OnClickListener { dialogInterface, i ->
                    editNote(it)
                }, DialogInterface.OnClickListener { dialogInterface, i ->
            removeNotes(it)
        })

    }

    private fun removeNotes(it: Int) {
        NetworkCalls(object : JSONCommunicationManager {
            override fun onResponse(response: String, jsonCommunicationManager: JSONCommunicationManager) {
                try {

                    Log.i("Response :", response)
                    onProcessNext(adapter.tasks)

                    hideLoading()
                } catch (e: Exception) {
                    e.printStackTrace()
                    hideLoading()
                    showAlert(getString(R.string.title_alert), getString(R.string.FETCHING_DETAILS_PROBLEM), this@MainActivity)
                }

            }

            override fun onProcessNext(listObject: MutableList<Task>) {
                adapter.deleteTask(it)
                adapter.notifyDataSetChanged()
            }

            override fun onPreRequest() {
                showLoading()
            }

            override fun onError(s: String) {
                hideLoading()

                showAlert(getString(R.string.title_alert), s, this@MainActivity)
            }
        }, this).execute(Constants.removeNote, adapter.tasks[it].id)
    }

    private fun editNote(it: Int) {
        val intent = Intent(this, AddTaskActivity::class.java)
        intent.putExtra(TASK, adapter.tasks[it])
        startActivityForResult(intent, ADD_TASK_REQUEST)
        itemPosition = it
        editFlag = true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == ADD_TASK_REQUEST && resultCode == Activity.RESULT_OK) {
            val task: Task = data?.extras?.get(TASK) as Task
            if (editFlag) {
                adapter.editTask(itemPosition, task)
                editFlag = false
            } else {
                adapter.addTask(task)
            }
        } else if (requestCode == SETTINGS_ACTIVITY && resultCode == Activity.RESULT_OK) {
            recreate()
        }
    }

    private fun fetchNotes() {
        NetworkCalls(object : JSONCommunicationManager {
            override fun onResponse(response: String, jsonCommunicationManager: JSONCommunicationManager) {
                try {

                    Log.i("Response :", response)
                    val jsonArray = JSONArray(response)
                    val gson = Gson()
                    var newTasks: MutableList<Task> = ArrayList<Task>() as MutableList<Task>
                    for (i in 0 until jsonArray.length()) {
                        var jsonObject: JSONObject = jsonArray.getJSONObject(i)
                        var task: Task = gson.fromJson<Task>(jsonObject.toString(), Task::class.java)
                        newTasks.add(task)
                    }
                    adapter.tasks.clear()
                    adapter.tasks.addAll(newTasks)
                    onProcessNext(adapter.tasks)

                    hideLoading()
                } catch (e: Exception) {
                    e.printStackTrace()
                    hideLoading()
                    showAlert(getString(R.string.title_alert), getString(R.string.FETCHING_DETAILS_PROBLEM), this@MainActivity)
                }

            }

            override fun onProcessNext(listObject: MutableList<Task>) {
                adapter.notifyDataSetChanged()
            }

            override fun onPreRequest() {
                showLoading()
            }

            override fun onError(s: String) {
                hideLoading()

                showAlert(getString(R.string.title_alert), s, this@MainActivity)
            }
        }, this).execute(Constants.retrieveNotes)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivityForResult(intent, SETTINGS_ACTIVITY)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        private const val ADD_TASK_REQUEST = 0
        const val ID = "id"
        const val TITLE_TEXT = "title"
        const val TASK = "task"
    }
}
