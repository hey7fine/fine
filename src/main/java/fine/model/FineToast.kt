package fine.model

import android.content.Context
import android.widget.Toast
import es.dmoral.toasty.Toasty

class FineToast(
    val context: Context,
    private val message: String,
    private val duration: Int = Toasty.LENGTH_SHORT,
    private val type:Type = Type.NORMAL
){
    fun bake() = type.bake(context, message, duration)

    enum class Type {
        NORMAL{
            override fun bake(context: Context, message: String, duration: Int): Toast = Toasty.normal(context, message, duration)
        },
        WARNING{
            override fun bake(context: Context, message: String, duration: Int): Toast = Toasty.warning(context, message, duration)
        },
        INFO{
            override fun bake(context: Context, message: String, duration: Int): Toast = Toasty.info(context, message, duration)
        },
        SUCCESS{
            override fun bake(context: Context, message: String, duration: Int): Toast = Toasty.success(context, message, duration)
        },
        ERROR{
            override fun bake(context: Context, message: String, duration: Int): Toast = Toasty.error(context, message, duration)
        };

        abstract fun bake(context: Context,message: String,duration:Int) : Toast
    }
}
