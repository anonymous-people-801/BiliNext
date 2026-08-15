package cn.spacexc.wearbili.remake.app.search.ui

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import cn.spacexc.wearbili.common.domain.video.toShortChinese
import cn.spacexc.wearbili.remake.app.bangumi.info.ui.BANGUMI_ID_TYPE_SSID
import cn.spacexc.wearbili.remake.app.live.ui.LiveRoomCard
import cn.spacexc.wearbili.remake.app.player.livestream.ui.LiveStreamActivity
import cn.spacexc.wearbili.remake.app.player.livestream.ui.PARAM_ROOM_ID
import cn.spacexc.wearbili.remake.app.player.livestream.ui.PARAM_ROOM_ONLINE
import cn.spacexc.wearbili.remake.app.player.livestream.ui.PARAM_ROOM_TITLE
import cn.spacexc.wearbili.remake.app.search.domain.remote.result.mediaft.SearchedMediaFt
import cn.spacexc.wearbili.remake.app.search.domain.remote.result.user.SearchedUser
import cn.spacexc.wearbili.remake.app.search.domain.remote.result.video.SearchedVideo
import cn.spacexc.wearbili.remake.app.space.ui.UserSpaceScreen
import cn.spacexc.wearbili.remake.app.video.info.ui.VIDEO_TYPE_BVID
import cn.spacexc.wearbili.remake.common.toUIState
import cn.spacexc.wearbili.remake.common.ui.BilibiliPink
import cn.spacexc.wearbili.remake.common.ui.Card
import cn.spacexc.wearbili.remake.common.ui.LargeBangumiCard
import cn.spacexc.wearbili.remake.common.ui.LargeUserCard
import cn.spacexc.wearbili.remake.common.ui.LoadingTip
import cn.spacexc.wearbili.remake.common.ui.TitleBackground
import cn.spacexc.wearbili.remake.common.ui.VideoCard
import cn.spacexc.wearbili.remake.common.ui.clickVfx
import cn.spacexc.wearbili.remake.common.ui.isRound
import cn.spacexc.wearbili.remake.common.ui.theme.wearbiliFontFamily
import cn.spacexc.wearbili.remake.common.ui.titleBackgroundHorizontalPadding
import cn.spacexc.wearbili.remake.common.ui.toLoadingState
import cn.spacexc.wearbili.remake.common.ui.toOfficialVerify

/**
 * Created by XC-Qan on 2023/5/3.
 * I'm very cute so please be nice to my code!
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 */

@kotlinx.serialization.Serializable
data class SearchResultScreen(val keyword: String)

/*@UnstableApi*/
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SearchResultScreen(
    navController: NavController,
    keyword: String,
    viewModel: SearchResultViewModel = hiltViewModel(),
    searchViewModel: SearchViewModel = hiltViewModel()
) {
    var currentKeyword by rememberSaveable {
        mutableStateOf(keyword)
    }
    var searchInputValue by rememberSaveable {
        mutableStateOf(keyword)
    }
    var searchType by rememberSaveable {
        mutableStateOf(0)  //0=视频 1=直播
    }
    val searchResult = remember(currentKeyword) {
        viewModel.getSearchResultFlow(currentKeyword)
    }.collectAsLazyPagingItems()
    val pullToRefreshState = rememberPullRefreshState(
        refreshing = searchResult.loadState.refresh is LoadState.Loading,
        onRefresh = searchResult::refresh,
        refreshThreshold = 60.dp
    )
    val liveSearchState = viewModel.liveSearchState
    val context = LocalContext.current
    fun submitSearch() {
        if (searchInputValue.isNotEmpty()) {
            searchViewModel.addSearchHistory(searchInputValue)
            currentKeyword = searchInputValue
            if (searchType == 1) {
                viewModel.searchLiveRooms(searchInputValue)
            }
        }
    }
    //切到直播选项时自动搜当前词条
    LaunchedEffect(key1 = searchType, key2 = currentKeyword, block = {
        if (searchType == 1) {
            viewModel.searchLiveRooms(currentKeyword)
        }
    })
    TitleBackground(
        navController = navController,
        title = "搜索结果",
        onBack = navController::navigateUp,
        uiState = searchResult.loadState.refresh.toUIState(),
        onRetry = searchResult::retry
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pullRefresh(state = pullToRefreshState)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    vertical = if (isRound()) 10.dp else 4.dp,
                    horizontal = titleBackgroundHorizontalPadding()
                )
            ) {
                item {
                    Card(
                        innerPaddingValues = PaddingValues(vertical = 10.dp, horizontal = 8.dp),
                        outerPaddingValues = PaddingValues(bottom = 6.dp),
                        isClickEnabled = false,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            BasicTextField(
                                value = searchInputValue,
                                onValueChange = {
                                    searchInputValue = it
                                },
                                textStyle = TextStyle(
                                    fontSize = 14.sp,
                                    fontFamily = wearbiliFontFamily,
                                    color = Color.White
                                ),
                                modifier = Modifier.weight(1f),
                                cursorBrush = SolidColor(BilibiliPink),
                                maxLines = 1,
                                keyboardActions = KeyboardActions(onSearch = {
                                    submitSearch()
                                }),
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Icon(
                                imageVector = Icons.Outlined.Search,
                                tint = Color.White,
                                contentDescription = null,
                                modifier = Modifier.clickVfx {
                                    submitSearch()
                                }
                            )
                        }
                    }
                }
                item {
                    CapsuleTypeSlider(
                        selectedIndex = searchType,
                        onSelect = { searchType = it },
                        labels = listOf("视频", "直播")
                    )
                }
                if (searchType == 0) {
                    items(searchResult.itemCount) {
                        searchResult[it]?.let { searchObject ->
                            when (searchObject.item) {
                                is SearchedVideo -> {
                                    val video = searchObject.item
                                    VideoCard(
                                        videoName = video.title.replace("<em class=\"keyword\">", "")
                                            .replace("</em>", ""),
                                        uploader = video.author,
                                        views = video.play.toShortChinese(),
                                        coverUrl = "https:" + video.pic,
                                        videoIdType = VIDEO_TYPE_BVID,
                                        videoId = video.bvid,
                                        navController = navController
                                    )
                                }

                                is SearchedUser -> {
                                    val user = searchObject.item
                                    LargeUserCard(
                                        avatar = "https:" + user.upic,
                                        username = user.uname,
                                        mid = user.mid,
                                        officialVerify = user.officialVerify.type.toOfficialVerify(),
                                        userInfo = (if (user.officialVerify.desc.isEmpty()) "" else user.officialVerify.desc + "\n") + user.usign,
                                        navController = navController
                                    )
                                }

                                is SearchedMediaFt -> {
                                    val media = searchObject.item
                                    LargeBangumiCard(
                                        title = media.title.replace("<em class=\"keyword\">", "")
                                            .replace("</em>", ""),
                                        cover = media.cover,
                                        score = media.mediaScore.score,
                                        areas = listOf(media.areas),
                                        updateInformation = media.indexShow,
                                        badge = media.badges?.map { badge -> badge.text },
                                        badgeColor = media.badges?.map { badge -> badge.bgColor },
                                        bangumiId = media.seasonid,
                                        bangumiIdType = BANGUMI_ID_TYPE_SSID,
                                        navController = navController
                                    )
                                }
                            }
                        }
                    }
                    item {
                        LoadingTip(
                            loadingState = searchResult.loadState.append.toLoadingState(),
                            onRetry = searchResult::retry
                        )
                    }
                } else {
                    when {
                        liveSearchState.isLoading -> item {
                            Text(
                                text = "直播搜索中...",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontFamily = wearbiliFontFamily,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 24.dp)
                                    .alpha(0.7f)
                            )
                        }

                        liveSearchState.failed -> item {
                            Text(
                                text = "直播搜索失败, 点击重试",
                                color = BilibiliPink,
                                fontSize = 12.sp,
                                fontFamily = wearbiliFontFamily,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 24.dp)
                                    .clickVfx {
                                        viewModel.searchLiveRooms(currentKeyword)
                                    }
                            )
                        }

                        liveSearchState.results.isEmpty() -> item {
                            Text(
                                text = "没有找到相关直播",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontFamily = wearbiliFontFamily,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 24.dp)
                                    .alpha(0.7f)
                            )
                        }

                        else -> items(liveSearchState.results, key = { it.roomid }) { room ->
                            LiveRoomCard(
                                room = room,
                                onClick = {
                                    context.startActivity(
                                        Intent(context, LiveStreamActivity::class.java)
                                            .putExtra(PARAM_ROOM_ID, room.roomid)
                                            .putExtra(PARAM_ROOM_TITLE, room.title)
                                            .putExtra(
                                                PARAM_ROOM_ONLINE,
                                                "${room.online.toShortChinese()}人在看"
                                            )
                                    )
                                },
                                onOpenUserSpace = {
                                    if (room.uid > 0) {
                                        navController.navigate(UserSpaceScreen(room.uid))
                                    }
                                }
                            )
                        }
                    }
                }
            }
            PullRefreshIndicator(
                refreshing = searchResult.loadState.refresh is LoadState.Loading,
                state = pullToRefreshState,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }
}

@Composable
private fun CapsuleTypeSlider(
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    labels: List<String>
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 6.dp)
            .clip(RoundedCornerShape(50))
            .background(Color(38, 38, 38, 130))
            .padding(3.dp)
    ) {
        labels.forEachIndexed { index, label ->
            val isSelected = selectedIndex == index
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(50))
                    .background(if (isSelected) BilibiliPink else Color.Transparent)
                    .clickVfx { onSelect(index) }
                    .padding(vertical = 7.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    color = if (isSelected) Color.White else Color.White.copy(alpha = 0.65f),
                    fontSize = 12.sp,
                    fontFamily = wearbiliFontFamily,
                    fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
                )
            }
        }
    }
}