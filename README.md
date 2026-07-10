# TimeNexus 日期时间框架

<div align="center">
  <img src="time-nexus-logo.svg" width="500" alt="time-nexus-logo">
</div>

![Version](https://img.shields.io/badge/version-3.2.0-blue)
![License](https://img.shields.io/badge/license-Apache%202.0-green)
![API](https://img.shields.io/badge/API-19%2B-brightgreen)

> 在时间的荒野里，每一秒都是深沉而无声的脚印。

## 一、简介

TimeNexus 是一个面向 Android 的日期时间框架，通过统一的上下文模型（`TimeContext`）驱动，把原本分散的格式化、解析、时间计算等操作整合到一起。

你可以用一行代码完成大部分常见时间操作；在复杂场景下，也可以通过链式 API 逐步组合时间逻辑，按需扩展。

> Time flows. Structure remains.
> TimeNexus is a context-driven datetime framework for Android.

---

## 特性

- 上下文驱动（所有操作基于 `TimeContext` 统一时区与 Locale）
- 一行完成常见操作（`DateTimeNexus` 覆盖 80% 高频使用场景）
- 链式时间 API
- 时间区间建模
- 日历数据建模（`MonthGrid` 提供标准 6x7 的数据模型，`MonthCalendar` 负责月历状态与年月切换）
- 高性能 & 线程安全
- 多格式智能解析

相比传统开发方案：

| 能力     | Date / Calendar | TimeNexus      |
|--------|-----------------|----------------|
| 时区管理   | ❌ 隐式依赖系统        | ✅ Context 显式控制 |
| 线程安全   | ❌ 需手动处理         | ✅ 内部保证         |
| API 体验 | ❌ 命令式、冗长        | ✅ Fluent API   |
| 区间计算   | ❌ 手写逻辑          | ✅ 内置模型         |
| 日历数据   | ❌ 手写逻辑          | ✅ 纯数据模型        |
| 多格式解析  | ❌ 易错复杂          | ✅ 智能解析         |

---

## 适用场景

- 多时区时间处理
- 日历 / 时间选择组件
- 时间区间计算（预约 / 排期）
- 日志 / 埋点时间统一处理

---

## 二、SDK 适用范围

| 项目         | 要求                 |
|------------|--------------------|
| Min SDK    | 19（Android 4.4）及以上 |
| JVM Target | 1.8                |
| Kotlin     | 1.6+               |

---

## 三、集成方式

### 1. 根据 Gradle 版本或项目配置自行选择在合适的位置添加仓库地址
```groovy
maven {
    // jitpack仓库
    url 'https://jitpack.io' 
}
```

### 2. 在 `build.gradle` (Module 级) 中添加依赖：
```groovy
implementation 'com.github.starseaway:time-nexus:3.1.1'
```

```kotlin
implementation("com.github.starseaway:time-nexus:3.1.1")
```

## 四、快速开始

### 1. 时间上下文

这是整个框架的核心抽象，所有时间能力（格式化、解析、计算等）都基于该上下文执行。

```kotlin
// 框架默认使用系统时区与语言环境，也支持自定义指定
val context = TimeContext.Builder()
        .timeZone(TimeZone.getTimeZone("Asia/Shanghai")) // 中国时区（东八区）
        .locale(Locale.CHINA) // 中文环境
        .build()
DateTimeNexus.setDefaultContext(context)
```

### 2. 快捷调用类

* 简单直接、覆盖 80% 的高频使用场景
* 内部自动使用 DEFAULT_CONTEXT 默认的时间上下文
* 更多使用请查看：[DateTimeNexus.java](library/src/main/java/com/xinyi/timenexus/DateTimeNexus.java)

```kotlin
// 格式化当前时间，默认 yyyy-MM-dd HH:mm:ss
DateTimeNexus.format(Date())
// 自定义格式
DateTimeNexus.format(date, DatePatterns.DATE)

// 星期x：星期三
DateTimeNexus.getWeekDayName()
// 周x：周三
DateTimeNexus.getWeekDayShortName()

// 毫秒时间戳 to String
DateTimeNexus.formatMillis(1711880000000L)
// 秒级时间戳 to String
DateTimeNexus.formatSeconds(1711880000)
// 时间字符串 to 毫秒时间戳
DateTimeNexus.toMillis("2026-04-01 10:20:30") 

// 标准时间格式解析
DateTimeNexus.parse("2026-03-31 10:20:30")
// 自动识别格式（支持基本的标准格式）
DateTimeNexus.smartParse("2026/03/31")
// 智能解析 增强版（支持任意格式）
DateTimeNexus.smartParsePlus("1711880000") // 时间戳
// 自定义多格式解析（按顺序匹配）
DateTimeNexus.parseMultiple(
  text,
  DatePatterns.DATETIME, // 优先匹配
  DatePatterns.DATE,
  DatePatterns.DATE_SLASH
)

// 当前时间 +3 天
DateTimeNexus.plusDays(3)
// 当前时间 -1 月
DateTimeNexus.minusMonths(1)

// 是否今天
DateTimeNexus.isToday(date)
// 是否昨天
DateTimeNexus.isYesterday(date)
// 是否同一天
DateTimeNexus.isSameDay(d1, d2) 
```

### 3. 链式时间模型

* Fluent API（链式调用）
* 可变对象（高性能）
* Calendar 封装
* 更多使用请查看：[DateTime.java](library/src/main/java/com/xinyi/timenexus/core/DateTime.java)

```java
Date result = DateTime.from(new Date()) // 基于当前时间创建
        .plusDays(1) // +1 天
        .plusMonths(1) // +1 月
        .startOfDay(); // 获取当天的 00:00:00.000
```

### 4. 时间区间模型

* 区间包含判断
* 重叠检测
* 精确位置获取 [Position.java](library/src/main/java/com/xinyi/timenexus/enums/Position.java)
* 更多使用请查看：[DateTimeRange.java](library/src/main/java/com/xinyi/timenexus/core/DateTimeRange.java)

```kotlin
// 创建区间
val range = DateTimeRange(start, end) 
// 是否在区间内
range.contains(date) 
// 判断两个时间区间是否存在重叠
range.overlap(other)
// 两天相差天数
range.daysBetween()
// 获取位置（BEFORE / INSIDE / AFTER / 等）
range.locate(date)
```

### 5. 日历数据模型

日历相关能力分为两层：

| 类               | 适用场景                  |
|-----------------|-----------------------|
| `MonthGrid`     | 一次性生成某月的 6x7 网格数据     |
| `MonthCalendar` | 长期持有、需要年月切换与基准时间管理日历网格的场景 |

MonthGrid — 月度网格：
 -  固定 6x7 = 42 格
 - 自动补齐前后月份
 - 支持 `rebuild()` 原地刷新（长期复用同一实例）
 - UI 无关（纯数据）
 - 更多使用请查看：[MonthGrid.java](library/src/main/java/com/xinyi/timenexus/calendar/MonthGrid.java)

```java
// 一次性生成：获取 date 所在月的日历网格，一周从周一开始
MonthGrid grid = MonthGrid.of(date, context, Calendar.MONDAY);

// 获取 42 天完整网格
List<DayInfo> days = grid.getDays();
// 仅获取当月天数（28~31 天）
List<DayInfo> monthDays = grid.getCurrentMonthDays();
// 获取某一周（weekIndex: 0~5）
List<DayInfo> week = grid.getWeek(0);
```

MonthCalendar — 月历状态管理：
 - 内部维护基准时间（默认当前系统时间），日历列表据此生成
 - 外部通过 `getAnchorDate()` / `getAnchorDateTime()` 获取副本，避免共享可变状态
 - 支持年份范围限制（默认 1900 ~ 2100）
 - 配置项（`setYearRange` / `setFirstDayOfWeek`）返回 `void`；基准定位与导航（`goToXxx` / `nextXxx` / `prevXxx`）返回 `boolean`
 - 网格构建为轻量操作（微秒级），可在主线程直接调用
 - 更多使用请查看：[MonthCalendar.java](library/src/main/java/com/xinyi/timenexus/calendar/MonthCalendar.java)

```java
public void test() {
  // 默认以当前系统时间作为基准时间创建
  MonthCalendar calendar = new MonthCalendar();

  // 指定初始基准日期
  MonthCalendar calendar = new MonthCalendar(date);

  // 配置项（void，非法参数抛异常）
  calendar.setYearRange(2000, 2030);
  calendar.setFirstDayOfWeek(Calendar.SUNDAY);

  // 导航（到达边界时返回 false）
  if (calendar.nextMonth()) {
      // 刷新 UI
  }
  calendar.prevYear();

  // 定位基准时间到指定年月（超出范围或值未变化时返回 false）
  calendar.goToYear(2026);
  calendar.goToMonth(7);

  // 获取基准时间副本
  Date anchorDate = calendar.getAnchorDate();
  DateTime anchorDateTime = calendar.getAnchorDateTime();

  // 定位基准时间到指定日期（年份超出范围时返回 false）
  calendar.goToDate(date);

  // 获取天数列表（切换月后自动为最新数据）
  List<DayInfo> days = calendar.getDays(); // 42 天完整网格
  List<DayInfo> monthDays = calendar.getCurrentMonthDays(); // 仅当月

  // 边界判断（可用于禁用 上一月 / 下一月 等按钮）
  boolean canPrev = calendar.canPrevMonth();
  boolean canNext = calendar.canNextMonth();
}
```

典型 UI 绑定流程：

```java
// 页面初始化时创建一次，长期持有
MonthCalendar calendar = new MonthCalendar();

// 渲染日历列表
void render() {
    List<DayInfo> days = calendar.getDays();
    // adapter.submitList(days) ...
    prevBtn.setEnabled(calendar.canPrevMonth());
    nextBtn.setEnabled(calendar.canNextMonth());
}

// 点击下一月
void onNextMonth() {
    if (calendar.nextMonth()) {
        render();
    }
}

// 用户点击某一天，将基准时间定位到该日
void onDayClick(Date date) {
    if (calendar.goToDate(date)) {
        render();
    }
}
```

### 6. Kotlin 扩展

* 更便捷的调用方式：[DatetimeExtension.kt](library/src/main/java/com/xinyi/timenexus/extension/DatetimeExtension.kt)

```kotlin
val str = date.format() // Date to String
val next = date.plusDays(1) // 日期 +1 天
val isToday = date.isToday() // 是否今天
```

---

## 五、目录结构

```text
com.xinyi.timenexus
│
├── DateTimeNexus                # 快捷调用、门面入口
│
├── consts
│   └── DatePatterns             # 时间格式常量定义
│
├── core                         
│   ├── DateTime                 # 链式时间操作模型（核心类）
│   ├── TimeContext              # 时间上下文（时区 / Locale / 扩展能力核心）
│   └── DateTimeRange            # 时间区间模型（区间计算 / 判断）
│
├── calendar
│   ├── DayInfo                  # 单日模型（包含日期 / 星期 / 所属月份类型）
│   ├── MonthGrid                # 月度网格模型（42格日历数据生成 / 原地刷新）
│   └── MonthCalendar            # 月历状态管理（基准时间 / 年月切换 / 年份范围）
│
├── enums
│   ├── Position                 # 时间在区间中的位置（BEFORE / INSIDE / AFTER 等）
│   └── WeekDay                  # 星期枚举（封装 Calendar.DAY_OF_WEEK）
│
├── model                        # 预留的业务模型（偏 UI 状态抽象）
│   └── SelectionModel           # 时间选择模型（单选 / 区间选择）
│
├── util                         
│   └── DateFormatter            # 格式化与解析（线程安全 + 缓存复用）
│
└── extension                    
    └── DateExtensions.kt        # Datetime 相关 扩展函数，方便快捷调用
```

## 六、版本变更记录

### V3.2.0 (2026-07-10)
- 🦄 refactor: `MonthCalendar` 移除链式调用；设置或切换方法统一返回 `boolean`。

### V3.1.0 (2026-07-09)
- ✨ feat: `DateTime` 新增更快实用的 Api 方法
- ✨ feat: `MonthGrid` 新增 `rebuild()`，支持原地刷新网格数据，长期复用同一实例
- ✨ feat: `MonthCalendar` 新增月历状态管理类：选中日期、年份范围（默认 1900~2100）、年月切换、自动刷新天数列表

### V3.0.2 (2026-04-13)
🦄 refactor:
- 扩展函数统一添加 object 包裹，优化结构与命名空间
- 添加 @JvmStatic，提升 Java 互操作性

### V3.0.0 (2026-04-01)
- 引入 **TimeContext（核心架构升级）**
- 所有时间操作统一依赖上下文
- 新增：
    * DateTime（链式 API）
    * DateTimeRange（区间模型）
    * MonthGrid（日历模型）
- 废弃旧版本中各种工具类
- 从该版本开始，正式在 GitHub 开源

### V2.1.0 (2025-07-29)
- 新增一种时间解析格式yyyyMMddHHmmssSSS

### V2.0.0 (2025-05-29)
- 重大修改：重构日期格式化工具类，将不再兼容旧版本的日期格式化方式

### V1.0.1 (2025-04-26)
- 重载str2Date方法，并根据传入的字符串自动生成要求的日期格式

### V1.0.0 (2025-03-31)
- 初始版本，包含基础日期时间处理工具。