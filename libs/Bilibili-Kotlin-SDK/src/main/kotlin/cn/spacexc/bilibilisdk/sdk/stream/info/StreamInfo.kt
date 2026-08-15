package cn.spacexc.bilibilisdk.sdk.stream.info

import cn.spacexc.bilibilisdk.network.KtorNetworkUtils
import cn.spacexc.bilibilisdk.network.NetworkResponse
import cn.spacexc.bilibilisdk.sdk.stream.info.remote.search.LiveRoomSearchResponse
import cn.spacexc.bilibilisdk.sdk.stream.info.remote.url.LiveStreamUrl
import java.net.URLEncoder

/**
 * Created by XC-Qan on 2023/10/12.
 * I'm very cute so please be nice to my code!
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 */

object StreamInfo {
    suspend fun getLiveStreamPlayUrlFromRoomId(roomId: Long): NetworkResponse<LiveStreamUrl> {
        return KtorNetworkUtils.get("https://api.live.bilibili.com/room/v1/Room/playUrl?cid=$roomId")
    }

    /**
     * 搜索直播间 (需要 wbi 签名, 启动时 Splash 已获取签名 key)
     */
    suspend fun searchLiveRooms(
        keyword: String,
        page: Int = 1
    ): NetworkResponse<LiveRoomSearchResponse> {
        return KtorNetworkUtils.getWithWebiSignature(
            host = "https://api.bilibili.com/x/web-interface/wbi/search/type",
            origParams = "search_type=live_room&keyword=${URLEncoder.encode(keyword, "UTF-8")}&page=$page&page_size=20"
        )
    }
}