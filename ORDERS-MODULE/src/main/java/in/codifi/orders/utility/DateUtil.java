package in.codifi.orders.utility;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import io.quarkus.logging.Log;

/**
 * 
 * @author Pradeep Ravichandran
 *
 */
public class DateUtil {
	public static final String DDMMYYYY = "dd/MM/yyyy";
	public static final String YYYYMMDD = "yyyyMMdd";
	public static final DateFormat DATEFORMAT_YYYYMMDD = new SimpleDateFormat(YYYYMMDD);
	public static final DateFormat DATEFORMAT_DDMMYYYY = new SimpleDateFormat(DDMMYYYY);

	public static String parseDate(Date date) {
		String date2 = null;
		DateFormat formatter;
		formatter = new SimpleDateFormat("dd-MMM-yy");
		date2 = (String) formatter.format(date);

		return date2;
	}

	public static Calendar getCalendar(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal;
	}

	public static Date parseString(String dateString, DateFormat format) {
		Date date = null;
		if (StringUtil.isNotNullOrEmpty(dateString) && format != null) {
			try {
				date = (Date) format.parse(dateString);
			} catch (ParseException e) {
				System.out.println("ParseException :" + e);
			} catch (Exception e) {
				System.out.println("Exception :" + e);
			}
		}
		return date;
	}

	public static String formatDate(Date date, String format) {
		DateFormat dateFormat = new SimpleDateFormat(format);
		return formatDateByformat(date, dateFormat);
	}

	public static String formatDateByformat(Date date, DateFormat dateFormat) {
		String dateStr = null;
		try {
			if (date != null) {
				dateStr = dateFormat.format(date);
			}
		} catch (Exception e) {
			return null;
		}
		return dateStr;
	}

	public static Date parseString(String dateString, String format) {
		Date date = null;
		SimpleDateFormat formatter = null;
		if (StringUtil.isNotNullOrEmpty(dateString)) {
			try {
				formatter = new SimpleDateFormat(format);
				date = (Date) formatter.parse(dateString);
			} catch (ParseException e) {
				Log.error("Exception :" + e);
			}
		}
		return date;
	}

	public static Date getTodayDate() {
		String date = DATEFORMAT_DDMMYYYY.format(getNewDateWithCurrentTime());
		return parseString(date, DATEFORMAT_DDMMYYYY);
	}

	public static Date getNewDateWithCurrentTime() {
		Calendar calendar = Calendar.getInstance();
		return calendar.getTime();
	}

	/**
	 * 
	 * Method to convert date from Millisec to date
	 * 
	 * @author Dinesh Kumar
	 *
	 * @param dateInMills
	 * @return
	 */
	public static String milliSecToDate(long dateInMills) {
		String dateInString = "";
		try {
			long datemsec = dateInMills * 1000;
			DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
			formatter.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(datemsec);
			dateInString = formatter.format(calendar.getTime());
		} catch (Exception e) {
			Log.error(e);
		}
		return dateInString;
	}

}
