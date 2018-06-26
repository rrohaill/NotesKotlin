package rohail.noteskotlin.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.v7.widget.AppCompatButton
import android.util.Log
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_add_task.*
import org.json.JSONObject
import rohail.noteskotlin.R
import rohail.noteskotlin.adapters.NetworkCalls
import rohail.noteskotlin.interfaces.JSONCommunicationManager
import rohail.noteskotlin.models.Task
import rohail.noteskotlin.utils.Constants

class AddTaskActivity : BaseActivity() {

    private var title: TextInputEditText? = null
    private var submit: AppCompatButton? = null
    private var task: Task? = null
    private var editFlag: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_task)

        fetchIntent()

        initView()

        setView()
    }

    private fun setView() {
        if (!task?.title.isNullOrBlank()) {
            title?.setText(task?.title)
        }
    }

    private fun initView() {

        title = task_title
        submit = btn_submit

        submit?.setOnClickListener {
            if (title?.text?.toString().isNullOrBlank()) {
                title?.error = getString(R.string.enter_title_error)
            } else {
                saveData()
            }
        }
    }

    private fun saveData() {
        if (editFlag) {
            task = Task(task!!.id, title?.text.toString())
        } else {
            task = Task("", title?.text.toString())

        }

        NetworkCalls(object : JSONCommunicationManager {
            override fun onResponse(response: String, jsonCommunicationManager: JSONCommunicationManager) {
                try {

                    Log.i("Response :", response)
                    val jsonObject = JSONObject(response)
                    val gson = Gson()

                    task = gson.fromJson<Task>(jsonObject.toString(), Task::class.java!!)

                    onProcessNext(arrayListOf())

                    hideLoading()
                } catch (e: Exception) {
                    e.printStackTrace()
                    hideLoading()
                    showAlert(getString(R.string.title_alert), getString(R.string.FETCHING_DETAILS_PROBLEM), this@AddTaskActivity)
                }

            }

            override fun onProcessNext(listObject: MutableList<Task>) {
                val data = Intent()
                data.putExtra(MainActivity.TASK, task)
                setResult(Activity.RESULT_OK, data)

                finish()
            }

            override fun onPreRequest() {
                showLoading()
            }

            override fun onError(s: String) {
                hideLoading()

                showAlert(getString(R.string.title_alert), s, this@AddTaskActivity)
            }
        }, this).execute(if (!editFlag) Constants.createNote else Constants.updateNote, task?.title, task?.id)


    }

    private fun fetchIntent() {
        if (intent.extras != null && intent.extras.get(MainActivity.TASK)!=null) {
            task = intent.extras.get(MainActivity.TASK) as Task?
            editFlag = true
        }
    }
}
