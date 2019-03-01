package com.heaven7.java.data.io.music.mock;

import com.heaven7.java.data.io.poi.ExcelCol;
import com.heaven7.java.data.io.poi.ExcelRow;
import com.heaven7.java.visitor.collection.VisitServices;

import java.util.ArrayList;
import java.util.List;

/**
 * @author heaven7
 */
public class MockExcelRow extends ExcelRow {

    private int rowIndex;
    private final List<ExcelCol> cols;

    public MockExcelRow(){
        this(new ArrayList<ExcelCol>());
    }
    public MockExcelRow(List<? extends ExcelCol> cols) {
        this.cols = (List<ExcelCol>) cols;
    }
    //每隔周期有一个 要用的ExcelCol
    public MockExcelRow(List<? extends ExcelCol> cols, int period) {
        this.cols = new ArrayList<>();
        int groupCount = cols.size();
        List<ExcelCol> nopCols = new ArrayList<>();
        for (int j = 0 ; j < period - 1 ; j ++){
            nopCols.add(new MockExcelCol(""));
        }
        for (int i = 0 ; i < groupCount ; i ++){
            this.cols.addAll(nopCols);
            this.cols.add(cols.get(i));
        }
    }

    public void setRowIndex(int rowIndex) {
        this.rowIndex = rowIndex;
    }

    @Override
    public int getRowIndex() {
        return rowIndex;
    }

    @Override
    public List<ExcelCol> getColumns() {
        return cols;
    }

    @Override
    public String toString() {
        return "[ " + VisitServices.from(cols).joinToString(",") + " ]";
    }
}
