package com.heaven7.poi;

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

import com.heaven7.xml.Array;
import com.heaven7.xml.StreamUtils;
import com.heaven7.xml.XmlReader;
import com.heaven7.xml.XmlReader.Element;
import com.heaven7.xml.XmlWriter;
import com.heaven7.xml.android.AndroidTags;
import com.heaven7.xml.android.AndroidTool;
import com.heaven7.xml.android.ResourceElement;
import com.heaven7.xml.android.StringArrayElement;
import com.heaven7.xml.android.StringElement;

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

	public static void writeAndroidStringXml(String excelFilename,
			String xmlPath) {
		File f = new File(xmlPath);
		if (f.exists())
			f.delete();
		// 存放string array
		HashMap<String, StringArrayElement> arrayMap = new HashMap<String, StringArrayElement>();
		final ResourceElement re = new ResourceElement();
		
		TreeMap<String, String> map = readFromExcel(excelFilename,
				new int[] { 1 });
		Entry<String, String> en = null;
		while((en = map.pollFirstEntry())!=null){
			String key = en.getKey();
			String val = en.getValue();
			if("".equals(key)) 
				continue;
			//System.out.println((val = String.format(Locale.GERMANY, "%s", val)));
			final char ch = key.charAt(0);
			if (ch == TYPE_ITEM) {
				String realKey = null;
				// 第2和第3个字符是数字-那么肯定是item标记
				if (Character.isDigit(key.charAt(1))
						&& Character.isDigit(key.charAt(2))) {
					realKey = key.substring(3);
				} else
					realKey = key.substring(2);
				// 112xxx 第一位是标志位
				StringArrayElement sae = arrayMap.get(realKey);
				if (sae == null) {
					sae = new StringArrayElement();
					sae.setNamedValue(realKey);
					arrayMap.put(realKey, sae);
				}
				sae.putAnQuotedItemText(val);
			} else if (ch == TYPE_STRING) {
				StringElement se = new StringElement();
				se.setNamedValue(key.substring(1)).setQuotedText(val);
				re.addStringElement(se);
			}
		}
		re.addAllStringArrayElements(arrayMap.values());
		// write
		Writer writer = null;
		try {
			writer = new FileWriter(xmlPath);
			re.write(new XmlWriter(writer));
			writer.flush();
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			StreamUtils.closeQuietly(writer);
		}
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
		for (int i = 0, len = ignoredRows.length; i < len; i++) {
			if (ignoredRows[i] == targetRow)
				return true;
		}
		return false;
	}

	/**
	 * @param filename
	 *            the absolute path
	 */
	public static ExcelHelper buildFromAndroidStringXml(String filename) {
		Builder b = new Builder().setSheetName("i18n").addFirstRow(ENGLISH,
				TRANSLATE);
		try {
			Element e = new XmlReader().parse(new FileInputStream(filename));
			Array<Element> stringEles = e
					.getChildrenByNameRecursively(AndroidTags.STRING);
			Array<Element> strArrayEles = e
					.getChildrenByNameRecursively(AndroidTags.STRING_ARRAY);
			// Array<Element> itemEles =
			// e.getChildrenByNameRecursively(AndroidTags.ITEM);
			int len = stringEles.size;
			for (int i = 0; i < len; i++) {
				b.addRow(stringEles.get(i));
			}
			// the items of string array
			len = strArrayEles.size;
			for (int i = 0; i < len; i++) {
				final Element strArrayEle = strArrayEles.get(i);
				final String namedValue = strArrayEle.getAttribute(AndroidTags.NAME);
				final Array<Element> eles = strArrayEle
						.getChildrenByName(AndroidTags.ITEM);
				for (int j = 0, len2 = eles.size; j < len2; j++) {
					b.addRow(eles.get(j), namedValue, j);
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return b.build();
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

		/** 添加string array中的一个item */
		public Builder addRow(Element e, String stringArrayNamedValue, int index) {
			createDefaultRow(sheet, rowIndex, "" + TYPE_ITEM + index
					+ stringArrayNamedValue, AndroidTool.trimQuote(e.getText()));
			rowIndex++;
			return this;
		}

		/** 添加一个string */
		public Builder addRow(Element e) {
			// System.out.println(e.getText()); //cdata被过滤了
			try {
				createDefaultRow(sheet, rowIndex,
						TYPE_STRING + e.getAttribute(AndroidTags.NAME),
						AndroidTool.trimQuote(e.getText()));
			} catch (Exception e1) {
				throw new RuntimeException(
						"this method #addRow(Element e) only support the tag like: "
								+ " <String name=\"aaaa\">dfdfsfs</string> ");
			}
			rowIndex++;
			return this;
		}
		
		public ExcelHelper build() {
			// to do something?
			return new ExcelHelper(workbook);
		}
	}
}
