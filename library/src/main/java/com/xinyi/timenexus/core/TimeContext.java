package com.xinyi.timenexus.core;

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * 时间上下文
 *
 * <p>
 *   所有时间计算、格式化、范围获取等操作，都应依赖该上下文，
 *   这样可以避免系统默认时区带来的不确定性问题。
 * </p>
 *
 * <p>
 *   设计目的：
 * </p>
 * <ul>
 *   <li> 支持跨时区计算（如 UTC / 国际化业务） </li>
 *   <li> 提升 API 一致性（所有操作共享同一上下文） </li>
 *   <li> 为后续扩展（虚拟时间 / Hook / 测试注入）提供基础能力 </li>
 * </ul>
 *
 * @author 新一
 * @date 2026/3/31 10:12
 */
public class TimeContext {

    /**
     * 时区
     */
    private final TimeZone timeZone;

    /**
     * 语言环境
     */
    private final Locale locale;

    /**
     * 构造函数
     *
     * @param builder 时区&语言环境 构建器
     */
    private TimeContext(Builder builder) {
        this.timeZone = builder.timeZone;
        this.locale = builder.locale;
    }

    /**
     * 创建一个新的 Calendar 实例（绑定当前上下文的时区和语言环境）
     */
    public Calendar newCalendar() {
        return Calendar.getInstance(timeZone, locale);
    }

    /**
     * 获取当前时区
     */
    public TimeZone getTimeZone() {
        return timeZone;
    }

    /**
     * 获取当前语言环境
     */
    public Locale getLocale() {
        return locale;
    }

    /**
     * Builder 模式构建上下文
     */
    public static class Builder {

        /**
         * 默认时区
         */
        private TimeZone timeZone = TimeZone.getDefault();

        /**
         * 默认语言环境
         */
        private Locale locale = Locale.getDefault();

        /**
         * 设置时区
         */
        public Builder timeZone(TimeZone timeZone) {
            if (timeZone != null) {
                this.timeZone = timeZone;
            }
            return this;
        }

        /**
         * 设置语言环境
         */
        public Builder locale(Locale locale) {
            if (locale != null) {
                this.locale = locale;
            }
            return this;
        }

        /**
         * 构建 DateTimeContext
         */
        public TimeContext build() {
            return new TimeContext(this);
        }
    }
}