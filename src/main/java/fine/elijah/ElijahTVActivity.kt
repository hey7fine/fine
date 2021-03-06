package fine.elijah

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
//import androidx.datastore.dataStore
//import androidx.datastore.preferences.*
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.list.listItems
import com.afollestad.materialdialogs.list.listItemsMultiChoice
import fine.databinding.DialogCastBinding
import fine.databinding.DialogIpBinding
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import fine.model.TVBoard
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.isActive

abstract class ElijahTVActivity : AppCompatActivity() {
    var currentLine=""
    var chosenLines= mutableListOf<String>()
    var currentCast=0
    var errorT=0
    var serverT=0L
    var dialogs= listOf<DialogTYPE>()
    var boards= listOf<TVBoard>()
    var lines = mapOf<String,String>()
    var IPAddress="192.168.1.1:80"
    var UDPAddress="192.168.1.1:80"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //fullscreen
        val localLayoutParams = window.attributes
        localLayoutParams.flags = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS or
                localLayoutParams.flags or WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON

        //hdpi
//        val dm = resources.displayMetrics
//        if (dm.densityDpi != DisplayMetrics.DENSITY_HIGH) {
//            val config = resources.configuration
//            dm.densityDpi = DisplayMetrics.DENSITY_HIGH
//            config.densityDpi = DisplayMetrics.DENSITY_HIGH
//            resources.updateConfiguration(config, dm)
//        }

        //sharedPreferences
//        sharedPreferences = getSharedPreferences("app_preference", Context.MODE_PRIVATE)
//        dataStore = createDataStore(name = "data-store-prefs",migrations = listOf(SharedPreferencesMigration(this,"app_preference")))
//        timeLooperHandler = Handler()
//        IPAddress = sharedPreferences.getString(IP_ADDRESS,"192.168.1.1:80")?:"192.168.1.1:80"

        initView()
        //Main
        MainScope().launch {
            var sec=0
            initData()

            while (isActive){
                if (sec%5==0) refresh()
                doLoop(sec)
                sec++
                delay(1000)
            }
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        val window = window
        val localLayoutParams = getWindow().attributes
        localLayoutParams.flags = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS or
                localLayoutParams.flags
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LOW_PROFILE
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
            if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_ENTER) {
            if (dialogs.size>0) showDialog(dialogs)
        }
        return super.onKeyDown(keyCode, event)
    }

    abstract fun initView()

    abstract fun initData()

    open fun doLoop(sec: Int){}

    abstract fun refresh()

    fun showDialog(types:List<DialogTYPE>){
        val dialog = MaterialDialog(this)
        val titles = List(types.size){types[it].j}
        dialog.title(text = "??????")
                .show {
                    listItems(items = titles){ dialog, index, text ->
                        when(types[index]){
                            DialogTYPE.SingleLineChoose->{
                                val dialog = MaterialDialog(this@ElijahTVActivity)
                                dialog.title(text = "????????????")
                                        .show {
                                            listItems(items = List(lines.size){lines.values.toList()[it]}){ dialog, index, text ->
                                                MainScope().launch {
                                                    currentLine =lines.keys.toList()[index]
//                                                    dataStore.setValue(CURRENT_LINE,currentLine)
                                                    dataStore.edit { it[CURRENT_LINE] = currentLine }
                                                }
                                            }
                                        }
                            }
                            DialogTYPE.MultiLineChoose ->{
                                if (lines.size>0){
                                    val dialog = MaterialDialog(this@ElijahTVActivity)
                                    dialog.title(text = "????????????")
                                            .show {
                                                MainScope().launch {
                                                    // ????????????????????????????????????false??????????????????
                                                    val linenames = ArrayList<String>()
                                                    lines.forEach {
                                                        linenames.add(it.key)
                                                    }
                                                    var valueset = mutableSetOf<String>()
//                                                valueset = sharedPreferences.getStringSet("chosen_lines",mutableSetOf<String>())?:mutableSetOf()
//                                                    dataStore.getValueFlow(CHOSEN_LINES, mutableSetOf()).collect {
//
//                                                    }

                                                    dataStore.data.map { it[CHOSEN_LINES] }
                                                        .collect {
                                                            if (it != null) {
                                                                valueset = it.toMutableSet()
                                                            }
                                                            val initial = Array(valueset.size){valueset.toTypedArray()[it].toInt()}.toIntArray()

                                                            listItemsMultiChoice(items = linenames,initialSelection = initial) {dialog, indices, items ->
                                                                MainScope().launch {
                                                                    Log.e("TAG", "showDialog: $indices \n $items")
                                                                    valueset.clear()
                                                                    for (i in indices){
                                                                        valueset.add("$i")
                                                                    }
//                                                                    dataStore.setValue(preferencesSetKey<String>("chosen_lines"),valueset)
                                                                    dataStore.edit { it[CHOSEN_LINES] = valueset }
                                                                    chosenLines.clear()
                                                                    valueset.forEach {
                                                                        try {
                                                                            chosenLines.add(lines.keys.toList()[it.toInt()])
                                                                        }catch (e:Exception){0}
                                                                    }
                                                                    refresh()
                                                                }
                                                            }
                                                            positiveButton {}
                                                        }
                                                }
                                            }
                                }
                            }
                            DialogTYPE.BoardSwitch ->{
                                if (boards.size>0){
                                    val dialogBS = MaterialDialog(this@ElijahTVActivity)
                                    val titles = List(boards.size){boards[it].title}
                                    dialogBS.title(text = "????????????")
                                            .show {
                                                listItems(items = titles){ dialog, index, text ->
                                                    MainScope().launch {
//                                                        dataStore.setValue(CURRENT_BOARD,index)
                                                        dataStore.edit { it[CURRENT_BOARD] = index }

//                                                        startActivity(boards[index].intent)
//                                                        finish()
                                                    }
                                                }
                                            }
                                }
                            }
                            DialogTYPE.CastSetting ->{
                                val customizeDialog = MaterialDialog(this@ElijahTVActivity)
                                val binding = DialogCastBinding.inflate(layoutInflater)
                                customizeDialog.title(text = "???????????? (???)")
                                        .customView(view = binding.root)
                                        .positiveButton {
                                            MainScope().launch {
                                                try {
                                                    currentCast = binding.editText.text.toString().toInt()
//                                                    dataStore.setValue(CURRENT_CAST,currentCast)
                                                    dataStore.edit { it[CURRENT_CAST] = currentCast }
                                                }catch (e:Exception){

                                                }
                                            }
                                        }.show()
                                // ??????EditView??????????????????
                                binding.editText.apply {
                                    MainScope().launch {
//                                        dataStore.getValueFlow(CURRENT_CAST,10).collect {
//                                            hint = "$it"
//                                        }
                                        dataStore.data.map { it[CURRENT_CAST] }
                                            .collect{
                                                hint="$it"
                                            }
                                    }
                                }
                            }
                            DialogTYPE.IPConfig ->{
                                Log.e("IP",IPAddress)
                                val customizeDialog = MaterialDialog(this@ElijahTVActivity)
                                val binding = DialogIpBinding.inflate(layoutInflater)
                                customizeDialog.title(text = "????????????")
                                        .customView(view = binding.root)
                                        .positiveButton {
                                            IPAddress="${
                                                if("${binding.txtIp.text}"=="") IPAddress.substringBeforeLast(":")
                                                else "${binding.txtIp.text}"
                                            }:${
                                                if("${binding.txtPort.text}"=="") IPAddress.substringAfterLast(":")
                                                else "${binding.txtPort.text}"
                                            }"

                                            MainScope().launch {
//                                                dataStore.setValue(IP_ADDRESS,IPAddress)
                                                dataStore.edit { it[IP_ADDRESS] = IPAddress }
                                            }

                                            initData()
                                        }.show()
                                // ??????EditView??????????????????

                                binding.txtIp.apply {
                                    hint = IPAddress.substringBeforeLast(":")
                                }
                                binding.txtPort.apply {
                                    hint = IPAddress.substringAfterLast(":")
                                }
                            }
                            DialogTYPE.UDPConfig ->{
                                Log.e("IP",UDPAddress)
                                val customizeDialog = MaterialDialog(this@ElijahTVActivity)
                                val binding = DialogIpBinding.inflate(layoutInflater)
                                customizeDialog.title(text = "????????????")
                                        .customView(view = binding.root)
                                        .positiveButton {
                                            UDPAddress="${
                                                if("${binding.txtIp.text}"=="") UDPAddress.substringBeforeLast(":")
                                                else "${binding.txtIp.text}"
                                            }:${
                                                if("${binding.txtPort.text}"=="") UDPAddress.substringAfterLast(":")
                                                else "${binding.txtPort.text}"
                                            }"

                                            MainScope().launch {
//                                                dataStore.setValue(IP_ADDRESS,IPAddress)
                                                dataStore.edit { it[UDP_ADDRESS] = UDPAddress }
                                            }

                                            initData()
                                        }.show()
                                // ??????EditView??????????????????

                                binding.txtIp.apply {
                                    hint = UDPAddress.substringBeforeLast(":")
                                }
                                binding.txtPort.apply {
                                    hint = UDPAddress.substringAfterLast(":")
                                }
                            }
                        }
                    }
                }
    }

    fun <T> getPreference(key:Preferences.Key<T>):Flow<T?>{
        return dataStore.data.map { it[key] }
    }

    override fun onBackPressed() {
        finish()
    }

    companion object{
        val Context.dataStore by preferencesDataStore("data-store-prefs")
        val CURRENT_LINE= stringPreferencesKey("current_line")
        val CHOSEN_LINES = stringSetPreferencesKey("chosen_lines")
        val CURRENT_BOARD= intPreferencesKey("current_board")
        val CURRENT_CAST= intPreferencesKey("current_cast")
        val IP_ADDRESS=stringPreferencesKey("ip_address")
        val UDP_ADDRESS=stringPreferencesKey("udp_address")
    }

    enum class DialogTYPE(val i:Int,val j:String){
        BoardSwitch(0,"????????????"),
        SingleLineChoose(1,"????????????"),
        MultiLineChoose(2,"????????????"),
        CastSetting(3,"????????????"),
        IPConfig(4,"????????????"),
        UDPConfig(5,"????????????")
    }
}