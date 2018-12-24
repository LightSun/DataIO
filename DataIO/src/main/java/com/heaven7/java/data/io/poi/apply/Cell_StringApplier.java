package com.heaven7.java.data.io.poi.apply;

import com.heaven7.java.data.io.apply.ApplyDelegate;
import com.heaven7.java.data.io.poi.write.PoiContext;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;

/**
 * @author heaven7
 */
public class Cell_StringApplier implements ApplyDelegate<PoiContext> {

    final String value;

    public Cell_StringApplier(String value) {
        this.value = value;
    }

    @Override
    public void apply(PoiContext context) {
        Cell cell = context.getCell();
        cell.setCellType(CellType.STRING);
        cell.setCellValue(value);
    }
}
