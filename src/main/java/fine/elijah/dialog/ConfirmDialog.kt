package fine.elijah.dialog

import android.content.Context
import android.view.LayoutInflater
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import fine.R
import fine.databinding.ViewConfirmBinding

class ConfirmDialog constructor(
    private val context : Context,
    message:String,
    onSubmit : ()->Unit
) {
    private val binding = ViewConfirmBinding.inflate(LayoutInflater.from(context))
    private val dialog = MaterialDialog(context)

    fun show() {
        dialog.title(R.string.app_alarm_state)
            .customView(view = binding.root)
            .show()
    }

    init {
        binding.apply {
            txtMessage.text = message
            btnSubmit.setOnClickListener {
                onSubmit()
                dialog.dismiss()
            }
            btnCancel.setOnClickListener {
                dialog.dismiss()
            }
        }
    }
}