package fine.elijah.dialog

import android.content.Context
import android.view.LayoutInflater
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.list.listItems
import fine.R
import fine.databinding.ViewAlarmArgsBinding
import fine.model.DialogType

class TVSettingDialog constructor(
    val context: Context,
    private val types:List<DialogType> = listOf()
){
    private val dialog = MaterialDialog(context)

    fun show() {
        val titles = List(types.size){types[it].title}
        dialog.title(text = "设置")
            .show {
                listItems(items = titles){ _, index, _ ->
                    /*
                    when(types[index]){
                        ElijahTVActivity.DialogTYPE.SingleLineChoose->{
                            val dialog = MaterialDialog(this@ElijahTVActivity)
                            dialog.title(text = "产线选择")
                                .show {
                                    listItems(items = List(lines.size){lines.values.toList()[it]}){ dialog, index, text ->
                                        MainScope().launch {
                                            currentLine =lines.keys.toList()[index]
//                                                    dataStore.setValue(CURRENT_LINE,currentLine)
                                            dataStore.edit { it[ElijahTVActivity.CURRENT_LINE] = currentLine }
                                        }
                                    }
                                }
                        }
                        ElijahTVActivity.DialogTYPE.MultiLineChoose ->{
                            if (lines.size>0){
                                val dialog = MaterialDialog(this@ElijahTVActivity)
                                dialog.title(text = "产线选择")
                                    .show {
                                        MainScope().launch {
                                            // 设置默认选中的选项，全为false默认均未选中
                                            val linenames = ArrayList<String>()
                                            lines.forEach {
                                                linenames.add(it.key)
                                            }
                                            var valueset = mutableSetOf<String>()
//                                                valueset = sharedPreferences.getStringSet("chosen_lines",mutableSetOf<String>())?:mutableSetOf()
//                                                    dataStore.getValueFlow(CHOSEN_LINES, mutableSetOf()).collect {
//
//                                                    }

                                            dataStore.data.map { it[ElijahTVActivity.CHOSEN_LINES] }
                                                .collect {
                                                    if (it != null) {
                                                        valueset = it.toMutableSet()
                                                    }
                                                    val initial = Array(valueset.size){valueset.toTypedArray()[it].toInt()}.toIntArray()

                                                    listItemsMultiChoice(items = linenames,initialSelection = initial) {dialog, indices, items ->
                                                        MainScope().launch {
                                                            Log.e("TAG", "showDialog: $indices \n $items")
                                                            valueset.clear()
                                                            for (i in indices){
                                                                valueset.add("$i")
                                                            }
//                                                                    dataStore.setValue(preferencesSetKey<String>("chosen_lines"),valueset)
                                                            dataStore.edit { it[ElijahTVActivity.CHOSEN_LINES] = valueset }
                                                            chosenLines.clear()
                                                            valueset.forEach {
                                                                try {
                                                                    chosenLines.add(lines.keys.toList()[it.toInt()])
                                                                }catch (e:Exception){0}
                                                            }
                                                            refresh()
                                                        }
                                                    }
                                                    positiveButton {}
                                                }
                                        }
                                    }
                            }
                        }
                        ElijahTVActivity.DialogTYPE.BoardSwitch ->{
                            if (boards.size>0){
                                val dialogBS = MaterialDialog(this@ElijahTVActivity)
                                val titles = List(boards.size){boards[it].title}
                                dialogBS.title(text = "切换看板")
                                    .show {
                                        listItems(items = titles){ dialog, index, text ->
                                            MainScope().launch {
//                                                        dataStore.setValue(CURRENT_BOARD,index)
                                                dataStore.edit { it[ElijahTVActivity.CURRENT_BOARD] = index }

                                                startActivity(boards[index].intent)
//                                                        finish()
                                            }
                                        }
                                    }
                            }
                        }
                        ElijahTVActivity.DialogTYPE.CastSetting ->{
                            val customizeDialog = MaterialDialog(this@ElijahTVActivity)
                            val binding = DialogCastBinding.inflate(layoutInflater)
                            customizeDialog.title(text = "轮播设置 (秒)")
                                .customView(view = binding.root)
                                .positiveButton {
                                    MainScope().launch {
                                        try {
                                            currentCast = binding.editText.text.toString().toInt()
//                                                    dataStore.setValue(CURRENT_CAST,currentCast)
                                            dataStore.edit { it[ElijahTVActivity.CURRENT_CAST] = currentCast }
                                        }catch (e:Exception){

                                        }
                                    }
                                }.show()
                            // 获取EditView中的输入内容
                            binding.editText.apply {
                                MainScope().launch {
//                                        dataStore.getValueFlow(CURRENT_CAST,10).collect {
//                                            hint = "$it"
//                                        }
                                    dataStore.data.map { it[ElijahTVActivity.CURRENT_CAST] }
                                        .collect{
                                            hint="$it"
                                        }
                                }
                            }
                        }
                        ElijahTVActivity.DialogTYPE.IPConfig ->{
                            Log.e("IP",IPAddress)
                            val customizeDialog = MaterialDialog(this@ElijahTVActivity)
                            val binding = DialogIpBinding.inflate(layoutInflater)
                            customizeDialog.title(text = "主机配置")
                                .customView(view = binding.root)
                                .positiveButton {
                                    IPAddress="${
                                        if("${binding.txtIp.text}"=="") IPAddress.substringBeforeLast(":")
                                        else "${binding.txtIp.text}"
                                    }:${
                                        if("${binding.txtPort.text}"=="") IPAddress.substringAfterLast(":")
                                        else "${binding.txtPort.text}"
                                    }"

                                    MainScope().launch {
//                                                dataStore.setValue(IP_ADDRESS,IPAddress)
                                        dataStore.edit { it[ElijahTVActivity.IP_ADDRESS] = IPAddress }
                                    }

                                    initData()
                                }.show()
                            // 获取EditView中的输入内容

                            binding.txtIp.apply {
                                hint = IPAddress.substringBeforeLast(":")
                            }
                            binding.txtPort.apply {
                                hint = IPAddress.substringAfterLast(":")
                            }
                        }
                        ElijahTVActivity.DialogTYPE.UDPConfig ->{
                            Log.e("IP",UDPAddress)
                            val customizeDialog = MaterialDialog(this@ElijahTVActivity)
                            val binding = DialogIpBinding.inflate(layoutInflater)
                            customizeDialog.title(text = "塔灯配置")
                                .customView(view = binding.root)
                                .positiveButton {
                                    UDPAddress="${
                                        if("${binding.txtIp.text}"=="") UDPAddress.substringBeforeLast(":")
                                        else "${binding.txtIp.text}"
                                    }:${
                                        if("${binding.txtPort.text}"=="") UDPAddress.substringAfterLast(":")
                                        else "${binding.txtPort.text}"
                                    }"

                                    MainScope().launch {
//                                                dataStore.setValue(IP_ADDRESS,IPAddress)
                                        dataStore.edit { it[ElijahTVActivity.UDP_ADDRESS] = UDPAddress }
                                    }

                                    initData()
                                }.show()
                            // 获取EditView中的输入内容

                            binding.txtIp.apply {
                                hint = UDPAddress.substringBeforeLast(":")
                            }
                            binding.txtPort.apply {
                                hint = UDPAddress.substringAfterLast(":")
                            }
                        }
                    }*/
//                    onSubmit(types[index])
                    types[index].show(context)
                    dialog.dismiss()
                }
            }
    }
}