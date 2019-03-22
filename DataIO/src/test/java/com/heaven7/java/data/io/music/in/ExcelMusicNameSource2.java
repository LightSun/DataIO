package com.heaven7.java.data.io.music.in;

import com.heaven7.java.data.io.poi.ExcelHelper;
import com.heaven7.java.data.io.poi.ExcelRow;

/**
 * 只保留第6 行。有数字的
 * @author heaven7
 */
public class ExcelMusicNameSource2 extends ExcelMusicNameSource {

    public ExcelMusicNameSource2(ExcelHelper standName, ExcelHelper sortName) {
        super(standName, sortName);
    }

    @Override
    protected int getMusicNameIndex() {
        return 0;
    }
    @Override
    protected boolean filterRow(ExcelRow row) {
        String str = row.getColumns().get(6).getColumnString().trim();
        if(str.isEmpty()){
            return true;
        }
        return super.filterRow(row);
    }
}
