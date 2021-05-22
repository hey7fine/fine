package fine.eliyah.model

data class SiteInfo(
    val lineId: String,
    val lineName: String,
    val loopTime: Int,
    val notify: String,
    val orderDid: String,
    val processCode: String,
    val siteId: String,
    val siteName: String,
    val workShopId: String,
    val workShopName: String,
    val ynAddProNum: Boolean
)