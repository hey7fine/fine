package fine.elijah.model

data class AlarmInfoInsert(
    val alarmCode: String,
    var alarmContent: String,
    val siteId: String,
    val startWorkerNo: String
)