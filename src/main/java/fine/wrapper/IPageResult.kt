package fine.wrapper

fun interface IPageResult<T> {
    suspend fun run():PageResult<T>
}