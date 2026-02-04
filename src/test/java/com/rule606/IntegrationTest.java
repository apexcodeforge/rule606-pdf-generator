package com.rule606;

import com.rule606.model.ReportType;
import com.rule606.model.a1.A1Report;
import com.rule606.model.b1.B1Report;
import com.rule606.model.b3.B3Report;
import com.rule606.pdf.A1PdfGenerator;
import com.rule606.pdf.B1PdfGenerator;
import com.rule606.pdf.B3PdfGenerator;
import com.rule606.xml.XmlParser;
import com.rule606.xml.XmlValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class IntegrationTest {

    private static final File XSD_FILE = new File("oh-20191231.xsd");
    private static final String SAMPLES_DIR = "samples";

    @TempDir
    Path tempDir;

    @Test
    void generateA1Report() throws Exception {
        File xmlFile = new File(SAMPLES_DIR, "606a1_held_order_public_report.xml");
        if (!xmlFile.exists() || !XSD_FILE.exists()) return;

        XmlValidator.validate(XSD_FILE, xmlFile);

        XmlParser parser = new XmlParser();
        ReportType type = parser.parse(xmlFile);
        assertEquals(ReportType.A1, type);

        A1Report report = parser.parseA1();
        File outFile = tempDir.resolve("test_a1.pdf").toFile();
        new A1PdfGenerator().generate(report, outFile);
        assertTrue(outFile.exists());
        assertTrue(outFile.length() > 0);
    }

    @Test
    void generateB1Report() throws Exception {
        File xmlFile = new File(SAMPLES_DIR, "606b1_held_exempt_not_held_order_routing_customer_report.xml");
        if (!xmlFile.exists() || !XSD_FILE.exists()) return;

        XmlValidator.validate(XSD_FILE, xmlFile);

        XmlParser parser = new XmlParser();
        ReportType type = parser.parse(xmlFile);
        assertEquals(ReportType.B1, type);

        B1Report report = parser.parseB1();
        File outFile = tempDir.resolve("test_b1.pdf").toFile();
        List<File> files = new B1PdfGenerator().generate(report, outFile);
        assertFalse(files.isEmpty());
        for (File f : files) {
            assertTrue(f.exists());
            assertTrue(f.length() > 0);
        }
    }

    @Test
    void generateB3Report() throws Exception {
        File xmlFile = new File(SAMPLES_DIR, "606b3_not_held_order_handling_customer_report.xml");
        if (!xmlFile.exists() || !XSD_FILE.exists()) return;

        XmlValidator.validate(XSD_FILE, xmlFile);

        XmlParser parser = new XmlParser();
        ReportType type = parser.parse(xmlFile);
        assertEquals(ReportType.B3, type);

        B3Report report = parser.parseB3();
        File outFile = tempDir.resolve("test_b3.pdf").toFile();
        new B3PdfGenerator().generate(report, outFile);
        assertTrue(outFile.exists());
        assertTrue(outFile.length() > 0);
    }
}
