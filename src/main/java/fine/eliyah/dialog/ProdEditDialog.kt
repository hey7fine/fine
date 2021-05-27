package fine.eliyah.dialog

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import fine.R
import fine.databinding.ViewProdEditBinding
import fine.eliyah.model.BadArg
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
    private var args : List<BadArg> = ArrayList()

    fun upload(args:List<BadArg>) = this.also {
        this.args = args
        binding.argSp.adapter = ArrayAdapter(context, R.layout.item_spinner, List(args.size){args[it].badName})
    }

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
            argSp.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    contentSp.adapter = ArrayAdapter(context, R.layout.item_spinner,List(args[position].badContents.size){args[position].badContents[it].badContent})
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }
            }
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
                        prod.badCode = args[argSp.selectedItemPosition].badCode
                        prod.badName = args[argSp.selectedItemPosition].badName
                        prod.badContent = args[argSp.selectedItemPosition].badContents[contentSp.selectedItemPosition].badContent
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