package fine.eliyah

import android.graphics.Color
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.abs

object CommonUtils {

    const val EMPTY = ""

    fun getTime(time: Long) = getTime("yyyy年MM月dd日 HH:mm:ss",time)

    fun getTime(patten:String,time: Long) = getTime(patten, Locale.CHINA ,time)

    fun getTime(patten:String,locale: Locale,time: Long) = SimpleDateFormat(patten,locale).format(Date(time))

    fun getKeepTime(time: Long) = getKeepTime(time,TimeUnit.SECONDS)

    fun getKeepTime(time: Long,unit:TimeUnit) = String.format(
        "%02d:%02d:%02d",
        abs(unit.toSeconds(time) / 3600),
        abs(unit.toSeconds(time) % 3600 / 60),
        abs(unit.toSeconds(time) % 60))

    fun getColor(color: String) = try {
            Color.parseColor(color)
        } catch (e: Exception) {
            Color.RED
        }
}