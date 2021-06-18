package fine.wrapper

fun interface IListResult<T> {
    suspend fun run():ListResult<T>
}