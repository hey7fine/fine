package fine.elijah.adapter

import android.content.res.ColorStateList
import android.os.Build
import android.view.View
import fine.BaseAdapter
import fine.R
import fine.databinding.ItemAlarmArgBinding
import fine.elijah.model.AlarmArg

class CellAlarmArgsAdapter(
    private val onItemClick:(AlarmArg)->Unit = {}
) : BaseAdapter<AlarmArg>(){

    override val layoutRes: Int
        get() = R.layout.item_alarm_arg

    override fun fill(view: View, position: Int, it: AlarmArg) {
        super.fill(view, position, it)
        val binding = ItemAlarmArgBinding.bind(view)
        binding.apply {
            arg.text = it.alarmName
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                arg.backgroundTintList = ColorStateList.valueOf(it.formattedColor)
            }
            arg.setOnClickListener { _->
                onItemClick(it)
            }
        }
    }
}