package fine.wrapper

fun interface IResult<T> {
    suspend fun run():Result<T>
}