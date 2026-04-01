package com.xinyi.timenexus.calendar;

import androidx.annotation.NonNull;

import java.util.Date;

/**
 * 单日信息模型
 *
 * <p> 表示日历中的一个 “格子”，包含日期对象、包含所属月份类型 </p>
 *
 * @author 新一
 * @date 2026/3/31 11:12
 */
public class DayInfo {

    /**
     * 上个月
     */
    public static final int PREV_MONTH = -1;

    /**
     * 当前月
     */
    public static final int CURRENT_MONTH = 0;

    /**
     * 下个月
     */
    public static final int NEXT_MONTH = 1;

    /**
     * 日期
     */
    private Date date;

    /**
     * 日（1-31）
     */
    private int day;

    /**
     * 星期（1-7）
     */
    private int week;

    /**
     * 类型（上月 / 当月 / 下月）
     */
    private int type;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getWeek() {
        return week;
    }

    public void setWeek(int week) {
        this.week = week;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    /**
     * 是否属于当前月份
     */
    public boolean isCurrentMonth() {
        return type == CURRENT_MONTH;
    }

    @NonNull
    @Override
    public String toString() {
        return "DayInfo{" +
                "date=" + date +
                ", day=" + day +
                ", week=" + week +
                ", type=" + type +
                '}';
    }
}