package fine.elijah.model

import fine.elijah.CommonUtils
import java.util.concurrent.TimeUnit

data class AlarmInfo(
    val alarmCode: String,
    val alarmContent: String,
    val alarmName: String,
    val color: String,
    val id: Int,
    val signTime: String,
    val signWorkerName: String,
    val signWorkerNo: String,
    val siteId: String,
    val siteName: String,
    val startTime: String,
    val startWorkerName: String,
    val startWorkerNo: String,
    val lastTime:Long
){
    val formattedColor get() = CommonUtils.getColor(color)
    val duration get() = CommonUtils.getKeepTime(lastTime,TimeUnit.SECONDS)
}