package fine.eliyah.model

data class ProdDetail(
    val id: Int = 0,
    val orderDid: String = "",
    val orderNo: String = "",
    var badCode: String = "",
    var badName: String = "",
    var badContent: String = "",
    var badNum: Int = 0,
    var reportNum: Int = 0,
    val siteId: String = "",
    val siteName: String = "",
    val updateTime: String = "",
    val workerNo: String = "",
    val workerName: String = ""
){
    enum class Type{ GOOD,BAD }
}