package com.heaven7.java.data.io.poi;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author heaven7
 */
public class XSSFExcelInput extends BaseExcelInput implements ExcelInput {

    @Override
    protected Workbook onCreateWorkbook(InputStream in)throws IOException{
        return new XSSFWorkbook(in);
    }

}
