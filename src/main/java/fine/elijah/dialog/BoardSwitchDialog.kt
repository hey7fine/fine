package fine.elijah.dialog

import android.content.Context
import android.view.LayoutInflater
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.list.listItems
import fine.R
import fine.databinding.ViewAuthorityBinding
import fine.model.TVBoard

class BoardSwitchDialog constructor(
    private val context:Context,
    val data : List<TVBoard>,
    val onSubmit : (Int,TVBoard)->Unit
){
    private val dialog = MaterialDialog(context)
    private var titles :List<String> = List(data.size) { data[it].title }

    fun show() {
        dialog.title(R.string.app_board_switch)
            .listItems(items = titles){_,index,_->
                onSubmit(index,data[index])
            }
            .show()
    }
}