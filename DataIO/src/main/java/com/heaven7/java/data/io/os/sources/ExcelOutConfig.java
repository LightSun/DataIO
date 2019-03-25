package com.heaven7.java.data.io.os.sources;

import java.util.List;

public final class ExcelOutConfig {

    private String sheetName;
    private List<String> columnNames;
    private int width;
    private int height;

    protected ExcelOutConfig(ExcelOutConfig.Builder builder) {
        this.sheetName = builder.sheetName;
        this.columnNames = builder.columnNames;
        this.width = builder.width;
        this.height = builder.height;
    }

    public String getSheetName() {
        return this.sheetName;
    }

    public List<String> getColumnNames() {
        return this.columnNames;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public static class Builder {
        private String sheetName;
        private List<String> columnNames;
        private int width;
        private int height;

        public Builder setSheetName(String sheetName) {
            this.sheetName = sheetName;
            return this;
        }

        public Builder setColumnNames(List<String> columnNames) {
            this.columnNames = columnNames;
            return this;
        }

        public Builder setWidth(int width) {
            this.width = width;
            return this;
        }

        public Builder setHeight(int height) {
            this.height = height;
            return this;
        }

        public ExcelOutConfig build() {
            return new ExcelOutConfig(this);
        }
    }
}