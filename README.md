# Bvideo

一个基于 Kotlin 开发的 Android 视频应用练习项目，目标是逐步实现类似 B 站的首页推荐流、搜索页、短视频页以及可扩展的播放器模块。

项目地址：<https://github.com/woshilaixuex/Bvideo>

## 项目预览

### 首页效果

![首页效果](github/image/homepage.jpg)

## 当前实现

- 启动页与用户协议弹窗
- 首页顶部搜索条，点击跳转搜索页
- 首页双列视频推荐流，使用 mock 数据渲染
- 基于 `RecyclerView + GridLayoutManager(2)` 的双列卡片布局
- 基于 `Glide` 的封面图加载
- 独立 `player` 模块，完成最小可用播放器封装

## 模块结构

```text
app     应用入口、页面导航、首页/搜索页/用户页等 UI
common  通用工具与基础封装
data    数据层预留
domain  领域模型与业务实体
player  播放器封装与可替换播放内核设计
```

## Player 模块说明

`player` 模块是当前项目的重点基础设施，已经拆出独立播放器层，方便后续扩展倍速、全屏、手势控制、清晰度切换等能力。

当前包含：

- `VideoPlayerEngine`：播放器能力接口，约束播放、暂停、seek、倍速、音量等基础能力
- `BVideoPlayerController`：基于 `Media3 / ExoPlayer` 的最小控制器封装，负责设置数据源、准备播放、开始/暂停/释放
- `BPlayerView`：对 `Media3 PlayerView` 的轻量包装，统一播放器视图绑定
- `BPlayerDataSource`：视频源管理的基础抽象
- `BPlayerCore`：播放器交互与状态控制的后续扩展入口

这套结构的目标不是只“播起来”，而是为后续替换内核或扩展复杂播放器交互预留空间。

## 技术栈

- Kotlin
- Jetpack Navigation
- RecyclerView
- ViewBinding
- Material Components
- Glide
- Media3 / ExoPlayer
- 多模块 Gradle 工程

## 本地运行

```bash
./gradlew assembleDebug
```

或在 Windows 下：

```powershell
.\gradlew assembleDebug
```

## 开发规划

- 接入真实视频列表数据源
- 完善搜索页与搜索结果流
- 支持视频详情页与点击卡片跳转
- 丰富播放器控制层：倍速、全屏、进度拖动、状态管理
- 补充短视频纵向滑动播放页

## 说明

当前仓库仍处于持续迭代阶段，部分页面和模块已完成基础骨架，后续会围绕播放器能力、首页体验和业务流继续完善。
