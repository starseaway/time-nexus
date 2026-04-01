package com.xinyi.timenexus.enums;

import java.util.Calendar;

/**
 * 星期枚举
 *
 * <p> 对 {@link Calendar#DAY_OF_WEEK} 的语义封装，避免直接使用魔法值 </p>
 *
 * <p> 映射关系：</p>
 * <ul>
 *   <li> {@link Calendar#SUNDAY} = 1 </li>
 *   <li> {@link Calendar#MONDAY} = 2 </li>
 *   <li> {@link Calendar#TUESDAY} = 3 </li>
 *   <li> {@link Calendar#WEDNESDAY} = 4 </li>
 *   <li> {@link Calendar#THURSDAY} = 5 </li>
 *   <li> {@link Calendar#FRIDAY} = 6 </li>
 *   <li> {@link Calendar#SATURDAY} = 7 </li>
 * </ul>
 *
 * <p> 注意：</p>
 * <ul>
 *   <li> 该枚举与 {@link Calendar} 保持一致（周日为一周第一天）</li>
 *   <li> 如需 ISO 标准（周一为第一天），需自行转换 </li>
 * </ul>
 */
public enum WeekDay {

    /// 星期日
    SUNDAY(Calendar.SUNDAY),
    /// 星期一
    MONDAY(Calendar.MONDAY),
    /// 星期二
    TUESDAY(Calendar.TUESDAY),
    /// 星期三
    WEDNESDAY(Calendar.WEDNESDAY),
    /// 星期四
    THURSDAY(Calendar.THURSDAY),
    /// 星期五
    FRIDAY(Calendar.FRIDAY),
    /// 星期六
    SATURDAY(Calendar.SATURDAY);
    /**
     * Calendar 对应值（1~7）
     */
    private final int value;

    /**
     * 构造函数
     *
     * @param value Calendar 对应值
     */
    WeekDay(int value) {
        this.value = value;
    }

    /**
     * 获取 Calendar 对应值
     */
    public int value() {
        return value;
    }

    /**
     * 从 Calendar 值转换为枚举
     *
     * @param calendarValue {@link Calendar#DAY_OF_WEEK} 的值
     */
    public static WeekDay from(int calendarValue) {
        for (WeekDay day : values()) {
            if (day.value == calendarValue) {
                return day;
            }
        }
        throw new IllegalArgumentException("Invalid dayOfWeek: " + calendarValue);
    }
}