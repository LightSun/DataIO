package com.heaven7.java.data.io.poi;

import com.heaven7.java.visitor.collection.ListVisitService;

import java.util.List;

public interface ExcelInput {
	
	
	/*
	 * prepare the input of excel
	 * @param in the input of excel
	 */
	//void prepare(InputStream in);

	ExcelInput visitor(ExcelVisitor visitor);

	/**
	 * skip to the target row
	 * @param rowIndex the row index.
	 */
	ExcelInput skipToRow(int rowIndex);


	/**
	 * the row interceptor
	 * @param interceptor the row interceptor
	 */
	ExcelInput rowInterceptor(RowInterceptor interceptor);

	/**
	 * assign the excel file path
	 * @param filePath the file path
	 */
	ExcelInput filePath(String filePath);

	/**
	 * read the excel with default sheet index = 0.
	 * @return the all rows.
	 */
	List<ExcelRow> read();

	/**
	 * read the excel file as rows result.
	 * @param sheetParam the sheet parameter, can be the sheet name or the sheet index.
	 * @return the rows.
	 */
	List<ExcelRow> read(Object sheetParam);

	
	/**
	 * read result as list service with default sheet index = 0.
	 * @return {@linkplain ListVisitService}
	 */
	ListVisitService<ExcelRow> readService();

	
	/**
	 * read result as list service 
	 * @return {@linkplain ListVisitService}
	 */
	ListVisitService<ExcelRow> readService(Object param);


}