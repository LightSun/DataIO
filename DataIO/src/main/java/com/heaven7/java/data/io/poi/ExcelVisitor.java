package com.heaven7.java.data.io.poi;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.List;

/**
 * the excel visitor
 *
 * @author heaven7
 */
public interface ExcelVisitor {

    /**
     * start visit the sheet of the workbook
     *
     * @param workbook the workbook
     * @param sheet the work sheet
     */
    void startVisitSheet(Workbook workbook, Sheet sheet);

    /**
     * visit the row
     *
     * @param workbook the workbook
     * @param sheet the sheet
     * @param row the current row
     * @param rows the rows previous already visited. exclude current
     */
    void visitRow(Workbook workbook, Sheet sheet, ExcelRow row, List<ExcelRow> rows);

    /**
     * end visit the sheet
     *
     * @param workbook the workbook
     * @param sheet the sheet
     * @param rows the all rows.
     */
    void endVisitSheet(Workbook workbook, Sheet sheet, List<ExcelRow> rows);
}
