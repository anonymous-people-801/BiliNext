package cn.spacexc.wearbili.remake.app.live.domain.remote

/**
 * B站直播推荐列表接口
 * https://api.live.bilibili.com/room/v1/room/get_user_recommend
 * 响应: { code: 0, data: [ LiveRoomItem, ... ] }
 */
data class LiveRoomRecommendResponse(
    val code: Int = 0,
    val message: String = "",
    val data: List<LiveRoomItem> = emptyList()
)

data class LiveRoomItem(
    val roomid: Long = 0,
    val uid: Long = 0,
    val title: String = "",
    val uname: String = "",
    val online: Long = 0,
    val user_cover: String = "",
    val system_cover: String = "",
    val cover: String = "",
    val keyframe: String = "",
    val face: String = "",
    val link: String = ""
) {
    val coverUrl: String
        get() {
            val raw = user_cover.ifEmpty { cover }.ifEmpty { keyframe }.ifEmpty { system_cover }.ifEmpty { face }
            return if (raw.startsWith("//")) "https:$raw" else raw
        }
}
