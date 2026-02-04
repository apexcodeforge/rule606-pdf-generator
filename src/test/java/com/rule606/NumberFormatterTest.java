package com.rule606;

import com.rule606.util.NumberFormatter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NumberFormatterTest {

    @Test
    void addCommasToLargeNumber() {
        assertEquals("1,000", NumberFormatter.addCommas("1000"));
        assertEquals("1,000,000", NumberFormatter.addCommas("1000000"));
        assertEquals("100", NumberFormatter.addCommas("100"));
    }

    @Test
    void addCommasWithDecimal() {
        assertEquals("1,000.50", NumberFormatter.addCommas("1000.50"));
        assertEquals("1,234,567.89", NumberFormatter.addCommas("1234567.89"));
    }

    @Test
    void addCommasNoChange() {
        assertEquals("0", NumberFormatter.addCommas("0"));
        assertEquals("999", NumberFormatter.addCommas("999"));
        assertEquals("0.0021", NumberFormatter.addCommas("0.0021"));
    }

    @Test
    void addCommasEmptyOrNull() {
        assertEquals("", NumberFormatter.addCommas(""));
        assertNull(NumberFormatter.addCommas(null));
    }

    @Test
    void addCommasNegative() {
        assertEquals("-1,000", NumberFormatter.addCommas("-1000"));
        assertEquals("-0.0030", NumberFormatter.addCommas("-0.0030"));
    }
}
