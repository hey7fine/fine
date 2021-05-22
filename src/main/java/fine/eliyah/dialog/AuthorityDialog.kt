package fine.eliyah.dialog

import android.content.Context
import android.view.LayoutInflater
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import fine.R
import fine.databinding.ViewAuthorityBinding

class AuthorityDialog constructor(
    context : Context,
    onSubmit : (String,String)->Unit
    ) {
    private val binding = ViewAuthorityBinding.inflate(LayoutInflater.from(context))
    private val dialog = MaterialDialog(context)

    fun show() {
        dialog.title(R.string.app_authority)
            .customView(view = binding.root)
            .show()
    }

    init {
        binding.apply {
            btnSubmit.setOnClickListener {
                onSubmit("${txtUsername.text}","${txtPassword.text}")
                dialog.dismiss()
            }
            btnCancel.setOnClickListener {
                dialog.dismiss()
            }
        }
    }
}