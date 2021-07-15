package fine.elijah.dialog

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import fine.R
import fine.databinding.ViewSiteConfigBinding
import fine.elijah.model.LineInfo

class SiteConfigDialog constructor(
    private val context : Context,
    onRefresh : SiteConfigDialog.()->Unit,
    onSubmit : (Int,Int)->Unit
) {
    private val binding = ViewSiteConfigBinding.inflate(LayoutInflater.from(context))
    private val dialog = MaterialDialog(context)
    private var data : List<LineInfo> = ArrayList()

    fun upload(list :List<LineInfo>) = this.also {
        data = list
        binding.spnLines.adapter = ArrayAdapter(context,R.layout.item_spinner, List(data.size){data[it].lineName})
    }

    fun show() {
        dialog.title(R.string.app_site_config)
            .customView(view = binding.root)
            .show()
    }

    init {
        binding.apply {
            btnSubmit.setOnClickListener {
                onSubmit(spnLines.selectedItemPosition,spnSites.selectedItemPosition)
                dialog.dismiss()
            }
            btnCancel.setOnClickListener {
                dialog.dismiss()
            }
            btnRefresh.setOnClickListener {
                onRefresh(this@SiteConfigDialog)
            }
            spnLines.onItemSelectedListener = object :AdapterView.OnItemSelectedListener{
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    selectLine(position)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }
            }
            spnLines.adapter = ArrayAdapter(context,R.layout.item_spinner, List(data.size){data[it].lineName})
        }
         onRefresh(this)
    }

    private fun selectLine(i:Int){
        binding.spnSites.adapter = try{
            ArrayAdapter(context,R.layout.item_spinner,List(data[i].siteInfos.size){data[i].siteInfos[it].siteName})
        }catch(e:Exception){
            ArrayAdapter(context,R.layout.item_spinner,listOf<String>())
        }
    }
}