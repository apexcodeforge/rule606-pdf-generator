package com.rule606.pdf;

import java.awt.Color;

public class PdfStyles {

    // Colors from JS
    public static final Color FILL_COLOR = new Color(0xCC, 0xE6, 0xFF); // #CCE6FF blue
    public static final Color FAIL_COLOR = new Color(0xFF, 0xCC, 0xE6); // #FFCCE6 pink
    public static final Color GRAY_COLOR = new Color(0xCC, 0xCC, 0xCC); // #CCCCCC gray
    public static final Color BLACK = Color.BLACK;
    public static final Color WHITE = Color.WHITE;

    // Page dimensions (points, 72 per inch)
    public static final float LETTER_WIDTH = 612;
    public static final float LETTER_HEIGHT = 792;
    public static final float MARGIN = 40;

    // Landscape content area
    public static final float LANDSCAPE_WIDTH = LETTER_HEIGHT;
    public static final float LANDSCAPE_HEIGHT = LETTER_WIDTH;
    public static final float LANDSCAPE_CONTENT_WIDTH = LANDSCAPE_WIDTH - 2 * MARGIN;
    public static final float LANDSCAPE_CONTENT_HEIGHT = LANDSCAPE_HEIGHT - 2 * MARGIN;

    // Portrait content area
    public static final float PORTRAIT_CONTENT_WIDTH = LETTER_WIDTH - 2 * MARGIN;
    public static final float PORTRAIT_CONTENT_HEIGHT = LETTER_HEIGHT - 2 * MARGIN;

    // A1 styles
    public static final float A1_HEADER_SIZE = 16;
    public static final float A1_HEADER3_SIZE = 10;
    public static final float A1_HEADER4_SIZE = 8;
    public static final float A1_SECTION_HEADER_SIZE = 12;
    public static final float A1_SUB_SECTION_HEADER_SIZE = 8;
    public static final float A1_TEXT_SIZE = 6;
    public static final float A1_TABLE_HEADER_SIZE = 6;
    public static final float A1_TABLE_VALUE_SIZE = 6;
    public static final float A1_SUMMARY_COL_WIDTH = 60;

    // B1 styles
    public static final float B1_HEADER_SIZE = 16;
    public static final float B1_HEADER3_SIZE = 10;
    public static final float B1_HEADER4_SIZE = 8;
    public static final float B1_SECTION_HEADER_SIZE = 12;
    public static final float B1_TABLE_HEADER_SIZE = 9;
    public static final float B1_TABLE_VALUE_SIZE = 8;
    public static final float B1_TABLE_NAME_VALUE_SIZE = 9;
    public static final float B1_FAIL_HEADER_SIZE = 9;

    // B3 styles
    public static final float B3_HEADER_SIZE = 16;
    public static final float B3_HEADER3_SIZE = 10;
    public static final float B3_HEADER4_SIZE = 8;
    public static final float B3_SECTION_HEADER_SIZE = 12;
    public static final float B3_SUB_SECTION_HEADER_SIZE = 9;
    public static final float B3_TEXT_SIZE = 8;
    public static final float B3_TABLE_HEADER_SIZE = 3.5f;
    public static final float B3_TABLE_VALUE_SIZE = 4;
    public static final float B3_TABLE_NAME_VALUE_SIZE = 4;
    public static final float B3_DETAIL_VENUE_COL_WIDTH = 40;

    // Cell padding
    public static final float CELL_PADDING = 2;
    public static final float CELL_PADDING_TOP = 1;
    public static final float CELL_PADDING_BOTTOM = 1;

    // Line heights
    public static final float LINE_HEIGHT = 1.2f;
}
