package cn.spacexc.wearbili.remake.app.live.ui

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import cn.spacexc.wearbili.common.domain.video.toShortChinese
import cn.spacexc.wearbili.remake.app.live.domain.remote.LiveRoomItem
import cn.spacexc.wearbili.remake.app.player.livestream.ui.LiveStreamActivity
import cn.spacexc.wearbili.remake.app.player.livestream.ui.PARAM_ROOM_ID
import cn.spacexc.wearbili.remake.app.player.livestream.ui.PARAM_ROOM_ONLINE
import cn.spacexc.wearbili.remake.app.player.livestream.ui.PARAM_ROOM_TITLE
import cn.spacexc.wearbili.remake.app.space.ui.UserSpaceScreen
import cn.spacexc.wearbili.remake.common.ui.BiliImage
import cn.spacexc.wearbili.remake.common.ui.BilibiliPink
import cn.spacexc.wearbili.remake.common.ui.Card
import cn.spacexc.wearbili.remake.common.ui.LoadableBox
import cn.spacexc.wearbili.remake.common.ui.clickVfx
import cn.spacexc.wearbili.remake.common.ui.theme.wearbiliFontFamily

/**
 * 直播页内容(首页 pager 第4页): 展示推荐直播间, 点击进播放器, ⋮ 进主播主页
 */
@Composable
fun LiveScreen(
    viewModel: LiveViewModel = androidx.hilt.navigation.compose.hiltViewModel(),
    navController: NavController
) {
    val state = viewModel.screenState
    val context = LocalContext.current
    LoadableBox(
        uiState = state.uiState,
        onRetry = viewModel::getLiveRooms
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(state.rooms, key = { it.roomid }) { room ->
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

@Composable
fun LiveRoomCard(
    room: LiveRoomItem,
    onClick: () -> Unit,
    onOpenUserSpace: () -> Unit
) {
    Card(
        innerPaddingValues = PaddingValues(8.dp),
        onClick = onClick
    ) {
        Box {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(modifier = Modifier.width(88.dp)) {
                    BiliImage(
                        url = room.coverUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .aspectRatio(16f / 10f)
                            .clip(RoundedCornerShape(6.dp)),
                        contentScale = ContentScale.Crop,
                        optimized = false
                    )
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 20.dp)
                ) {
                    Text(
                        text = room.title,
                        color = Color.White,
                        fontSize = 12.sp,
                        fontFamily = wearbiliFontFamily,
                        fontWeight = FontWeight.Medium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = room.uname,
                            color = Color.White,
                            fontSize = 10.sp,
                            fontFamily = wearbiliFontFamily,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.alpha(0.7f)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${room.online.toShortChinese()}人观看",
                            color = BilibiliPink,
                            fontSize = 10.sp,
                            fontFamily = wearbiliFontFamily,
                            maxLines = 1
                        )
                    }
                }
            }
            //右上角 ⋮: 进入主播主页
            Icon(
                imageVector = Icons.Outlined.MoreVert,
                tint = Color.White,
                contentDescription = "主播主页",
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(18.dp)
                    .clickVfx(onClick = onOpenUserSpace)
            )
        }
    }
}
