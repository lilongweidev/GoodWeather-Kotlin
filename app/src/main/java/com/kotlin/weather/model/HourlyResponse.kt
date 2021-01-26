/**
 * 逐小时天气数据实体
 *
 * @author llw
 */
data class HourlyResponse(
    val code: String,//状态码 200
    val updateTime: String,//当前API更新时间
    val fxLink: String,
    val hourly: List<HourlyBean>
)

data class HourlyBean(
    val fxTime: String,//逐小时预报时间
    val temp: String,//逐小时预报温度
    val icon: String,//逐小时预报天气状况图标代码
    val cloud: String,//逐小时预报云量，百分比数值
    val pressure: String,//逐小时预报大气压强，默认单位：百帕
    val precip: String,//逐小时预报降水量，默认单位：毫米
    val pop: String,//逐小时预报降水概率，百分比数值，可能为空
    val humidity: String,//逐小时预报相对湿度，百分比数值
    val windSpeed: String,//逐小时预报风速，公里/小时
    val windScale: String,//逐小时预报风力等级
    val windDir: String,//逐小时预报风向
    val wind360: String,//逐小时预报风向360角度
    val text: String,//逐小时预报天气状况文字描述
    val dew: String//逐小时预报露点温度
)
