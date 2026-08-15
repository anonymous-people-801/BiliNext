package cn.spacexc.wearbili.remake.app.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import cn.spacexc.wearbili.remake.app.Application
import cn.spacexc.wearbili.remake.common.data.appConfigurationDataStore
import cn.spacexc.wearbili.remake.common.ui.BilibiliPink
import cn.spacexc.wearbili.remake.proto.settings.AppConfiguration
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

/**
 * Created by XC-Qan on 2023/4/6.
 * I'm very cute so please be nice to my code!
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 */

object SettingsManager {
    private val context = Application.getApplication()
    private val configurationDataStore = context.appConfigurationDataStore

    fun getConfiguration() = runBlocking { configurationDataStore.data.first() }
    val configuration
        @Composable get() = configurationDataStore.data.collectAsState(initial = getConfiguration())

    suspend fun updateConfiguration(newConfiguration: AppConfiguration.() -> AppConfiguration) {
        configurationDataStore.updateData { currentConfig ->
            currentConfig.newConfiguration()
        }
    }
}

val LocalConfiguration = staticCompositionLocalOf<AppConfiguration> {
    error("No AppConfiguration provided")
}

@Composable
fun ProvideConfiguration(
    content: @Composable () -> Unit
) {
    val configuration by SettingsManager.configuration
    // 用户自定义主题色应用到全局主题色, 使应用内所有引用 BilibiliPink 的 UI 生效
    // 注意: 必须在组合期同步赋值(而非 LaunchedEffect 异步), 否则转场/lookahead 布局期间
    // 写入全局状态会触发跨帧重组, 导致 "Placement happened before lookahead" 崩溃
    val userThemeColor = configuration.customization.themeColor
        .takeIf { it.isNotBlank() }
        ?.let { runCatching { Color(android.graphics.Color.parseColor(it)) }.getOrNull() }
    val targetThemeColor = userThemeColor ?: cn.spacexc.wearbili.remake.app.nicknameThemeColorOverride
    if (targetThemeColor != null && BilibiliPink != targetThemeColor) {
        BilibiliPink = targetThemeColor
    }
    CompositionLocalProvider(LocalConfiguration provides configuration) {
        content()
    }
}