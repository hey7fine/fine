package fine.eliyah.adapter

import android.view.View
import fine.BaseAdapter
import fine.R
import fine.databinding.ItemProdDetailBinding
import fine.eliyah.model.ProdDetail

class CellProdDetailAdapter(
    private val type: ProdDetail.Type,
    private val onDelete:(ProdDetail)->Unit,
    private val onUpdate:(ProdDetail)->Unit
    ) : BaseAdapter<ProdDetail>() {
    override val layoutRes: Int
        get() = R.layout.item_prod_detail

    override fun fill(view: View, position: Int, it: ProdDetail) {
        super.fill(view, position, it)
        ItemProdDetailBinding.bind(view).apply {
            txtOrderNo.text = it.orderNo
            txtSiteName.text = it.siteName
            when(type){
                ProdDetail.Type.GOOD->{
                    txtBadName.visibility = View.GONE
                    txtNum.text = "${it.reportNum}"
                }
                ProdDetail.Type.BAD->{
                    txtBadName.text = it.badContent
                    txtNum.text = "${it.badNum}"
                }
            }
            txtWorkerName.text = it.workerName
            btnDelete.setOnClickListener { _-> onDelete(it) }
            btnUpdate.setOnClickListener { _-> onUpdate(it) }
        }
    }
}