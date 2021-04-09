package fine.wrapper

data class ListResult<T> (
    val error:String,
    val values: List<T>,
    val time:Long
){
        val isResult get() = values.isNotEmpty() && error==""
}