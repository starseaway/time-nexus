package com.xinyi.timenexus.util;

import com.xinyi.timenexus.consts.DatePatterns;
import com.xinyi.timenexus.core.TimeContext;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 日期格式化器（线程安全）
 *
 * <p> 提供统一的时间格式化与解析能力：</p>
 * <ul>
 *   <li> Date / Calendar -> String </li>
 *   <li> String -> Date / Calendar </li>
 *   <li> 时间戳（秒 / 毫秒）转换 </li>
 * </ul>
 *
 * @author 新一
 * @date 2026/3/31 13:50
 */
public final class DateFormatter {

    /**
     * 默认格式
     */
    private static final String DEFAULT_PATTERN = DatePatterns.DATETIME;

    /**
     * 格式化器缓存（线程隔离）
     *
     * <p> key 组成：pattern + locale + timezone </p>
     */
    private static final ThreadLocal<Map<String, SimpleDateFormat>> CACHE = new ThreadLocal<Map<String, SimpleDateFormat>>() {

        @Override
        protected Map<String, SimpleDateFormat> initialValue() {
            return new ConcurrentHashMap<>();
        }
    };

    private DateFormatter() { }

    /**
     * 格式化 Date（使用默认格式）
     *
     * @param context 日期时间上下文
     * @param date 日期对象
     */
    public static String format(TimeContext context, Date date) {
        return format(context, date, DEFAULT_PATTERN);
    }

    /**
     * 格式化 Date
     *
     * @param context 日期时间上下文
     * @param date 日期对象
     * @param pattern 格式
     */
    public static String format(TimeContext context, Date date, String pattern) {
        if (date == null) {
            return null;
        }
        if (isEmpty(pattern)) {
            pattern = DEFAULT_PATTERN;
        }

        SimpleDateFormat sdf = obtain(context, pattern);
        return sdf.format(date);
    }

    /**
     * 格式化 Calendar
     *
     * @param context 日期时间上下文
     * @param calendar 日历对象
     * @param pattern 格式
     */
    public static String format(TimeContext context, Calendar calendar, String pattern) {
        if (calendar == null) {
            return null;
        }
        return format(context, calendar.getTime(), pattern);
    }

    /**
     * 格式化时间戳
     *
     * @param context 日期时间上下文
     * @param timestamp 时间戳
     * @param isSeconds 是否为秒级
     * @param pattern 格式
     */
    public static String formatTimestamp(TimeContext context, long timestamp, boolean isSeconds, String pattern) {
        long millis = isSeconds ? timestamp * 1000L : timestamp;
        return format(context, new Date(millis), pattern);
    }

    /**
     * 当前时间格式化
     *
     * @param context 日期时间上下文
     */
    public static String now(TimeContext context) {
        return format(context, new Date(), DEFAULT_PATTERN);
    }

    /**
     * 当前时间格式化（指定格式）
     *
     * @param context 日期时间上下文
     * @param pattern 格式
     */
    public static String now(TimeContext context, String pattern) {
        return format(context, new Date(), pattern);
    }

    /**
     * 解析字符串为 Date（默认格式）
     *
     * @param context 日期时间上下文
     * @param datetimeStr 时间字符串
     */
    public static Date parse(TimeContext context, String datetimeStr) {
        return parse(context, datetimeStr, DEFAULT_PATTERN);
    }

    /**
     * 解析字符串为 Date
     *
     * @param context 日期时间上下文
     * @param datetimeStr 时间字符串
     * @param pattern 格式
     */
    public static Date parse(TimeContext context, String datetimeStr, String pattern) {
        if (isEmpty(datetimeStr)) {
            return null;
        }
        if (isEmpty(pattern)) {
            pattern = DEFAULT_PATTERN;
        }

        try {
            SimpleDateFormat sdf = obtain(context, pattern);
            return sdf.parse(datetimeStr);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 解析为 Calendar
     *
     * @param context 日期时间上下文
     * @param datetimeStr 时间字符串
     * @param pattern 格式
     */
    public static Calendar parseToCalendar(TimeContext context, String datetimeStr, String pattern) {
        Date date = parse(context, datetimeStr, pattern);
        if (date == null) {
            return null;
        }

        Calendar cal = context.newCalendar();
        cal.setTime(date);
        return cal;
    }

    /**
     * 智能解析（自动识别格式）
     *
     * <p> 支持常见格式自动判断 </p>
     *
     * @param context 日期时间上下文
     * @param datetimeStr 时间字符串
     */
    public static Date smartParse(TimeContext context, String datetimeStr) {
        if (isEmpty(datetimeStr)) {
            return null;
        }
        String text = datetimeStr.trim();
        // ===== ISO8601 =====
        if (text.contains("T")) {
            Date date;
            // 带时区
            if (text.contains("+") || text.endsWith("Z")) {
                date = parse(context, text, DatePatterns.ISO8601_TZ);
                if (date != null) return date;
            }
            // 带毫秒
            if (text.contains(".")) {
                date = parse(context, text, DatePatterns.ISO8601_MS);
                if (date != null) return date;
            }
            return parse(context, text, DatePatterns.ISO8601);
        }
        // ===== 普通格式 =====
        if (text.contains(":")) {
            if (text.contains("/")) {
                return parse(context, text, DatePatterns.DATETIME_SLASH);
            }
            return parse(context, text, DatePatterns.DATETIME);
        }
        if (text.contains("/")) {
            return parse(context, text, DatePatterns.DATE_SLASH);
        }
        return parse(context, text, DatePatterns.DATE);
    }

    /**
     * 智能解析（增强版）
     *
     * <p> 支持：紧凑格式、时间戳（秒 / 毫秒） </p>
     *
     * @param context 日期时间上下文
     * @param text 时间字符串
     */
    public static Date smartParsePlus(TimeContext context, String text) {
        if (isEmpty(text)) {
            return null;
        }

        text = text.trim();

        // 纯数字：时间戳 or 紧凑格式
        if (isDigits(text)) {
            long value;
            try {
                value = Long.parseLong(text);
            } catch (Exception exception) {
                return null;
            }
            int len = text.length();
            // yyyyMMdd
            if (len == 8) {
                Date d = parse(context, text, DatePatterns.DATE_COMPACT);
                if (d != null) return d;
            }
            // yyyyMMddHHmmss
            if (len == 14) {
                Date d = parse(context, text, DatePatterns.DATETIME_COMPACT);
                if (d != null) return d;
            }
            // ===== 时间戳（智能判断）=====
            if (value > 1_000_000_000_000L) {
                return new Date(value); // 毫秒
            } else {
                return new Date(value * 1000L); // 秒
            }
        }
        return smartParse(context, text);
    }

    /**
     * 判断是否为纯数字
     *
     * @param text 字符串
     */
    public static boolean isDigits(String text) {
        if (text == null || text.isEmpty()) {
            return false;
        }
        for (int i = 0; i < text.length(); i++) {
            if (!Character.isDigit(text.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 多格式尝试解析（按顺序匹配）
     *
     * <p> 解析策略：</p>
     * <ol>
     *   <li> 依次使用 patterns 尝试解析 </li>
     *   <li> 任一成功立即返回 </li>
     *   <li> 全部失败返回 null（不抛异常）</li>
     * </ol>
     *
     * @param context 时间上下文
     * @param text 时间字符串
     * @param patterns 格式列表（优先级从前到后）
     *
     * @return 解析成功的 Date，否则 null
     */
    public static Date tryParseMultiple(TimeContext context, String text, String... patterns) {
        if (isEmpty(text) || patterns == null || patterns.length == 0) {
            return null;
        }
        text = text.trim();
        for (String pattern : patterns) {
            if (isEmpty(pattern)) {
                continue;
            }
            try {
                Date date = parse(context, text, pattern);
                if (date != null) {
                    return date;
                }
            } catch (Exception ignore) { /* 忽略异常 */}
        }
        return null;
    }

    /**
     * 多格式尝试解析（增强版）
     *
     * <p> 优先使用智能解析（smartParsePlus），失败后再按 patterns 尝试 </p>
     *
     * @param context 时间上下文
     * @param text 时间字符串
     * @param patterns 备用格式列表
     */
    public static Date tryParseMultiplePlus(TimeContext context, String text, String... patterns) {
        if (isEmpty(text)) {
            return null;
        }
        // 智能解析优先
        Date result = smartParsePlus(context, text);
        if (result != null) {
            return result;
        }
        // fallback 多格式
        return tryParseMultiple(context, text, patterns);
    }

    /**
     * 获取格式化器（带缓存）
     *
     * @param context 日期时间上下文
     * @param pattern 格式
     */
    private static SimpleDateFormat obtain(TimeContext context, String pattern) {
        Map<String, SimpleDateFormat> map = CACHE.get();
        // 如果 map 为空，创建新的并设置回 ThreadLocal
        if (map == null) {
            map = new ConcurrentHashMap<>();
            CACHE.set(map);
        }

        TimeContext safeContext = safeContext(context);
        Locale locale = safeContext.getLocale();
        TimeZone timeZone = safeContext.getTimeZone();
        String key = buildKey(pattern, locale, timeZone);

        SimpleDateFormat sdf = map.get(key);
        if (sdf == null) {
            sdf = new SimpleDateFormat(pattern, locale);
            sdf.setTimeZone(timeZone);
            map.put(key, sdf);
        }

        return sdf;
    }

    /**
     * 构建缓存 key
     *
     * <p> key 组成：pattern + locale + timezone </p>
     *
     * @param pattern 格式
     * @param locale Locale
     * @param timeZone 时区
     */
    private static String buildKey(String pattern, Locale locale, TimeZone timeZone) {
        String localeKey = locale.getLanguage() + "_" + locale.getCountry();
        return pattern + "_" + localeKey + "_" + timeZone.getID();
    }

    /**
     * 获取安全上下文（兜底默认值）
     *
     * <p> 避免 null 异常 </p>
     *
     * @param context 上下文
     */
    private static TimeContext safeContext(TimeContext context) {
        return context != null ? context : new TimeContext.Builder().build();
    }

    /**
     * 判空字符串
     *
     * @param text 字符串
     */
    private static boolean isEmpty(String text) {
        return text == null || text.isEmpty();
    }
}