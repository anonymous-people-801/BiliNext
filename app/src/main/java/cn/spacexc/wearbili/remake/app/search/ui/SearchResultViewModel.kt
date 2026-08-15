package cn.spacexc.wearbili.remake.app.search.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import cn.spacexc.bilibilisdk.sdk.stream.info.StreamInfo
import cn.spacexc.wearbili.remake.app.live.domain.remote.LiveRoomItem
import cn.spacexc.wearbili.remake.app.search.domain.paging.SearchObject
import cn.spacexc.wearbili.remake.app.search.domain.paging.SearchPagingSource
import cn.spacexc.wearbili.remake.common.networking.KtorNetworkUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LiveSearchState(
    val results: List<LiveRoomItem> = emptyList(),
    val isLoading: Boolean = false,
    val failed: Boolean = false
)

/**
 * Created by XC-Qan on 2023/5/3.
 * I'm very cute so please be nice to my code!
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 */

@HiltViewModel
class SearchResultViewModel @Inject constructor(
    private val networkUtils: KtorNetworkUtils
) : ViewModel() {
    private var flow: Flow<PagingData<SearchObject>>? = null
    private var currentKeyword: String? = null

    fun getSearchResultFlow(keyword: String): Flow<PagingData<SearchObject>> {
        if (flow == null || currentKeyword != keyword) {
            currentKeyword = keyword
            flow = Pager(config = PagingConfig(pageSize = 1)) {
                SearchPagingSource(
                    networkUtils = networkUtils,
                    keyword = keyword
                )
            }.flow.cachedIn(viewModelScope)
        }
        return flow!!
    }

    var liveSearchState by mutableStateOf(LiveSearchState())
        private set

    fun searchLiveRooms(keyword: String) {
        if (keyword.isBlank()) return
        viewModelScope.launch {
            liveSearchState = LiveSearchState(isLoading = true)
            val response = StreamInfo.searchLiveRooms(keyword)
            liveSearchState = if (response.code != 0) {
                LiveSearchState(failed = true)
            } else {
                LiveSearchState(
                    results = response.data?.data?.result?.map {
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
}
