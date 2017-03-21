package com.heaven7.java.data.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import com.heaven7.java.data.io.util.StreamUtils;


public class ExcelHelper {
	private static final float DEFAULT_HEIGHT = 30;
	private static final int DEFAULT_COL_WIDTH = 20000;
	// 标记是item还是string
	private static final char TYPE_ITEM = '1';
	private static final char TYPE_STRING = '0';
	private static final String ENGLISH = "English";
	private static final String TRANSLATE = "Translate";
	//按照放置顺序的比较器
	private static final Comparator<String> sDefaultComparator = new Comparator<String>() {
		@Override
		public int compare(String arg0, String arg1) {
			return 1;
		}
	};

	private final HSSFWorkbook workbook;

	private ExcelHelper(HSSFWorkbook workbook) {
		super();
		this.workbook = workbook;
	}


	/** @see #readFromExcel(String, int, int...) */
	public static TreeMap<String, String> readFromExcel(String filename,
			int... ignoredRows) {
		return readFromExcel(filename, 0, ignoredRows);
	}

	/**
	 * @param filename
	 *            the absolute path eg:xxx.xls
	 * @param sheetIndex
	 *            the index of sheet
	 * @param ignoredRows
	 *            the all row you want to ignored
	 * @return map with the key = the namedvalue, value = translated text
	 */
	public static TreeMap<String, String> readFromExcel(String filename,
			int sheetIndex, int... ignoredRows) {

		final TreeMap<String, String> map = new TreeMap<String, String>(sDefaultComparator);
		InputStream in = null;
		try {
			in = new FileInputStream(filename);
			POIFSFileSystem ts = new POIFSFileSystem(in);
			HSSFSheet sh = new HSSFWorkbook(ts).getSheetAt(sheetIndex);

			int currentRow = 0;
			Iterator<Row> itRows = sh.rowIterator();
			while (itRows.hasNext()) {
				final Row row = itRows.next();
				currentRow++;
				// 忽略的行
				if (ignored(currentRow, ignoredRows))
					continue;

				String key = null;
				String value = null;

				int tmpColIndex = 1;
				final Iterator<Cell> itCells = row.cellIterator();
				while (itCells.hasNext()) {
					Cell cell = itCells.next();
					try {
						//String str = cell.getStringCellValue();//遇到纯数字的会出异常
						String str = cell.toString();
						System.out.println("str = "+str);
						if (tmpColIndex == 1) {
							key = str;
						} else if (tmpColIndex == 3) {
							value = str;
						} else {
							if (tmpColIndex != 2) // 第2行忽略
				System.err.println("error cells>3,while currentRow = "+ currentRow);
						}
						tmpColIndex++;
					} catch (Exception e) {
						System.out.println(e);
					}
				}
				map.put(key, value != null ? value : "");
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			StreamUtils.closeQuietly(in);
		}
		return map;
	}

	private static boolean ignored(int targetRow, int... ignoredRows) {
		if(ignoredRows == null){
			return false;
		}
		for (int i = 0, len = ignoredRows.length; i < len; i++) {
			if (ignoredRows[i] == targetRow)
				return true;
		}
		return false;
	}

	/** 将excel输出到指定路径(后缀名应该是xx.xls) */
	public void writeExcel(String outPath) {
		// 删除旧的
		File f = new File(outPath);
		if (f.exists())
			f.delete();
		OutputStream out = null;
		try {
			out = new FileOutputStream(outPath);
			workbook.write(out);
			out.flush();
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			StreamUtils.closeQuietly(out);
		}
	}

	public static void createDefaultRow(HSSFSheet sheet, int rowIndex,
			String key, String text) {
		HSSFRow row = sheet.createRow(rowIndex);
		row.setHeightInPoints(DEFAULT_HEIGHT);
		// 作为后面读取的key
		HSSFCell cell = row.createCell(0);
		cell.setCellType(HSSFCell.CELL_TYPE_STRING);
		cell.setCellValue(key);
		// 被翻译的列
		cell = row.createCell(1);
		cell.setCellType(HSSFCell.CELL_TYPE_STRING);
		cell.setCellValue(text);
	}

	public static HSSFSheet createDefaultSheet(HSSFWorkbook workbook,
			String sheetName) {
		//TODO 四列: 1列存   item 或者 string 标记+ 截断标志 + key, item索引？ 截断索引？
		HSSFSheet sheet = workbook.createSheet(sheetName != null ? sheetName
				: "");
		sheet.setDefaultRowHeight((short) 500);
		sheet.setColumnHidden(0, true);
		//sheet.setColumnHidden(1, true);
		// 3列，第一列为xml的关键key（默认隐藏）,一列为要翻译的，另一列为翻译的结果
		sheet.setColumnWidth(0, 0);
		//sheet.setColumnWidth(1, 0);
		sheet.setColumnWidth(1, DEFAULT_COL_WIDTH);
		sheet.setColumnWidth(2, DEFAULT_COL_WIDTH);

		return sheet;
	}

	public static class Builder {
		private final HSSFWorkbook workbook;
		private int rowIndex;
		private HSSFSheet sheet;

		public Builder() {
			workbook = new HSSFWorkbook();
		}

		public Builder setSheetName(String sheetName) {
			sheet = createDefaultSheet(workbook, sheetName);
			return this;
		}

		public Builder addFirstRow(String key, String value) {
			HSSFRow row = sheet.createRow(rowIndex);
			setStyle(row);
			// 0列隐藏
			HSSFCell cell = row.createCell(1);
			cell.setCellType(Cell.CELL_TYPE_STRING);
			cell.setCellValue(key);

			cell = row.createCell(2);
			cell.setCellType(Cell.CELL_TYPE_STRING);
			cell.setCellValue(value);
			rowIndex++;
			return this;
		}

		private void setStyle(HSSFRow row) {
			HSSFFont font = workbook.createFont();
			font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
			font.setFontHeightInPoints((short) DEFAULT_HEIGHT);
			// font.setFontName("黑体");
			font.setColor(HSSFFont.COLOR_RED);
			HSSFCellStyle style = workbook.createCellStyle();
			style.setFont(font);
			row.setRowStyle(style);
		}

		public ExcelHelper build() {
			// to do something?
			return new ExcelHelper(workbook);
		}
	}
}
