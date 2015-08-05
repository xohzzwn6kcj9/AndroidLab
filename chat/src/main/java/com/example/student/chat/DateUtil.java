package com.example.student.chat;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DateUtil {

	public static String getNow(String formatStr) {
		if (formatStr == null || formatStr.length() == 0
				|| formatStr.toUpperCase().equals("DEFAULT"))
			formatStr = "yyyy-MM-dd HH:mm:ss";

		SimpleDateFormat formatter = new SimpleDateFormat(formatStr);
		Calendar cal = Calendar.getInstance();

		return formatter.format(cal.getTime());
	}
	

	public static String getDatetime(long datetime, String formatStr) {
		if (formatStr == null || formatStr.length() == 0
				|| formatStr.toUpperCase().equals("DEFAULT"))
			formatStr = "yyyy-MM-dd HH:mm:ss";

		SimpleDateFormat formatter = new SimpleDateFormat(formatStr);
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(datetime);

		return formatter.format(cal.getTime());
	}
}
