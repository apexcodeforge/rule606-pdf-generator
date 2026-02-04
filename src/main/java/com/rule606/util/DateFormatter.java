package com.rule606.util;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateFormatter {

    public static String formatTimestamp(String isoTimestamp) {
        if (isoTimestamp == null || isoTimestamp.isEmpty()) return "";
        try {
            ZonedDateTime zdt = ZonedDateTime.parse(isoTimestamp);
            return zdt.format(DateTimeFormatter.RFC_1123_DATE_TIME);
        } catch (DateTimeParseException e) {
            return isoTimestamp;
        }
    }

    public static String getMonthName(int month) {
        return switch (month) {
            case 1 -> "January";
            case 2 -> "February";
            case 3 -> "March";
            case 4 -> "April";
            case 5 -> "May";
            case 6 -> "June";
            case 7 -> "July";
            case 8 -> "August";
            case 9 -> "September";
            case 10 -> "October";
            case 11 -> "November";
            case 12 -> "December";
            default -> "";
        };
    }

    public static String getQuarterLabel(String qtr) {
        return switch (qtr) {
            case "1" -> "1st Quarter";
            case "2" -> "2nd Quarter";
            case "3" -> "3rd Quarter";
            case "4" -> "4th Quarter";
            default -> "";
        };
    }
}
