package fine.eliyah.model

data class LineInfo(
    val lineId: String,
    val lineName: String,
    val workshopId: String,
    val workshopName: String,
    val siteInfos: List<SiteInfo>
)