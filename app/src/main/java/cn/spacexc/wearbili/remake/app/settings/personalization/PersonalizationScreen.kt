package cn.spacexc.wearbili.remake.app.settings.personalization

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
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
import cn.spacexc.wearbili.remake.common.ui.Switch
import cn.spacexc.wearbili.remake.common.ui.TitleBackground
import cn.spacexc.wearbili.remake.common.ui.clickVfx
import cn.spacexc.wearbili.remake.common.ui.isRound
import cn.spacexc.wearbili.remake.common.ui.theme.wearbiliFontFamily
import cn.spacexc.wearbili.remake.common.ui.titleBackgroundHorizontalPadding
import cn.spacexc.wearbili.remake.proto.settings.Theme
import cn.spacexc.wearbili.remake.proto.settings.copy
import kotlinx.coroutines.launch

private val themeColorPresets = listOf(
    "#FF3679",
    "#00A1D6",
    "#00C49A",
    "#9C27B0",
    "#FF8F1F",
    "#3F3F3F"
)

@kotlinx.serialization.Serializable
object PersonalizationScreen

@Composable
fun PersonalizationScreen(navController: NavController) {
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
                text = "个性化",
                color = Color.White,
                fontFamily = wearbiliFontFamily,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth(),
                textAlign = if (isRound()) TextAlign.Center else TextAlign.Start
            )
            Text(
                text = "调整主题与外观设置",
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
                title = "主题",
                options = listOf("明亮", "纯黑", "圆角"),
                selectedIndex = when (configuration.customization.theme) {
                    Theme.Black -> 1
                    Theme.Round -> 2
                    else -> 0
                }
            ) { index ->
                val theme = when (index) {
                    1 -> Theme.Black
                    2 -> Theme.Round
                    else -> Theme.Light
                }
                scope.launch {
                    SettingsManager.updateConfiguration {
                        copy {
                            customization = customization.copy {
                                this.theme = theme
                            }
                        }
                    }
                }
            }
            ThemeColorSelector(
                themeColor = configuration.customization.themeColor
            ) { color ->
                scope.launch {
                    SettingsManager.updateConfiguration {
                        copy {
                            customization = customization.copy {
                                themeColor = color
                            }
                        }
                    }
                }
            }
            SwitchCard(
                title = "视频封面颜色吸附",
                description = "播放页背景吸收视频封面颜色",
                isOn = configuration.customization.videoCoverColorAbsorb
            ) { isOn ->
                scope.launch {
                    SettingsManager.updateConfiguration {
                        copy {
                            customization = customization.copy {
                                videoCoverColorAbsorb = isOn
                            }
                        }
                    }
                }
            }
            SwitchCard(
                title = "推荐页大卡片",
                description = "推荐页由列表更换为卡片展示",
                isOn = configuration.customization.recommendPageLargeCard
            ) { isOn ->
                scope.launch {
                    SettingsManager.updateConfiguration {
                        copy {
                            customization = customization.copy {
                                recommendPageLargeCard = isOn
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
fun ThemeColorSelector(
    themeColor: String,
    onSelect: (String) -> Unit
) {
    Card(innerPaddingValues = PaddingValues(14.dp), shape = RoundedCornerShape(13)) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "主题色",
                color = Color.White,
                fontFamily = wearbiliFontFamily,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.horizontalScroll(rememberScrollState())
            ) {
                themeColorPresets.forEach { color ->
                    val isSelected = themeColor.equals(color, ignoreCase = true)
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .background(Color(android.graphics.Color.parseColor(color)), CircleShape)
                            .then(
                                if (isSelected) Modifier.border(2.dp, Color.White, CircleShape) else Modifier
                            )
                            .clickVfx(onClick = { onSelect(color) })
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
