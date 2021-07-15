package fine.elijah.dialog

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import fine.R
import fine.databinding.ViewAlarmStateBinding
import fine.elijah.adapter.CellAlarmStateAdapter
import fine.elijah.model.AlarmInfo

class AlarmStatesDialog constructor(
    private val context : Context,type:Type,
    onRefresh : AlarmStatesDialog.()->Unit,
    onSubmit : (AlarmInfo)->Unit
) {
    private val binding = ViewAlarmStateBinding.inflate(LayoutInflater.from(context))
    private val dialog = MaterialDialog(context)
    private val adapter = CellAlarmStateAdapter(type){
        onSubmit(it)
        dialog.dismiss()
    }
    private var data : List<AlarmInfo> = ArrayList()

    fun upload(list :List<AlarmInfo>) = this.also {
        data = list
        adapter.setData(data)
    }

    fun show() {
        dialog.title(R.string.app_alarm_state)
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
            if (type == Type.Sign){
                signName.visibility = View.GONE
                signTime.visibility = View.GONE
            }
            recyclerView.adapter=adapter
        }
        onRefresh(this)
    }

    enum class Type{ Sign, Normal }
}