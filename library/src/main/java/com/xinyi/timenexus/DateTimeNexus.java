package com.xinyi.timenexus;

import com.xinyi.timenexus.consts.DatePatterns;
import com.xinyi.timenexus.core.DateTime;
import com.xinyi.timenexus.core.TimeContext;
import com.xinyi.timenexus.core.DateTimeRange;
import com.xinyi.timenexus.util.DateFormatter;

import java.util.Date;

/**
 * 日期时间框架的快捷函数调用、门面入口
 *
 * <p> 对外常用入口，屏蔽底层实现复杂度，支持：</p>
 * <ul>
 *    <li> 80% 常用场景必须 “一行搞定” </li>
 *    <li> 高级能力仍可回退到底层 DateTime / Formatter </li>
 * </ul>
 *
 * @author 新一
 * @date 2026/3/31 14:38
 */
public final class DateTimeNexus {

    /**
     * 全局默认时间上下文（系统时区）
     */
    public static volatile TimeContext DEFAULT_CONTEXT = new TimeContext.Builder().build();

    private DateTimeNexus() { }

    /**
     * 设置全局默认上下文
     */
    public static void setDefaultContext(TimeContext context) {
        if (context != null) {
            DEFAULT_CONTEXT = context;
        }
    }

    /**
     * 获取全局默认上下文
     */
    public static TimeContext getDefaultContext() {
        return DEFAULT_CONTEXT;
    }

    /**
     * 当前时间
     */
    public static Date now() {
        return new Date();
    }

    /**
     * 当前时间（毫秒）
     *
     * @return 当前时间（毫秒）
     */
    public static long nowMs() {
        return System.currentTimeMillis();
    }

    /**
     * 当前时间（秒）
     *
     * @return 当前时间（秒）
     */
    public static long nowSeconds() {
        return System.currentTimeMillis() / 1000;
    }

    /**
     * 当前时间字符串
     */
    public static String nowString() {
        return format(now());
    }

    /**
     * 获取星期名称
     */
    public static String getWeekDayName() {
        return DateTime.withDefault().getWeekDayName();
    }

    /**
     * 获取星期名称（简写）
     */
    public static String getWeekDayShortName() {
        return DateTime.withDefault().getWeekDayShortName();
    }

    /**
     * 标准格式：yyyy-MM-dd HH:mm:ss
     *
     * @param date 日期
     */
    public static String format(Date date) {
        return format(date, DatePatterns.DATETIME);
    }

    /**
     * 获取指定 Date 对象的日期 yyyy-MM-dd
     */
    public static String formatToday(Date date) {
        return format(date, DatePatterns.DATE);
    }

    /**
     * 当前时间 HH:mm:ss
     */
    public static String formatTime() {
        return format(now(), DatePatterns.TIME);
    }

    /**
     * 自定义格式
     *
     * @param date 日期
     * @param pattern 格式化模式
     */
    public static String format(Date date, String pattern) {
        return DateFormatter.format(DEFAULT_CONTEXT, date, pattern);
    }

    /**
     * 毫秒时间戳 -> 标准时间字符串
     *
     * <p> 格式：yyyy-MM-dd HH:mm:ss </p>
     *
     * @param millis 毫秒时间戳
     */
    public static String formatMillis(long millis) {
        return format(new Date(millis));
    }

    /**
     * 毫秒时间戳 -> 指定格式字符串
     *
     * @param millis 毫秒时间戳
     * @param pattern 格式化模式
     */
    public static String formatMillis(long millis, String pattern) {
        return format(new Date(millis), pattern);
    }

    /**
     * 秒级时间戳 -> 标准时间字符串
     *
     * <p> 格式：yyyy-MM-dd HH:mm:ss </p>
     *
     * @param seconds 秒级时间戳
     */
    public static String formatSeconds(long seconds) {
        return formatMillis(seconds * 1000L);
    }

    /**
     * 秒级时间戳 -> 指定格式字符串
     *
     * @param seconds 秒级时间戳
     * @param pattern 格式化模式
     */
    public static String formatSeconds(long seconds, String pattern) {
        return formatMillis(seconds * 1000L, pattern);
    }

    /**
     * 智能时间戳格式化（自动识别秒/毫秒）
     *
     * @param timestamp 时间戳
     */
    public static String formatTimestamp(long timestamp) {
        // 10位 -> 秒，13位 -> 毫秒
        if (String.valueOf(timestamp).length() == 10) {
            return formatSeconds(timestamp);
        }
        return formatMillis(timestamp);
    }

    /**
     * 标准时间字符串 -> 毫秒时间戳
     *
     * <p> 格式：yyyy-MM-dd HH:mm:ss </p>
     *
     * @param datetime 日期时间字符串
     */
    public static long toMillis(String datetime) {
        Date date = parse(datetime);
        return date != null ? date.getTime() : 0L;
    }

    /**
     * 指定格式字符串 -> 毫秒时间戳
     *
     * @param datetime 日期时间字符串
     * @param pattern 格式化模式
     */
    public static long toMillis(String datetime, String pattern) {
        Date date = parse(datetime, pattern);
        return date != null ? date.getTime() : 0L;
    }

    /**
     * 标准时间字符串 -> 秒级时间戳
     *
     * <p> 格式：yyyy-MM-dd HH:mm:ss </p>
     *
     * @param datetime 日期时间字符串
     */
    public static long toSeconds(String datetime) {
        return toMillis(datetime) / 1000L;
    }

    /**
     * 指定格式字符串 -> 秒级时间戳
     *
     * @param datetime 日期时间字符串
     * @param pattern 格式化模式
     */
    public static long toSeconds(String datetime, String pattern) {
        return toMillis(datetime, pattern) / 1000L;
    }

    /**
     * Date -> 毫秒时间戳
     *
     * @param date 日期
     */
    public static long toMillis(Date date) {
        return date != null ? date.getTime() : 0L;
    }

    /**
     * Date -> 秒级时间戳
     *
     * @param date 日期
     */
    public static long toSeconds(Date date) {
        return date != null ? date.getTime() / 1000L : 0L;
    }

    /**
     * 毫秒时间戳 -> Date
     *
     * @param millis 毫秒时间戳
     */
    public static Date fromMillis(long millis) {
        return new Date(millis);
    }

    /**
     * 秒级时间戳 -> Date
     *
     * @param seconds 秒级时间戳
     */
    public static Date fromSeconds(long seconds) {
        return new Date(seconds * 1000L);
    }

    /**
     * 默认解析 yyyy-MM-dd HH:mm:ss
     *
     * @param datetime 日期时间字符串
     */
    public static Date parse(String datetime) {
        return parse(datetime, DatePatterns.DATETIME);
    }

    /**
     * 指定格式解析
     *
     * @param datetime 日期时间字符串
     * @param pattern 格式化模式
     */
    public static Date parse(String datetime, String pattern) {
        return DateFormatter.parse(DEFAULT_CONTEXT, datetime, pattern);
    }

    /**
     * 智能解析
     *
     * @param datetime 日期时间字符串
     */
    public static Date smartParse(String datetime) {
        return DateFormatter.smartParse(DEFAULT_CONTEXT, datetime);
    }

    /**
     * 智能解析 Plus
     *
     * @param datetime 日期时间字符串
     */
    public static Date smartParsePlus(String datetime) {
        return DateFormatter.smartParsePlus(DEFAULT_CONTEXT, datetime);
    }

    /**
     * 多格式解析
     *
     * @param text 时间字符串
     * @param patterns 格式列表
     */
    public static Date parseMultiple(String text, String... patterns) {
        return DateFormatter.tryParseMultiple(DEFAULT_CONTEXT, text, patterns);
    }

    /**
     * 多格式解析（增强版）
     */
    public static Date parseMultiplePlus(String text, String... patterns) {
        return DateFormatter.tryParseMultiplePlus(DEFAULT_CONTEXT, text, patterns);
    }

    /**
     * 在当天日期上增加天数
     *
     * @param days 天数
     */
    public static Date plusDays(int days) {
        return DateTime.withDefault().plusDays(days).toDate();
    }

    /**
     * 在当天日期上减少指定天数
     *
     * @param days 天数
     */
    public static Date minusDays(int days) {
        return DateTime.withDefault().minusDays(days).toDate();
    }

    /**
     * 在当前时间基础上增加指定月份数
     *
     * @param months 月份
     */
    public static Date plusMonths(int months) {
        return DateTime.withDefault().plusMonths(months).toDate();
    }

    /**
     * 在当前时间基础上减少指定月份
     *
     * @param months 月份
     */
    public static Date minusMonths(int months) {
        return DateTime.withDefault().minusMonths(months).toDate();
    }

    /**
     * 获取指定 Date 对象的某日开始时间
     *
     * @param date 指定时间
     */
    public static Date startOfDay(Date date) {
        return DateTime.from(date).startOfDay();
    }

    /**
     * 获取指定 Date 对象的某日结束时间
     *
     * @param date 指定时间
     */
    public static Date endOfDay(Date date) {
        return DateTime.from(date).endOfDay();
    }

    /**
     * 获取当天时间区间对象
     */
    public static DateTimeRange todayRange() {
        DateTime dt = DateTime.withDefault();
        return new DateTimeRange(dt.startOfDay(), dt.endOfDay());
    }

    /**
     * 两天相差天数
     *
     * @param start 开始时间
     * @param end 结束时间
     */
    public static int diffDays(Date start, Date end) {
        return new DateTimeRange(start, end).daysBetween();
    }

    /**
     * 判断指定时间是否为当天内（包含边界）
     *
     * @param date 指定时间
     */
    public static boolean isToday(Date date) {
        if (date == null) {
            return false;
        }
        DateTimeRange range = todayRange();
        return range.contains(date);
    }

    /**
     * 判断是否为昨天
     *
     * @param date 指定时间
     */
    public static boolean isYesterday(Date date) {
        if (date == null) {
            return false;
        }
        Date yesterdayStart = DateTime.withDefault().plusDays(-1).startOfDay();
        Date yesterdayEnd = DateTime.withDefault().plusDays(-1).endOfDay();
        return !date.before(yesterdayStart) && !date.after(yesterdayEnd);
    }

    /**
     * 判断是否为明天
     *
     * @param date 指定时间
     */
    public static boolean isTomorrow(Date date) {
        if (date == null) {
            return false;
        }
        Date tomorrowStart = DateTime.withDefault().plusDays(1).startOfDay();
        Date tomorrowEnd = DateTime.withDefault().plusDays(1).endOfDay();
        return !date.before(tomorrowStart) && !date.after(tomorrowEnd);
    }

    /**
     * 判断两个时间是否为同一天（忽略时分秒）
     */
    public static boolean isSameDay(Date d1, Date d2) {
        if (d1 == null || d2 == null) {
            return false;
        }
        return startOfDay(d1).equals(startOfDay(d2));
    }

    /**
     * 判断是否为同一月
     */
    public static boolean isSameMonth(Date d1, Date d2) {
        if (d1 == null || d2 == null) {
            return false;
        }
        DateTime t1 = DateTime.from(d1);
        DateTime t2 = DateTime.from(d2);
        return t1.getYear() == t2.getYear() && t1.getMonth() == t2.getMonth();
    }

    /**
     * 获取某月的开始时间
     */
    public static Date startOfMonth(Date date) {
        if (date == null) {
            return null;
        }
        return DateTime.from(date).startOfMonth().toDate();
    }

    /**
     * 获取某月的结束时间
     */
    public static Date endOfMonth(Date date) {
        if (date == null) {
            return null;
        }
        return DateTime.from(date).endOfMonth().toDate();
    }
}