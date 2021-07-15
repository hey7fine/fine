package fine.elijah.dialog

import android.content.Context
import android.view.LayoutInflater
import android.widget.Toast
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import fine.databinding.ViewMaterialAlarmCreateBinding
import fine.elijah.model.MaterialAlarmInfo
import fine.elijah.model.MaterialArgs

class MaterialAlarmInsertDialog constructor(
    private val context : Context,data : List<MaterialArgs>,
    private var materialInfo: MaterialAlarmInfo,
    onSubmit : (MaterialAlarmInfo)->Unit
) {
    private val binding = ViewMaterialAlarmCreateBinding.inflate(LayoutInflater.from(context))
    private val dialog = MaterialDialog(context)

    fun show() {
        dialog.title(fine.R.string.app_material_insert)
            .customView(view = binding.root)
            .show()
    }

    init {
        binding.apply {
            txtMaterial.setOnClickListener {
                MaterialArgsDialog(context,onRefresh = {
                    upload(data)
                }){
                    materialInfo.materialCode = it.materialCode
                    txtMaterial.text = it.materialName
                }.show()
            }

            btnSubmit.setOnClickListener {
                materialInfo.alarmNum = try {
                    "${txtNum.text}".toFloat()
                }catch (e:Exception){
                    0f
                }
                if (materialInfo.materialCode != "" && materialInfo.alarmNum !=0f){
                    onSubmit(materialInfo)
                    dialog.dismiss()
                }else
                    Toast.makeText(context, "完善物料报警信息", Toast.LENGTH_SHORT).show()
            }
            btnCancel.setOnClickListener {
                dialog.dismiss()
            }
        }
    }
}