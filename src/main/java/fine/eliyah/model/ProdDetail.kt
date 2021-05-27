package fine.eliyah.model

import fine.eliyah.CommonUtils

data class ProdDetail(
    val id: Int = 0,
    val orderDid: String = "",
    val orderNo: String = "",
    var badCode: String = "",
    var badName: String = "",
    var badContent: String = "",
    var badNum: Int = 0,
    val color: String ="",
    var reportNum: Int = 0,
    val siteId: String = "",
    val siteName: String = "",
    val updateTime: String = "",
    val workerNo: String = "",
    val workerName: String = ""
){
    val formattedColor get() = CommonUtils.getColor(color)
    enum class Type{ GOOD,BAD }
}