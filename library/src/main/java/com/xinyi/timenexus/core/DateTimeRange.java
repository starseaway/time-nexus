package com.xinyi.timenexus.core;

import com.xinyi.timenexus.DateTimeNexus;
import com.xinyi.timenexus.calendar.DayInfo;
import com.xinyi.timenexus.calendar.MonthGrid;
import com.xinyi.timenexus.enums.Position;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 时间区间模型
 *
 * <p> 用于表示一个闭区间 [start, end] 的时间范围，支持区间计算能力（包含、重叠等） </p>
 *
 * @author 新一
 * @date 2026/3/31 11:00
 */
public class DateTimeRange {

    /**
     * 起始时间
     */
    private final Date start;

    /**
     * 结束时间
     */
    private final Date end;

    /**
     * 构造函数
     *
     * @param start 开始时间
     * @param end 结束时间
     */
    public DateTimeRange(Date start, Date end) {
        if (start == null || end == null) {
            throw new IllegalArgumentException("start 或 end 不能为空");
        }
        if (start.after(end)) {
            throw new IllegalArgumentException("start 不能晚于 end");
        }
        this.start = start;
        this.end = end;
    }

    /**
     * 获取开始时间
     */
    public Date getStart() {
        return start;
    }

    /**
     * 获取结束时间
     */
    public Date getEnd() {
        return end;
    }

    /**
     * 获取时间区间长度（毫秒）
     */
    public long duration() {
        return end.getTime() - start.getTime();
    }

    /**
     * 计算两个时间之间相差的天数（按自然日计算，取绝对值）
     *
     * <p> 忽略时分秒，仅按 yyyy-MM-dd 计算 </p>
     *
     * @return 天数差（>= 0）
     */
    public int daysBetween() {
        long startDay = DateTimeNexus.startOfDay(start).getTime();
        long endDay = DateTimeNexus.startOfDay(end).getTime();

        long diff = Math.abs(endDay - startDay);

        return (int) (diff / (24L * 60 * 60 * 1000));
    }

    /**
     * 转为每天列表
     *
     * <p> 将当前时间区间按自然日拆分，每天生成一个 {@link DayInfo} 对象 </p>
     *
     * @return 每天的 DayInfo 列表
     */
    public List<DayInfo> toDayInfoList() {
        List<DayInfo> dayList = new ArrayList<>();
        if (start == null || end == null) {
            return dayList;
        }
        Date current = DateTimeNexus.startOfDay(start);
        Date lastDay = DateTimeNexus.startOfDay(end);
        while (!current.after(lastDay)) {
            DateTime dateTime = DateTime.from(current);

            // 生成 DayInfo，并根据 referenceMonth 设置类型
            DayInfo dayInfo = MonthGrid.createDayInfo(dateTime.toCalendar(), dateTime.getMonth());
            dayList.add(dayInfo);

            // 移动到下一天
            current = dateTime.plusDays(1).toDate();
        }
        return dayList;
    }

    /**
     * 判断指定时间是否在该区间内（包含边界）
     *
     * @param date 指定时间
     */
    public boolean contains(Date date) {
        if (date == null) {
            return false;
        }
        return !date.before(start) && !date.after(end);
    }

    /**
     * 判断两个时间区间是否存在重叠
     *
     * @param other 另一个时间区间
     */
    public boolean overlap(DateTimeRange other) {
        if (other == null) {
            return false;
        }
        return start.before(other.end) && end.after(other.start);
    }

    /**
     * 判断时间是否在区间开始之前（严格小于 start）
     *
     * @param date 指定时间
     */
    public boolean isBeforeStart(Date date) {
        if (date == null) {
            return false;
        }
        return date.before(start);
    }

    /**
     * 判断时间是否在区间结束之后（严格大于 end）
     *
     * @param date 指定时间
     */
    public boolean isAfterEnd(Date date) {
        if (date == null) {
            return false;
        }
        return date.after(end);
    }

    /**
     * 判断时间是否在区间之外（不在区间内）
     *
     * <p> 与 {@link #contains(Date)} 相反 </p>
     *
     * @param date 指定时间
     */
    public boolean isOutside(Date date) {
        if (date == null) {
            return false;
        }
        return isBeforeStart(date) || isAfterEnd(date);
    }

    /**
     * 判断时间是否在区间内（不包含边界）
     *
     * <p> 注意：{@link #contains(Date)} 是包含边界 </p>
     *
     * @param date 指定时间
     */
    public boolean containsExclusive(Date date) {
        if (date == null) {
            return false;
        }
        return date.after(start) && date.before(end);
    }

    /**
     * 判断时间是否等于开始时间
     *
     * @param date 指定时间
     */
    public boolean isStart(Date date) {
        if (date == null) {
            return false;
        }
        return start.equals(date);
    }

    /**
     * 判断时间是否等于结束时间
     *
     * @param date 指定时间
     */
    public boolean isEnd(Date date) {
        if (date == null) {
            return false;
        }
        return end.equals(date);
    }

    /**
     * 判断时间是否在开始边界或之前
     *
     * <p> date <= start </p>
     *
     * @param date 指定时间
     */
    public boolean isBeforeOrEqualStart(Date date) {
        if (date == null) {
            return false;
        }
        return !date.after(start);
    }

    /**
     * 判断时间是否在结束边界或之后
     *
     * <p> date >= end </p>
     *
     * @param date 指定时间
     */
    public boolean isAfterOrEqualEnd(Date date) {
        if (date == null) {
            return false;
        }
        return !date.before(end);
    }

    /**
     * 获取时间在区间中的位置
     *
     * @param date 指定时间
     * @return Position 枚举
     */
    public Position locate(Date date) {
        if (date == null) {
            return null;
        }
        if (date.before(start)) {
            return Position.BEFORE;
        }
        if (date.after(end)) {
            return Position.AFTER;
        }
        if (date.equals(start)) {
            return Position.START;
        }
        if (date.equals(end)) {
            return Position.END;
        }
        return Position.INSIDE;
    }

    /**
     * 获取时间在区间中的位置（按 “天” 维度判断）
     *
     * <p> 忽略时分秒，只比较日期（yyyy-MM-dd）</p>
     *
     * @param date 指定时间
     */
    public Position locateByDay(Date date) {
        if (date == null) {
            return null;
        }

        long target = DateTimeNexus.startOfDay(date).getTime();
        long startDay = DateTimeNexus.startOfDay(start).getTime();
        long endDay = DateTimeNexus.startOfDay(end).getTime();

        if (target < startDay) {
            return Position.BEFORE;
        }
        if (target > endDay) {
            return Position.AFTER;
        }
        if (target == startDay) {
            return Position.START;
        }
        if (target == endDay) {
            return Position.END;
        }
        return Position.INSIDE;
    }
}