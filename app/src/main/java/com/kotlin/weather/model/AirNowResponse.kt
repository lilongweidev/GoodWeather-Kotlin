/**
 * 当天空气质量数据实体
 *
 * @author llw
 */
data class AirNowResponse(
    val code: String,
    val updateTime: String,
    val fxLink: String,
    val now: NowBean,
    val station: List<StationBean>
)

data class NowBean(
    val aqi: String,
    val category: String,
    val primary: String,
    val pm10: String,
    val pm2p5: String,
    val no2: String,
    val so2: String,
    val co: String,
    val o3: String
)


data class StationBean(
    val pubTime: String,
    val o3: String,
    val co: String,
    val so2: String,
    val no2: String,
    val pm2p5: String,
    val pm10: String,
    val primary: String,
    val category: String,
    val level: String,
    val aqi: String,
    val id: String,
    val name: String
)
