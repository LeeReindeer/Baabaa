package moe.leer.baabaa.util

import android.util.Log
import moe.leer.baabaa.model.Solar
import java.text.SimpleDateFormat
import java.util.*

/**
 *
 * Created by leer on 8/15/18.
 * Copyright (c) 2017 LeeReindeer
 * https://github.com/LeeReindeer
 */
object DateUtil {

  private val TAG = "DateUtil"
  private val DATE_PATTERN = "yyyy-MM-dd"
  private val formater = SimpleDateFormat(DATE_PATTERN, Locale.CHINA)

  /**
   * format date in pattern, if not fit, return null
   * @return formatted date string
   */
  fun format(date: Date): String? {
    var str: String? = null
    try {
      str = formater.format(date)
    } catch (e: Exception) {
      e.printStackTrace()
    }
    return str
  }

  fun parse(dateStr: String): Date? {
    var date: Date? = null
    try {
      date = formater.parse(dateStr)
    } catch (e: Exception) {
      e.printStackTrace()
    }
    return date
  }

  fun daysAfterNow(dateStringTo: String): Int {
    return daysBetween(format(Date())!!, dateStringTo)
  }

  fun daysBeforeNow(dateStringTo: String): Int {
    return daysBetween(dateStringTo, format(Date())!!)
  }

  /**
   * @return days between dateStringFrom and dateStringTo
   */
  fun daysBetween(dateStringFrom: String, dateStringTo: String): Int {
    val calendar = Calendar.getInstance()

    calendar.time = parse(dateStringFrom)
    val time1 = calendar.timeInMillis

    calendar.time = parse(dateStringTo)
    val time2 = calendar.timeInMillis
    val between = (time2 - time1).div(1000 * 3600 * 24) // one day
    Log.d(TAG, "daysBetween: $dateStringFrom and $dateStringTo is $between")

    return between.toInt()
  }

  data class Birthday(var years: Int, var days: Int, var solar: Solar?)

  /**
   * @param dateStr string of birthday date
   */
  fun birthday(dateStr: String): Birthday {
    val todayTime = DateUtil.format(Date())!! // 2018-08-15, 1998-01-28, birthday in this year: 2018-01-28
    var years = Integer.parseInt(todayTime.substring(0, 4)) - Integer.parseInt(dateStr.substring(0, 4)) //2018 - 1998
    var days = DateUtil.daysAfterNow(todayTime.substring(0, 4) + dateStr.substring(4)) // 2018-01-28
    if (days < 0) { // your birthday is already passed
      years += 1
      days = DateUtil.daysAfterNow((Integer.parseInt(todayTime.substring(0, 4)) + 1).toString() + dateStr.substring(4)) // 2019-01-28
    }
    return Birthday(years, days, null)
  }

  /**
   * @param dateStr string of birthday date
   * @return next lunar birthday
   */
  fun lunarBirthday(dateStr: String): Birthday {
    val todayTime = DateUtil.format(Date())!! // 2018-08-15, 1998-01-28, birthday in this year: 2018-01-28
    val nowSolar = Solar(todayTime)
    val nowLunar = LunarSolarConverter.solarToLunar(nowSolar)
    val lunarBirthday = LunarSolarConverter.solarToLunar(Solar(dateStr))
    val thisLunarBirthday = lunarBirthday.copy(lunarYear = nowLunar.lunarYear)

    var years = nowLunar.lunarYear - lunarBirthday.lunarYear
    var days = DateUtil.daysAfterNow(LunarSolarConverter.lunarToSolar(thisLunarBirthday).toString())
    if (days < 0) {
      years += 1
      thisLunarBirthday.lunarYear += 1
      days = DateUtil.daysAfterNow(LunarSolarConverter.lunarToSolar(thisLunarBirthday).toString())
    }

    return Birthday(years, days, LunarSolarConverter.lunarToSolar(thisLunarBirthday))
  }

}