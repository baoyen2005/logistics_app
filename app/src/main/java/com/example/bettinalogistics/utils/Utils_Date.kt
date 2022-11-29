package com.example.bettinalogistics.utils

import android.text.TextUtils
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object Utils_Date {
    const val DATE_PATTERN_ddMMYYYY = "dd/MM/yyyy"
    const val DATE_PATTERN_yyyyMMdd = "yyyyMMdd"
    const val DATE_PATTERN_yyyyMMdd_2 = "yyyy-MM-dd"
    const val RE_DATE_PATTERN_yyyyMMdd_2 = "dd-MM-yyyy"
    const val DATE_PATTERN_EEEEddMMYYYY = "EEEE dd/MM/yyyy"
    const val DATE_PATTERN_yyyyMMddEEEE = "yyyy/MM/dd EEEE"
    const val DATE_PATTERN_EEEETMYYYY = "dd,EEEE,'T.'M yyyy"
    const val DATE_PATTERN_DDM = "dd 'thg' M"
    const val DATE_PATTERN_DDM_EN = "dd MMM"
    const val DATE_PATTERN_YYYYMMDD_HH_MM = "yyyy-MM-dd HH:mm"
    const val DATE_PATTERN_HH_MM = "HH:mm"
    const val DATE_PATTERN_YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss"
    const val FULLTIME_FORMAT_yyyyMMddHHmmss = "yyyyMMddHHmmss"
    const val FULLTIME_FORMAT_MMddHHmmss = "MMddHHmmss"
    const val DATE_PATTERN_SS_MM_HH_DD_MM_YYYY = "HH:mm:ss dd/MM/yyyy"
    const val DATE_PATTERN_SS_MM_HH_DD_MM_YYYY_V2 = "HH:mm:ss dd-MM-yyyy"
    const val DATE_PATTERN_HH_MM_DD_MM_YYYY = "HH:mm dd/MM/yyyy"
    const val DATE_PATTERN_MM_HH_DD_MM_YYYY = "mm:HH dd/MM/yyyy"
    const val DATE_PATTERN_YYYY_MM_DD_HH_MM = "yyyy-MM-dd HH:mm"
    const val DATE_PATTERN_YYYYMMDDHHMM = "yyyyMMddHHmm"
    const val DATE_PATTERN_DD_MM_YYYY = "dd/MM/yyyy',' HH:mm"
    const val DATE_PATTERN_DD_MM_YYYY_HH_MM_SS = "dd/MM/yyyy HH:mm:ss"
    const val DATE_PATTERN_MM_HH_DD_MM = "HH:mm dd/MM"
    const val DATE_PATTERN_HH_MM_SS = "HH:mm:ss"

    fun getCurrentTimeByFormat(format: String): String {
        val milliseconds = System.currentTimeMillis()
        val sdf = SimpleDateFormat(format)
        val resultdate = Date(milliseconds)
        return sdf.format(resultdate)
    }

    fun getCurrentTimeByFormat(format: String, timeMillis: Long): String {
        val sdf = SimpleDateFormat(format)
        val resultdate = Date(timeMillis)
        return sdf.format(resultdate)
    }

    fun convertDate(fromFormat: String, toFormat: String, strDate: String): String {
        try {
            val simpleDateFormat = SimpleDateFormat(fromFormat)
            val simpleTo = SimpleDateFormat(toFormat)
            return simpleTo.format(simpleDateFormat.parse(strDate))
        } catch (ex: ParseException) {
        }
        return strDate
    }

    val billTime: String
        get() {
            val milliseconds = System.currentTimeMillis()
            val sdf = SimpleDateFormat(DATE_PATTERN_HH_MM_DD_MM_YYYY)
            return sdf.format(Date(milliseconds))
        }
    val createBillTime: String
        get() {
            val milliseconds = System.currentTimeMillis()
            val sdf = SimpleDateFormat(DATE_PATTERN_SS_MM_HH_DD_MM_YYYY)
            return sdf.format(Date(milliseconds))
        }

    fun getCreateBillTimeFromString(billTime: String): Long {
        if (TextUtils.isEmpty(billTime)) return -1
        val df: DateFormat = SimpleDateFormat(DATE_PATTERN_SS_MM_HH_DD_MM_YYYY, Locale.getDefault())
        try {
            return df.parse(billTime).time
        } catch (ex: Exception) {
        }
        return -1
    }

    fun convertformDate(dateTime: Date, convertPattern: String): String {
        if (dateTime == null) return ""
        val sdf2 = SimpleDateFormat(convertPattern, Locale.getDefault())
        return sdf2.format(dateTime)
    }

    fun getTimeByMiliseconds(time: String, timeFormat: String): String {
        return try {
            val date = Date(time.toLong())
            convertformDate(date, timeFormat)
        } catch (ex: Exception) {
            ""
        }
    }

    fun getTimeByMiliseconds(timeStamp: Long, timeFormat: String): String {
        return try {
            convertformDate(Date(timeStamp), timeFormat)
        } catch (ex: Exception) {
            ""
        }
    }

    fun convertDateFromString(time: String, format: String?): String {
        val date = Date(time.toLong())
        val date1 = SimpleDateFormat(format, Locale.getDefault())
        return date1.format(date)
    }

    fun dateToString(date: Date, pattern: String): String {
        val dateFormat: DateFormat = SimpleDateFormat(pattern, Locale.getDefault())
        return dateFormat.format(date)
    }

    fun stringToDate(dateString: String, pattern: String): Date {
        val dateFormat: DateFormat = SimpleDateFormat(pattern, Locale.getDefault())
        return dateFormat.parse(dateString)
    }

    fun changeDateFormat(s: String, beforePattern: String, afterPattern: String): String {
        val dateFormat: DateFormat = SimpleDateFormat(beforePattern, Locale.getDefault())
        val sdf1: DateFormat = SimpleDateFormat(afterPattern, Locale.getDefault())
        return try {
            val date = dateFormat.parse(s)
            sdf1.format(date)
        } catch (e: ParseException) {
            ""
        }
    }

    fun changeDateFormatHistory(s: String): String {
        val dateFormat: DateFormat =
            SimpleDateFormat(FULLTIME_FORMAT_yyyyMMddHHmmss, Locale.getDefault())
        val sdf1: DateFormat = SimpleDateFormat(DATE_PATTERN_MM_HH_DD_MM_YYYY, Locale.getDefault())
        return try {
            val date = dateFormat.parse(s)
            sdf1.format(date)
        } catch (e: ParseException) {
            ""
        }
    }

    fun changeDateFormatSearch(s: String): String {
        val dateFormat: DateFormat = SimpleDateFormat(DATE_PATTERN_ddMMYYYY, Locale.getDefault())
        val sdf1: DateFormat = SimpleDateFormat(DATE_PATTERN_yyyyMMdd, Locale.getDefault())
        return try {
            val date = dateFormat.parse(s)
            sdf1.format(date)
        } catch (e: ParseException) {
            ""
        }
    }

    fun calculateAfterDateForMonth(date: String, numberOfMonth: Int): String {
        val sdf = SimpleDateFormat(DATE_PATTERN_yyyyMMdd, Locale.getDefault())
        val calculateDate: Date
        return try {
            calculateDate = sdf.parse(date) as Date
            val calendar = Calendar.getInstance()
            calendar.time = calculateDate
            calendar.add(Calendar.MONTH, numberOfMonth)
            sdf.format(calendar.time)
        } catch (e: ParseException) {
            ""
        }
    }

    fun calculateAfterDateForDay(date: String, numberOfDay: Int, pattern: String): String {
        val sdf = SimpleDateFormat(pattern, Locale.getDefault())
        val calculateDate: Date
        return try {
            calculateDate = sdf.parse(date) as Date
            val calendar = Calendar.getInstance()
            calendar.time = calculateDate
            calendar.add(Calendar.DAY_OF_WEEK, numberOfDay)
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            sdf.format(calendar.time)
        } catch (e: ParseException) {
            ""
        }
    }

    fun getFirstDayOfCurrentMonth(pattern: String): String {
        val sdf = SimpleDateFormat(pattern, Locale.getDefault())
        val calendar = Calendar.getInstance()
        calendar[Calendar.DAY_OF_MONTH] = 1
        calendar[Calendar.HOUR_OF_DAY] = calendar.getActualMinimum(Calendar.HOUR_OF_DAY)
        calendar[Calendar.MINUTE] = calendar.getMinimum(Calendar.MINUTE)
        calendar[Calendar.SECOND] = calendar.getMinimum(Calendar.SECOND)
        return sdf.format(calendar.time)
    }

    fun getFirstDayOfPreMonth(pattern: String): String {
        val sdf = SimpleDateFormat(pattern, Locale.getDefault())
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MONTH, -1)
        calendar[Calendar.DAY_OF_MONTH] = 1
        calendar[Calendar.HOUR_OF_DAY] = calendar.getActualMinimum(Calendar.HOUR_OF_DAY)
        calendar[Calendar.MINUTE] = calendar.getMinimum(Calendar.MINUTE)
        calendar[Calendar.SECOND] = calendar.getMinimum(Calendar.SECOND)
        return sdf.format(calendar.time)
    }

    fun getCurrentDay(pattern: String): String {
        val sdf = SimpleDateFormat(pattern, Locale.getDefault())
        val calendar = Calendar.getInstance()
        calendar[Calendar.HOUR_OF_DAY] = calendar.getActualMinimum(Calendar.HOUR_OF_DAY)
        calendar[Calendar.MINUTE] = calendar.getMinimum(Calendar.MINUTE)
        calendar[Calendar.SECOND] = calendar.getMinimum(Calendar.SECOND)
        return sdf.format(calendar.time)
    }

}