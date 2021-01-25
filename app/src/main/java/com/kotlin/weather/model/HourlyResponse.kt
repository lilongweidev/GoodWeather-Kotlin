/**
 * 逐小时天气数据实体
 *
 * @author llw
 */
data class HourlyResponse(
    val code: String,
    val hourly: List<HourlyBean>,
    val fxLink: String,
    val updateTime: String
)

data class HourlyBean(
    val fxTime: String,
    val temp: String,
    val icon: String,
    val cloud: String,
    val pressure: String,
    val precip: String,
    val pop: String,
    val humidity: String,
    val windSpeed: String,
    val windScale: String,
    val windDir: String,
    val wind360: String,
    val text: String,
    val dew: String
)
