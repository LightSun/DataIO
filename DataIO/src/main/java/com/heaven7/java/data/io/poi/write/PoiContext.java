package com.heaven7.java.data.io.poi.write;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * @author heaven7
 */
public class PoiContext {

    private Workbook workbook;
    private Sheet sheet;
    private Row row;
    private Cell cell;

    public void setWorkbook(Workbook workbook) {
        this.workbook = workbook;
    }
    public void setSheet(Sheet sheet) {
        this.sheet = sheet;
    }
    public void setRow(Row row) {
        this.row = row;
    }
    public void setCell(Cell cell) {
        this.cell = cell;
    }

    public Workbook getWorkbook() {
        return workbook;
    }

    public Sheet getSheet() {
        return sheet;
    }

    public Row getRow() {
        return row;
    }

    public Cell getCell() {
        return cell;
    }
}
