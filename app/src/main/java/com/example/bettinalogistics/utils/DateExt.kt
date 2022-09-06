package com.example.bettinalogistics.utils

import android.annotation.SuppressLint
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

fun String.toDate (format : String) : Date?{
    val dateFormatter = SimpleDateFormat(format, Locale.US)
    return try {
        dateFormatter.parse(this)
    } catch (e: ParseException) {
        null
    }
}

@SuppressLint("SimpleDateFormat")
fun stringToDate(dateString : String): Date? {
    val dateFormatter = SimpleDateFormat("dd-MM-yyyy")
    return dateFormatter.parse(dateString)
}

@SuppressLint("SimpleDateFormat")
fun dateToString(date: Date): String {
    val dateFormatter = SimpleDateFormat("dd-MM-yyyy")
    return dateFormatter.format(date)
}

fun String.getCurrentDayName(): String {
    val cal = Calendar.getInstance()
    val date = toDate("yyyy/MM/dd")
    if (date != null) {
        cal.time = date
    }
    return when (cal[Calendar.DAY_OF_WEEK]) {
        Calendar.MONDAY -> "Mon"
        Calendar.TUESDAY -> "Tue"
        Calendar.WEDNESDAY -> "Wed"
        Calendar.THURSDAY -> "Thu"
        Calendar.FRIDAY -> "Fri"
        Calendar.SATURDAY -> "Sat"
        else -> "Sun"
    }
}