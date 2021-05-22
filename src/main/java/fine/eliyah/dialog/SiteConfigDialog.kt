package fine.eliyah.dialog

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import fine.R
import fine.databinding.ViewSiteConfigBinding
import fine.eliyah.model.LineInfo

class SiteConfigDialog constructor(
    private val context : Context,
    onRefresh : ()->Unit,
    onSubmit : (Int,Int)->Unit
    ) {
    private val binding = ViewSiteConfigBinding.inflate(LayoutInflater.from(context))
    private val dialog = MaterialDialog(context)
    private var data : List<LineInfo> = ArrayList<LineInfo>()
    private var lines :List<String> = ArrayList()
    private var sites : List<String> = ArrayList()
    var adapterLines = ArrayAdapter(context,R.layout.item_spinner,lines)
    var adapterSites = ArrayAdapter(context,R.layout.item_spinner,sites)

    fun upload(lines :List<LineInfo>) = this.also {
        this.data = lines
        adapterLines = ArrayAdapter(context,R.layout.item_spinner, List(lines.size){lines[it].lineName})
        binding.spnLines.adapter = adapterLines
    }

    fun uploadLines(lines : List<String>) = this.also {
        this.lines = lines
        adapterLines = ArrayAdapter(context,R.layout.item_spinner,lines)
        binding.spnLines.adapter = adapterLines
    }

    fun uploadSites(sites : List<String>) = this.also {
        this.sites = sites
        adapterSites = ArrayAdapter(context,R.layout.item_spinner,sites)
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
                onRefresh()
            }
            spnLines.onItemSelectedListener = object :AdapterView.OnItemSelectedListener{
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    selectLine(position)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }
            }
        }
    }

    fun selectLine(i:Int){
        this.sites = List(data[i].siteInfos.size){data[i].siteInfos[it].siteName}
        adapterSites = ArrayAdapter(context,R.layout.item_spinner,sites)
        binding.spnSites.adapter = adapterSites
    }
}