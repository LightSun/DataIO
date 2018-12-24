package com.heaven7.java.data.io.poi;

import java.util.List;

/**
 * output with the target data which is read from excel
 * @author heaven7
 *
 */
public interface ExcelOutput {

	/**
	 * write the all rows  
	 * @param rows
	 */
	void writeBatch(List<ExcelRow> rows);
	
	void write(ExcelRow row);

}
