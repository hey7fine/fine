package fine.elijah.dialog

import android.content.Context
import android.view.LayoutInflater
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import fine.R
import fine.databinding.DialogCastBinding

class CastSettingDialog constructor(
    context: Context,
    private val initial:Int,
    val onSubmit : (Int)->Unit
) {
    val binding = DialogCastBinding.inflate(LayoutInflater.from(context))
    val dialog = MaterialDialog(context)

    fun show(){
        dialog.title(R.string.app_cast_setting)
            .customView(view = binding.root)
            .positiveButton {
                onSubmit("${binding.editText.text}".toIntOrNull()?:initial)
            }
            .show()
    }

    init {
        binding.editText.hint = "$initial"
    }
}