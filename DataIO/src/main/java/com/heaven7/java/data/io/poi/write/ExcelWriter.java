package com.heaven7.java.data.io.poi.write;

import com.heaven7.java.data.io.apply.ApplyDelegate;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;

import java.io.IOException;

/**
 * @author heaven7
 */
public interface ExcelWriter {

    /** for '.xlsx' */
    byte TYPE_XSSF = 1;
    /** for '.xls' */
    byte TYPE_HSSF = 2;

    ExcelWriter newWorkbook(byte type);
    ExcelWriter apply(ApplyDelegate<PoiContext> delegate);
    WorkbookFactory nesting();

    void write(String file);

    void writeIO(String file) throws IOException;

    interface WorkbookFactory{
        WorkbookFactory newSheet(String name);
        WorkbookFactory apply(ApplyDelegate<PoiContext> delegate);
        SheetFactory nesting();
        ExcelWriter end();
    }

    interface SheetFactory{
        SheetFactory newRow(int rownum, CellStyle style);
        SheetFactory newRow(int rowNum);
        SheetFactory apply(ApplyDelegate<PoiContext> delegate);
        RowFactory nesting();
        WorkbookFactory end();
    }

    interface RowFactory{
        RowFactory newCell(int column, CellType type);
        RowFactory newCell(int column);

        RowFactory apply(ApplyDelegate<PoiContext> delegate);
        SheetFactory end();
    }

}
