package com.xinyi.timenexus.extension

import com.xinyi.timenexus.DateTimeNexus
import com.xinyi.timenexus.consts.DatePatterns
import com.xinyi.timenexus.core.DateTime
import java.util.Date

/**
 * Datetime 相关的扩展函数（高频调用简化）
 *
 * @author 新一
 * @date 2026/3/31 17:44
 */

/** 格式化 */
fun Date.format(pattern: String = DatePatterns.DATETIME): String {
    return DateTimeNexus.format(this, pattern)
}

/** 获取某日开始时间 */
fun Date.startOfDay(): Date? {
    return DateTime.from(this).startOfDay()
}

/** 获取某日结束时间 */
fun Date.endOfDay(): Date? {
    return DateTime.from(this).endOfDay()
}

/** 是否今天 */
fun Date.isToday(): Boolean {
    return DateTimeNexus.isToday(this)
}

/** 是否昨天 */
fun Date.isYesterday(): Boolean {
    return DateTimeNexus.isYesterday(this)
}

/** 是否同一天 */
fun Date.isSameDay(other: Date): Boolean {
    return DateTimeNexus.isSameDay(this, other)
}

/** 加天数 */
fun Date.plusDays(days: Int): Date {
    return DateTime.from(this).plusDays(days).toDate()
}

/** 减天数 */
fun Date.minusDays(days: Int): Date {
    return DateTime.from(this).minusDays(days).toDate()
}

/**
 * 智能解析
 */
fun String.toDate(): Date? {
    return DateTimeNexus.smartParsePlus(this)
}