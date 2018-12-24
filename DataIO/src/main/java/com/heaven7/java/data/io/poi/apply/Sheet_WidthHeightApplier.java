package com.heaven7.java.data.io.poi.apply;

import com.heaven7.java.data.io.apply.ApplyDelegate;
import com.heaven7.java.data.io.poi.write.PoiContext;
import org.apache.poi.ss.usermodel.Sheet;

/**
 * @author heaven7
 */
public class Sheet_WidthHeightApplier implements ApplyDelegate<PoiContext>{

    private final int cellWidth;
    private final int colHeight;

    public Sheet_WidthHeightApplier(int cellWidth, int colHeight) {
        this.cellWidth = cellWidth;
        this.colHeight = colHeight;
    }

    @Override
    public void apply(PoiContext context) {
        Sheet sheet = context.getSheet();
        sheet.setDefaultRowHeight((short) colHeight);
        sheet.setDefaultColumnWidth(cellWidth);
    }
}
