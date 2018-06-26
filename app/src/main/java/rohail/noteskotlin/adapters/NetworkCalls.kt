package rohail.noteskotlin.adapters

import android.content.Context
import android.content.DialogInterface
import android.os.AsyncTask
import android.util.Log
import com.google.gson.JsonObject
import rohail.noteskotlin.R
import rohail.noteskotlin.activities.BaseActivity
import rohail.noteskotlin.interfaces.JSONCommunicationManager
import rohail.noteskotlin.utils.Constants
import java.io.*
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL


open class NetworkCalls : AsyncTask<String, Int, String> {

    private var communicationManager: JSONCommunicationManager
    internal var context: Context
    private var response: String = ""
    private var webAddress = Constants.baseUrl
    private var error: Boolean? = false
    private var isInternetConnected = true
    private var urlConnection: HttpURLConnection? = null
    private var httpMethod: String? = "GET"

    constructor(communicationManager: JSONCommunicationManager, context: Context) {
        this.communicationManager = communicationManager
        this.context = context
    }

    override fun onPreExecute() {
        if (!(context as BaseActivity).haveInternet(context)) {
            (context as BaseActivity).showAlertDialog(context.getString(R.string.title_alert),
                    context.getString(R.string.title_internet_problem),
                    context, DialogInterface.OnClickListener { dialogInterface, i ->
                (context as BaseActivity).dismissDialog()
                (context as BaseActivity).finish()
            })
            isInternetConnected = false
            return
        } else {
            super.onPreExecute()
            communicationManager.onPreRequest()
        }
    }

    override fun doInBackground(vararg params: String): String? {
        if (isInternetConnected) {
            val url: URL
            val result = StringBuilder()
            var putpostFlag: Boolean = false
            var jsonObject = JsonObject()

            try {

                when {
                    params[0] == Constants.retrieveNotes -> {

                        url = URL(webAddress)
                        httpMethod = "GET"

                    }
                    params[0] == Constants.createNote -> {

                        jsonObject.addProperty("title", params[1])
                        url = URL(webAddress)
                        httpMethod = "POST"
                        putpostFlag = true

                    }
                    params[0] == Constants.updateNote -> {

                        var jsonObject = JsonObject()
                        jsonObject.addProperty("title", params[2])
                        url = URL(webAddress + "/" + params[2])
                        httpMethod = "PUT"
                        putpostFlag = true

                    }
                    params[0] == Constants.removeNote -> {

                        var jsonObject = JsonObject()
                        url = URL(webAddress + "/" + params[1])
                        httpMethod = "DELETE"

                    }
                    else -> {

                        url = URL(webAddress + params[0])
                        httpMethod = "GET"

                    }
                }

                Log.d("URL", url.toString())


                urlConnection = url.openConnection() as HttpURLConnection
                urlConnection!!.requestMethod = httpMethod
                urlConnection!!.readTimeout = 10000
                urlConnection!!.connectTimeout = 15000
                urlConnection!!.setRequestProperty("Content-Type", "application/json")
                urlConnection!!.setRequestProperty("charset", "utf-8")

                if (putpostFlag) {
                    urlConnection?.doOutput = true
                    val wr: DataOutputStream = DataOutputStream(urlConnection!!.outputStream)
                    wr.writeChars(jsonObject.toString())
                    wr.flush()
                    putpostFlag = false

                    val `in` = BufferedInputStream(urlConnection!!.inputStream)

                    val reader = BufferedReader(InputStreamReader(`in`))

                    var line: String = reader.readLine()
                    var flag: Boolean = true
                    while (flag) {
                        result.append(line)
                        try {
                            line = reader.readLine()
                        } catch (ex: Exception) {
                            flag = false
                        }
                    }
                    wr.close()

                } else if (params[0] == Constants.removeNote) {
                    result.append("Success")
                } else {
                    val `in` = BufferedInputStream(urlConnection!!.inputStream)

                    val reader = BufferedReader(InputStreamReader(`in`))

                    var line: String = reader.readLine()
                    var flag: Boolean = true
                    while (flag) {
                        result.append(line)
                        try {
                            line = reader.readLine()
                        } catch (ex: Exception) {
                            flag = false
                        }
                    }
                }


            } catch (e: FileNotFoundException) {
                e.printStackTrace()
                error = true
                response = context.getString(R.string.exception_invalid_response)
            } catch (e: SocketTimeoutException) {
                e.printStackTrace()
                error = true
                response = context.getString(R.string.exception_time_out)
            } catch (e: Exception) {
                e.printStackTrace()
                error = true
                response = context.getString(R.string.exception_invalid_response)
            } finally {
                urlConnection!!.disconnect()
            }


            response = result.toString()
            Log.i("response", response)

            if (response.isEmpty()) {
                error = true
                response = context.getString(R.string.exception_invalid_response)
            }


            return response
        } else {
            return null
        }
    }


    override fun onPostExecute(response: String?) {
        super.onPostExecute(response)
        if (response != null) {
            if (error!!) {
                communicationManager.onError(response)
            } else {
                communicationManager.onResponse(response, communicationManager)
            }
        }
    }
}
