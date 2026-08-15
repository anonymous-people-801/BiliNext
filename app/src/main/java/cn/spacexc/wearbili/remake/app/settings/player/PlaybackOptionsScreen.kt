package cn.spacexc.wearbili.remake.app.settings.player

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import cn.spacexc.wearbili.remake.app.settings.LocalConfiguration
import cn.spacexc.wearbili.remake.app.settings.SettingsManager
import cn.spacexc.wearbili.remake.common.ui.Card
import cn.spacexc.wearbili.remake.common.ui.GradientSlider
import cn.spacexc.wearbili.remake.common.ui.Switch
import cn.spacexc.wearbili.remake.common.ui.TitleBackground
import cn.spacexc.wearbili.remake.common.ui.isRound
import cn.spacexc.wearbili.remake.common.ui.theme.wearbiliFontFamily
import cn.spacexc.wearbili.remake.common.ui.titleBackgroundHorizontalPadding
import cn.spacexc.wearbili.remake.proto.settings.Player
import cn.spacexc.wearbili.remake.proto.settings.VideoDecoder
import cn.spacexc.wearbili.remake.proto.settings.VideoDisplaySurface
import cn.spacexc.wearbili.remake.proto.settings.copy
import kotlinx.coroutines.launch
import java.util.Locale

@kotlinx.serialization.Serializable
object PlaybackOptionsScreen

@Composable
fun PlaybackOptionsScreen(navController: NavController) {
    val scope = rememberCoroutineScope()
    TitleBackground(
        navController = navController,
        title = "",
        onRetry = { },
        onBack = navController::navigateUp
    ) {
        val configuration = LocalConfiguration.current
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = titleBackgroundHorizontalPadding(), vertical = 8.dp),
            horizontalAlignment = if (isRound()) Alignment.CenterHorizontally else Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "播放选项",
                color = Color.White,
                fontFamily = wearbiliFontFamily,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth(),
                textAlign = if (isRound()) TextAlign.Center else TextAlign.Start
            )
            Text(
                text = "调整播放与弹幕设置",
                color = Color.White,
                fontFamily = wearbiliFontFamily,
                fontSize = 11.sp,
                fontWeight = FontWeight.Normal,
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(0.7f),
                textAlign = if (isRound()) TextAlign.Center else TextAlign.Start
            )

            OptionSelectorCard(
                title = "默认播放器",
                options = listOf("视频播放器", "音频播放器"),
                selectedIndex = if (configuration.defaultPlayer == Player.AudioPlayer) 1 else 0
            ) { index ->
                scope.launch {
                    SettingsManager.updateConfiguration {
                        copy {
                            defaultPlayer = if (index == 1) Player.AudioPlayer else Player.VideoPlayer
                        }
                    }
                }
            }
            OptionSelectorCard(
                title = "视频解码器",
                options = listOf("硬件解码", "软件解码"),
                selectedIndex = if (configuration.videoDecoder == VideoDecoder.Software) 1 else 0
            ) { index ->
                scope.launch {
                    SettingsManager.updateConfiguration {
                        copy {
                            videoDecoder = if (index == 1) VideoDecoder.Software else VideoDecoder.Hardware
                        }
                    }
                }
            }
            OptionSelectorCard(
                title = "视频显示表面",
                options = listOf("SurfaceView", "TextureView"),
                selectedIndex = if (configuration.videoDisplaySurface == VideoDisplaySurface.TextureView) 1 else 0
            ) { index ->
                scope.launch {
                    SettingsManager.updateConfiguration {
                        copy {
                            videoDisplaySurface = if (index == 1) VideoDisplaySurface.TextureView else VideoDisplaySurface.SurfaceView
                        }
                    }
                }
            }
            SwitchCard(
                title = "低性能模式",
                description = "降低视频播放性能开销，提升流畅度",
                isOn = configuration.isVideoLowPerformance
            ) { isOn ->
                scope.launch {
                    SettingsManager.updateConfiguration {
                        copy {
                            isVideoLowPerformance = isOn
                        }
                    }
                }
            }

            Text(
                text = "弹幕设置",
                color = Color.White,
                fontFamily = wearbiliFontFamily,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp),
                textAlign = if (isRound()) TextAlign.Center else TextAlign.Start
            )
            val danmaku = configuration.danmakuPlayerSettings
            DanmakuSettingSlider(
                title = "弹幕透明度",
                value = danmaku.alpha,
                range = 0f..1f,
                displayedValue = { "${(it * 100).toInt()}%" }
            ) { value ->
                SettingsManager.updateConfiguration {
                    copy {
                        danmakuPlayerSettings = danmakuPlayerSettings.copy {
                            alpha = value
                        }
                    }
                }
            }
            DanmakuSettingSlider(
                title = "弹幕显示区域",
                value = danmaku.displayArea,
                range = 0f..1f,
                displayedValue = { "${(it * 100).toInt()}%" }
            ) { value ->
                SettingsManager.updateConfiguration {
                    copy {
                        danmakuPlayerSettings = danmakuPlayerSettings.copy {
                            displayArea = value
                        }
                    }
                }
            }
            DanmakuSettingSlider(
                title = "弹幕字号",
                value = danmaku.fontScale,
                range = 0.5f..2f,
                displayedValue = { String.format(Locale.getDefault(), "%.2f", it) }
            ) { value ->
                SettingsManager.updateConfiguration {
                    copy {
                        danmakuPlayerSettings = danmakuPlayerSettings.copy {
                            fontScale = value
                        }
                    }
                }
            }
            DanmakuSettingSlider(
                title = "屏蔽等级",
                value = danmaku.blockLevel.toFloat(),
                range = 0f..10f,
                displayedValue = { "${it.toInt()}级" }
            ) { value ->
                SettingsManager.updateConfiguration {
                    copy {
                        danmakuPlayerSettings = danmakuPlayerSettings.copy {
                            blockLevel = value.toInt()
                        }
                    }
                }
            }
            SwitchCard(
                title = "普通弹幕",
                description = "显示普通弹幕",
                isOn = danmaku.isNormalDanmakuEnabled
            ) { isOn ->
                scope.launch {
                    SettingsManager.updateConfiguration {
                        copy {
                            danmakuPlayerSettings = danmakuPlayerSettings.copy {
                                isNormalDanmakuEnabled = isOn
                            }
                        }
                    }
                }
            }
            SwitchCard(
                title = "高级弹幕",
                description = "显示高级弹幕（滚动、顶部、底部弹幕等）",
                isOn = danmaku.isAdvanceDanmakuEnabled
            ) { isOn ->
                scope.launch {
                    SettingsManager.updateConfiguration {
                        copy {
                            danmakuPlayerSettings = danmakuPlayerSettings.copy {
                                isAdvanceDanmakuEnabled = isOn
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OptionSelectorCard(
    title: String,
    options: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit
) {
    Card(innerPaddingValues = PaddingValues(14.dp), shape = RoundedCornerShape(13)) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = title,
                color = Color.White,
                fontFamily = wearbiliFontFamily,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
            options.forEachIndexed { index, option ->
                val isSelected = index == selectedIndex
                Card(
                    shape = RoundedCornerShape(percent = 50),
                    innerPaddingValues = PaddingValues(horizontal = 14.dp, vertical = 10.dp),
                    outerPaddingValues = PaddingValues(0.dp),
                    fillMaxSize = false,
                    isHighlighted = isSelected,
                    onClick = { onSelect(index) }
                ) {
                    Text(
                        text = option,
                        color = Color.White,
                        fontFamily = wearbiliFontFamily,
                        fontSize = 12.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .alpha(if (isSelected) 1f else 0.7f),
                        textAlign = if (isRound()) TextAlign.Center else TextAlign.Start
                    )
                }
            }
        }
    }
}

@Composable
fun SwitchCard(
    title: String,
    description: String,
    isOn: Boolean,
    onValueChanged: (Boolean) -> Unit
) {
    Card(innerPaddingValues = PaddingValues(), shape = RoundedCornerShape(12)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = Color.White,
                    fontFamily = wearbiliFontFamily,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = description,
                    color = Color.White,
                    fontFamily = wearbiliFontFamily,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier.alpha(0.7f)
                )
            }
            Switch(isOn = isOn, onValueChanged = onValueChanged)
        }
    }
}

@Composable
fun DanmakuSettingSlider(
    title: String,
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    displayedValue: (Float) -> String,
    onPersist: suspend (Float) -> Unit
) {
    val scope = rememberCoroutineScope()
    var currentValue by remember {
        mutableFloatStateOf(value)
    }
    Card(innerPaddingValues = PaddingValues(14.dp), shape = RoundedCornerShape(13)) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = title,
                    color = Color.White,
                    fontFamily = wearbiliFontFamily,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = displayedValue(currentValue),
                    color = Color.White,
                    fontFamily = wearbiliFontFamily,
                    fontSize = 11.sp,
                    modifier = Modifier.alpha(0.7f)
                )
            }
            GradientSlider(
                value = currentValue,
                range = range,
                modifier = Modifier.fillMaxWidth(),
                onValueChanged = {
                    currentValue = it
                },
                onSlideFinished = {
                    scope.launch {
                        onPersist(currentValue)
                    }
                }
            )
        }
    }
}
