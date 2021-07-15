package fine.elijah.dialog

import android.content.Context
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.listItems
import com.afollestad.materialdialogs.list.listItemsMultiChoice
import fine.R
import fine.elijah.model.LineInfo
import fine.model.TVBoard
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class TVBoardsDialog constructor(
    val context:Context,
    val data:List<TVBoard>,
    val initial:IntArray = intArrayOf(),
    val onSubmit :suspend (List<TVBoard>)->Unit
) {
    private val dialog = MaterialDialog(context)
    private var titles :List<String> = List(data.size) { data[it].title }

    fun show() {
        var indexes = intArrayOf()
        dialog.title(R.string.app_board_switch)
            .listItemsMultiChoice(items = titles,initialSelection = initial){ _, res, _ ->
                indexes = res
            }
            .positiveButton {
                MainScope().launch {
                    val result = mutableListOf<TVBoard>()
                    for (index in indexes){
                        result.add(data[index])
                    }
                    onSubmit(result)
                }
            }
            .show()
    }
}