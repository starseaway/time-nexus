package com.xinyi.timenexus.core;

import com.xinyi.timenexus.DateTimeNexus;
import com.xinyi.timenexus.consts.DatePatterns;
import com.xinyi.timenexus.enums.WeekDay;
import com.xinyi.timenexus.util.DateFormatter;

import java.util.Calendar;
import java.util.Date;

/**
 * 核心时间操作类，支持链式调用
 *
 * <p> 注意事项：</p>
 * <ul>
 *   <li> 该类是 “可变对象”，链式调用会直接修改内部 Calendar </li>
 *   <li> 如需线程安全，请避免跨线程共享实例 </li>
 * </ul>
 *
 * @author 新一
 * @date 2026/3/31 10:32
 */
public class DateTime {

    /**
     * 时间上下文
     */
    private final TimeContext context;

    /**
     * 内部时间对象
     */
    private final Calendar calendar;

    /**
     * 构造函数
     *
     * @param context 时间上下文
     */
    private DateTime(TimeContext context) {
        this.context = context;
        this.calendar = context.newCalendar();
    }

    /**
     * 使用全局的时间上下文创建
     */
    public static DateTime with() {
        return new DateTime(DateTimeNexus.getContext());
    }

    /**
     * 使用指定上下文创建 DateTime
     *
     * @param context 时间上下文
     */
    public static DateTime with(TimeContext context) {
        return new DateTime(context);
    }

    /**
     * 从指定 Date 创建 DateTime
     *
     * @param date Date 对象
     */
    public static DateTime from(Date date) {
        return with().setDate(date);
    }

     /**
     * 从毫秒时间戳创建 DateTime
     *
     * @param millis 毫秒时间戳
     */
    public static DateTime fromMillis(long millis) {
        return with().setTimeInMillis(millis);
    }

    /**
     * 从秒级时间戳创建 DateTime
     *
     * @param seconds 秒级时间戳
     */
    public static DateTime fromSeconds(long seconds) {
        return fromMillis(seconds * 1000L);
    }

    /**
     * 获取时间上下文
     */
    public TimeContext getContext() {
        return context;
    }

    /**
     * 设置当前内部时间
     *
     * @param date Date 对象
     */
    public DateTime setDate(Date date) {
        if (date != null) {
            calendar.setTime(date);
        }
        return this;
    }

    /**
     * 设置当前内部时间
     *
     * @param millis 毫秒时间戳
     */
    public DateTime setTimeInMillis(long millis) {
        calendar.setTimeInMillis(millis);
        return this;
    }

    /**
     * 按指定格式格式化当前时间
     *
     * @param pattern 时间格式
     * @return 格式化后的时间字符串
     */
    public String format(String pattern) {
        return DateFormatter.format(context, calendar.getTime(), pattern);
    }

    /**
     * 在当前时间基础上增加指定天数
     *
     * @param days 天数（可为负数）
     */
    public DateTime plusDays(int days) {
        calendar.add(Calendar.DAY_OF_MONTH, days);
        return this;
    }

    /**
     * 减少指定天数
     *
     * @param days 天数（可为负数）
     */
    public DateTime minusDays(int days) {
        return plusDays(-days);
    }

    /**
     * 增加指定月份数
     *
     * @param months 月份数
     */
    public DateTime plusMonths(int months) {
        calendar.add(Calendar.MONTH, months);
        return this;
    }

    /**
     * 在当前时间基础上减少指定月份
     *
     * @param months 月份数
     */
    public DateTime minusMonths(int months) {
        calendar.add(Calendar.MONTH, -months);
        return this;
    }

    /**
     * 增加指定年数
     *
     * @param years 年数
     */
    public DateTime plusYears(int years) {
        calendar.add(Calendar.YEAR, years);
        return this;
    }

    /**
     * 减少指定年数
     *
     * @param years 年数
     */
    public DateTime minusYears(int years) {
        return plusYears(-years);
    }

    /**
     * 获取当天开始时间（00:00:00.000）
     *
     * @return Date 对象
     */
    public Date startOfDay() {
        Calendar cal = (Calendar) calendar.clone();
        resetToStartOfDay(cal);
        return cal.getTime();
    }

    /**
     * 获取当天结束时间（23:59:59.999）
     *
     * @return Date 对象
     */
    public Date endOfDay() {
        Calendar cal = (Calendar) calendar.clone();
        resetToEndOfDay(cal);
        return cal.getTime();
    }

    /**
     * 设置到当月起始时间（当月第一天 00:00:00.000）
     *
     * @return DateTime 对象
     */
    public DateTime startOfMonth() {
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        resetToStartOfDay(calendar);
        return this;
    }

    /**
     * 设置到当月结束时间（当月最后一天 23:59:59.999）
     *
     * @return DateTime 对象
     */
    public DateTime endOfMonth() {
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        resetToEndOfDay(calendar);
        return this;
    }

    /**
     * 获取当前时间的 Date 对象
     */
    public Date toDate() {
        return calendar.getTime();
    }

    /**
     * 获取当前时间戳（毫秒）
     */
    public long toMillis() {
        return calendar.getTimeInMillis();
    }

    /**
     * 获取当前时间戳（秒）
     */
    public long toSeconds() {
        return calendar.getTimeInMillis() / 1000L;
    }

    /**
     * 复制当前时间点为新的 DateTime 实例
     */
    public DateTime copy() {
        return with(context).setTimeInMillis(calendar.getTimeInMillis());
    }

    /**
     * 获取年份
     */
    public int getYear() {
        return calendar.get(Calendar.YEAR);
    }

    /**
     * 获取月份（1-12）
     */
    public int getMonth() {
        return calendar.get(Calendar.MONTH) + 1;
    }

    /**
     * 获取日期（1-31）
     */
    public int getDayOfMonth() {
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 设置年份
     *
     * @param year 年份
     */
    public DateTime setYear(int year) {
        calendar.set(Calendar.YEAR, year);
        return this;
    }

    /**
     * 设置月份（1-12）
     *
     * @param month 月份（1-12）
     */
    public DateTime setMonth(int month) {
        calendar.set(Calendar.MONTH, month - 1);
        return this;
    }

    /**
     * 设置日期（1-31）
     *
     * @param day 日期（1-31）
     */
    public DateTime setDayOfMonth(int day) {
        calendar.set(Calendar.DAY_OF_MONTH, day);
        return this;
    }

    /**
     * 获取小时（0-23）
     */
    public int getHour() {
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    /**
     * 获取分钟（0-59）
     */
    public int getMinute() {
        return calendar.get(Calendar.MINUTE);
    }

    /**
     * 获取秒（0-59）
     */
    public int getSecond() {
        return calendar.get(Calendar.SECOND);
    }

    /**
     * 获取毫秒（0-999）
     */
    public int getMillisecond() {
        return calendar.get(Calendar.MILLISECOND);
    }

    /**
     * 设置小时（0-23）
     *
     * @param hour 小时（0-23）
     */
    public DateTime setHour(int hour) {
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        return this;
    }

    /**
     * 设置分钟（0-59）
     *
     * @param minute 分钟（0-59）
     */
    public DateTime setMinute(int minute) {
        calendar.set(Calendar.MINUTE, minute);
        return this;
    }

    /**
     * 设置秒（0-59）
     *
     * @param second 秒（0-59）
     */
    public DateTime setSecond(int second) {
        calendar.set(Calendar.SECOND, second);
        return this;
    }

    /**
     * 设置毫秒（0-999）
     *
     * @param millisecond 毫秒（0-999）
     */
    public DateTime setMillisecond(int millisecond) {
        calendar.set(Calendar.MILLISECOND, millisecond);
        return this;
    }

    /**
     * 获取星期（1-7，按 Calendar 的定义）
     */
    public int getDayOfWeek() {
        return calendar.get(Calendar.DAY_OF_WEEK);
    }

    /**
     * 获取当前是星期几（枚举）
     *
     * <p> 推荐使用该方法作为基础语义判断 </p>
     *
     * @return {@link WeekDay}
     */
    public WeekDay getWeekDay() {
        return WeekDay.from(calendar.get(Calendar.DAY_OF_WEEK));
    }

    /**
     * 获取星期名称
     *
     * <p> 格式：EEEE，示例：</p>
     * <ul>
     *   <li> zh_CN：星期一 </li>
     *   <li> en_US：Monday </li>
     * </ul>
     *
     * @return 本地化星期名称
     */
    public String getWeekDayName() {
        return DateFormatter.format(
                context,
                calendar.getTime(),
                DatePatterns.WEEK_FULL_EN
        );
    }

    /**
     * 获取星期名称（简写）
     *
     * <p> 格式：EEE，示例：</p>
     * <ul>
     *   <li> zh_CN → 周一 </li>
     *   <li> en_US → Mon </li>
     * </ul>
     *
     * @return 本地化简写星期
     */
    public String getWeekDayShortName() {
        return DateFormatter.format(
                context,
                calendar.getTime(),
                DatePatterns.WEEK_SHORT_EN
        );
    }

    /**
     * 判断是否为周末
     *
     * <p> 周六 / 周日 返回 true </p>
     */
    public boolean isWeekend() {
        WeekDay day = getWeekDay();
        return day == WeekDay.SATURDAY || day == WeekDay.SUNDAY;
    }

    /**
     * 判断是否为工作日
     *
     * <p> 周一 ~ 周五 返回 true </p>
     */
    public boolean isWeekday() {
        return !isWeekend();
    }

    /**
     * 获取内部 Calendar 的副本，避免外部直接修改内部状态
     */
    public Calendar toCalendar() {
        return (Calendar) calendar.clone();
    }

    /**
     * 重置为当天开始时间
     *
     * @param cal Calendar 对象
     */
    private void resetToStartOfDay(Calendar cal) {
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
    }

    /**
     * 重置为当天结束时间
     *
     * @param cal Calendar 对象
     */
    private void resetToEndOfDay(Calendar cal) {
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
    }
}