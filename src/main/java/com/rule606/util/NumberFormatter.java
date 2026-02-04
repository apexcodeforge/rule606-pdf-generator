package com.rule606.util;

public class NumberFormatter {

    public static String addCommas(String number) {
        if (number == null || number.isEmpty()) return number;
        String[] parts = number.split("\\.");
        String intPart = parts[0].replaceAll("(\\d)(?=(\\d{3})+(?!\\d))", "$1,");
        if (parts.length > 1) {
            return intPart + "." + parts[1];
        }
        return intPart;
    }
}
