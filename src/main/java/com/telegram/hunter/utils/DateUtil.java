package com.telegram.hunter.utils;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

/**
 * date util
 */
public class DateUtil extends DateUtils {

	private static String[] parsePatterns = { "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy-MM",
			"yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm", "yyyy/MM", "yyyy.MM.dd", "yyyy.MM.dd HH:mm:ss",
			"yyyy.MM.dd HH:mm", "yyyy.MM", "yyyyMMddHHmmss", "yyyy-MM-dd'T'HH:mm:ss.SSSZ", "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
			"yyyy-MM-dd'T'HH:mm:ss.SSS", "EEE, dd MMM yyyy HH:mm:ss zzz", "yyyy-MM-dd HH", "HH:mm"};

	/**
	 * yyyy-MM-dd
	 */
	public static String getDate() {
		return getDate("yyyy-MM-dd");
	}

	/**
	 * yyyy-MM-dd" "HH:mm:ss" "E"
	 */
	public static String getDate(String pattern) {
		return DateFormatUtils.format(new Date(), pattern);
	}
	
	public static Date from(Date date, Object... pattern) {
		return parseDate(formatDate(date, pattern));
	}
	
	/**
	 * from
	 * @param str
	 * @param pattern
	 * @return
	 */
	public static String from(String str, Object... pattern) {
		return formatDate(parseDate(str), pattern);
	}

	/**
	 * yyyy-MM-dd
	 */
	public static String formatDate(Date date, Object... pattern) {
		if (date == null) {
			return null;
		}
		String formatDate = null;
		if (pattern != null && pattern.length > 0) {
			formatDate = DateFormatUtils.format(date, pattern[0].toString());
		} else {
			formatDate = DateFormatUtils.format(date, "yyyy-MM-dd");
		}
		return formatDate;
	}

	/**
	 * yyyy-MM-dd HH:mm:ss
	 */
	public static String formatDateTime(Date date) {
		return formatDate(date, "yyyy-MM-dd HH:mm:ss");
	}

	/**
	 * HH:mm:ss
	 */
	public static String getTime() {
		return formatDate(new Date(), "HH:mm:ss");
	}

	/**
	 * yyyy-MM-dd HH:mm:ss
	 */
	public static String getDateTime() {
		return formatDate(new Date(), "yyyy-MM-dd HH:mm:ss");
	}

	/**
	 * yyyy
	 */
	public static String getYear() {
		return formatDate(new Date(), "yyyy");
	}

	/**
	 * MM
	 */
	public static String getMonth() {
		return formatDate(new Date(), "MM");
	}

	/**
	 * dd
	 */
	public static String getDay() {
		return formatDate(new Date(), "dd");
	}

	public static String getWeek() {
		return formatDate(new Date(), "E");
	}

	/**
	 * { "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm",
	 * "yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm", "yyyy.MM.dd",
	 * "yyyy.MM.dd HH:mm:ss", "yyyy.MM.dd HH:mm" }
	 */
	public static Date parseDate(String str) {
		return parseDate(str, parsePatterns);
	}
	
	/**
	 * { "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm",
	 * "yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm", "yyyy.MM.dd",
	 * "yyyy.MM.dd HH:mm:ss", "yyyy.MM.dd HH:mm" }
	 */
	public static Date parseDate(final String str, final String... parsePatterns) {
		if (str == null) {
			return null;
		}
        try {
			return parseDate(str, null, parsePatterns);
		} catch (ParseException e) {
			return null;
		}
    }

	/**
	 *
	 * @param date
	 * @return
	 */
	public static long pastDays(Date date) {
		long t = System.currentTimeMillis() - date.getTime();
		return t / (24 * 60 * 60 * 1000);
	}

	/**
	 *
	 * @param date
	 * @return
	 */
	public static long pastHour(Date date) {
		long t = System.currentTimeMillis() - date.getTime();
		return t / (60 * 60 * 1000);
	}

	/**
	 *
	 * @param date
	 * @return
	 */
	public static long pastMinutes(Date date) {
		long t = System.currentTimeMillis() - date.getTime();
		return t / (60 * 1000);
	}

	/**
	 *
	 * @param timeMillis
	 * @return
	 */
	public static String formatDateTime(long timeMillis) {
		long day = timeMillis / (24 * 60 * 60 * 1000);
		long hour = (timeMillis / (60 * 60 * 1000) - day * 24);
		long min = ((timeMillis / (60 * 1000)) - day * 24 * 60 - hour * 60);
		long s = (timeMillis / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
		long sss = (timeMillis - day * 24 * 60 * 60 * 1000 - hour * 60 * 60 * 1000 - min * 60 * 1000 - s * 1000);
		return (day > 0 ? day + "," : "") + hour + ":" + min + ":" + s + "." + sss;
	}

	/**
	 *
	 * @param before
	 * @param after
	 * @return
	 */
	public static double getDistanceOfTwoDate(Date before, Date after) {
		long beforeTime = before.getTime();
		long afterTime = after.getTime();
		return (afterTime - beforeTime) / (1000 * 60 * 60 * 24);
	}

	public static String getFirstDayOfMonth() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Calendar c = Calendar.getInstance();
		c.add(Calendar.MONTH, 0);
		c.set(Calendar.DAY_OF_MONTH, 1);//
		String first = format.format(c.getTime());
		return first;
	}
	
	
	/**
	 * @param day
	 * @return
	 */
	public static String getPastDay(int day) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.DATE, - day);
        Date d = c.getTime();
        return format.format(d);
	}

	public static String[] getPastDayRange(int day) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
		SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd 23:59:59");
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		c.add(Calendar.DATE, - day);
		Date d = c.getTime();
		String[] dateArray = new String[]{format.format(d),format2.format(d)};
		return dateArray;
	}

	/**
	 * @param month
	 * @return
	 */
	public static String getPastMonth(int month) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.MONTH, -month);
        Date m = c.getTime();
        return format.format(m);
	}
	
	/**
	 * @param year
	 * @return
	 */
	public static String getPastYear(int year) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.YEAR, -year);
        Date y = c.getTime();
        return format.format(y);
	}

	/**
	 * @param str
	 * @return
	 */
	public static boolean checkDateLegitimate (String str) {
		SimpleDateFormat sd=new SimpleDateFormat("yyyy-MM-dd");
		try {
			sd.setLenient(false);
			sd.parse(str);
		}
		catch (Exception e) {
			return false;
		}
		return true;
	}


	public static Date getLastDate(Date date){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DAY_OF_MONTH, -1);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		return calendar.getTime();
	}
	
	/**
	 * @param time
	 * @return
	 */
	public static int getDay(long time) {
        Instant instant = Instant.ofEpochMilli(time);
        ZoneId zone = ZoneId.systemDefault();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zone);
        return localDateTime.getDayOfMonth();
    }
	
	/**
	 * @param time
	 * @return
	 */
	public static int getDay(String time, String... pattern) {
		String format = null;
		if (pattern != null && pattern.length > 0) {
			format = pattern[0];
		} else {
			format = "yyyy-MM-dd HH:mm:ss";
		}
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(format);
        LocalDateTime localDateTime = LocalDateTime.parse(time, dateTimeFormatter);
        return localDateTime.getDayOfMonth();
    }

	/**
	 * dateStr to double
	 * @param dateStr
	 * @return
	 */
	public static Double getDoubleDateTime(String dateStr){
		Double res = null;
		try {
			Date date = parseDate(dateStr);
			if (date != null){
				res = Double.valueOf(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().toEpochSecond(ZoneOffset.of("+8")));
	        }
		} catch (Exception e) {
			
		}
		return res;
	}
	
	/**
	 * date to double
	 * @param date
	 * @return
	 */
	public static Double getDoubleDateTime(Date date){
		Double res = null;
		try {
			res = Double.valueOf(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().toEpochSecond(ZoneOffset.of("+8")));
		} catch (Exception e) {
			
		}
		return res;
	}

	/**
	 * @param str
	 * @return
	 */
	public static boolean isValidDate(String str, String dateFormat) {
		boolean convertSuccess = true;
		SimpleDateFormat format = new SimpleDateFormat(dateFormat);
		try {
			format.setLenient(false);
			format.parse(str);
		} catch (ParseException e) {
			convertSuccess = false;
		}
		return convertSuccess;
	}
	
	public static Date preciseToDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }
    
    public static Date preciseToDayEnd(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }
	
}
