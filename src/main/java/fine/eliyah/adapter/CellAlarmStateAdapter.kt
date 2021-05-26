package fine.eliyah.adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.view.View
import fine.BaseAdapter
import fine.R
import fine.databinding.ItemAlarmStateBinding
import fine.eliyah.dialog.AlarmStatesDialog
import fine.eliyah.model.AlarmInfo

class CellAlarmStateAdapter constructor(
    private val type:AlarmStatesDialog.Type,
    private val onAction:(item: AlarmInfo)->Unit = {}
): BaseAdapter<AlarmInfo>() {
    override val layoutRes: Int
        get() = R.layout.item_alarm_state

    override fun fill(view: View, position:Int, it: AlarmInfo) {
        super.fill(view,position, it)
        val binding = ItemAlarmStateBinding.bind(view)
        binding.apply {
            if (type == AlarmStatesDialog.Type.Sign){
                signName.visibility = View.GONE
                signTime.visibility = View.GONE
                btnNormal.visibility = View.GONE
            }else{
                btnSign.visibility = View.GONE
            }
            arg.text = it.alarmName
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                arg.backgroundTintList = ColorStateList.valueOf(it.formattedColor)
            }
            content.text = it.alarmContent
            startTime.text = it.startTime
            startName.text = it.startWorkerName
            signTime.text = it.signTime
            signName.text = it.signWorkerName
            keepTime.text = it.duration

            btnSign.setOnClickListener { _->
                onAction(it)
            }
            btnNormal.setOnClickListener { _->
                onAction(it)
            }
        }
    }
}