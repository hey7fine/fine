package fine.elijah.dialog

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import fine.R
import fine.databinding.ViewMaterialAlarmStateBinding
import fine.elijah.adapter.CellMaterialAlarmStateAdapter
import fine.elijah.model.MaterialAlarmInfo
import fine.elijah.model.MaterialArgs
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MaterialAlarmStatesDialog constructor(
    private val context : Context,
    siteId:String,orderDid:String,workerNo:String,
    onRefresh : MaterialAlarmStatesDialog.()->Unit,
    onSign : MaterialAlarmStatesDialog.(MaterialAlarmInfo)->Unit,
    onNormal : MaterialAlarmStatesDialog.(MaterialAlarmInfo)->Unit,
    onInsert : (MaterialAlarmInfo)->Unit
) {
    private val binding = ViewMaterialAlarmStateBinding.inflate(LayoutInflater.from(context))
    private val dialog = MaterialDialog(context)
    private val adapter = CellMaterialAlarmStateAdapter(onSign = {
        onSign(it)
    }){
        onNormal(it)
    }
    private var data : List<MaterialAlarmInfo> = ArrayList()
    private var data2 : List<MaterialArgs> = ArrayList()

    fun upload(list :List<MaterialAlarmInfo>,list2: List<MaterialArgs>) = this.also {
        data = list
        data2 = list2
        adapter.setData(data)
        if (data.isNotEmpty()) {
            binding.txtEmpty3.visibility = View.GONE
            binding.recyclerView.visibility = View.VISIBLE
        }
        else {
            binding.txtEmpty3.visibility = View.VISIBLE
            binding.recyclerView.visibility = View.GONE
        }
    }

    fun show() {
        dialog.title(R.string.app_material_alarm)
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
            recyclerView.adapter=adapter
            btnAdd2.setOnClickListener {
                MaterialAlarmInsertDialog(context, data2,
                    MaterialAlarmInfo(
                        siteId = siteId,
                        orderDid = orderDid,
                        startWorkerNo = workerNo
                    )
                ){
                    MainScope().launch {
                        onInsert(it)
                        delay(300)
                        onRefresh()
                    }
                }.show()
            }
        }
        onRefresh(this)
    }
}