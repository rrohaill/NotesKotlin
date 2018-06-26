package rohail.noteskotlin.utils

import android.content.Context
import android.content.DialogInterface
import android.support.v7.app.AlertDialog
import rohail.noteskotlin.R

object PopupDialogs {


    fun createAlertDialog(reason: String, ac: Context,
                          editListener: DialogInterface.OnClickListener, deleteListener: DialogInterface.OnClickListener) {

        AlertDialog.Builder(ac)
                .setMessage(reason)
                .setPositiveButton(ac.getString(R.string.btn_edit)) { dialog, which -> editListener.onClick(dialog, which) }
                .setNegativeButton(ac.getString(R.string.btn_delete)) { dialog, which -> deleteListener.onClick(dialog, which) }
                .setNeutralButton(ac.getString(R.string.btn_cancel)){ dialog, _ -> dialog.dismiss() }
                .show()

    }

}
