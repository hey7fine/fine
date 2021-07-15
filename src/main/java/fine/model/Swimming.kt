package fine.model

import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs

class Swimming(
    val id: Int,
    val name: String,
    var interval: Long = 1000,
    var isSwimming: Boolean = false,
    var t: Long = 0,
    var isCount:Boolean = false,
    val stopTask:()->Unit = {},
    val task: suspend (t: Long) -> Unit
){
    fun start(time: Long = 0) {
        if (abs(time - t) > 1000) t = time
        if (isSwimming) return
        if (!isCount || (isCount && t>0)){
            isSwimming = true
            MainScope().launch {
                while (isSwimming) {
                    task(t)
                    delay(interval)
                    t += interval
                }
            }
        }
    }

    fun stop() {
        isSwimming = false
        stopTask()
    }
}

