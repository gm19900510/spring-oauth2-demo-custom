package com.gm.authorization.server.custom.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtils {
	final static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

	public static Integer timeDiff(Date startTime, Date endTime) {
		long start = startTime.getTime();
		long end = endTime.getTime();
		int second = (int) ((end - start) / 1000);
		return second;
	}

}
