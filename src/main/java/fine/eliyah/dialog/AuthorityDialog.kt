package fine.eliyah.dialog

import android.content.Context
import android.view.LayoutInflater
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import fine.R
import fine.databinding.ViewAuthorityBinding
import fine.eliyah.model.PersonBaseInfo

class AuthorityDialog constructor(
    context : Context,
    onRefresh : AuthorityDialog.(String,String)->Unit,
    val onSubmit : (String,String)->Unit
    ) {
    private val binding = ViewAuthorityBinding.inflate(LayoutInflater.from(context))
    private val dialog = MaterialDialog(context)

    fun upload(user:PersonBaseInfo){
        if (user.workerNo.equals("${binding.txtUsername.text}"))
            onSubmit(user.workerNo,user.psw)
        dialog.dismiss()
    }

    fun show() {
        dialog.title(R.string.app_authority)
            .customView(view = binding.root)
            .show()
    }

    init {
        binding.apply {
            btnSubmit.setOnClickListener {
                onRefresh("${txtUsername.text}","${txtPassword.text}")
            }
            btnCancel.setOnClickListener {
                dialog.dismiss()
            }
        }
    }
}