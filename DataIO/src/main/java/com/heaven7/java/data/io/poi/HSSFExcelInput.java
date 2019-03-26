package com.heaven7.java.data.io.poi;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * read excel.
 * @author heaven7
 *
 */
public class HSSFExcelInput extends BaseExcelInput implements ExcelInput {

	@Override
	protected Workbook onCreateWorkbook(InputStream in) throws IOException {
		return new HSSFWorkbook(in);
	}


	
}
