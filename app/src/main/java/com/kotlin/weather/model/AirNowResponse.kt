/**
 * 当天空气质量数据实体
 *
 * @author llw
 */
data class AirNowResponse(
    val code: String,//状态码 200
    val updateTime: String,//当前API更新时间
    val fxLink: String,
    val now: NowBean
)

data class NowBean(
    val pubTime: String,//实时空气质量数据发布时间
    val aqi: String,//实时空气质量指数
    val level: String,//实时空气质量指数等级
    val category: String,//实时空气质量指数级别
    val primary: String,//实时空气质量的主要污染物，空气质量为优时，返回值为NA
    val pm10: String,//实时 pm10
    val pm2p5: String,//实时 pm2.5
    val no2: String,//实时 二氧化氮
    val so2: String,//实时 二氧化硫
    val co: String,//实时 一氧化碳
    val o3: String//实时 臭氧
)

