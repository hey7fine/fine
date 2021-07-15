package fine.elijah.model

data class LoginResponse(
    val errValue: String,
    val mesTTableButtonArgs: Any,
    val personBaseInfo: PersonBaseInfo,
    val result: Boolean,
    val roleButtons: Any,
    val roleMenus: Any,
    val ynFirst: Boolean,
    val ynInTime: Boolean
)