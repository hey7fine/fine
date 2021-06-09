package fine.eliyah.dialog

import android.content.Context
import android.view.LayoutInflater
import android.widget.Toast
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
        if (user.workerNo == "${binding.txtUsername.text}")
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
                if(txtUsername.text.isNotEmpty() && txtPassword.text.isNotEmpty())
                    onRefresh("${txtUsername.text}","${txtPassword.text}")
                else
                    Toast.makeText(context, "必填项不能为空", Toast.LENGTH_SHORT).show()
            }
            btnCancel.setOnClickListener {
                dialog.dismiss()
            }
        }
    }
}