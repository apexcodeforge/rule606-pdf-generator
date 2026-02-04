package com.rule606;

import com.rule606.model.ReportType;
import com.rule606.model.a1.A1Report;
import com.rule606.model.b1.B1Report;
import com.rule606.model.b3.B3Report;
import com.rule606.xml.XmlParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class XmlParserTest {

    private static final String SAMPLES_DIR = "samples";

    private File sampleFile(String name) {
        return new File(SAMPLES_DIR, name);
    }

    @Test
    void detectA1ReportType() throws Exception {
        File f = sampleFile("606a1_held_order_public_report.xml");
        if (!f.exists()) return;
        XmlParser parser = new XmlParser();
        ReportType type = parser.parse(f);
        assertEquals(ReportType.A1, type);
    }

    @Test
    void parseA1Report() throws Exception {
        File f = sampleFile("606a1_held_order_public_report.xml");
        if (!f.exists()) return;
        XmlParser parser = new XmlParser();
        parser.parse(f);
        A1Report report = parser.parseA1();
        assertEquals("B. Roker", report.getBrokerDealer());
        assertEquals("2020", report.getYear());
        assertEquals("1", report.getQuarter());
        assertNotNull(report.getTimestamp());
        assertFalse(report.getMonthlyData().isEmpty());
    }

    @Test
    void detectB1ReportType() throws Exception {
        File f = sampleFile("606b1_held_exempt_not_held_order_routing_customer_report.xml");
        if (!f.exists()) return;
        XmlParser parser = new XmlParser();
        ReportType type = parser.parse(f);
        assertEquals(ReportType.B1, type);
    }

    @Test
    void parseB1Report() throws Exception {
        File f = sampleFile("606b1_held_exempt_not_held_order_routing_customer_report.xml");
        if (!f.exists()) return;
        XmlParser parser = new XmlParser();
        parser.parse(f);
        B1Report report = parser.parseB1();
        assertEquals("B. Roker", report.getBrokerDealer());
        assertEquals("J. Customer", report.getCustomer());
        assertEquals(3, report.getSections().size());
    }

    @Test
    void detectB3ReportType() throws Exception {
        File f = sampleFile("606b3_not_held_order_handling_customer_report.xml");
        if (!f.exists()) return;
        XmlParser parser = new XmlParser();
        ReportType type = parser.parse(f);
        assertEquals(ReportType.B3, type);
    }

    @Test
    void parseB3Report() throws Exception {
        File f = sampleFile("606b3_not_held_order_handling_customer_report.xml");
        if (!f.exists()) return;
        XmlParser parser = new XmlParser();
        parser.parse(f);
        B3Report report = parser.parseB3();
        assertEquals("B. Roker", report.getBrokerDealer());
        assertEquals("J. Customer", report.getCustomer());
        assertNotNull(report.getDirected());
        assertNotNull(report.getNonDirected());
    }

    @Test
    void reportTypeFromRootElement() {
        assertEquals(ReportType.A1, ReportType.fromRootElement("heldOrderRoutingPublicReport"));
        assertEquals(ReportType.B1, ReportType.fromRootElement("heldExemptNotHeldOrderRoutingCustomerReport"));
        assertEquals(ReportType.B3, ReportType.fromRootElement("notHeldOrderHandlingCustomerReport"));
        assertNull(ReportType.fromRootElement("unknown"));
    }
}
