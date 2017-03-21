package com.heaven7.java.data.io.poi;

import java.util.List;

import com.heaven7.java.visitor.collection.ListVisitService;

public interface ExcelInput {
	
	
	/*
	 * prepare the input of excel
	 * @param in the input of excel
	 */
	//void prepare(InputStream in);

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