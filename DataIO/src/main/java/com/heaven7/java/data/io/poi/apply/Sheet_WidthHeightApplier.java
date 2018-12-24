package com.heaven7.java.data.io.poi.apply;

import com.heaven7.java.data.io.apply.ApplyDelegate;
import com.heaven7.java.data.io.poi.write.PoiContext;
import org.apache.poi.ss.usermodel.Sheet;

/** @author heaven7 */
public class Sheet_WidthHeightApplier implements ApplyDelegate<PoiContext> {

    private static final float DEFAULT_HEIGHT = 30;

    private final int cellWidth;
    private final int colHeight;
    private final int colCount;

    public Sheet_WidthHeightApplier(int cellWidth, int colHeight, int colCount) {
        this.cellWidth = cellWidth;
        this.colHeight = colHeight;
        this.colCount = colCount;
    }

    @Override
    public void apply(PoiContext context) {
        Sheet sheet = context.getSheet();
        sheet.setFitToPage(true);
        sheet.setDefaultRowHeight((short) colHeight);
        //sheet.setDefaultColumnWidth(cellWidth); //set this will cause problem
        sheet.setDefaultRowHeightInPoints(DEFAULT_HEIGHT);
        for (int i = 0; i < colCount; i++) sheet.setColumnWidth(i, cellWidth);
    }
}
