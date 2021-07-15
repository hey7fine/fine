package fine.elijah

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import es.dmoral.toasty.Toasty
import fine.model.FineToast
import fine.model.Swimming
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

abstract class ElijahTVFragment(
    private val dateFormat: SimpleDateFormat = SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss", Locale.CHINA)
) : Fragment(){
    abstract val bindingView:View
    private var toast: Toast? =null
    private val pool = listOf(
        Swimming(0,"MAIN",interval = 5000){
            refresh()
        },
        //isCount = true -> [must] startT > 0
        Swimming(1,"TIME",isCount = true,stopTask = {
            showServerTime("")
        }){
            showServerTime(dateFormat.format(it))
        },
        Swimming(2,"CAST",interval = 6000){
            //don't swim when start
            if (it>0) doCast()
        },
        Swimming(3,"TOAST",interval = 10000){
            toast?.show()
        }
    )
    val timeSwimming = pool[1]
    val castSwimming = pool[2]
    val bakeSwimming = pool[3]

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (isAdded){
            initView()
            MainScope().launch {
                try{
                    initData()
                }catch (e:Exception){
                    bakeToast(e.message,type = FineToast.Type.ERROR)
                }
            }
        }
        return bindingView
    }

    abstract fun initView()
    abstract suspend fun initData()
    abstract suspend fun refresh()
    open fun showServerTime(time: String){ }
    open fun doCast(){ }

    override fun onResume() {
        super.onResume()
        if (isAdded) pool.forEach { it.start() }
    }

    override fun onPause() {
        super.onPause()
        if (isAdded) pool.forEach { it.stop() }
    }

    fun bakeToast(message:String?, duration:Int = Toasty.LENGTH_SHORT, type: FineToast.Type = FineToast.Type.NORMAL){
        if (isAdded) context?.apply {
            toast?.cancel()
            toast = null
            toast= FineToast(this,"$message",duration,type).bake()
        }
    }

}