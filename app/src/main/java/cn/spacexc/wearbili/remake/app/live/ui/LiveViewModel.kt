package cn.spacexc.wearbili.remake.app.live.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.spacexc.bilibilisdk.sdk.stream.info.StreamInfo
import cn.spacexc.wearbili.remake.app.live.domain.remote.LiveRoomItem
import cn.spacexc.wearbili.remake.app.live.domain.remote.LiveRoomRecommendResponse
import cn.spacexc.wearbili.remake.common.UIState
import cn.spacexc.wearbili.remake.common.networking.KtorNetworkUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LiveScreenState(
    val rooms: List<LiveRoomItem> = emptyList(),
    val uiState: UIState = UIState.Loading,
    val isRefreshing: Boolean = false,
    val searchKeyword: String = "",
    val searchResults: List<LiveRoomItem> = emptyList(),
    val isSearching: Boolean = false,
    val searchFailed: Boolean = false
)

@HiltViewModel
class LiveViewModel @Inject constructor(
    private val networkUtils: KtorNetworkUtils
) : ViewModel() {
    var screenState by mutableStateOf(LiveScreenState())
        private set

    init {
        getLiveRooms()
    }

    fun getLiveRooms() {
        viewModelScope.launch {
            if (screenState.rooms.isEmpty()) {
                screenState = screenState.copy(uiState = UIState.Loading)
            }
            screenState = screenState.copy(isRefreshing = true)
            val response = networkUtils.get<LiveRoomRecommendResponse>(
                "https://api.live.bilibili.com/room/v1/room/get_user_recommend"
            )
            screenState = if (response.code != 0) {
                screenState.copy(
                    uiState = UIState.Failed(response.code),
                    isRefreshing = false
                )
            } else {
                screenState.copy(
                    uiState = UIState.Success,
                    rooms = response.data?.data ?: emptyList(),
                    isRefreshing = false
                )
            }
        }
    }

    /**
     * 搜索直播间 (wbi 签名接口)
     */
    fun searchLiveRooms(keyword: String) {
        if (keyword.isBlank()) return
        viewModelScope.launch {
            screenState = screenState.copy(
                searchKeyword = keyword,
                isSearching = true,
                searchFailed = false
            )
            val response = StreamInfo.searchLiveRooms(keyword)
            screenState = if (response.code != 0) {
                screenState.copy(
                    isSearching = false,
                    searchFailed = true,
                    searchResults = emptyList()
                )
            } else {
                screenState.copy(
                    isSearching = false,
                    searchFailed = false,
                    searchResults = response.data?.data?.result?.map {
                        LiveRoomItem(
                            roomid = it.roomid,
                            uid = it.uid,
                            title = it.title
                                .replace("<em class=\"keyword\">", "")
                                .replace("</em>", ""),
                            uname = it.uname,
                            online = it.online,
                            user_cover = it.user_cover,
                            system_cover = it.system_cover,
                            cover = it.cover,
                            keyframe = it.keyframe,
                            face = it.face
                        )
                    } ?: emptyList()
                )
            }
        }
    }

    fun clearSearch() {
        screenState = screenState.copy(
            searchKeyword = "",
            searchResults = emptyList(),
            isSearching = false,
            searchFailed = false
        )
    }
}
