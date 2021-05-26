package fine.eliyah.dialog

import android.content.Context
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import fine.R
import fine.databinding.ViewAlarmCreateBinding
import fine.databinding.ViewAuthorityBinding
import fine.eliyah.model.AlarmArg
import fine.eliyah.model.AlarmContent
import fine.eliyah.model.AlarmInfo
import fine.eliyah.model.AlarmInfoInsert
import kotlinx.coroutines.channels.consumesAll

class AlarmErrorDialog constructor(
    private val context : Context,
    alarm: AlarmInfoInsert,
    onRefresh : AlarmErrorDialog.()->Unit,
    onSubmit : (AlarmInfoInsert)->Unit
) {
    private val binding = ViewAlarmCreateBinding.inflate(LayoutInflater.from(context))
    private val dialog = MaterialDialog(context)
    private var data : List<AlarmContent> = ArrayList()
    private var adapter = ArrayAdapter(context, android.R.layout.simple_expandable_list_item_1, data)

    fun upload(arg:AlarmArg,list :List<AlarmContent>) = this.also {
        binding.alarmArg.text = arg.alarmName
        data = list
        binding.contentSpinner.setItems(List(data.size){i->data[i].alarmContent})
    }

    fun show() {
        dialog.title(R.string.app_alarm_error)
            .customView(view = binding.root)
            .show()
    }

    init {
        binding.apply {
            btnSubmit.setOnClickListener {
                alarm.alarmContent = contentSpinner.text
                onSubmit(alarm)
                dialog.dismiss()
            }
            btnCancel.setOnClickListener {
                dialog.dismiss()
            }
            contentSpinner.setOnFocusChangeListener { v, hasFocus ->
                if (hasFocus)
                    (v as AppCompatAutoCompleteTextView ).showDropDown()
            }
        }
        onRefresh(this)
    }
}