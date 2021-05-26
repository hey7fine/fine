package fine.eliyah.model

import fine.eliyah.CommonUtils

data class AlarmArg(
    val alarmCode: String,
    val alarmName: String,
    val color: String
){
    val formattedColor get() = CommonUtils.getColor(color)
}