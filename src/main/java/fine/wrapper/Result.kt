package fine.wrapper

data class Result<T> (
        val error:String,
        val value:T,
        val time:Long
){
        val isResult get() = value!=null && error==""
}