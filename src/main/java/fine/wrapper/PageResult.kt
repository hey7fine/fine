package fine.wrapper

data class PageResult<T> (
        private val result:Boolean,
        val error:String,
        val values:List<T>,
        val time:Long,
        var count:Int = 1,
        var page :Int = 0
){
        val isResult get() = result

        val next get() = page*count

        private val size get() = if (values.size/count==0) 1 else values.size/count

        val pageData: List<T> get() = if (page+1>size) values.subList(page*count,values.size) else values.subList(page*count,(page+1)*count-1)

        fun cast():Int {
                page = if (page++>=size) 0 else page
                return next
        }
}