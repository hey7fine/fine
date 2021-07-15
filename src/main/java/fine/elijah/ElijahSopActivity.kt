package fine.elijah

import android.Manifest
import android.content.Context
import android.os.Bundle
import android.os.Environment
import android.util.DisplayMetrics
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.haoge.easyandroid.easy.EasyPermissions
import fine.clients.FineFTP
import fine.model.FineFile
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.File

abstract class ElijahSopActivity : AppCompatActivity() {
    abstract val ftp: FineFTP
    var serverT=0L
    lateinit var localpath: File
    private lateinit var remotepath: File

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //fullscreen
        val localLayoutParams = window.attributes
        localLayoutParams.flags = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS or
                localLayoutParams.flags or WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON

        //hdpi
        val dm = resources.displayMetrics
        if (dm.densityDpi != DisplayMetrics.DENSITY_HIGH) {
            val config = resources.configuration
            dm.densityDpi = DisplayMetrics.DENSITY_HIGH
            config.densityDpi = DisplayMetrics.DENSITY_HIGH
            resources.updateConfiguration(config, dm)
        }

        initView()

        EasyPermissions.create(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ).callback { grant ->
            if (grant) {
                localpath = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)!!
                remotepath = File("/files")
                if (!localpath.exists()) localpath.mkdir()
                //Main
                MainScope().launch {
                    var sec=0
                    initData()

                    while (isActive){
                        try {
                            if (sec%5==0) refresh()
                            doLoop(sec)
                            sec++
                            delay(1000)
                        }catch (e:Exception){
                            if (sec%60==0)
                                Toast.makeText(applicationContext, "${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }.request(this)
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

    abstract fun initView()

    abstract fun initData()

    open fun doLoop(sec: Int){}

    abstract fun refresh()

    fun download(list: List<FineFile>,localNames:List<String>,onDownloadSuccess:(List<File>)->Unit){
        val dList = mutableListOf<FineFile>()
        val aNames = List(list.size){list[it].dealFileName}
        aNames.forEach {
            if (!localNames.contains(it))
                dList.add(list[aNames.indexOf(it)])
        }
        if(dList.isNotEmpty()){
            var toast = Toast(applicationContext)
            MainScope().launch(Dispatchers.IO) {
                try {
                    if (list.isNotEmpty())
                        ftp.apply {
                            openConnect()
                            downloadFile("/files", dList, { position: Int, _: String, downProcess: Long->
                                runOnUiThread {
                                    toast.cancel()
                                    toast = Toast.makeText(applicationContext, "${position+1}/${list.size} ${list[position].fileName} ${downProcess.toInt()}%", Toast.LENGTH_SHORT)
                                    toast.show()
                                }
                            },{ files->
                                onDownloadSuccess(List(files.size){ files[it].getFile() })
                            },{
                                runOnUiThread {
                                    toast.cancel()
                                    toast = Toast.makeText(applicationContext, it, Toast.LENGTH_SHORT)
                                    toast.show()
                                }
                            })
                        }
                }catch (e:Exception){
                    runOnUiThread {
                        toast.cancel()
                        toast = Toast.makeText(applicationContext, "${e.message}", Toast.LENGTH_SHORT)
                        toast.show()
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
    }
}