package com.rule606.util;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

public class DateFormatter {

    // Target timezone: Eastern Time (handles both EDT and EST automatically)
    private static final ZoneId EASTERN = ZoneId.of("America/New_York");

    // Format: "Wed Apr 01 2020 18:55:48 GMT-0400 (Eastern Daylight Time)"
    private static final DateTimeFormatter JS_DATE_FORMAT = DateTimeFormatter.ofPattern(
            "EEE MMM dd yyyy HH:mm:ss 'GMT'XX", Locale.US);

    public static String formatTimestamp(String isoTimestamp) {
        if (isoTimestamp == null || isoTimestamp.isEmpty()) return "";
        try {
            ZonedDateTime zdt = ZonedDateTime.parse(isoTimestamp);
            // Convert to Eastern time
            ZonedDateTime eastern = zdt.withZoneSameInstant(EASTERN);
            String formatted = eastern.format(JS_DATE_FORMAT);
            // Add timezone name in parentheses
            String tzName = eastern.getZone().getDisplayName(
                    java.time.format.TextStyle.FULL, Locale.US);
            // Use the specific daylight/standard name
            boolean isDst = eastern.getZone().getRules().isDaylightSavings(eastern.toInstant());
            String tzDisplayName = isDst ? "Eastern Daylight Time" : "Eastern Standard Time";
            return formatted + " (" + tzDisplayName + ")";
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
