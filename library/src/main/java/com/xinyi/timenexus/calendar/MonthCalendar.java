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
     * 设置选中日期，并重建天数列表
     *
     * @param date 日期
     */
    public MonthCalendar setSelectedDate(Date date) {
        if (date != null) {
            dateTime.setDate(date);
            clampYearToRange();
            rebuildGrid();
        }
        return this;
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
     */
    public MonthCalendar setYearRange(int minYear, int maxYear) {
        if (minYear > maxYear) {
            throw new IllegalArgumentException("minYear 不能大于 maxYear");
        }
        this.minYear = minYear;
        this.maxYear = maxYear;
        clampYearToRange();
        rebuildGrid();
        return this;
    }

    /**
     * 设置一周起始日，并重建天数列表
     *
     * @param firstDayOfWeek Calendar.SUNDAY 或 Calendar.MONDAY
     */
    public MonthCalendar setFirstDayOfWeek(int firstDayOfWeek) {
        this.firstDayOfWeek = firstDayOfWeek;
        rebuildGrid();
        return this;
    }

    /**
     * 设置年份，并重建天数列表
     *
     * <p> 会自动限制在 minYear ~ maxYear 范围内 </p>
     *
     * @param year 年份
     */
    public MonthCalendar setYear(int year) {
        dateTime.setYear(clampYear(year));
        rebuildGrid();
        return this;
    }

    /**
     * 设置月份（1-12），并重建天数列表
     *
     * @param month 月份（1-12）
     */
    public MonthCalendar setMonth(int month) {
        dateTime.setMonth(month);
        rebuildGrid();
        return this;
    }

    /**
     * 切换到上一年
     */
    public MonthCalendar prevYear() {
        if (canPrevYear()) {
            dateTime.minusYears(1);
            rebuildGrid();
        }
        return this;
    }

    /**
     * 切换到下一年
     */
    public MonthCalendar nextYear() {
        if (canNextYear()) {
            dateTime.plusYears(1);
            rebuildGrid();
        }
        return this;
    }

    /**
     * 切换到上一月
     */
    public MonthCalendar prevMonth() {
        if (canPrevMonth()) {
            dateTime.minusMonths(1);
            rebuildGrid();
        }
        return this;
    }

    /**
     * 切换到下一月
     */
    public MonthCalendar nextMonth() {
        if (canNextMonth()) {
            dateTime.plusMonths(1);
            rebuildGrid();
        }
        return this;
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
}