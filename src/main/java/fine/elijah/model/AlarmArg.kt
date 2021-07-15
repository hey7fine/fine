package fine.elijah.model

import fine.elijah.CommonUtils

data class AlarmArg(
    val alarmCode: String,
    val alarmName: String,
    val color: String
){
    val formattedColor get() = CommonUtils.getColor(color)
}