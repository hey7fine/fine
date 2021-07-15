package fine.elijah.dialog

import android.content.Context
import android.view.LayoutInflater
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import fine.R
import fine.databinding.ViewAlarmArgsBinding
import fine.elijah.adapter.CellAlarmArgsAdapter
import fine.elijah.model.AlarmArg

class AlarmArgsDialog constructor(
    context : Context,
    onRefresh : AlarmArgsDialog.()->Unit,
    onSubmit : (AlarmArg)->Unit
) {
    private val binding = ViewAlarmArgsBinding.inflate(LayoutInflater.from(context))
    private val dialog = MaterialDialog(context)
    private val adapter = CellAlarmArgsAdapter{
        onSubmit(it)
        dialog.dismiss()
    }
    private var data : List<AlarmArg> = ArrayList()

    fun upload(list :List<AlarmArg>) = this.also {
        data = list
        adapter.setData(data)
    }

    fun show() {
        dialog.title(R.string.app_alarm_arg)
            .customView(view = binding.root)
            .show()
    }

    init {
        binding.apply {
            recyclerView.adapter=adapter
        }
        onRefresh(this)
    }
}