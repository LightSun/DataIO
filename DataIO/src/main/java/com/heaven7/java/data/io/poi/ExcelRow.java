package com.heaven7.java.data.io.poi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.heaven7.java.data.io.util.Utils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

public final class ExcelRow implements Comparable<ExcelRow>{

	private final Row mRow;
	private List<ExcelCol> mCols;
	
	public ExcelRow(Row mRow) {
		super();
		this.mRow = mRow;
	}

	//often used for merged-multi-row
	public String getCellString(List<ExcelRow> rows,int requireColumnIndex, int maxMergeCount){
		return Utils.getColumnForMultiRow(this, rows, requireColumnIndex, maxMergeCount);
	}

	public final List<ExcelCol> getColumns() {
		if(mCols == null){
			final Iterator<Cell> it = mRow.cellIterator();
			final List<ExcelCol> list = new ArrayList<ExcelCol>();
			for (; it.hasNext();) {
				list.add(new ExcelCol(it.next()));
			}
			Collections.sort(list);
			mCols = list;
		}
		return mCols;
	}
	
	public final int getRowIndex(){
		return mRow.getRowNum();
	}
	
	public Row getRow() {
		return mRow;
	}

	@Override
	public int compareTo(ExcelRow o) {
		return Integer.compare(this.mRow.getRowNum(), o.mRow.getRowNum());
	}
	
	@Override
	public String toString() {
		return mRow.toString();
	}
	
}
