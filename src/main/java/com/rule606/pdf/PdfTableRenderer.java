package com.rule606.pdf;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PdfTableRenderer {

    private final PDDocument document;
    private PDPageContentStream contentStream;
    private PDPage currentPage;
    private float currentY;
    private final float margin;
    private final float contentWidth;
    private final float pageHeight;
    private final float pageWidth;
    private final boolean landscape;

    private static final PDFont HELVETICA = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
    private static final PDFont HELVETICA_BOLD = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);

    public PdfTableRenderer(PDDocument document, boolean landscape) {
        this.document = document;
        this.landscape = landscape;
        this.margin = PdfStyles.MARGIN;
        if (landscape) {
            this.pageWidth = PdfStyles.LANDSCAPE_WIDTH;
            this.pageHeight = PdfStyles.LANDSCAPE_HEIGHT;
            this.contentWidth = PdfStyles.LANDSCAPE_CONTENT_WIDTH;
        } else {
            this.pageWidth = PdfStyles.LETTER_WIDTH;
            this.pageHeight = PdfStyles.LETTER_HEIGHT;
            this.contentWidth = PdfStyles.PORTRAIT_CONTENT_WIDTH;
        }
        addNewPage();
    }

    public void addNewPage() {
        try {
            if (contentStream != null) {
                contentStream.close();
            }
            PDRectangle size = landscape
                    ? new PDRectangle(PdfStyles.LANDSCAPE_WIDTH, PdfStyles.LANDSCAPE_HEIGHT)
                    : PDRectangle.LETTER;
            currentPage = new PDPage(size);
            document.addPage(currentPage);
            contentStream = new PDPageContentStream(document, currentPage);
            currentY = pageHeight - margin;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void close() throws IOException {
        if (contentStream != null) {
            contentStream.close();
        }
    }

    public float getCurrentY() { return currentY; }
    public float getContentWidth() { return contentWidth; }
    public float getMargin() { return margin; }

    public void ensureSpace(float needed) {
        if (currentY - needed < margin) {
            addNewPage();
        }
    }

    // --- Text drawing ---

    public void drawCenteredText(String text, float fontSize, boolean bold) throws IOException {
        drawCenteredText(text, fontSize, bold, Color.BLACK);
    }

    public void drawCenteredText(String text, float fontSize, boolean bold, Color color) throws IOException {
        PDFont font = bold ? HELVETICA_BOLD : HELVETICA;
        float textWidth = font.getStringWidth(text) / 1000 * fontSize;
        float x = margin + (contentWidth - textWidth) / 2;
        ensureSpace(fontSize + 4);
        contentStream.beginText();
        contentStream.setFont(font, fontSize);
        contentStream.setNonStrokingColor(color);
        contentStream.newLineAtOffset(x, currentY - fontSize);
        contentStream.showText(text);
        contentStream.endText();
        contentStream.setNonStrokingColor(Color.BLACK);
        currentY -= fontSize * PdfStyles.LINE_HEIGHT + 2;
    }

    public void drawLeftText(String text, float fontSize, boolean bold) throws IOException {
        PDFont font = bold ? HELVETICA_BOLD : HELVETICA;
        ensureSpace(fontSize + 4);
        contentStream.beginText();
        contentStream.setFont(font, fontSize);
        contentStream.newLineAtOffset(margin, currentY - fontSize);
        contentStream.showText(sanitize(text));
        contentStream.endText();
        currentY -= fontSize * PdfStyles.LINE_HEIGHT + 2;
    }

    public void drawMixedLine(String[][] segments) throws IOException {
        // segments: each is {text, fontSize, bold ("true"/"false")}
        float totalWidth = 0;
        for (String[] seg : segments) {
            PDFont font = "true".equals(seg[2]) ? HELVETICA_BOLD : HELVETICA;
            float fs = Float.parseFloat(seg[1]);
            totalWidth += font.getStringWidth(seg[0]) / 1000 * fs;
        }
        float x = margin + (contentWidth - totalWidth) / 2;
        float maxFs = 0;
        for (String[] seg : segments) {
            float fs = Float.parseFloat(seg[1]);
            if (fs > maxFs) maxFs = fs;
        }
        ensureSpace(maxFs + 4);
        contentStream.beginText();
        contentStream.newLineAtOffset(x, currentY - maxFs);
        for (String[] seg : segments) {
            PDFont font = "true".equals(seg[2]) ? HELVETICA_BOLD : HELVETICA;
            float fs = Float.parseFloat(seg[1]);
            contentStream.setFont(font, fs);
            contentStream.showText(sanitize(seg[0]));
        }
        contentStream.endText();
        currentY -= maxFs * PdfStyles.LINE_HEIGHT + 2;
    }

    public void drawWrappedText(String text, float fontSize, boolean bold, float maxWidth) throws IOException {
        PDFont font = bold ? HELVETICA_BOLD : HELVETICA;
        List<String> lines = wrapText(text, font, fontSize, maxWidth);
        for (String line : lines) {
            ensureSpace(fontSize + 2);
            contentStream.beginText();
            contentStream.setFont(font, fontSize);
            contentStream.newLineAtOffset(margin, currentY - fontSize);
            contentStream.showText(sanitize(line));
            contentStream.endText();
            currentY -= fontSize * PdfStyles.LINE_HEIGHT;
        }
    }

    public void addVerticalSpace(float space) {
        currentY -= space;
    }

    // --- Lines ---

    public void drawHorizontalLine(float lineWidth) throws IOException {
        drawHorizontalLine(lineWidth, false);
    }

    public void drawHorizontalLine(float lineWidth, boolean dashed) throws IOException {
        ensureSpace(10);
        contentStream.setStrokingColor(Color.BLACK);
        contentStream.setLineWidth(lineWidth);
        if (dashed) {
            contentStream.setLineDashPattern(new float[]{5}, 0);
        }
        float lineY = currentY - 5;
        contentStream.moveTo(margin, lineY);
        contentStream.lineTo(margin + contentWidth, lineY);
        contentStream.stroke();
        if (dashed) {
            contentStream.setLineDashPattern(new float[]{}, 0);
        }
        currentY -= 10;
    }

    // --- Table drawing ---

    public static class CellStyle {
        public float fontSize = 6;
        public boolean bold = false;
        public Color fillColor = null;
        public Color textColor = Color.BLACK;
        public String alignment = "left"; // left, center, right
        public boolean borderLeft = true, borderTop = true, borderRight = true, borderBottom = true;
    }

    public float calculateAutoColumnWidth(float[] fixedWidths, int totalColumns) {
        float fixedTotal = 0;
        int autoCount = 0;
        for (float w : fixedWidths) {
            if (w <= 0) autoCount++;
            else fixedTotal += w;
        }
        if (autoCount == 0) return 0;
        return (contentWidth - fixedTotal) / autoCount;
    }

    public float[] resolveColumnWidths(float[] widths) {
        float[] resolved = new float[widths.length];
        float fixedTotal = 0;
        int autoCount = 0;
        for (float w : widths) {
            if (w <= 0) autoCount++;
            else fixedTotal += w;
        }
        float autoWidth = autoCount > 0 ? (contentWidth - fixedTotal) / autoCount : 0;
        for (int i = 0; i < widths.length; i++) {
            resolved[i] = widths[i] <= 0 ? autoWidth : widths[i];
        }
        return resolved;
    }

    public float drawTable(String[][] data, float[] columnWidths, CellStyle[] headerStyles,
                           CellStyle[] dataStyles, int headerRows) throws IOException {
        float[] resolved = resolveColumnWidths(columnWidths);
        return drawTableResolved(data, resolved, headerStyles, dataStyles, headerRows, null);
    }

    public float drawTableResolved(String[][] data, float[] colWidths, CellStyle[] headerStyles,
                                   CellStyle[] dataStyles, int headerRows,
                                   boolean[][] borders) throws IOException {
        if (data.length == 0) return currentY;

        String[][] headerData = new String[headerRows][];
        for (int i = 0; i < headerRows && i < data.length; i++) {
            headerData[i] = data[i];
        }

        // Draw header rows
        for (int r = 0; r < headerRows && r < data.length; r++) {
            float rowHeight = calculateRowHeight(data[r], colWidths, headerStyles);
            ensureSpace(rowHeight);
            drawRow(data[r], colWidths, headerStyles, rowHeight, borders != null ? borders[r] : null);
        }

        // Draw data rows
        for (int r = headerRows; r < data.length; r++) {
            float rowHeight = calculateRowHeight(data[r], colWidths, dataStyles);
            if (currentY - rowHeight < margin) {
                addNewPage();
                // Redraw header on new page
                for (int h = 0; h < headerRows && h < data.length; h++) {
                    float hHeight = calculateRowHeight(headerData[h], colWidths, headerStyles);
                    drawRow(headerData[h], colWidths, headerStyles, hHeight, null);
                }
            }
            drawRow(data[r], colWidths, dataStyles, rowHeight, borders != null ? borders[r] : null);
        }
        return currentY;
    }

    public void drawRow(String[] cells, float[] colWidths, CellStyle[] styles,
                        float rowHeight, boolean[] borderFlags) throws IOException {
        float x = margin;
        for (int c = 0; c < cells.length && c < colWidths.length; c++) {
            CellStyle style = (styles != null && c < styles.length) ? styles[c] : new CellStyle();
            boolean[] cellBorders = null;
            if (borderFlags != null) {
                // borderFlags packs as [col0_left, col0_top, col0_right, col0_bottom, col1_left, ...]
                int baseIdx = c * 4;
                if (baseIdx + 3 < borderFlags.length) {
                    cellBorders = new boolean[]{
                            borderFlags[baseIdx], borderFlags[baseIdx + 1],
                            borderFlags[baseIdx + 2], borderFlags[baseIdx + 3]
                    };
                }
            }
            drawCell(cells[c], x, currentY, colWidths[c], rowHeight, style, cellBorders);
            x += colWidths[c];
        }
        currentY -= rowHeight;
    }

    public void drawCell(String text, float x, float y, float width, float height,
                         CellStyle style, boolean[] borders) throws IOException {
        // borders: [left, top, right, bottom]
        boolean bLeft = borders != null ? borders[0] : style.borderLeft;
        boolean bTop = borders != null ? borders[1] : style.borderTop;
        boolean bRight = borders != null ? borders[2] : style.borderRight;
        boolean bBottom = borders != null ? borders[3] : style.borderBottom;

        // Fill background
        if (style.fillColor != null) {
            contentStream.setNonStrokingColor(style.fillColor);
            contentStream.addRect(x, y - height, width, height);
            contentStream.fill();
            contentStream.setNonStrokingColor(Color.BLACK);
        }

        // Draw borders
        contentStream.setStrokingColor(Color.BLACK);
        contentStream.setLineWidth(0.5f);
        if (bTop) {
            contentStream.moveTo(x, y);
            contentStream.lineTo(x + width, y);
            contentStream.stroke();
        }
        if (bBottom) {
            contentStream.moveTo(x, y - height);
            contentStream.lineTo(x + width, y - height);
            contentStream.stroke();
        }
        if (bLeft) {
            contentStream.moveTo(x, y);
            contentStream.lineTo(x, y - height);
            contentStream.stroke();
        }
        if (bRight) {
            contentStream.moveTo(x + width, y);
            contentStream.lineTo(x + width, y - height);
            contentStream.stroke();
        }

        // Draw text
        if (text != null && !text.isEmpty() && !text.equals("\u200B")) {
            PDFont font = style.bold ? HELVETICA_BOLD : HELVETICA;
            float padding = PdfStyles.CELL_PADDING;
            float availWidth = width - 2 * padding;
            List<String> lines = wrapText(text, font, style.fontSize, availWidth);
            float textY = y - PdfStyles.CELL_PADDING_TOP - style.fontSize;
            for (String line : lines) {
                if (textY < y - height) break;
                float textX;
                float lineWidth = font.getStringWidth(sanitize(line)) / 1000 * style.fontSize;
                textX = switch (style.alignment) {
                    case "center" -> x + (width - lineWidth) / 2;
                    case "right" -> x + width - padding - lineWidth;
                    default -> x + padding;
                };
                contentStream.beginText();
                contentStream.setFont(font, style.fontSize);
                contentStream.setNonStrokingColor(style.textColor);
                contentStream.newLineAtOffset(textX, textY);
                contentStream.showText(sanitize(line));
                contentStream.endText();
                textY -= style.fontSize * PdfStyles.LINE_HEIGHT;
            }
            contentStream.setNonStrokingColor(Color.BLACK);
        }
    }

    public float calculateRowHeight(String[] cells, float[] colWidths, CellStyle[] styles) {
        float maxHeight = 0;
        for (int c = 0; c < cells.length && c < colWidths.length; c++) {
            CellStyle style = (styles != null && c < styles.length) ? styles[c] : new CellStyle();
            float cellHeight = calculateCellHeight(cells[c], colWidths[c], style);
            if (cellHeight > maxHeight) maxHeight = cellHeight;
        }
        return maxHeight;
    }

    public float calculateCellHeight(String text, float colWidth, CellStyle style) {
        if (text == null || text.isEmpty() || text.equals("\u200B")) {
            return style.fontSize + PdfStyles.CELL_PADDING_TOP + PdfStyles.CELL_PADDING_BOTTOM + 2;
        }
        PDFont font = style.bold ? HELVETICA_BOLD : HELVETICA;
        float availWidth = colWidth - 2 * PdfStyles.CELL_PADDING;
        List<String> lines = wrapText(text, font, style.fontSize, availWidth);
        float textHeight = lines.size() * style.fontSize * PdfStyles.LINE_HEIGHT;
        return textHeight + PdfStyles.CELL_PADDING_TOP + PdfStyles.CELL_PADDING_BOTTOM + 2;
    }

    // --- B1 special: draw table with per-cell border control ---

    public static class B1Row {
        public String[] cells;
        public boolean[] borderLeft;
        public boolean[] borderTop;
        public boolean[] borderRight;
        public boolean[] borderBottom;
        public boolean isHeader;
    }

    public void drawB1Table(List<B1Row> rows, float[] colWidths, CellStyle headerStyle,
                            CellStyle dataStyle, int headerRowCount) throws IOException {
        float[] resolved = resolveColumnWidths(colWidths);

        // Save header rows for reprinting
        List<B1Row> headerRows = new ArrayList<>();
        for (int i = 0; i < headerRowCount && i < rows.size(); i++) {
            headerRows.add(rows.get(i));
        }

        for (int r = 0; r < rows.size(); r++) {
            B1Row row = rows.get(r);
            CellStyle baseStyle = row.isHeader ? headerStyle : dataStyle;
            float rowHeight = calculateRowHeightFromStyle(row.cells, resolved, baseStyle);

            if (currentY - rowHeight < margin && r >= headerRowCount) {
                addNewPage();
                for (B1Row hRow : headerRows) {
                    float hh = calculateRowHeightFromStyle(hRow.cells, resolved, headerStyle);
                    drawB1RowInternal(hRow, resolved, headerStyle, hh);
                }
            }

            drawB1RowInternal(row, resolved, baseStyle, rowHeight);
        }
    }

    private void drawB1RowInternal(B1Row row, float[] colWidths, CellStyle baseStyle,
                                   float rowHeight) throws IOException {
        float x = margin;
        for (int c = 0; c < row.cells.length && c < colWidths.length; c++) {
            CellStyle cellStyle = new CellStyle();
            cellStyle.fontSize = baseStyle.fontSize;
            cellStyle.bold = baseStyle.bold;
            cellStyle.fillColor = baseStyle.fillColor;
            cellStyle.textColor = baseStyle.textColor;
            cellStyle.alignment = baseStyle.alignment;

            boolean[] borders = {
                    row.borderLeft != null ? row.borderLeft[c] : true,
                    row.borderTop != null ? row.borderTop[c] : true,
                    row.borderRight != null ? row.borderRight[c] : true,
                    row.borderBottom != null ? row.borderBottom[c] : true
            };
            drawCell(row.cells[c], x, currentY, colWidths[c], rowHeight, cellStyle, borders);
            x += colWidths[c];
        }
        currentY -= rowHeight;
    }

    private float calculateRowHeightFromStyle(String[] cells, float[] colWidths, CellStyle style) {
        CellStyle[] styles = new CellStyle[cells.length];
        java.util.Arrays.fill(styles, style);
        return calculateRowHeight(cells, colWidths, styles);
    }

    // --- Material aspects (bulleted list) ---

    public void drawBulletedItem(String label, String body, float fontSize) throws IOException {
        float bulletIndent = margin + 10;
        float textIndent = margin + 20;
        float availWidth = contentWidth - 20;

        // Label line
        ensureSpace(fontSize * 2 + 4);
        contentStream.beginText();
        contentStream.setFont(HELVETICA_BOLD, fontSize);
        contentStream.newLineAtOffset(bulletIndent, currentY - fontSize);
        contentStream.showText("- " + sanitize(label));
        contentStream.endText();
        currentY -= fontSize * PdfStyles.LINE_HEIGHT;

        // Body text with wrapping
        List<String> lines = wrapText(body, HELVETICA, fontSize, availWidth);
        for (String line : lines) {
            ensureSpace(fontSize + 2);
            contentStream.beginText();
            contentStream.setFont(HELVETICA, fontSize);
            contentStream.newLineAtOffset(textIndent, currentY - fontSize);
            contentStream.showText(sanitize(line));
            contentStream.endText();
            currentY -= fontSize * PdfStyles.LINE_HEIGHT;
        }
        currentY -= fontSize; // Extra spacing after item
    }

    // --- Utilities ---

    public static List<String> wrapText(String text, PDFont font, float fontSize, float maxWidth) {
        List<String> lines = new ArrayList<>();
        if (text == null || text.isEmpty()) {
            lines.add("");
            return lines;
        }
        // Handle newlines
        String[] paragraphs = text.split("\n");
        for (String paragraph : paragraphs) {
            if (paragraph.isEmpty()) {
                lines.add("");
                continue;
            }
            String[] words = paragraph.split("(?<=\\s)|(?=\\s)");
            StringBuilder currentLine = new StringBuilder();
            for (String word : words) {
                String testLine = currentLine + word;
                try {
                    float testWidth = font.getStringWidth(sanitize(testLine.toString())) / 1000 * fontSize;
                    if (testWidth > maxWidth && !currentLine.isEmpty()) {
                        lines.add(currentLine.toString().trim());
                        currentLine = new StringBuilder(word.trim());
                        if (!word.trim().isEmpty()) currentLine.append(" ");
                    } else {
                        currentLine.append(word);
                    }
                } catch (IOException e) {
                    currentLine.append(word);
                }
            }
            if (!currentLine.isEmpty()) {
                lines.add(currentLine.toString().trim());
            }
        }
        if (lines.isEmpty()) lines.add("");
        return lines;
    }

    public static String sanitize(String text) {
        if (text == null) return "";
        // Replace characters that aren't in the Helvetica encoding
        StringBuilder sb = new StringBuilder(text.length());
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '\u200B') continue; // zero-width space
            if (c == '\t') { sb.append("    "); continue; }
            if (c == '\r' || c == '\n') continue;
            if (c < 32 && c != '\t') continue;
            // Replace any char that Helvetica can't encode with '?'
            if (c > 255) { sb.append('?'); continue; }
            sb.append(c);
        }
        return sb.toString();
    }
}
