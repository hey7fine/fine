package fine.elijah.dialog

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import fine.R
import fine.databinding.ViewProdDetailsBinding
import fine.elijah.adapter.CellProdDetailAdapter
import fine.elijah.model.*

class ProdDetailDialog constructor(
    private val context : Context,
    private val type: ProdDetail.Type,
    onRefresh : ProdDetailDialog.()->Unit,
    onInsert : (ProdDetail)->Unit,
    onDelete : (ProdDetail)->Unit,
    onUpdate : (ProdDetail)->Unit
) {
    private val binding = ViewProdDetailsBinding.inflate(LayoutInflater.from(context))
    private val dialog = MaterialDialog(context)
    private val adapter = CellProdDetailAdapter(type,
        onDelete = {
            ConfirmDialog(context,context.getString(R.string.tips_bad_delete)){
                onDelete(it)
            }.show()
        },
        onUpdate = { showEditDialog(it){ onUpdate(it) } }
    )
    private var data : List<ProdDetail> = ArrayList()
    private var args : List<BadArg> = ArrayList()
    private var order = OrderInfo()
    private var site = SiteInfo()
    private var worker = PersonBaseInfo()

    fun upload(data :List<ProdDetail>,args:List<BadArg>,order:OrderInfo,site: SiteInfo,worker:PersonBaseInfo) = this.also {
        this.data = data
        this.args = args
        this.order = order
        this.site = site
        this.worker = worker
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
            btnAdd.setOnClickListener {
                showEditDialog(
                    ProdDetail(
                        orderDid = order.orderDid,
                        orderNo = order.orderNo,
                        siteId = site.siteId,
                        siteName = site.siteName,
                        workerNo = worker.workerNo,
                        workerName = worker.workerName)
                ){
                    onInsert(it)
                }
            }
        }
        onRefresh()
    }

    private fun showEditDialog(prod:ProdDetail, onCommit:(ProdDetail)->Unit){
        ProdEditDialog(context,type,prod,
            onRefresh = {
                this.upload(args)
            }
        ){
            onCommit(it)
        }.show()
    }
}