package com.xinyi.timenexus.calendar;

import com.xinyi.timenexus.core.DateTime;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 月历状态管理
 *
 * <p> 面向日历面板场景，统一管理：</p>
 * <ul>
 *   <li> 网格生成所用的基准时间（默认系统当前时间） </li>
 *   <li> 可展示的年份范围与一周起始日 </li>
 *   <li> 年月导航、定位，以及固定 42 格天数列表的构建与读取 </li>
 * </ul>
 *
 * <p> API 分层：</p>
 * <ul>
 *   <li> 配置：{@link #setYearRange(int, int)}、{@link #setFirstDayOfWeek(int)} </li>
 *   <li> 导航 / 定位：{@code goToXxx}、{@code nextXxx}、{@code prevXxx}（返回是否生效） </li>
 *   <li> 构建：{@link #build()} 按当前基准时间生成或刷新 {@link MonthGrid} </li>
 *   <li> 读取：{@link #getDays()}、{@link #getCurrentMonthDays()} 等，不产生副作用 </li>
 * </ul>
 *
 * <p> 基准状态变更不会自动构建网格；需要最新天数数据时由调用方调用 {@link #build()} </p>
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
     * 日历网格生成的基准时间
     */
    private final DateTime anchorDateTime;

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
     * 默认使用当前系统时间作为基准时间创建
     */
    public MonthCalendar() {
        this(DateTime.with());
    }

    /**
     * 使用指定日期作为基准时间创建
     *
     * @param date 日期
     */
    public MonthCalendar(Date date) {
        this(DateTime.from(date));
    }

    /**
     * 使用指定 DateTime 作为基准时间创建
     *
     * @param source 源 DateTime
     */
    public MonthCalendar(@NotNull DateTime source) {
        this.anchorDateTime = source.copy();
        clampAnchorYearToRange();
        this.monthGrid = MonthGrid.create(source.getContext(), firstDayOfWeek);
    }

    /**
     * 获取基准日期的副本
     */
    public Date getAnchorDate() {
        return new Date(anchorDateTime.toMillis());
    }

    /**
     * 获取基准时间的副本（新的 DateTime 实例）
     */
    public DateTime getAnchorDateTime() {
        return anchorDateTime.copy();
    }

    /**
     * 按当前基准时间、一周起始日构建或刷新网格数据
     */
    public void build() {
        monthGrid.build(anchorDateTime.toDate(), firstDayOfWeek);
    }

    /**
     * 是否已完成至少一次网格构建
     */
    public boolean isBuilt() {
        return monthGrid.isBuilt();
    }

    /**
     * 将基准时间定位到指定日期
     *
     * @param date 日期
     * @return 日期有效且在年份范围内时返回 {@code true}，否则返回 {@code false}
     */
    public boolean goToDate(Date date) {
        if (!isDateInRange(date)) {
            return false;
        }
        anchorDateTime.setDate(date);
        return true;
    }

    /**
     * 获取完整日历网格（固定 42 天；未构建时为空列表）
     */
    public List<DayInfo> getDays() {
        return monthGrid.getDays();
    }

    /**
     * 获取当前月份的天数列表（未构建时为空列表）
     */
    public List<DayInfo> getCurrentMonthDays() {
        return monthGrid.getCurrentMonthDays();
    }

    /**
     * 获取当前月历网格对象
     */
    public MonthGrid getMonthGrid() {
        return monthGrid;
    }

    /**
     * 获取基准时间对应的年份
     */
    public int getYear() {
        return anchorDateTime.getYear();
    }

    /**
     * 获取基准时间对应的月份（1-12）
     */
    public int getMonth() {
        return anchorDateTime.getMonth();
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
     * 设置可展示的年份范围；若当前基准年份越界会自动校正
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
        clampAnchorYearToRange();
    }

    /**
     * 设置一周起始日
     *
     * @param firstDayOfWeek {@link Calendar#SUNDAY} 或 {@link Calendar#MONDAY}
     * @throws IllegalArgumentException 参数不是 SUNDAY 或 MONDAY 时抛出
     */
    public void setFirstDayOfWeek(int firstDayOfWeek) {
        if (!isFirstDayOfWeekValid(firstDayOfWeek)) {
            throw new IllegalArgumentException("firstDayOfWeek 必须为 Calendar.SUNDAY 或 Calendar.MONDAY");
        }
        this.firstDayOfWeek = firstDayOfWeek;
    }

    /**
     * 将基准时间定位到指定年份
     *
     * @param year 年份
     * @return 年份在允许范围内且发生变化时返回 {@code true}，否则返回 {@code false}
     */
    public boolean goToYear(int year) {
        if (!isYearInRange(year) || anchorDateTime.getYear() == year) {
            return false;
        }
        anchorDateTime.setYear(year);
        return true;
    }

    /**
     * 将基准时间定位到指定月份（1-12）
     *
     * @param month 月份（1-12）
     * @return 月份有效且发生变化时返回 {@code true}，否则返回 {@code false}
     */
    public boolean goToMonth(int month) {
        if (!isMonthInRange(month) || anchorDateTime.getMonth() == month) {
            return false;
        }
        anchorDateTime.setMonth(month);
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
        anchorDateTime.minusYears(1);
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
        anchorDateTime.plusYears(1);
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
        anchorDateTime.minusMonths(1);
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
        anchorDateTime.plusMonths(1);
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
     * 将基准时间的年份限制在允许范围内
     */
    private void clampAnchorYearToRange() {
        anchorDateTime.setYear(clampYear(getYear()));
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
        Calendar cal = anchorDateTime.getContext().newCalendar();
        cal.setTime(date);
        return isYearInRange(cal.get(Calendar.YEAR));
    }
}