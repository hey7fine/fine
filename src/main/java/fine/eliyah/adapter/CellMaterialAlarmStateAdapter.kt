package fine.eliyah.adapter

import android.content.res.ColorStateList
import android.os.Build
import android.view.View
import fine.BaseAdapter
import fine.R
import fine.databinding.ItemMaterialAlarmStateBinding
import fine.eliyah.model.MaterialAlarmInfo
import kotlin.math.roundToInt

class CellMaterialAlarmStateAdapter constructor(
    private val onSign:(item: MaterialAlarmInfo)->Unit = {},
    private val onNormal:(item: MaterialAlarmInfo)->Unit = {}
): BaseAdapter<MaterialAlarmInfo>() {
    override val layoutRes: Int
        get() = R.layout.item_material_alarm_state

    override fun fill(view: View, position:Int, it: MaterialAlarmInfo) {
        super.fill(view,position, it)
        val binding = ItemMaterialAlarmStateBinding.bind(view)
        binding.apply {
            if (it.signInTime==null){
                btnNormal.visibility = View.GONE
                btnSign.visibility = View.VISIBLE
            }else{
                btnNormal.visibility = View.VISIBLE
                btnSign.visibility = View.GONE
            }
            name.text = it.materialCode
            num.text = "${it.alarmNum.roundToInt()}"
            commons.text = it.comment
            startTime.text = it.startTime
            startName.text = it.startWorkerNo
            signTime.text = it.signInTime
            signName.text = it.signWorkerNo
            keepTime.text = it.duration

            btnSign.setOnClickListener { _->
                onSign(it)
            }
            btnNormal.setOnClickListener { _->
                onNormal(it)
            }
        }
    }
}