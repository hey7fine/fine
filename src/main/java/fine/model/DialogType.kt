package fine.model

import android.content.Context
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.listItems

class DialogType(
    val id:Int,
    val title:String,
    val show:(Context)->Unit
){
//    SingleLineChoose(0,"产线选择",{ context->
//        MaterialDialog(context).apply {
//            title(text = "产线选择")
//            .show {
//                listItems(items = List(lines.size) { lines.values.toList()[it] }){ dialog, index, text ->
//                    currentLine =lines.keys.toList()[index]
////                                                    dataStore.setValue(CURRENT_LINE,currentLine)
//                    dataStore.edit { it[ElijahTVActivity.CURRENT_LINE] = currentLine }
//                }
//            }
//        }
//    })
}