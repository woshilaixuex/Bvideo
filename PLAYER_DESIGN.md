# Bvideo 播放器设计文档

## 1. 目标

本文档聚焦 `player` 模块设计，目标是在当前最小可用播放器的基础上，逐步演进出一套适合 Bvideo 的播放器架构。设计重点不是单纯“能播视频”，而是支持后续扩展：

- 首页卡片播放
- 视频详情页播放
- 短视频竖滑播放
- 全屏切换
- 倍速、进度拖动、手势控制
- 未来可能的多播放内核兼容

当前工程已具备基础播放器能力：

- `BVideoPlayerController`：最小控制器，负责设置数据源、准备播放、播放/暂停/释放
- `BPlayerView`：对 `Media3 PlayerView` 的包装
- `VideoPlayerEngine`：播放器能力接口雏形
- `BPlayerDataSource`：视频源抽象
- `BPlayerCore`：播放器交互与状态骨架

## 2. 设计原则

### 2.1 分层

播放器按四层拆分：

1. **UI 层**
   负责按钮、进度条、全屏按钮、手势浮层、倍速面板等视图展示。

2. **Controller 层**
   负责业务控制入口，例如 `play`、`pause`、`togglePlayPause`、`seekTo`、`enterFullscreen`。

3. **Engine 层**
   负责和具体播放器内核交互，例如 ExoPlayer、IjkPlayer、系统播放器。

4. **Session / State 层**
   负责播放器状态、当前数据源、屏幕模式、进度同步、错误状态与恢复策略。

### 2.2 面向接口而不是面向内核

`app` 模块不应直接依赖 `ExoPlayer` 类型。业务层只能依赖统一播放器能力接口，否则后续切换播放器内核时，页面层会被迫一起改动。

### 2.3 单一职责

- `BPlayerView` 只负责显示视频画面和承载播放器控件
- `BVideoPlayerController` 只负责控制行为
- `VideoPlayerEngine` 只负责与底层播放器交互
- `BPlayerCore` 负责后续状态机、手势与生命周期控制

## 3. 当前实现分析

### 3.1 已完成能力

- 基于 `Media3 / ExoPlayer` 的 URL 播放
- `BPlayerView` 与播放器实例绑定
- `play / pause / stop / release` 基础控制
- 首页双列推荐流与视频数据模型联通

### 3.2 当前局限

- `BVideoPlayerController` 仍直接暴露 `ExoPlayer` 实例，不利于模块隔离
- `VideoPlayerEngine` 接口已经定义，但还没有真正落地实现类
- `BPlayerCore` 只有骨架，状态机、手势、屏幕切换尚未接入
- `BPlayerDataSource` 只支持简单 URL 集合，无法表达清晰度、字幕、试看等信息
- 播放器还没有统一的 `PlayerState`

## 4. 建议架构

### 4.1 推荐类结构

```text
player/
├── engine/
│   ├── VideoPlayerEngine.kt
│   └── ExoPlayerEngine.kt
├── controller/
│   └── BVideoPlayerController.kt
├── model/
│   ├── BPlayerDataSource.kt
│   ├── BPlayerState.kt
│   └── BPlayerCommand.kt
├── view/
│   ├── BPlayerView.kt
│   └── BPlayerCore.kt
└── session/
    └── BPlayerSessionManager.kt
```

### 4.2 状态定义

建议播放器状态统一定义为：

```kotlin
enum class BPlayerState {
    IDLE,
    PREPARING,
    PLAYING,
    PAUSED,
    BUFFERING,
    COMPLETED,
    ERROR
}
```

作用：

- UI 根据状态更新按钮、loading、错误页
- 页面根据状态决定是否自动播放
- 列表场景根据状态决定是否回收、暂停或恢复

### 4.3 数据源定义

当前 `BPlayerDataSource` 建议扩展为：

```kotlin
data class BPlayerSource(
    val id: String,
    val title: String,
    val coverUrl: String,
    val playUrl: String,
    val definitionList: List<DefinitionItem> = emptyList()
)
```

这样后续可以支持：

- 不同清晰度
- 不同播放线路
- 标题、封面等 UI 信息同步

## 5. 核心流程设计

### 5.1 播放流程

1. 页面创建 `BVideoPlayerController`
2. `controller.attach(playerView)`
3. `controller.setSource(source)`
4. `controller.prepare()`
5. Engine 通知状态从 `IDLE -> PREPARING -> PLAYING`
6. UI 依据状态更新播放按钮与 loading

### 5.2 页面销毁

1. `Fragment/Activity` 调用 `controller.release()`
2. `controller` 通知 `engine.release()`
3. `playerView.unbind()`
4. 清理状态与监听器，防止内存泄漏

### 5.3 列表场景

首页推荐流不建议每个 item 长期持有一个播放器实例。更合理的策略是：

- 当前可见项持有播放会话
- 滑出屏幕后暂停或释放
- 点击卡片进入详情页时复用当前数据源

## 6. Player 模块扩展路线

### 第一阶段：基础能力

- `ExoPlayerEngine` 落地
- `BPlayerState` 接入
- `togglePlayPause()`、`seekTo()`、`setSpeed()` 完成

### 第二阶段：交互增强

- 全屏切换
- 播放进度同步
- 倍速面板
- 错误重试

### 第三阶段：高级播放器能力

- 横向滑动快进快退
- 左侧亮度、右侧音量手势
- 清晰度切换
- 列表自动播放与复用
- 短视频页连续播放

## 7. 与业务层的关系

`app` 模块应通过 `controller` 使用播放器，不直接操作底层 `ExoPlayer`。

推荐调用方式：

```kotlin
controller.attach(playerView)
controller.play(url)
```

而不推荐：

```kotlin
controller.player.play()
```

原因：

- 破坏封装
- 业务层和内核耦合
- 不利于后续兼容不同播放器实现

## 8. 首页与播放器关系

当前首页已经是双列推荐流，因此播放器设计要兼顾“卡片流场景”：

- 首页卡片默认以封面图为主
- 点击卡片进入视频详情或切换播放页
- 不建议在双列首页里直接把所有卡片都接成自动播放
- 自动播放更适合短视频页或详情页

因此首页推荐流与播放器模块的关系应是：

- 首页负责展示视频卡片与点击事件
- 播放器负责承载真正播放逻辑
- 两者通过 `Video` / `BPlayerSource` 建立数据连接

## 9. 后续落地建议

建议按下面顺序推进：

1. 将 `BVideoPlayerController` 改为依赖 `VideoPlayerEngine`
2. 新增 `ExoPlayerEngine`
3. 定义 `BPlayerState`
4. 在 `BPlayerCore` 中补状态机和手势控制入口
5. 为详情页引入独立播放器页面
6. 再扩展短视频竖滑播放

## 10. 总结

当前 Bvideo 的播放器模块已经从“页面里直接调用 ExoPlayer”迈出了第一步，开始具备模块化封装基础。后续设计重点不在于简单增加 API 数量，而在于：

- 明确状态
- 明确分层
- 控制业务层和播放器内核的耦合
- 为首页、详情页、短视频页建立统一播放器能力模型

这套设计会让播放器模块从练习性质代码，逐步演进成可维护、可扩展、可复用的业务基础设施。
