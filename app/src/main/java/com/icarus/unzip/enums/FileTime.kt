package com.icarus.unzip.enums

import com.icarus.unzip.ad.Constants

enum class FileTime(val id: String, val msg: String) {
    RECENT("0", "刚刚"),
    ONE_HOURS("1", "一小时前"),
    THREE_HOURS("2", "三小时前"),
    ONE_DAY("3", "一天前"),
    THREE_DAY("4", "三天前"),
    ONE_WEEK("5", "一周前"),
    ONE_MONTH("6", "一个月前"),
    THREE_MONTH("7", "三个月前"),
    ONE_YEAR("8", "一年前"),
    EARLIER("9", "更早");

    companion object {
        private const val RECENT_VALUE: Long = 0
        private const val ONE_HOURS_VALUE = (60 * 60 * 1000).toLong()
        private const val THREE_HOURS_VALUE = 3 * ONE_HOURS_VALUE
        private const val ONE_DAY_VALUE = 24 * ONE_HOURS_VALUE
        private const val THREE_DAY_VALUE = 3 * ONE_DAY_VALUE
        private const val ONE_WEEK_VALUE = 7 * ONE_DAY_VALUE
        private const val ONE_MONTH_VALUE = 30 * ONE_DAY_VALUE
        private const val THREE_MONTH_VALUE = 3 * ONE_MONTH_VALUE
        private const val ONE_YEAR_VALUE = 365 * ONE_DAY_VALUE
        private const val EARLIER_VALUE = 2 * ONE_YEAR_VALUE

        fun getFileTime(value: Long): FileTime {
            return when (System.currentTimeMillis() - value) {
                in RECENT_VALUE until ONE_HOURS_VALUE -> RECENT
                in ONE_HOURS_VALUE until THREE_HOURS_VALUE -> ONE_HOURS
                in THREE_HOURS_VALUE until ONE_DAY_VALUE -> THREE_HOURS
                in ONE_DAY_VALUE until THREE_DAY_VALUE -> ONE_DAY
                in THREE_DAY_VALUE until ONE_WEEK_VALUE -> THREE_DAY
                in ONE_WEEK_VALUE until ONE_MONTH_VALUE -> ONE_WEEK
                in ONE_MONTH_VALUE until THREE_MONTH_VALUE -> ONE_MONTH
                in THREE_MONTH_VALUE until ONE_YEAR_VALUE -> THREE_MONTH
                in ONE_YEAR_VALUE until EARLIER_VALUE -> ONE_YEAR
                else -> EARLIER
            }
        }
    }
}