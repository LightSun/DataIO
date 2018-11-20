package com.heaven7.java.data.io.poi;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;

/**
 * @author heaven7
 */
public class XSSFExcelInput extends BaseExcelInput implements ExcelInput {

    @Override
    protected Workbook onCreateWorkbook(String filePath)throws IOException{
        return new XSSFWorkbook(filePath);
    }

}
