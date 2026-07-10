package com.xinyi.timenexus.calendar;

import com.xinyi.timenexus.core.DateTime;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 月历状态管理，负责日历列表的构建与年月切换
 *
 * <p> 内部维护一个 {@link DateTime} 作为当前选中日期（默认系统时间），
 * 切换年 / 月后通过 {@link MonthGrid#rebuild(Date)} 刷新天数列表。 </p>
 *
 * <p> 性能说明：网格构建仅创建 42 个 {@link DayInfo} 对象并完成简单 Calendar 运算，
 * 耗时通常在微秒级，可在主线程安全调用，无需异步处理。 </p>
 *
 * @author 新一
 * @date 2026/7/9 19:33
 */
public class MonthCalendar {

    /**
     * 默认最小年份
     */
    public static final int DEFAULT_MIN_YEAR = 1900;

    /**
     * 默认最大年份
     */
    public static final int DEFAULT_MAX_YEAR = 2100;

    /**
     * 当前选中日期
     */
    private final DateTime dateTime;

    /**
     * 默认周一起始（Calendar.SUNDAY / Calendar.MONDAY）
     */
    private int firstDayOfWeek = Calendar.MONDAY;

    /**
     * 最小年份
     */
    private int minYear = DEFAULT_MIN_YEAR;

    /**
     * 最大年份
     */
    private int maxYear = DEFAULT_MAX_YEAR;

    /**
     * 当前月历网格数据模型构建器
     */
    private final MonthGrid monthGrid;

    /**
     * 默认使用当前系统时间创建
     */
    public MonthCalendar() {
        this(DateTime.with());
    }

    /**
     * 使用指定日期创建
     *
     * @param date 日期
     */
    public MonthCalendar(Date date) {
        this(DateTime.from(date));
    }

    /**
     * 使用指定 DateTime 创建（内部会复制其时间点，不共享可变实例）
     *
     * @param source 源 DateTime
     */
    public MonthCalendar(@NotNull DateTime source) {
        this.dateTime = source.copy();
        clampYearToRange();
        this.monthGrid = MonthGrid.of(dateTime.toDate(), source.getContext(), firstDayOfWeek);
    }

    /**
     * 获取当前选中日期的副本
     */
    public Date getSelectedDate() {
        return new Date(dateTime.toMillis());
    }

    /**
     * 获取当前选中日期的副本
     */
    public DateTime getSelectedDateTime() {
        return dateTime.copy();
    }

    /**
     * 选择日期，并重建天数列表
     *
     * @param date 日期
     * @return 日期有效且在年份范围内时返回 {@code true}，否则返回 {@code false}
     */
    public boolean selectDate(Date date) {
        if (!isDateInRange(date)) {
            return false;
        }
        dateTime.setDate(date);
        rebuildGrid();
        return true;
    }

    /**
     * 获取完整日历网格（固定 42 天）
     */
    public List<DayInfo> getDays() {
        return monthGrid.getDays();
    }

    /**
     * 获取当前月份的天数列表
     */
    public List<DayInfo> getCurrentMonthDays() {
        return monthGrid.getCurrentMonthDays();
    }

    /**
     * 获取当前月历网格
     */
    public MonthGrid getMonthGrid() {
        return monthGrid;
    }

    /**
     * 获取当前年份
     */
    public int getYear() {
        return dateTime.getYear();
    }

    /**
     * 获取当前月份（1-12）
     */
    public int getMonth() {
        return dateTime.getMonth();
    }

    /**
     * 获取最小年份
     */
    public int getMinYear() {
        return minYear;
    }

    /**
     * 获取最大年份
     */
    public int getMaxYear() {
        return maxYear;
    }

    /**
     * 设置年份范围，并校正当前选中日期
     *
     * @param minYear 最小年份
     * @param maxYear 最大年份
     * @throws IllegalArgumentException minYear 大于 maxYear 时抛出
     */
    public void setYearRange(int minYear, int maxYear) {
        if (!isYearRangeValid(minYear, maxYear)) {
            throw new IllegalArgumentException("minYear 不能大于 maxYear");
        }
        if (this.minYear == minYear && this.maxYear == maxYear) {
            return;
        }
        this.minYear = minYear;
        this.maxYear = maxYear;
        clampYearToRange();
        rebuildGrid();
    }

    /**
     * 设置一周起始日，并重建天数列表
     *
     * @param firstDayOfWeek {@link Calendar#SUNDAY} 或 {@link Calendar#MONDAY}
     * @throws IllegalArgumentException 参数不是 SUNDAY 或 MONDAY 时抛出
     */
    public void setFirstDayOfWeek(int firstDayOfWeek) {
        if (!isFirstDayOfWeekValid(firstDayOfWeek)) {
            throw new IllegalArgumentException("firstDayOfWeek 必须为 Calendar.SUNDAY 或 Calendar.MONDAY");
        }
        if (this.firstDayOfWeek == firstDayOfWeek) {
            return;
        }
        this.firstDayOfWeek = firstDayOfWeek;
        rebuildGrid();
    }

    /**
     * 选择年份，并重建天数列表
     *
     * @param year 年份
     * @return 年份在允许范围内且发生变化时返回 {@code true}，否则返回 {@code false}
     */
    public boolean selectYear(int year) {
        if (!isYearInRange(year) || dateTime.getYear() == year) {
            return false;
        }
        dateTime.setYear(year);
        rebuildGrid();
        return true;
    }

    /**
     * 选择月份（1-12），并重建天数列表
     *
     * @param month 月份（1-12）
     * @return 月份有效且发生变化时返回 {@code true}，否则返回 {@code false}
     */
    public boolean selectMonth(int month) {
        if (!isMonthInRange(month) || dateTime.getMonth() == month) {
            return false;
        }
        dateTime.setMonth(month);
        rebuildGrid();
        return true;
    }

    /**
     * 切换到上一年
     *
     * @return 切换成功返回 {@code true}，已到达最小年份时返回 {@code false}
     */
    public boolean prevYear() {
        if (!canPrevYear()) {
            return false;
        }
        dateTime.minusYears(1);
        rebuildGrid();
        return true;
    }

    /**
     * 切换到下一年
     *
     * @return 切换成功返回 {@code true}，已到达最大年份时返回 {@code false}
     */
    public boolean nextYear() {
        if (!canNextYear()) {
            return false;
        }
        dateTime.plusYears(1);
        rebuildGrid();
        return true;
    }

    /**
     * 切换到上一月
     *
     * @return 切换成功返回 {@code true}，已到达边界时返回 {@code false}
     */
    public boolean prevMonth() {
        if (!canPrevMonth()) {
            return false;
        }
        dateTime.minusMonths(1);
        rebuildGrid();
        return true;
    }

    /**
     * 切换到下一月
     *
     * @return 切换成功返回 {@code true}，已到达边界时返回 {@code false}
     */
    public boolean nextMonth() {
        if (!canNextMonth()) {
            return false;
        }
        dateTime.plusMonths(1);
        rebuildGrid();
        return true;
    }

    /**
     * 是否可以切换到上一年
     */
    public boolean canPrevYear() {
        return getYear() > minYear;
    }

    /**
     * 是否可以切换到下一年
     */
    public boolean canNextYear() {
        return getYear() < maxYear;
    }

    /**
     * 是否可以切换到上一月
     */
    public boolean canPrevMonth() {
        return canPrevYear() || getMonth() > 1;
    }

    /**
     * 是否可以切换到下一月
     */
    public boolean canNextMonth() {
        return canNextYear() || getMonth() < 12;
    }

    /**
     * 刷新月历网格数据
     */
    private void rebuildGrid() {
        monthGrid.rebuild(dateTime.toDate(), firstDayOfWeek);
    }

    /**
     * 将当前年份限制在允许范围内
     */
    private void clampYearToRange() {
        dateTime.setYear(clampYear(getYear()));
    }

    /**
     * 将年份限制在 minYear ~ maxYear 范围内
     */
    private int clampYear(int year) {
        return Math.max(minYear, Math.min(maxYear, year));
    }

    /**
     * 判断年份是否在允许范围内
     */
    private boolean isYearInRange(int year) {
        return year >= minYear && year <= maxYear;
    }

    /**
     * 判断月份是否有效（1-12）
     */
    private boolean isMonthInRange(int month) {
        return month >= 1 && month <= 12;
    }

    /**
     * 判断年份范围参数是否有效
     */
    private boolean isYearRangeValid(int minYear, int maxYear) {
        return minYear <= maxYear;
    }

    /**
     * 判断一周起始日是否有效
     */
    private boolean isFirstDayOfWeekValid(int firstDayOfWeek) {
        return firstDayOfWeek == Calendar.SUNDAY || firstDayOfWeek == Calendar.MONDAY;
    }

    /**
     * 判断日期是否在允许的年份范围内
     */
    private boolean isDateInRange(Date date) {
        if (date == null) {
            return false;
        }
        Calendar cal = dateTime.getContext().newCalendar();
        cal.setTime(date);
        return isYearInRange(cal.get(Calendar.YEAR));
    }
}