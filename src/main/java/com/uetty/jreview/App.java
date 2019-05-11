package com.uetty.jreview;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Hello world!
 *
 */
public class App {
	
	static final int SYSTEM_GREORIAN = 1;
    static final int SYSTEM_JULIAN = 2;
    static final int SYSTEM_SP = 3;

    static boolean isGreorianLeapYear(int year) {
        if (year % 400 == 0) return true;
        if (year % 100 == 0) return false;
        if (year % 4 == 0) return true;
        return false;
    }

    static boolean isJulianLeapYear(int year) {
        return year % 4 == 0;
    }

    static int getMonthDays(int year, int month, int system) {
        int[] daySeq = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        if (month != 1) return daySeq[month];
        if (system == SYSTEM_GREORIAN && isGreorianLeapYear(year)
            || system == SYSTEM_JULIAN && isJulianLeapYear(year)) {
            return daySeq[1] + 1;
        } else if (system == SYSTEM_SP) {
        	return daySeq[1] - 13;
        }
        return daySeq[1];
    }

    static int getMonthByDayOfYear(int year, int dayOfYear, int system) {
        int month = 0;
        for (; month < 11; month++) {
            int monthDays = getMonthDays(year, month, system);
            if (dayOfYear <= monthDays) break;
            dayOfYear -= monthDays;
        }
        return month;
    }

    static int getDayOfMonthByDayOfYear(int year, int dayOfYear, int system) {
    	int month = 0;
        for (; month < 11; month++) {
            int monthDays = getMonthDays(year, month, system);
            if (dayOfYear <= monthDays) break;
            dayOfYear -= monthDays;
        }
        if (system == SYSTEM_SP && month == 1) {
        	return dayOfYear + 13;
        }
        return dayOfYear;
    }
	
	public static void main(String[] args) throws IOException, CloneNotSupportedException {
		Testb b = new Testb();
		
	}
}
