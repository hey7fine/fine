package fine.eliyah.dialog

import android.R
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import fine.databinding.ViewProdEditBinding
import fine.eliyah.model.ProdDetail

class ProdEditDialog constructor(
    private val context : Context,
    private val type:ProdDetail.Type,
    private var prod: ProdDetail,
    onRefresh : ProdEditDialog.()->Unit,
    onSubmit : (ProdDetail)->Unit
) {
    private val binding = ViewProdEditBinding.inflate(LayoutInflater.from(context))
    private val dialog = MaterialDialog(context)

    fun show() {
        dialog.title(fine.R.string.app_prod_good)
            .customView(view = binding.root)
            .show()
    }

    init {
        binding.apply {
            txtSiteName.text = prod.siteName
            txtOrderNo.text = prod.orderNo
            txtWorkerName.text = prod.workerName
            when(type){
                ProdDetail.Type.GOOD->{
                    txtNum.setText("${prod.reportNum}")
                    badCell.visibility = View.GONE
                }
                else -> {
                    txtNum.setText("${prod.badNum}")
                }
            }

            btnSubmit.setOnClickListener {
                when(type){
                    ProdDetail.Type.GOOD->{
                        prod.reportNum = "${txtNum.text}".toIntOrNull()?:0
                    }
                    else -> {
                        prod.badNum = "${txtNum.text}".toIntOrNull()?:0
                    }
                }
                onSubmit(prod)
                dialog.dismiss()
            }
            btnCancel.setOnClickListener {
                dialog.dismiss()
            }
        }
        onRefresh(this)
    }
}