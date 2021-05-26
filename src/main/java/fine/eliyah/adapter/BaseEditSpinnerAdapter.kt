package fine.eliyah.adapter

import android.widget.BaseAdapter

abstract class BaseEditSpinnerAdapter : BaseAdapter() {

    abstract val editSpinnerFilter: EditSpinnerFilter?

    abstract fun getItemString(position: Int): String?
}