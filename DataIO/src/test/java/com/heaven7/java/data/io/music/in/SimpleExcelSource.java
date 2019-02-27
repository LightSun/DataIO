package com.heaven7.java.data.io.music.in;

import com.heaven7.java.data.io.poi.ExcelHelper;
import com.heaven7.java.data.io.poi.ExcelRow;

import java.util.List;

/**
 * @author heaven7
 */
public class SimpleExcelSource implements ExcelSource {

    private List<ExcelRow> cacheRows;
    private final ExcelHelper excelHelper;

    public SimpleExcelSource(ExcelHelper excelHelper) {
        this.excelHelper = excelHelper;
    }

    @Override
    public List<ExcelRow> getRows() {
        if(cacheRows == null){
            cacheRows = excelHelper.read();
        }
        return cacheRows;
    }
}
