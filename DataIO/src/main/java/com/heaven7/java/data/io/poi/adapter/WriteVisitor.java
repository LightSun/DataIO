package com.heaven7.java.data.io.poi.adapter;

import com.heaven7.java.data.io.poi.ExcelRow;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.List;

/**
 * @author heaven7
 */
public class WriteVisitor extends ExcelVisitorAdapter {

    @Override
    public void endVisitSheet(Workbook workbook, Sheet sheet, List<ExcelRow> rows) {
      //TODO test  workbook.write();
    }
}
