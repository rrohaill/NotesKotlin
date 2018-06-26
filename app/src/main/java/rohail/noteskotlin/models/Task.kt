package rohail.noteskotlin.models

import java.io.Serializable

/**
 * Represents a task
 */
data class Task(var id: String, var title: String) : Serializable {
    override fun toString(): String {
        return "Task(id='$id', title='$title')"
    }
}