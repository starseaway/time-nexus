package com.xinyi.timenexus.consts;

/**
 * 日期格式常量定义
 *
 * <p> 统一管理所有时间格式 Pattern，避免硬编码 </p>
 *
 * <p> 命名规范：</p>
 * <ul>
 *   <li> DATE：仅日期 </li>
 *   <li> TIME：仅时间 </li>
 *   <li> DATETIME：日期 + 时间 </li>
 *   <li> *_SLASH：斜杠分隔 </li>
 *   <li> *_COMPACT：无分隔符（紧凑格式）</li>
 *   <li> *_MS：包含毫秒 </li>
 *   <li> *_TZ：包含时区 </li>
 *   <li> *_CN：中文格式 </li>
 * </ul>
 *
 * @author 新一
 * @date 2026/3/31 16:06
 */
public final class DatePatterns {

    private DatePatterns() { }

    /// ===== 标准日期时间 =====

    /// 日期（yyyy-MM-dd）
    public static final String DATE = "yyyy-MM-dd";

    /// 时间（HH:mm:ss）
    public static final String TIME = "HH:mm:ss";

    /// 日期时间（yyyy-MM-dd HH:mm:ss）
    public static final String DATETIME = "yyyy-MM-dd HH:mm:ss";

    /// 日期时间（含毫秒）
    public static final String DATETIME_MS = "yyyy-MM-dd HH:mm:ss.SSS";

    /// ===== 斜杠格式 =====

    /// 日期斜杠格式
    public static final String DATE_SLASH = "yyyy/MM/dd";

    /// 时间斜杠格式
    public static final String TIME_SLASH = "HH/mm/ss";

    /// 日期时间（日期斜杠，时间没有斜杠）
    public static final String DATETIME_SLASH = "yyyy/MM/dd HH:mm:ss";

    /// ===== 紧凑格式（无分隔符）=====

    /// 日期紧凑格式
    public static final String DATE_COMPACT = "yyyyMMdd";

    /// 时间紧凑格式
    public static final String TIME_COMPACT = "HHmmss";

    /// 日期时间紧凑格式
    public static final String DATETIME_COMPACT = "yyyyMMddHHmmss";

    /// 日期时间（紧凑+毫秒）
    public static final String DATETIME_COMPACT_MS = "yyyyMMddHHmmssSSS";

    /// ===== ISO 标准 =====

    /// ISO8601（无毫秒）2026-03-31T10:20:30
    public static final String ISO8601 = "yyyy-MM-dd'T'HH:mm:ss";

    /// ISO8601（含毫秒）2026-03-31T10:20:30.123
    public static final String ISO8601_MS = "yyyy-MM-dd'T'HH:mm:ss.SSS";

    /// ISO8601（带时区）2026-03-31T10:20:30+08:00
    public static final String ISO8601_TZ = "yyyy-MM-dd'T'HH:mm:ssXXX";

    /// ===== 星期格式 =====

    /// 星期（短格式：周一）
    public static final String WEEK_SHORT_EN = "EEE";

    /// 星期（完整格式：星期一）
    public static final String WEEK_FULL_EN = "EEEE";

    /// ==== 中文格式 ====

    /// 中文日期（2026年03月31日）
    public static final String DATE_CN = "yyyy年MM月dd日";

    /// 中文时间（10时20分30秒）
    public static final String TIME_CN = "HH时mm分ss秒";

    /// 中文日期时间（2026年03月31日 10时20分30秒）
    public static final String DATETIME_CN = "yyyy年MM月dd日 HH时mm分ss秒";

    /// ===== 文件 / 日志 =====

    /// 文件名安全格式（2026-03-31_10-20-30）
    public static final String DATETIME_FILE = "yyyy-MM-dd_HH-mm-ss";

    /// 日志格式（2026-03-31 10:20:30.123）
    public static final String DATETIME_LOG = "yyyy-MM-dd HH:mm:ss.SSS";

    /// ===== 网络协议 =====

    /// RFC1123（HTTP Header：Tue, 31 Mar 2026 10:20:30 GMT）
    public static final String RFC_1123 = "EEE, dd MMM yyyy HH:mm:ss z";

    /// ===== 其他常用补充 =====

    /// 年月
    public static final String YEAR_MONTH = "yyyy-MM";

    /// 年
    public static final String YEAR = "yyyy";

    /// 月日
    public static final String MONTH_DAY = "MM-dd";

    /// 时分
    public static final String HOUR_MINUTE = "HH:mm";
}