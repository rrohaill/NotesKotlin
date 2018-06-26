package rohail.noteskotlin.adapters

import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import rohail.noteskotlin.R
import rohail.noteskotlin.R.id.card_view
import rohail.noteskotlin.R.id.txt_title
import rohail.noteskotlin.models.Task

/**
 * Adapter that displays a list of Tasks.
 */
class TaskAdapter(tasks: MutableList<Task> = ArrayList(), val listener: (Int) -> Unit) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val context = parent.context
        val view = LayoutInflater.from(context)?.inflate(R.layout.list_item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bindTask(tasks[position], listener)
    }

    var tasks: MutableList<Task> = tasks
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount(): Int {
        return tasks.size
    }

    fun addTask(task: Task) {
        tasks.add(task)
        notifyDataSetChanged()
    }

    fun deleteTask(task: Int) {
        tasks.removeAt(task)
        notifyDataSetChanged()
    }

    fun editTask(position: Int, task: Task) {
        tasks.set(position, task)
        notifyDataSetChanged()
    }

    class TaskViewHolder(view: View?) : RecyclerView.ViewHolder(view) {
        private val titleTextView = view?.findViewById(txt_title) as TextView
        private val cardView = view?.findViewById<CardView>(card_view)

        fun bindTask(task: Task, listener: (Int) -> Unit) {
            titleTextView.text = task.title
            cardView?.setOnClickListener(View.OnClickListener { listener(adapterPosition) })
        }

        fun getView(): CardView? {
            return cardView
        }
    }
}