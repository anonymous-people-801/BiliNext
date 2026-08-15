package cn.spacexc.bilibilisdk.sdk.stream.info.remote.search

/**
 * 直播搜索接口响应
 * https://api.bilibili.com/x/web-interface/wbi/search/type?search_type=live_room&keyword=xxx
 */
data class LiveRoomSearchResponse(
    val code: Int = 0,
    val message: String = "",
    val data: LiveRoomSearchData? = null
)

data class LiveRoomSearchData(
    val result: List<LiveRoomSearchItem> = emptyList(),
    val total: Int = 0,
    val page: Int = 0,
    val pageSize: Int = 0
)

data class LiveRoomSearchItem(
    val roomid: Long = 0,
    val uid: Long = 0,
    val title: String = "",
    val uname: String = "",
    val online: Long = 0,
    val live_status: Int = 0,
    val user_cover: String = "",
    val system_cover: String = "",
    val cover: String = "",
    val keyframe: String = "",
    val face: String = "",
    val area: String = "",
    val parent_area: String = ""
)
