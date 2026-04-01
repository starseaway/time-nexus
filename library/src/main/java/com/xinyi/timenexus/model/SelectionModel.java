package com.xinyi.timenexus.model;

import java.util.Date;

/**
 * 时间选择模型
 *
 * <p> 用于描述“时间选择状态”，适用于：</p>
 * <ul>
 *   <li> 单点选择 </li>
 *   <li> 区间选择（开始 - 结束） </li>
 * </ul>
 *
 * @author 新一
 * @date 2026/3/31 11:27
 */
public class SelectionModel {

    /**
     * 起始时间
     */
    private Date start;

    /**
     * 结束时间
     */
    private Date end;

    /**
     * 设置单点选择（清空区间）
     *
     * @param date 时间
     */
    public void select(Date date) {
        this.start = date;
        this.end = null;
    }

    /**
     * 设置区间选择
     *
     * @param start 起始时间
     * @param end 结束时间
     */
    public void select(Date start, Date end) {
        if (start != null && end != null && start.after(end)) {
            // 自动纠正顺序
            this.start = end;
            this.end = start;
        } else {
            this.start = start;
            this.end = end;
        }
    }

    /**
     * 判断是否已选择
     */
    public boolean hasSelection() {
        return start != null;
    }

    /**
     * 判断是否是单点选择
     */
    public boolean isSingle() {
        return start != null && end == null;
    }

    /**
     * 判断是否是区间选择
     */
    public boolean isRange() {
        return start != null && end != null;
    }

    /**
     * 判断指定时间是否是选中起点
     */
    public boolean isStart(Date date) {
        return start != null && start.equals(date);
    }

    /**
     * 判断指定时间是否是选中终点
     */
    public boolean isEnd(Date date) {
        return end != null && end.equals(date);
    }

    /**
     * 判断是否在选中区间内（包含边界）
     */
    public boolean isInRange(Date date) {
        if (start == null || date == null) {
            return false;
        }

        if (end == null) {
            return start.equals(date);
        }

        return !date.before(start) && !date.after(end);
    }

    /**
     * 清空选择
     */
    public void clear() {
        start = null;
        end = null;
    }

    /**
     * 获取起始时间
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
}