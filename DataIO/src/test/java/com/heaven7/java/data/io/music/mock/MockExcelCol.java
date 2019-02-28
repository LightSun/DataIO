package com.heaven7.java.data.io.music.mock;

import com.heaven7.java.data.io.poi.ExcelCol;

/**
 * @author heaven7
 */
public class MockExcelCol extends ExcelCol {

    private final String text;

    public MockExcelCol(String text) {
        super();
        this.text = text;
    }

    @Override
    public String getColumnString() {
        return text;
    }

    @Override
    public String toString() {
        return text;
    }
}
