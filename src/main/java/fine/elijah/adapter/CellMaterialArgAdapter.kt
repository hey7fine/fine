package fine.elijah.adapter

import android.view.View
import fine.BaseAdapter
import fine.R
import fine.databinding.ItemMaterialArgBinding
import fine.elijah.model.MaterialArgs

class CellMaterialArgAdapter constructor(
    private val onClick:(item: MaterialArgs)->Unit = {}
): BaseAdapter<MaterialArgs>() {
    override val layoutRes: Int
        get() = R.layout.item_material_arg

    override fun fill(view: View, position:Int, it: MaterialArgs) {
        super.fill(view,position, it)
        val binding = ItemMaterialArgBinding.bind(view)
        binding.apply {
            code.text = it.materialCode
            name.text = it.materialCode
            model.text = it.materialModel
            spec.text = it.brand
            cell.setOnClickListener {_->
                onClick(it)
            }
        }
    }
}