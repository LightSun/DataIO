package com.heaven7.java.data.io.poi;

import java.util.List;

import com.heaven7.java.visitor.collection.ListVisitService;
import com.sun.rowset.internal.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

public interface ExcelInput {
	
	
	/*
	 * prepare the input of excel
	 * @param in the input of excel
	 */
	//void prepare(InputStream in);

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


	/**
	 * the excel visitor
	 * @author heaven7
	 */
	interface ExcelVisitor{
		/**
		 * start visit the sheet of the workbook
		 * @param workbook the workbook
		 * @param sheet the work sheet
		 */
		void startVisitSheet(Workbook workbook, Sheet sheet);

		/**
		 * visit the row
		 * @param workbook the workbook
		 * @param sheet the sheet
		 * @param row the current row
		 * @param rows the rows previous already visited. exclude current
		 */
		void visitRow(Workbook workbook, Sheet sheet, ExcelRow row, List<Row> rows);

		/**
		 * end visit the sheet
		 * @param workbook the workbook
		 * @param sheet the sheet
		 * @param rows the all rows.
		 */
		void endVisitSheet(Workbook workbook, Sheet sheet, List<Row> rows);
	}

}