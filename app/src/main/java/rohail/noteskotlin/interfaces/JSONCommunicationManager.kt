package rohail.noteskotlin.interfaces

import rohail.noteskotlin.models.Task

interface JSONCommunicationManager {
    fun onResponse(response: String, jsonCommunicationManager: JSONCommunicationManager)

    fun onProcessNext(listObject: MutableList<Task>)

    fun onPreRequest()

    fun onError(s: String)
}
