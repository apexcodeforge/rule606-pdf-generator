package com.rule606;

import com.rule606.xml.XmlUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class XmlUtilsTest {

    @Test
    void addCommas() {
        assertEquals("1,000", XmlUtils.addCommas("1000"));
        assertEquals("1,234,567.89", XmlUtils.addCommas("1234567.89"));
        assertEquals("100", XmlUtils.addCommas("100"));
        assertEquals("0.0021", XmlUtils.addCommas("0.0021"));
    }

    @Test
    void addCommasEmpty() {
        assertEquals("", XmlUtils.addCommas(""));
        assertNull(XmlUtils.addCommas(null));
    }
}
