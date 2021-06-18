package fine.eliyah.dialog

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import fine.R
import fine.databinding.ViewOrderListBinding
import fine.eliyah.adapter.CellOrderSelectAdapter
import fine.eliyah.model.OrderInfo

class OrderSelectDialog constructor(
    private val context : Context,
    onRefresh : OrderSelectDialog.()->Unit,
    onSubmit : (OrderInfo)->Unit
) {
    private val binding = ViewOrderListBinding.inflate(LayoutInflater.from(context))
    private val dialog = MaterialDialog(context)
    private var data : List<OrderInfo> = ArrayList()
    private var adapter = CellOrderSelectAdapter{
        onSubmit(it)
        dialog.dismiss()
    }

    fun upload(list :List<OrderInfo>) = this.also {
        data = list
        adapter.setData(list)
        binding.txtEmpty2.visibility = if (data.isNotEmpty()) View.GONE else View.VISIBLE
    }

    fun show() {
        dialog.title(R.string.app_order_select)
            .customView(view = binding.root,dialogWrapContent = true)
        val window : Window? = dialog.window
        if (window!=null){
            val lp : WindowManager.LayoutParams? = window.attributes
            val d = context.resources.displayMetrics
            if (lp!=null) {
                lp.width = (d.widthPixels*0.7).toInt()
                window.attributes = lp
            }
        }
        dialog.show()
    }

    init {
        binding.apply {
            recyclerView.adapter = adapter
            txtEmpty2.visibility = if (data.isNotEmpty()) View.GONE else View.VISIBLE
        }
        onRefresh()
    }
}