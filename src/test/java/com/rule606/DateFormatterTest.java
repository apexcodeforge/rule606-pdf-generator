package com.rule606;

import com.rule606.util.DateFormatter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DateFormatterTest {

    @Test
    void formatTimestamp() {
        String result = DateFormatter.formatTimestamp("2020-04-01T22:55:48Z");
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertTrue(result.contains("2020"));
    }

    @Test
    void formatTimestampEmpty() {
        assertEquals("", DateFormatter.formatTimestamp(""));
        assertEquals("", DateFormatter.formatTimestamp(null));
    }

    @Test
    void getMonthName() {
        assertEquals("January", DateFormatter.getMonthName(1));
        assertEquals("December", DateFormatter.getMonthName(12));
        assertEquals("", DateFormatter.getMonthName(0));
        assertEquals("", DateFormatter.getMonthName(13));
    }

    @Test
    void getQuarterLabel() {
        assertEquals("1st Quarter", DateFormatter.getQuarterLabel("1"));
        assertEquals("2nd Quarter", DateFormatter.getQuarterLabel("2"));
        assertEquals("3rd Quarter", DateFormatter.getQuarterLabel("3"));
        assertEquals("4th Quarter", DateFormatter.getQuarterLabel("4"));
        assertEquals("", DateFormatter.getQuarterLabel("5"));
    }
}
