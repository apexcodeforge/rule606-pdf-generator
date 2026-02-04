package com.rule606.pdf;

import com.rule606.model.a1.*;
import com.rule606.util.DateFormatter;
import com.rule606.util.NumberFormatter;
import org.apache.pdfbox.pdmodel.PDDocument;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class A1PdfGenerator {

    private static final String[] SECTION_HEADERS = {"S&P 500 Stocks", "Non-S&P 500 Stocks", "Options"};

    private static final String[] SUMMARY_HEADERS = {
            "Non-Directed Orders as % of All Orders",
            "Market Orders as % of Non-Directed Orders",
            "Marketable Limit Orders as % of Non-Directed Orders",
            "Non-Marketable Limit Orders as % of Non-Directed Orders",
            "Other Orders as % of Non-Directed Orders"
    };

    private static final String[] SUMMARY_TAGS = {
            "ndoPct", "ndoMarketPct", "ndoMarketableLimitPct",
            "ndoNonmarketableLimitPct", "ndoOtherPct"
    };

    private static final String[] VENUE_HEADERS = {
            "Venue - \nNon-directed Order Flow",
            "Non-Directed Orders (%)",
            "Market Orders (%)",
            "Marketable Limit Orders (%)",
            "Non-Marketable Limit Orders (%)",
            "Other Orders (%)",
            "Net Payment Paid/Received for Market Orders(USD)",
            "Net Payment Paid/Received for Market Orders(cents per hundred shares)",
            "Net Payment Paid/Received for Marketable Limit Orders(USD)",
            "Net Payment Paid/Received for Marketable Limit Orders(cents per hundred shares)",
            "Net Payment Paid/Received for Non-Marketable Limit Orders(USD)",
            "Net Payment Paid/Received for Non-Marketable Limit Orders(cents per hundred shares)",
            "Net Payment Paid/Received for Other Orders(USD)",
            "Net Payment Paid/Received for Other Orders(cents per hundred shares)"
    };

    private static final String[] VENUE_TAGS = {
            "orderPct", "marketPct", "marketableLimitPct",
            "nonMarketableLimitPct", "otherPct",
            "netPmtPaidRecvMarketOrdersUsd", "netPmtPaidRecvMarketOrdersCph",
            "netPmtPaidRecvMarketableLimitOrdersUsd", "netPmtPaidRecvMarketableLimitOrdersCph",
            "netPmtPaidRecvNonMarketableLimitOrdersUsd", "netPmtPaidRecvNonMarketableLimitOrdersCph",
            "netPmtPaidRecvOtherOrdersUsd", "netPmtPaidRecvOtherOrdersCph"
    };

    public void generate(A1Report report, File outputFile) throws IOException {
        try (PDDocument doc = new PDDocument()) {
            PdfTableRenderer renderer = new PdfTableRenderer(doc, true); // landscape

            String title = report.getBrokerDealer() + " - Held NMS Stocks and Options Order Routing Public Report";
            renderer.drawCenteredText(title, PdfStyles.A1_HEADER_SIZE, true);

            if (report.getTimestamp() != null) {
                renderer.drawCenteredText("Generated on " + DateFormatter.formatTimestamp(report.getTimestamp()),
                        PdfStyles.A1_HEADER4_SIZE, true);
            }

            renderer.addVerticalSpace(5);
            String quarterLabel = DateFormatter.getQuarterLabel(report.getQuarter()) + ", " + report.getYear();
            renderer.drawCenteredText(quarterLabel, PdfStyles.A1_HEADER3_SIZE, true);
            renderer.addVerticalSpace(5);

            for (MonthlyData monthly : report.getMonthlyData()) {
                String monthName = DateFormatter.getMonthName(monthly.getMonth());
                String year = monthly.getYear();

                for (int s = 0; s < 3; s++) {
                    OrderRoutingSection section = monthly.getSectionByIndex(s);
                    if (section == null) continue;

                    drawA1Section(renderer, monthName, year, SECTION_HEADERS[s], section);

                    if (s < 2) {
                        renderer.drawHorizontalLine(1, true); // dashed
                    }
                }
            }

            renderer.close();
            doc.save(outputFile);
        }
    }

    private void drawA1Section(PdfTableRenderer renderer, String monthName, String year,
                               String sectionHeader, OrderRoutingSection section) throws IOException {
        renderer.addVerticalSpace(5);
        renderer.drawLeftText(monthName + " " + year, PdfStyles.A1_SECTION_HEADER_SIZE, true);
        renderer.drawHorizontalLine(1);
        renderer.addVerticalSpace(3);
        renderer.drawLeftText(sectionHeader, 10, true);

        // Summary table
        if (!section.isEmpty()) {
            renderer.addVerticalSpace(3);
            renderer.drawLeftText("Summary", PdfStyles.A1_SUB_SECTION_HEADER_SIZE, true);

            float[] summaryWidths = {60, 60, 60, 60, 60};
            String[][] summaryData = new String[2][5];
            summaryData[0] = SUMMARY_HEADERS;

            boolean empty = section.isEmpty();
            for (int i = 0; i < SUMMARY_TAGS.length; i++) {
                String val = section.getSummaryValueByTag(SUMMARY_TAGS[i]);
                summaryData[1][i] = (empty || val == null || val.isEmpty()) ? "-" : NumberFormatter.addCommas(val);
            }

            PdfTableRenderer.CellStyle[] hStyles = createHeaderStyles(5);
            PdfTableRenderer.CellStyle[] dStyles = createValueStyles(5, "right");
            renderer.drawTable(summaryData, summaryWidths, hStyles, dStyles, 1);
        }

        // Venue table
        List<Venue> venues = section.getVenues();
        if (!venues.isEmpty()) {
            renderer.addVerticalSpace(3);
            renderer.drawLeftText("Venues", PdfStyles.A1_SUB_SECTION_HEADER_SIZE, true);

            float[] venueWidths = new float[14];
            venueWidths[0] = -1; // auto
            for (int i = 1; i < 14; i++) venueWidths[i] = -1; // all auto

            int rows = venues.size() + 1;
            String[][] venueData = new String[rows][14];
            venueData[0] = VENUE_HEADERS;

            for (int i = 0; i < venues.size(); i++) {
                Venue v = venues.get(i);
                venueData[i + 1][0] = v.getName();
                for (int j = 0; j < VENUE_TAGS.length; j++) {
                    String val = v.getValueByTag(VENUE_TAGS[j]);
                    venueData[i + 1][j + 1] = (val == null || val.isEmpty()) ? "\u200B" : NumberFormatter.addCommas(val);
                }
            }

            PdfTableRenderer.CellStyle[] vhStyles = createHeaderStyles(14);
            PdfTableRenderer.CellStyle[] vdStyles = new PdfTableRenderer.CellStyle[14];
            vdStyles[0] = createStyle(PdfStyles.A1_TABLE_VALUE_SIZE, false, null, "center");
            for (int i = 1; i < 14; i++) {
                vdStyles[i] = createStyle(PdfStyles.A1_TABLE_VALUE_SIZE, false, null, "right");
            }
            renderer.drawTable(venueData, venueWidths, vhStyles, vdStyles, 1);
        }

        // Material aspects
        List<Venue> venuesWithMa = venues.stream()
                .filter(v -> v.getMaterialAspects() != null && !v.getMaterialAspects().isEmpty())
                .toList();
        if (!venuesWithMa.isEmpty()) {
            renderer.addVerticalSpace(3);
            renderer.drawLeftText("Material Aspects:", PdfStyles.A1_SUB_SECTION_HEADER_SIZE, true);
            for (Venue v : venuesWithMa) {
                renderer.drawBulletedItem(v.getName() + ":", v.getMaterialAspects(), PdfStyles.A1_TEXT_SIZE);
            }
        }
    }

    private PdfTableRenderer.CellStyle[] createHeaderStyles(int cols) {
        PdfTableRenderer.CellStyle[] styles = new PdfTableRenderer.CellStyle[cols];
        for (int i = 0; i < cols; i++) {
            styles[i] = createStyle(PdfStyles.A1_TABLE_HEADER_SIZE, true, PdfStyles.FILL_COLOR, "center");
        }
        return styles;
    }

    private PdfTableRenderer.CellStyle[] createValueStyles(int cols, String alignment) {
        PdfTableRenderer.CellStyle[] styles = new PdfTableRenderer.CellStyle[cols];
        for (int i = 0; i < cols; i++) {
            styles[i] = createStyle(PdfStyles.A1_TABLE_VALUE_SIZE, false, null, alignment);
        }
        return styles;
    }

    private PdfTableRenderer.CellStyle createStyle(float fontSize, boolean bold, java.awt.Color fillColor, String alignment) {
        PdfTableRenderer.CellStyle style = new PdfTableRenderer.CellStyle();
        style.fontSize = fontSize;
        style.bold = bold;
        style.fillColor = fillColor;
        style.alignment = alignment;
        return style;
    }
}
