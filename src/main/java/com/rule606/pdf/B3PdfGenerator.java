package com.rule606.pdf;

import com.rule606.model.b3.*;
import com.rule606.util.DateFormatter;
import com.rule606.util.NumberFormatter;
import org.apache.pdfbox.pdmodel.PDDocument;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class B3PdfGenerator {

    private static final String[] SUMMARY_HEADERS = {
            "Total Shares Sent to Broker/Dealer",
            "Total Number of Shares Executed as Principal",
            "Total Orders Exposed (Actionable IOI)"
    };

    private static final String[] SUMMARY_TAGS = {"sentShr", "executedAsPrincipalShr", "ioiExposedOrd"};

    private static final String[] DETAIL_HEADERS = {
            "Venue",
            "Total Shares Routed",
            "Total Shares Routed Marked IOC",
            "Total Shares Routed that were further Routable",
            "Average Order Size Routed",
            "Total Shares Executed",
            "Fill Rate",
            "Average Fill Size",
            "Average Net Execution Fee or (Rebate)",
            "Total Shares Executed at Midpoint",
            "Percentage of Shares Executed at Midpoint",
            "Total Shares Executed that were Priced at the Near Side",
            "Percentage of Total Shares Executed that were Priced at the Near Side",
            "Total Shares Executed that were Priced at the Far Side",
            "Percentage of Total Shares Executed that were Priced at the Far Side",
            "Total Number of Shares that Provided Liquidity",
            "Percentage of Executed Shares that Provided Liquidity",
            "Average Duration of Orders that Provided Liquidity (in msec)",
            "Average Net Execution Rebate (or Fee Paid) for Shares that Provided Liquidity",
            "Total Number of Shares that Removed Liquidity",
            "Percentage of Executed Shares that Removed Liquidity",
            "Average Net Execution Fee (or rebate received) for Shares that Removed Liquidity"
    };

    private static final String[] DETAIL_TAGS = {
            "routedShr", "iocRoutedShr", "furtherRoutableShr",
            "orderSizeShr", "executedShr", "filledPct", "fillSizeShr",
            "netFeeOrRebateCph", "midpointShr", "midpointPct", "nearsideShr",
            "nearsidePct", "farsideShr", "farsidePct", "providedLiqudityShr",
            "providedLiquidityPct", "orderDurationMsec", "providedLiquidityNetCph",
            "removedLiquidityShr", "removedLiquidityPct", "removedLiquidityNetCph"
    };

    public void generate(B3Report report, File outputFile) throws IOException {
        try (PDDocument doc = new PDDocument()) {
            PdfTableRenderer renderer = new PdfTableRenderer(doc, true); // landscape

            String title = report.getBrokerDealer() + " - Not-Held NMS Stocks Order Handling Report";
            renderer.drawCenteredText(title, PdfStyles.B3_HEADER_SIZE, true);

            if (report.getTimestamp() != null) {
                renderer.drawCenteredText("Generated on " + DateFormatter.formatTimestamp(report.getTimestamp()),
                        PdfStyles.B3_HEADER4_SIZE, true);
            }

            renderer.drawMixedLine(new String[][]{
                    {" For ", String.valueOf(PdfStyles.B3_HEADER3_SIZE), "true"},
                    {report.getCustomer(), String.valueOf(PdfStyles.B3_TEXT_SIZE), "false"}
            });

            renderer.drawMixedLine(new String[][]{
                    {" Reporting Period ", String.valueOf(PdfStyles.B3_HEADER3_SIZE), "true"},
                    {report.getStartDate() + " to " + report.getEndDate(),
                            String.valueOf(PdfStyles.B3_TEXT_SIZE), "false"}
            });

            // Collect all unique year-month keys and sort them
            Set<String> monthKeys = new TreeSet<>();
            Map<String, HandlingMonthly> directedByMonth = new HashMap<>();
            Map<String, HandlingMonthly> nonDirectedByMonth = new HashMap<>();

            if (report.getDirected() != null) {
                for (HandlingMonthly m : report.getDirected().getMonthlyData()) {
                    String key = m.getYear() + "-" + String.format("%02d", m.getMonth());
                    monthKeys.add(key);
                    directedByMonth.put(key, m);
                }
            }
            if (report.getNonDirected() != null) {
                for (HandlingMonthly m : report.getNonDirected().getMonthlyData()) {
                    String key = m.getYear() + "-" + String.format("%02d", m.getMonth());
                    monthKeys.add(key);
                    nonDirectedByMonth.put(key, m);
                }
            }

            // Iterate by month, then by directed/non-directed
            for (String monthKey : monthKeys) {
                HandlingMonthly directed = directedByMonth.get(monthKey);
                HandlingMonthly nonDirected = nonDirectedByMonth.get(monthKey);

                // Directed first
                if (directed != null) {
                    drawMonthlySection(renderer, directed, true);
                }
                // Then non-directed
                if (nonDirected != null) {
                    drawMonthlySection(renderer, nonDirected, false);
                }
            }

            renderer.close();
            doc.save(outputFile);
        }
    }

    private void drawMonthlySection(PdfTableRenderer renderer, HandlingMonthly monthly, boolean isDirected) throws IOException {
        String monthName = DateFormatter.getMonthName(monthly.getMonth());
        String year = monthly.getYear();
        String dirLabel = isDirected ? "Directed" : "Non-directed";

        // Month header
        renderer.addVerticalSpace(5);
        renderer.drawLeftText(monthName + " " + year + " - " + dirLabel + " Orders",
                PdfStyles.B3_SECTION_HEADER_SIZE, true);
        renderer.drawHorizontalLine(1);

        // Summary table
        drawSummaryTable(renderer, monthly);

        // IOI exposed venues
        List<String> ioiVenues = monthly.getIoiExposedVenues();
        if (!ioiVenues.isEmpty()) {
            renderer.drawLeftText("Actionable IOI Exposed Venues",
                    PdfStyles.B3_SUB_SECTION_HEADER_SIZE, true);

            int ioiRows = ioiVenues.size() + 1;
            String[][] ioiData = new String[ioiRows][1];
            ioiData[0] = new String[]{"Venues"};
            for (int i = 0; i < ioiVenues.size(); i++) {
                ioiData[i + 1] = new String[]{ioiVenues.get(i)};
            }
            float[] ioiWidths = {-1}; // auto
            PdfTableRenderer.CellStyle[] ioiHStyles = {createStyle(
                    PdfStyles.B3_TABLE_HEADER_SIZE, true, PdfStyles.FILL_COLOR, "center")};
            PdfTableRenderer.CellStyle[] ioiDStyles = {createStyle(
                    PdfStyles.B3_TABLE_NAME_VALUE_SIZE, false, null, "left")};
            renderer.drawTable(ioiData, ioiWidths, ioiHStyles, ioiDStyles, 1);
        }

        // Detail table
        List<RoutingVenue> routingVenues = monthly.getRoutingVenues();
        if (!routingVenues.isEmpty()) {
            renderer.drawLeftText("Order Routing Venues",
                    PdfStyles.B3_SUB_SECTION_HEADER_SIZE, true);

            int detailRows = routingVenues.size() + 1;
            String[][] detailData = new String[detailRows][22];
            detailData[0] = DETAIL_HEADERS;

            for (int i = 0; i < routingVenues.size(); i++) {
                RoutingVenue rv = routingVenues.get(i);
                detailData[i + 1][0] = rv.getName();
                for (int j = 0; j < DETAIL_TAGS.length; j++) {
                    String val = rv.getValueByTag(DETAIL_TAGS[j]);
                    detailData[i + 1][j + 1] = (val == null || val.isEmpty()) ? "\u200B" : val;
                }
            }

            float[] detailWidths = new float[22];
            detailWidths[0] = PdfStyles.B3_DETAIL_VENUE_COL_WIDTH;
            for (int i = 1; i < 22; i++) detailWidths[i] = -1; // auto

            PdfTableRenderer.CellStyle[] detailHStyles = new PdfTableRenderer.CellStyle[22];
            for (int i = 0; i < 22; i++) {
                detailHStyles[i] = createStyle(
                        PdfStyles.B3_TABLE_HEADER_SIZE, true, PdfStyles.FILL_COLOR, "center");
            }

            PdfTableRenderer.CellStyle[] detailDStyles = new PdfTableRenderer.CellStyle[22];
            detailDStyles[0] = createStyle(PdfStyles.B3_TABLE_NAME_VALUE_SIZE, false, null, "left");
            for (int i = 1; i < 22; i++) {
                detailDStyles[i] = createStyle(PdfStyles.B3_TABLE_VALUE_SIZE, false, null, "right");
            }

            renderer.drawTable(detailData, detailWidths, detailHStyles, detailDStyles, 1);
        }
    }

    private void drawSummaryTable(PdfTableRenderer renderer, HandlingMonthly monthly) throws IOException {
        renderer.drawLeftText("Summary", PdfStyles.B3_SUB_SECTION_HEADER_SIZE, true);

        String[][] summaryData = new String[2][3];
        summaryData[0] = SUMMARY_HEADERS;
        for (int i = 0; i < SUMMARY_TAGS.length; i++) {
            String val = monthly.getSummaryValueByTag(SUMMARY_TAGS[i]);
            summaryData[1][i] = (val == null || val.isEmpty()) ? "\u200B" : NumberFormatter.addCommas(val);
        }

        float[] summaryWidths = {-1, -1, -1}; // auto
        PdfTableRenderer.CellStyle[] shStyles = new PdfTableRenderer.CellStyle[3];
        PdfTableRenderer.CellStyle[] sdStyles = new PdfTableRenderer.CellStyle[3];
        for (int i = 0; i < 3; i++) {
            shStyles[i] = createStyle(PdfStyles.B3_TABLE_HEADER_SIZE, true, PdfStyles.FILL_COLOR, "center");
            sdStyles[i] = createStyle(PdfStyles.B3_TABLE_VALUE_SIZE, false, null, "right");
        }
        renderer.drawTable(summaryData, summaryWidths, shStyles, sdStyles, 1);
    }

    private PdfTableRenderer.CellStyle createStyle(float fontSize, boolean bold,
                                                    java.awt.Color fillColor, String alignment) {
        PdfTableRenderer.CellStyle style = new PdfTableRenderer.CellStyle();
        style.fontSize = fontSize;
        style.bold = bold;
        style.fillColor = fillColor;
        style.alignment = alignment;
        return style;
    }
}
