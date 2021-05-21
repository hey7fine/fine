package fine.wrapper

data class Result<T> (
        val result:Boolean,
        val error:String,
        val value:T,
        val time:Long
){
        val isResult get() = result
}