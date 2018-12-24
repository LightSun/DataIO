package com.heaven7.java.data.io.poi.adapter;

import com.heaven7.java.data.io.poi.ExcelHelper;
import com.heaven7.java.data.io.poi.ExcelRow;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * @author heaven7
 */
public class WriteVisitor extends ExcelVisitorAdapter {

    private final String dstFile;

    public WriteVisitor(String dstFile) {
        this.dstFile = dstFile;
    }

    @Override
    public void endVisitSheet(Workbook workbook, Sheet sheet, List<ExcelRow> rows) {
        OutputStream out = null;
        try {
            out = new FileOutputStream(dstFile);
            workbook.write(out);
            out.flush();
        } catch (IOException e){
            throw new RuntimeException(e);
        }finally{
            if(out != null){
                try {
                    out.close();
                } catch (IOException e) {
                }
            }
        }
    }

  /*  public static void main(String[] args) {
        String src = "E:\\tmp\\bugfinds\\music8.xlsx";
        String dst = "E:\\tmp\\bugfinds\\test.xlsx";
        new ExcelHelper.Builder()
                .setExcelPath(src)
                .setSheetName("sheet3")
                .setUseXlsx(true)
                .setVisitor(new WriteVisitor(dst){
                    @Override
                    public void visitRow(Workbook workbook, Sheet sheet, ExcelRow row, List<ExcelRow> rows) {
                        //use this to change row
                        row.getRow().getCell(0).setCellValue("abcdefd__" + row.getRowIndex());
                    }
                })
                .build()
                .read();
    }*/
}
