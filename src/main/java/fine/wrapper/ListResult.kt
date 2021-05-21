package fine.wrapper

data class ListResult<T> (
    val result:Boolean,
    val error:String,
    val values: List<T>,
    val time:Long
){
        val isResult get() = result
}