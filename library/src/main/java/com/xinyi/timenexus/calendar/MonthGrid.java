package com.xinyi.timenexus.calendar;

import androidx.annotation.NonNull;

import com.xinyi.timenexus.DateTimeNexus;
import com.xinyi.timenexus.core.TimeContext;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * 月度日历网格构建器
 *
 * <p> 按指定基准日期生成完整月视图数据，能力包括：</p>
 * <ul>
 *   <li> 固定 6×7 = 42 格，自动补齐上月 / 下月日期 </li>
 *   <li> 可配置一周起始（周日或周一） </li>
 *   <li> 可先创建空实例，再按需 {@link #build(Date)} 生成或刷新数据 </li>
 *   <li> 也可通过 {@link #of(Date)} 一次性创建并完成构建 </li>
 * </ul>
 *
 * @author 新一
 * @date 2026/3/31 11:15
 */
public class MonthGrid {

    /**
     * 创建空的月历网格实例（默认上下文、周一开始）
     */
    public static MonthGrid create() {
        return create(DateTimeNexus.getContext(), Calendar.MONDAY);
    }

    /**
     * 创建空的月历网格实例
     *
     * @param context 时间上下文
     * @param firstDayOfWeek 一周起始（Calendar.SUNDAY / Calendar.MONDAY）
     */
    public static MonthGrid create(TimeContext context, int firstDayOfWeek) {
        return new MonthGrid(context, firstDayOfWeek);
    }

    /**
     * 创建月历网格并按指定日期完成数据构建
     *
     * @param date 任意日期
     */
    public static MonthGrid of(Date date) {
        return create().build(date);
    }

    /**
     * 创建月历网格并按指定日期、上下文与一周起始完成数据构建
     *
     * @param date 任意日期
     * @param context 时间上下文
     * @param firstDayOfWeek 一周起始（Calendar.SUNDAY / Calendar.MONDAY）
     */
    public static MonthGrid of(Date date, TimeContext context, int firstDayOfWeek) {
        return create(context, firstDayOfWeek).build(date);
    }

    /**
     * 一周的起始（Calendar.SUNDAY / Calendar.MONDAY）
     */
    private int firstDayOfWeek;

    /**
     * 网格对应的基准日期
     *
     * <p> 未 {@link #build(Date)} 构建时为 null </p>
     */
    private Date currentDate;

    /**
     * 时间上下文
     */
    private final TimeContext context;

    /**
     * 最终的42天数据
     */
    private final List<DayInfo> days = new ArrayList<>(42);

    /**
     * 构造函数
     *
     * @param context 日期时间上下文
     * @param firstDayOfWeek 一周起始（Calendar.SUNDAY / Calendar.MONDAY）
     */
    private MonthGrid(@NonNull TimeContext context, int firstDayOfWeek) {
        this.context = context;
        this.firstDayOfWeek = firstDayOfWeek;
    }

    /**
     * 是否已完成至少一次网格构建
     */
    public boolean isBuilt() {
        return currentDate != null;
    }

    /**
     * 按日期构建网格数据（保留当前一周起始设置）
     *
     * @param date 任意日期
     */
    public MonthGrid build(@NonNull Date date) {
        return build(date, firstDayOfWeek);
    }

    /**
     * 按日期与一周起始构建网格数据
     *
     * @param date 任意日期
     * @param firstDayOfWeek 一周起始（Calendar.SUNDAY / Calendar.MONDAY）
     */
    public MonthGrid build(@NonNull Date date, int firstDayOfWeek) {
        this.currentDate = date;
        this.firstDayOfWeek = firstDayOfWeek;
        days.clear();
        days.addAll(createDays());
        return this;
    }

    /**
     * 获取完整日历数据（固定42个；未构建时返回空列表）
     */
    public List<DayInfo> getDays() {
        return isBuilt() ? days : Collections.emptyList();
    }

    /**
     * 获取当前月份的天数集合
     */
    public List<DayInfo> getCurrentMonthDays() {
        List<DayInfo> result = new ArrayList<>();
        for (DayInfo day : getDays()) {
            if (day.getType() == DayInfo.CURRENT_MONTH) {
                result.add(day);
            }
        }
        return result;
    }

    /**
     * 生成 42 天网格数据
     */
    private List<DayInfo> createDays() {
        List<DayInfo> result = new ArrayList<>(42);

        Calendar cal = context.newCalendar();
        cal.setTime(currentDate);

        // 定位到当月第一天
        cal.set(Calendar.DAY_OF_MONTH, 1);

        int currentMonth = cal.get(Calendar.MONTH);

        // 计算偏移（关键逻辑）
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);

        int offset = computeOffset(dayOfWeek);

        // 回退到网格起点
        cal.add(Calendar.DAY_OF_MONTH, -offset);

        // 填充42天
        for (int i = 0; i < 42; i++) {
            DayInfo info = createDayInfo(cal, currentMonth);

            result.add(info);

            cal.add(Calendar.DAY_OF_MONTH, 1);
        }
        return result;
    }

    /**
     * 计算起始偏移（适配周一 / 周日开头）
     *
     * @param dayOfWeek 周几（1-7）
     */
    private int computeOffset(int dayOfWeek) {
        if (firstDayOfWeek == Calendar.MONDAY) {
            // 转换：让周一 = 1
            int normalized = (dayOfWeek == Calendar.SUNDAY) ? 7 : dayOfWeek - 1;
            return normalized - 1;
        } else {
            // 默认周日为第一天
            return dayOfWeek - 1;
        }
    }

    /**
     * 创建 DayInfo
     *
     * @param cal 当前日历
     * @param currentMonth 当前月份
     */
    public static DayInfo createDayInfo(Calendar cal, int currentMonth) {
        DayInfo info = new DayInfo();

        Date date = cal.getTime();

        info.setDate(date);
        info.setDay(cal.get(Calendar.DAY_OF_MONTH));
        info.setWeek(cal.get(Calendar.DAY_OF_WEEK));

        // 判断归属月份
        int month = cal.get(Calendar.MONTH);
        if (month < currentMonth) {
            info.setType(DayInfo.PREV_MONTH);
        } else if (month > currentMonth) {
            info.setType(DayInfo.NEXT_MONTH);
        } else {
            info.setType(DayInfo.CURRENT_MONTH);
        }
        return info;
    }

    /**
     * 获取指定位置的 DayInfo 对象
     *
     * @param index 0~41
     */
    public DayInfo get(int index) {
        return getDays().get(index);
    }

    /**
     * 获取某一周数据（0~5）
     *
     * @param weekIndex 0~5，0 是第一周
     */
    public List<DayInfo> getWeek(int weekIndex) {
        int start = weekIndex * 7;
        return getDays().subList(start, start + 7);
    }

    /**
     * 是否包含某一天
     *
     * @param date 指定日期
     */
    public boolean contains(Date date) {
        if (date == null) {
            return false;
        }
        for (DayInfo day : getDays()) {
            if (day.getDate().equals(date)) {
                return true;
            }
        }
        return false;
    }
}