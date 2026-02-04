package com.rule606;

import com.rule606.cli.CliArgs;
import com.rule606.model.ReportType;
import com.rule606.model.a1.A1Report;
import com.rule606.model.b1.B1Report;
import com.rule606.model.b3.B3Report;
import com.rule606.pdf.A1PdfGenerator;
import com.rule606.pdf.B1PdfGenerator;
import com.rule606.pdf.B3PdfGenerator;
import com.rule606.xml.XmlParser;
import com.rule606.xml.XmlValidator;

import java.io.File;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        try {
            CliArgs cli = CliArgs.parse(args);

            File xmlFile = cli.getXmlFile();
            File outputFile = cli.getOutputFile();

            if (!xmlFile.exists()) {
                System.err.println("XML file not found: " + xmlFile.getPath());
                System.exit(1);
            }

            // Validate against XSD if provided
            if (cli.getXsdFile() != null) {
                File xsdFile = cli.getXsdFile();
                if (!xsdFile.exists()) {
                    System.err.println("XSD file not found: " + xsdFile.getPath());
                    System.exit(1);
                }
                try {
                    XmlValidator.validate(xsdFile, xmlFile);
                    System.out.println("XML validation passed.");
                } catch (Exception e) {
                    System.err.println("XML validation failed: " + e.getMessage());
                    System.exit(2);
                }
            }

            // Parse XML
            XmlParser parser = new XmlParser();
            ReportType reportType = parser.parse(xmlFile);
            System.out.println("Detected report type: " + reportType);

            // Generate PDF
            switch (reportType) {
                case A1 -> {
                    A1Report report = parser.parseA1();
                    new A1PdfGenerator().generate(report, outputFile);
                    System.out.println("A1 report generated: " + outputFile.getPath());
                }
                case B1 -> {
                    B1Report report = parser.parseB1();
                    List<File> files = new B1PdfGenerator().generate(report, outputFile);
                    for (File f : files) {
                        System.out.println("B1 report generated: " + f.getPath());
                    }
                }
                case B3 -> {
                    B3Report report = parser.parseB3();
                    new B3PdfGenerator().generate(report, outputFile);
                    System.out.println("B3 report generated: " + outputFile.getPath());
                }
            }

            System.exit(0);

        } catch (IllegalArgumentException e) {
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace(System.err);
            System.exit(3);
        }
    }
}
