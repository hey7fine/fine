package fine.eliyah.adapter

import android.view.View
import fine.BaseAdapter
import fine.R
import fine.databinding.ItemOrderSelectBinding
import fine.eliyah.model.OrderInfo

class CellOrderSelectAdapter(
    val onSelect : (OrderInfo)->Unit
) : BaseAdapter<OrderInfo>(){
    override val layoutRes: Int
        get() = R.layout.item_order_select

    override fun fill(view: View, position: Int, it: OrderInfo) {
        super.fill(view, position, it)
        ItemOrderSelectBinding.bind(view).apply {
            txtOrderNo.text = it.orderNo
            txtProName.text = it.proModel
            txtPlanNum.text = "${it.planNum}"
            txtOrderDate.text = it.orderDate
            txtCompleteNum.text = "${it.reportNum}"
            txtCustomer.text = it.client
            btnUpdate.setOnClickListener { _->
                onSelect(it)
            }
        }
    }

}