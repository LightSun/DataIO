package com.heaven7.java.data.io.poi.adapter;

import com.heaven7.java.data.io.poi.ExcelRow;
import com.heaven7.java.data.io.poi.ExcelVisitor;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.List;

public class ExcelVisitorAdapter implements ExcelVisitor {

    public static final ExcelVisitorAdapter EMPTY = new ExcelVisitorAdapter();

    @Override
    public void startVisitSheet(Workbook workbook, Sheet sheet) {}

    @Override
    public void visitRow(Workbook workbook, Sheet sheet, ExcelRow row, List<ExcelRow> rows) {}

    @Override
    public void endVisitSheet(Workbook workbook, Sheet sheet, List<ExcelRow> rows) {}
}
