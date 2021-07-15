package fine.elijah.dialog

import android.content.Context
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.listItems
import com.afollestad.materialdialogs.list.listItemsMultiChoice
import fine.elijah.model.LineInfo
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class LineChooseDialog constructor(
    val context:Context,
    val data:List<LineInfo>,
//    onRefresh : LineChooseDialog.()->Unit,
    val onSubmit :suspend (LineInfo)->Unit
) {
    private val dialog = MaterialDialog(context)
//    private var data : List<LineInfo> = ArrayList()
    private var titles :List<String> = List(data.size) { data[it].lineName }

//    fun upload(list :List<LineInfo>) = this.also {
//        data = list
//        titles = List(data.size) { data[it].lineName }
//        show()
//    }

    fun show() {
        dialog.title(text = "产线选择")
            .listItems(items = titles){ _, index, _ ->
                MainScope().launch {
                    onSubmit(data[index])
                }
            }
            .show()
    }

//    init {
//        onRefresh(this)
//    }
}