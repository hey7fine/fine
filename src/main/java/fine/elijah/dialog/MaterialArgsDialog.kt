package fine.elijah.dialog

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import fine.R
import fine.databinding.ViewMaterialArgsBinding
import fine.elijah.adapter.CellMaterialArgAdapter
import fine.elijah.model.MaterialArgs

class MaterialArgsDialog constructor(
    context : Context,
    onRefresh : MaterialArgsDialog.()->Unit,
    onSelect : (MaterialArgs)->Unit
) {
    private val binding = ViewMaterialArgsBinding.inflate(LayoutInflater.from(context))
    private val dialog = MaterialDialog(context)
    private val adapter = CellMaterialArgAdapter{
        onSelect(it)
        dialog.dismiss()
    }
    private var data : List<MaterialArgs> = ArrayList()

    fun upload(list :List<MaterialArgs>) = this.also {
        data = list
        adapter.setData(data)
        if (data.isNotEmpty())
            binding.txtEmpty3.visibility = View.GONE
        else
            binding.recyclerView.visibility = View.GONE
    }

    fun show() {
        dialog.title(R.string.app_material_arg)
            .customView(view = binding.root,dialogWrapContent = true)
            .show()
    }

    init {
        binding.apply {
            recyclerView.adapter=adapter
        }
        onRefresh(this)
    }
}