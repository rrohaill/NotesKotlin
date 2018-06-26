package rohail.noteskotlin.activities

import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import rohail.noteskotlin.R
import rohail.noteskotlin.utils.LocaleHelper
import rohail.noteskotlin.utils.Preferences
import java.util.*


open class BaseActivity : AppCompatActivity() {

    var myLocale: Locale? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        if (Preferences.getInstance(this).isLightTheme) {
            setTheme(R.style.AppThemeLight)
        }

        if (Preferences.getInstance(this).isCzechLanguage) {

            var conf = Configuration()
            conf.setLocale(Locale("cs"))
            LocaleHelper.setLocale(this, "cs")


        } else {
            var conf = Configuration()
            conf.setLocale(Locale("en"))
            LocaleHelper.setLocale(this, "en")
        }

        super.onCreate(savedInstanceState)
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(updateBaseContextLocale(base))
    }

    private fun updateBaseContextLocale(context: Context): Context {
        var language: String? = LocaleHelper.getLanguage(context) // Helper method to get saved language from SharedPreferences
        val locale = Locale(language)
        Locale.setDefault(locale)

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            updateResourcesLocale(context, locale)
        } else updateResourcesLocaleLegacy(context, locale)

    }

    private fun updateResourcesLocale(context: Context, locale: Locale): Context {
        val configuration = context.resources.configuration
        configuration.setLocale(locale)
        return context.createConfigurationContext(configuration)
    }

    private fun updateResourcesLocaleLegacy(context: Context, locale: Locale): Context {
        val resources = context.resources
        val configuration = resources.configuration
        configuration.locale = locale
        resources.updateConfiguration(configuration, resources.displayMetrics)
        return context
    }

    private var dialog: AlertDialog? = null
    private var loadingDialog: ProgressDialog? = null

    fun showLoading() {
        if (loadingDialog == null) {
            loadingDialog = ProgressDialog(this, R.style.alert_dialog)
            loadingDialog!!.setTitle(getString(R.string.title_please_wait))
            if (Build.VERSION.SDK_INT < 21) {
                loadingDialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            }
            loadingDialog!!.setMessage(getString(R.string.title_processing))
            loadingDialog!!.setCancelable(false)
            loadingDialog!!.isIndeterminate = false
        }
        if (!loadingDialog!!.isShowing)
            loadingDialog!!.show()
    }

    fun hideLoading() {

        if (loadingDialog != null) {
            loadingDialog!!.dismiss()
            loadingDialog!!.cancel()
        }
    }

    fun haveInternet(con: Context): Boolean {

        val connectivity = con
                .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivity != null) {
            val info = connectivity.allNetworkInfo
            if (info != null)
                for (i in info.indices)
                    if (info[i].state == NetworkInfo.State.CONNECTED) {
                        return true
                    }
        }
        return false
    }

    fun showAlertDialog(title: String, message: String, context: Context, onClickListener: DialogInterface.OnClickListener) {
        val alertDialogBuilder = AlertDialog.Builder(
                context)

        // set title
        alertDialogBuilder.setTitle(title)

        // set dialog message
        alertDialogBuilder
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Ok", onClickListener)

        // create alert dialog
        val alertDialog = alertDialogBuilder.create()

        // show it
        alertDialog.show()
        setDialog(alertDialog)
    }

    fun showAlert(title: String, message: String, context: Context) {
        val alertDialogBuilder = AlertDialog.Builder(
                context)

        // set title
        alertDialogBuilder.setTitle(title)

        // set dialog message
        alertDialogBuilder
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(getString(R.string.btn_ok)) { dialog, id ->
                    // if this button is clicked, close
                    // current activity
                    dialog.dismiss()
                }
                .setNegativeButton(getString(R.string.btn_cancel)) { dialog, id ->
                    // if this button is clicked, just close
                    // the dialog box and do nothing
                    dialog.cancel()
                }

        // create alert dialog
        val alertDialog = alertDialogBuilder.create()

        // show it
        alertDialog.show()
        setDialog(alertDialog)
    }

    fun setDialog(d: AlertDialog) {
        dialog = d
    }

    fun dismissDialog() {
        dialog?.dismiss()
    }

}
