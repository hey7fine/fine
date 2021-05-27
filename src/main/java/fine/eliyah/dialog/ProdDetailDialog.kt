package fine.eliyah.dialog

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import fine.R
import fine.databinding.ViewProdDetailsBinding
import fine.eliyah.adapter.CellProdDetailAdapter
import fine.eliyah.model.ProdDetail

class ProdDetailDialog constructor(
    private val context : Context,
    private val type: ProdDetail.Type,
    onRefresh : ProdDetailDialog.()->Unit,
    onInsert : ProdDetailDialog.()->Unit,
    onDelete : (ProdDetail)->Unit,
    onUpdate : (ProdDetail)->Unit
) {
    private val binding = ViewProdDetailsBinding.inflate(LayoutInflater.from(context))
    private val dialog = MaterialDialog(context)
    private val adapter = CellProdDetailAdapter(type,onDelete,onUpdate)
    private var data : List<ProdDetail> = ArrayList()

    fun upload(list :List<ProdDetail>) = this.also {
        data = list
        adapter.setData(data)
        if (data.isNotEmpty()){
            binding.txtEmpty.visibility = View.GONE
            binding.recyclerView.visibility = View.VISIBLE
        } else{
            binding.txtEmpty.visibility = View.VISIBLE
            binding.recyclerView.visibility = View.GONE
        }
    }

    fun show() {
        dialog.title(when(type){
            ProdDetail.Type.GOOD -> R.string.app_prod_good
            else -> R.string.app_prod_bad
        })
            .customView(view = binding.root,dialogWrapContent = true)
        dialog.show()
    }

    init {
        binding.apply {
            recyclerView.adapter=adapter
            if (type == ProdDetail.Type.GOOD) {
                txtBadType.visibility = View.GONE
                txtEmpty.text = context.getString(R.string.tips_good_none)
            }
            if (data.isNotEmpty()){
                txtEmpty.visibility = View.GONE
            }else
                binding.recyclerView.visibility = View.GONE
            btnAdd.setOnClickListener { onInsert() }
        }
        onRefresh()
    }
}