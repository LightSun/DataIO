package com.heaven7.java.data.io.poi;

import com.heaven7.java.visitor.FireBatchVisitor;
import com.heaven7.java.visitor.collection.ListVisitService;
import com.heaven7.java.visitor.util.Collections2;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.Collection;

/**
 * the poi utils.
 * 
 * @author heaven7
 *
 */
public final class PoiUtils {

	public static Sheet getSheet(Workbook book, Object param) {
		if (param == null) {
			return book.getSheetAt(0);
		}
		if (param instanceof Integer) {
			return book.getSheetAt((Integer) param);
		} else if (param instanceof String) {
			return book.getSheet((String) param);
		}
		throw new IllegalStateException();
	}

	/**
	 * read the local excel file and write to target data service with the sheet
	 * parameter.
	 * 
	 * @param excelFilename
	 *            the local excel filename . absolute path
	 * @param outService
	 *            the output service.
	 */
	public static void readExcelAndWrite(String excelFilename, ExcelDataService outService) {
		readExcelAndWrite(excelFilename, 0, outService);
	}

	/**
	 * read the excel file and write to target data service.
	 * 
	 * @param excelFilename
	 *            the local excel filename . absolute path
	 * @param sheetParam
	 *            the sheet parameter of excel. must be sheet name or index.
	 * @param outService
	 *            the output service.
	 */
	public static void readExcelAndWrite(String excelFilename, Object sheetParam, final ExcelDataService outService) {
		readExcel(excelFilename, sheetParam).fireBatch(new FireBatchVisitor<ExcelRow>() {
			@Override
			public Void visit(Collection<ExcelRow> coll, Object param) {
				new ExcelOutputImpl(outService).writeBatch(Collections2.asList(coll));
				return null;
			}
		});
	}

	/**
	 * read the local excel file by target sheet index = 0.
	 * 
	 * @param excelFilename
	 *            the local excel filename . absolute path
	 */
	public static ListVisitService<ExcelRow> readExcel(String excelFilename) {
		return readExcel(excelFilename, 0);
	}

	/**
	 * read the local excel file by target sheet parameter.
	 * 
	 * @param excelFilename
	 *            the local excel filename . absolute path
	 * @param sheetParam
	 *            the sheet parameter of excel. must be sheet name or index.
	 */
	public static ListVisitService<ExcelRow> readExcel(String excelFilename, Object sheetParam) {
		return new HSSFExcelInput().filePath(excelFilename).readService(sheetParam);
	}


}
