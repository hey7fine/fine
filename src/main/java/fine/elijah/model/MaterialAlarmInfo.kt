package fine.elijah.model

data class MaterialAlarmInfo(
    val alarmContent: String = "",
    var alarmNum: Float = 0f,
    var comment: String = "",
    val id: Int = 0,
    val materialAlarmCode: String ="",
    var materialCode: String="",
    val orderDid: String="",
    val signInTime: String="",
    val signWorkerNo: String="",
    val siteId: String="",
    val startTime: String="",
    val startWorkerNo: String=""
){
    val duration get() = ""
}