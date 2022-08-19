package co.xendit.consumerapp.utils.extension

import android.content.Context
import android.os.Build.VERSION_CODES
import androidx.annotation.RequiresApi
import com.qpay.android.R
import java.text.SimpleDateFormat
import java.time.DateTimeException
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

/**
 * This method will return date as Today, Yesterday, 12 Aug, 12 Aug 2019
 *
 * @param date - the date to format
 */

const val FULL_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
const val SIMPLE_DATE_FORMAT = "dd MMM yyyy"
const val DATE_WITH_TIME_FORMAT = "MMM dd, yyyy HH:mm"
const val API_SIMPLE_FORMAT = "yyyy-MM-dd"

fun parseDayFromDate(context: Context, locale: Locale, date: Date): String {
  val todayCalendar = Calendar.getInstance()
  val yesterdayCalendar = Calendar.getInstance().apply { add(Calendar.DATE, -1) }
  val currentCalendar = Calendar.getInstance()
  currentCalendar.time = date

  val currentDay = currentCalendar.get(Calendar.DAY_OF_YEAR)
  val yesterdayDay = yesterdayCalendar.get(Calendar.DAY_OF_YEAR)
  val todayDay = todayCalendar.get(Calendar.DAY_OF_YEAR)

  val currentYear = currentCalendar.get(Calendar.YEAR)
  val yesterdayYear = yesterdayCalendar.get(Calendar.YEAR)
  val todayYear = todayCalendar.get(Calendar.YEAR)

  val isToday = (currentYear == todayYear && currentDay == todayDay)
  val isYesterday = (currentYear == yesterdayYear && currentDay == yesterdayDay)

  return when {
    isToday -> context.getString(R.string.today)
    isYesterday -> context.getString(R.string.yesterday)
    else -> parseFullDateChooseYear(locale, date)
  }
}

// Returns either 23 Aug 2019 or 23 Aug, depends on current year
fun parseFullDateChooseYear(locale: Locale, date: Date): String {
  val calendar = Calendar.getInstance()
  val currentYear = calendar.get(Calendar.YEAR)

  calendar.time = date
  val incomingYear = calendar.get(Calendar.YEAR)

  val pattern = if (incomingYear != currentYear) "dd MMM yyyy" else "dd MMM"
  val dfOut = SimpleDateFormat(pattern, locale)
  return dfOut.format(date)
}

fun parseFullDateYear(locale: Locale, date: Date): String {
  val calendar = Calendar.getInstance()
  val currentYear = calendar.get(Calendar.YEAR)

  calendar.time = date
  val incomingYear = calendar.get(Calendar.YEAR)

  val pattern = if (incomingYear != currentYear) "dd MMM yyyy" else "dd MMM yyyy"
  val dfOut = SimpleDateFormat(pattern, locale)
  return dfOut.format(date)
}

// Return formatted date counting timezone
@RequiresApi(VERSION_CODES.O) fun getFormattedDate(format: String, date: Date): String {
  val formatter: DateTimeFormatter =
    DateTimeFormatter.ofPattern(format, Locale.ENGLISH)

  return date.toInstant()
    .atOffset(ZoneOffset.UTC)
    .atZoneSameInstant(ZoneId.systemDefault())
    .format(formatter)
}

@RequiresApi(VERSION_CODES.O) fun parseDate(format: String, date: String?): Date {
  val formatter: DateTimeFormatter =
    DateTimeFormatter.ofPattern(format, Locale.ENGLISH)
  if (date.isNullOrEmpty()) return Date()

  return try {
    val zonedDate = LocalDateTime.parse(date, formatter)
      .atOffset(ZoneOffset.UTC)
      .atZoneSameInstant(ZoneId.systemDefault())
    Date.from(zonedDate.toInstant())
  } catch (d: DateTimeException) {
    try {
      SimpleDateFormat(format, Locale.ENGLISH).parse(date)
    } catch (e: Exception) {
      Date()
    }
  }
}

fun getDiffDate(firstDate: Date, secondDate: Date): Long {
  val diff = firstDate.time - secondDate.time
  val diffDays = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS)
  if (diffDays == 0L) {
    val day1 = Calendar.getInstance().apply {
      time = firstDate
    }.get(Calendar.DAY_OF_MONTH)

    val day2 = Calendar.getInstance().apply {
      time = secondDate
    }.get(Calendar.DAY_OF_MONTH)

    return if (day1 != day2) {
      1L
    } else {
      0L
    }
  } else {
    return diffDays
  }
}
