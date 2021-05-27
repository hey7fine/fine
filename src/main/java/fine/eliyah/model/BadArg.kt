package fine.eliyah.model

import fine.eliyah.CommonUtils

data class BadArg(
    val badCode: String,
    val badContents: List<BadContent>,
    val badName: String,
    val color: String
){
    val formattedColor get() = CommonUtils.getColor(color)
}