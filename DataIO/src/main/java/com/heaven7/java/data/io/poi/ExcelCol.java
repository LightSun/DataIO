package com.heaven7.java.data.io.poi;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;

/**
 * column
 * @author heaven7
 *
 */
public class ExcelCol implements Comparable<ExcelCol> {

	private final Cell cell;

	public ExcelCol(Cell cell) {
		super();
		this.cell = cell;
	}

	protected ExcelCol(){
		cell = null;
	}

	public int getColumnIndex() {
		return cell.getColumnIndex();
	}
	
	public String getColumnString() {
		cell.setCellType(CellType.STRING);
		return cell.getStringCellValue();
	}

	public int getColumnInt() {
		return Integer.parseInt(cell.getStringCellValue());
	}
	public float getColumnFloat() {
		return Float.valueOf(cell.getStringCellValue());
	}

	public final Cell getCell() {
		return cell;
	}
	public void setValue(String value){
		cell.setCellValue(value);
	}
	@Override
	public int compareTo(ExcelCol o) {
		return Integer.compare(this.getColumnIndex(), o.getColumnIndex());
	}
	
	@Override
	public String toString() {
		return cell.toString();
	}

}
