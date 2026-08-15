> **本仓库为 SpaceXC/Re-WearBili 的个人定制分支(修改日期: 2026-08-15), 修改内容:**
>
> - 全局主题色响应式(`BilibiliPink`), 应用内全部 UI 跟随自定义主题色
> - 播放器: 默认软解、硬解渲染超时降级(慢网不误判)、黑屏修复、播放/暂停图标响应式
> - 直播: 菜单入口 + 首页第 4 页、直播间搜索、推荐直播间列表
> - 搜索: 结果页可改词并写入历史、视频/直播胶囊滑块
> - `libs/Bilibili-Kotlin-SDK` 由子模块转为仓库内普通源码(克隆后可直接构建)
>
> 本项目遵循 **GNU General Public License v3.0**, 原作者版权声明均已保留。

<p align="center">
    <img src="https://repository-images.githubusercontent.com/625249285/2ce9fdfe-9ad8-46ec-9442-bfa69f268be1">
</p>


<h3 align="center">一个手表上的第三方Bilibili客户端，但是重制版</center>

## :watch:配置要求

- 最低内存(RAM)：512M
- 最低储存(ROM)：2G
- 最低兼容的系统：Android 5.0 (API Level 21 Lolipop)



- 推荐内存(RAM)：2G+
- 推荐储存：8G+
- 推荐系统：Android 8.0+ (API Level 26 Oreo)



## :rocket:下载

- 加入QQ群组`912493736`后在群文件处下载安装包。加入后请仔细阅读群公告中的内容。
- 你也可以自行clone该仓库并构建此项目。


##  :hammer_and_wrench: 构建

1. clone 本项目, 用你的 terminal 环境打开项目根目录
2. 输入 ```./gradlew build```
3. 在目录下的 ```build/libs/Re-WearBili - $versionName Ver.$releaseNumber Rel.$versionCode.apk``` 找到apk文件
4. 导入IDEA进行二次开发，或者将apk导入到你的手表使用

### :computer:我的构建环境：

```
Computer: Apple Macbook Pro 2021 M1 Pro 14 inch, 16GB RAM and 1TB ROM
Android Studio: Android Studio Iguana | 2023.2.1 Beta 2
AGP version: 8.2.0
Gradle version: 8.0
Java version: openjdk version "17.0.8.1" 2023-08-24
```

## :book:行为准则

见[行为准则](https://github.com/SpaceXC/Re-WearBili/blob/main/.github/files/CodeOfConduct.md)

## :page_facing_up:开源协议

- 这个项目使用GNU General Public License v3.0协议开源，详见[LICENSE.md](https://github.com/SpaceXC/Re-WearBili/blob/main/LICENSE)
- 这个项目的UI设计使用Creative Common 4.0协议共享。将会在日后正式公开。
