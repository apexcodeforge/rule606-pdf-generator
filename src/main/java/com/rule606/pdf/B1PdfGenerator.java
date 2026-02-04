package com.rule606.pdf;

import com.rule606.model.b1.*;
import com.rule606.util.DateFormatter;
import org.apache.pdfbox.pdmodel.PDDocument;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class B1PdfGenerator {

    private static final String[] TABLE_HEADERS = {"Order ID", "Type", "Venue", "Time of Transaction (UTC)"};
    private static final int THRESHOLD = 500;

    public List<File> generate(B1Report report, File outputFile) throws IOException {
        List<File> outputFiles = new ArrayList<>();
        String baseName = removeExtension(outputFile.getPath());
        boolean hasOutput = false;

        for (int sectionIdx = 0; sectionIdx < report.getSections().size(); sectionIdx++) {
            OrderSection section = report.getSections().get(sectionIdx);
            if (section.getOrders().isEmpty()) continue;

            String title = section.getTitle();
            List<List<Order>> chunks = chunkOrders(section.getOrders());

            for (int chunkIdx = 0; chunkIdx < chunks.size(); chunkIdx++) {
                hasOutput = true;
                List<Order> chunk = chunks.get(chunkIdx);
                boolean truncated = (chunks.size() > 1 && chunkIdx == chunks.size() - 1
                        && getTotalRows(section.getOrders()) > THRESHOLD);

                String fileName = baseName + "_section_" + (sectionIdx + 1)
                        + (chunkIdx > 0 ? "_file_" + (chunkIdx + 1) : "") + ".pdf";
                File outFile = new File(fileName);

                try (PDDocument doc = new PDDocument()) {
                    PdfTableRenderer renderer = new PdfTableRenderer(doc, false); // portrait

                    // Header
                    renderer.drawCenteredText(
                            report.getBrokerDealer() + " - " + title,
                            PdfStyles.B1_HEADER_SIZE, true);

                    if (report.getTimestamp() != null) {
                        renderer.drawMixedLine(new String[][]{
                                {"Generated on ", String.valueOf(PdfStyles.B1_HEADER3_SIZE), "true"},
                                {DateFormatter.formatTimestamp(report.getTimestamp()),
                                        String.valueOf(PdfStyles.B1_TABLE_VALUE_SIZE), "false"}
                        });
                    }

                    renderer.drawMixedLine(new String[][]{
                            {" For ", String.valueOf(PdfStyles.B1_HEADER3_SIZE), "true"},
                            {report.getCustomer(), String.valueOf(PdfStyles.B1_TABLE_VALUE_SIZE), "false"}
                    });

                    renderer.drawMixedLine(new String[][]{
                            {" Reporting Period ", String.valueOf(PdfStyles.B1_HEADER3_SIZE), "true"},
                            {report.getStartDate() + " to " + report.getEndDate(),
                                    String.valueOf(PdfStyles.B1_TABLE_VALUE_SIZE), "false"}
                    });

                    renderer.addVerticalSpace(5);

                    // Section title
                    String sectionTitle = "Orders - " + title
                            + (chunks.size() > 1 ? " Part " + chunkIdx : "");
                    renderer.drawLeftText(sectionTitle, PdfStyles.B1_SECTION_HEADER_SIZE, true);

                    // Build table rows with border control
                    List<PdfTableRenderer.B1Row> rows = buildB1Rows(chunk);

                    float[] colWidths = {-1, -1, -1, -1}; // all auto

                    PdfTableRenderer.CellStyle headerStyle = new PdfTableRenderer.CellStyle();
                    headerStyle.fontSize = PdfStyles.B1_TABLE_HEADER_SIZE;
                    headerStyle.bold = true;
                    headerStyle.fillColor = PdfStyles.FILL_COLOR;
                    headerStyle.alignment = "center";

                    PdfTableRenderer.CellStyle dataStyle = new PdfTableRenderer.CellStyle();
                    dataStyle.fontSize = PdfStyles.B1_TABLE_VALUE_SIZE;
                    dataStyle.bold = false;
                    dataStyle.alignment = "left";

                    renderer.drawB1Table(rows, colWidths, headerStyle, dataStyle, 1);

                    // Truncation warning
                    if (truncated) {
                        renderer.addVerticalSpace(5);
                        String failText = "Unable to render more than " + THRESHOLD + " " + title + " transactions.\n"
                                + "Open larger 606(b)(1) XML files in a spreadsheet or other application.";
                        float[] failWidths = {-1};
                        String[][] failData = {{failText}};
                        PdfTableRenderer.CellStyle[] failStyle = new PdfTableRenderer.CellStyle[1];
                        failStyle[0] = new PdfTableRenderer.CellStyle();
                        failStyle[0].fontSize = PdfStyles.B1_FAIL_HEADER_SIZE;
                        failStyle[0].bold = true;
                        failStyle[0].fillColor = PdfStyles.FAIL_COLOR;
                        failStyle[0].alignment = "center";
                        renderer.drawTable(failData, failWidths, failStyle, failStyle, 1);
                    }

                    renderer.close();
                    doc.save(outFile);
                }
                outputFiles.add(outFile);
            }
        }

        if (!hasOutput) {
            System.err.println("No transactions found, no output files generated.");
        }

        return outputFiles;
    }

    private List<List<Order>> chunkOrders(List<Order> orders) {
        List<List<Order>> chunks = new ArrayList<>();
        List<Order> currentChunk = new ArrayList<>();
        int accumulated = 0;

        for (Order order : orders) {
            if (accumulated + 1 > THRESHOLD) {
                chunks.add(currentChunk);
                currentChunk = new ArrayList<>();
                accumulated = 0;
                break; // truncate
            }
            accumulated++;
            currentChunk.add(order);
        }
        if (!currentChunk.isEmpty()) {
            chunks.add(currentChunk);
        }
        return chunks;
    }

    private int getTotalRows(List<Order> orders) {
        return orders.size();
    }

    private List<PdfTableRenderer.B1Row> buildB1Rows(List<Order> orders) {
        List<PdfTableRenderer.B1Row> rows = new ArrayList<>();

        // Header row
        PdfTableRenderer.B1Row header = new PdfTableRenderer.B1Row();
        header.cells = TABLE_HEADERS;
        header.isHeader = true;
        rows.add(header);

        // Data rows
        for (Order order : orders) {
            if (order.getRoutes().isEmpty()) {
                PdfTableRenderer.B1Row row = new PdfTableRenderer.B1Row();
                row.cells = new String[]{
                        order.getOrderId().isEmpty() ? "\u200B" : order.getOrderId(),
                        order.getTypeLabel().isEmpty() ? "\u200B" : order.getTypeLabel(),
                        "\u200B",
                        "\u200B"
                };
                row.borderTop = new boolean[]{true, true, true, true};
                row.borderBottom = new boolean[]{true, true, true, true};
                row.borderLeft = new boolean[]{true, true, true, true};
                row.borderRight = new boolean[]{true, true, true, true};
                row.isHeader = false;
                rows.add(row);
            } else {
                int routeCount = order.getRoutes().size();
                for (int rtIdx = 0; rtIdx < routeCount; rtIdx++) {
                    Route route = order.getRoutes().get(rtIdx);
                    boolean isFirstRoute = (rtIdx == 0);
                    boolean isLastRoute = (rtIdx == routeCount - 1);

                    if (route.getTransactions().isEmpty()) {
                        PdfTableRenderer.B1Row row = new PdfTableRenderer.B1Row();
                        row.cells = new String[]{
                                isFirstRoute ? order.getOrderId() : "\u200B",
                                isFirstRoute ? order.getTypeLabel() : "\u200B",
                                route.getVenueName(),
                                "-"
                        };
                        row.borderTop = new boolean[]{isFirstRoute, isFirstRoute, true, true};
                        row.borderBottom = new boolean[]{isLastRoute, isLastRoute, true, true};
                        row.borderLeft = new boolean[]{true, true, true, true};
                        row.borderRight = new boolean[]{true, true, true, true};
                        row.isHeader = false;
                        rows.add(row);
                    } else {
                        int txCount = route.getTransactions().size();
                        for (int txIdx = 0; txIdx < txCount; txIdx++) {
                            Transaction tx = route.getTransactions().get(txIdx);
                            boolean isFirstTx = (txIdx == 0);
                            boolean isLastTx = (txIdx == txCount - 1);

                            PdfTableRenderer.B1Row row = new PdfTableRenderer.B1Row();
                            row.cells = new String[]{
                                    (isFirstRoute && isFirstTx) ? order.getOrderId() : " ",
                                    (isFirstRoute && isFirstTx) ? order.getTypeLabel() : " ",
                                    isFirstTx ? route.getVenueName() : " ",
                                    tx.getFormattedDateTime()
                            };
                            row.borderTop = new boolean[]{
                                    isFirstRoute && isFirstTx,
                                    isFirstRoute && isFirstTx,
                                    isFirstRoute && isFirstTx,
                                    isFirstRoute && isFirstTx
                            };
                            row.borderBottom = new boolean[]{
                                    isLastRoute && isLastTx,
                                    isLastRoute && isLastTx,
                                    isLastTx,
                                    true
                            };
                            row.borderLeft = new boolean[]{true, true, true, true};
                            row.borderRight = new boolean[]{true, true, true, true};
                            row.isHeader = false;
                            rows.add(row);
                        }
                    }
                }
            }
        }
        return rows;
    }

    private String removeExtension(String filename) {
        int lastDot = filename.lastIndexOf('.');
        if (lastDot == -1) return filename;
        return filename.substring(0, lastDot);
    }
}
