package fine.wrapper

class PageResult<T> (
        val error:String,
        val page:Int,
        val size:Int,
        val values:List<T>,
        val time:Long
){
        val isResult get() = values.isNotEmpty() && error==""
}