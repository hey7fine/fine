package fine.elijah

import android.content.Context
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.preferences.preferencesDataStore
import es.dmoral.toasty.Toasty
import fine.model.FineToast
import fine.model.Swimming
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

abstract class ElijahTVRootActivity(
    private val hdpiEnable:Boolean = true,
    private val isLoop :Boolean = true,
    private val dateFormat: SimpleDateFormat = SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss", Locale.CHINA)
) : AppCompatActivity() {
    private var toast:Toast? =null
    private val pool = listOf(
        Swimming(0,"MAIN",interval = 5000){
            refresh()
        },
        //isCount = true -> [must] startT > 0
        Swimming(1,"TIME",isCount = true){
            showServerTime(dateFormat.format(it))
        },
        Swimming(2,"CAST",interval = 6000){
            //don't swim when start
            if (it>0) doCast()
        },
        Swimming(3,"TOAST",interval = 30000){
            toast?.show()
        }
    )
    val mainSwimming = pool[0]
    val timeSwimming = pool[1]
    val castSwimming = pool[2]
    val bakeSwimming = pool[3]

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //fullscreen
        val localLayoutParams = window.attributes
        localLayoutParams.flags = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS or
                localLayoutParams.flags or WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON

        //hdpi
        if (hdpiEnable){
            val dm = resources.displayMetrics
            if (dm.densityDpi != DisplayMetrics.DENSITY_HIGH) {
                val config = resources.configuration
                dm.densityDpi = DisplayMetrics.DENSITY_HIGH
                config.densityDpi = DisplayMetrics.DENSITY_HIGH
                resources.updateConfiguration(config, dm)
            }
        }
        initView()
        MainScope().launch {
            try{
                initData()
            }catch (e:Exception){
                bakeToast(e.message,type = FineToast.Type.ERROR)
            }
        }
    }

    abstract fun initView()
    abstract suspend fun initData()
    open suspend fun refresh(){}
    open fun showServerTime(time: String){}
    open fun doCast(){ }
    open fun showSettingDialog(){}

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        val localLayoutParams = window.attributes
        localLayoutParams.flags = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS or
                localLayoutParams.flags
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LOW_PROFILE
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
    }

    override fun onResume() {
        super.onResume()
        pool.forEach { it.start() }
    }

    override fun onPause() {
        super.onPause()
        pool.forEach { it.stop() }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return when (keyCode) {
            KeyEvent.KEYCODE_DPAD_CENTER -> {
                showSettingDialog()
                true
            }
            KeyEvent.KEYCODE_ENTER -> {
                showSettingDialog()
                true
            }
            else -> false
        }
    }

    fun bakeToast(message:String?, duration:Int = Toasty.LENGTH_SHORT, type:FineToast.Type = FineToast.Type.NORMAL):Toast {
        toast = FineToast(applicationContext,"$message",duration,type).bake()
        return toast!!
    }

    companion object{
        val Context.dataStore by preferencesDataStore("data-store-prefs")
    }
}