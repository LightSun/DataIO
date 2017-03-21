package com.heaven7.java.data.io.poi;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Row;

import com.heaven7.java.data.io.util.StreamUtils;
import com.heaven7.java.visitor.collection.ListVisitService;
import com.heaven7.java.visitor.collection.VisitServices;

/**
 * read excel.
 * @author heaven7
 *
 */
public class ExcelInputImpl implements ExcelInput {

	private final String mFileName;

	public ExcelInputImpl(String mFileName) {
		super();
		this.mFileName = mFileName;
	}
	
	@Override
	public List<ExcelRow> read(){
		return read(0);
	}
	
	@Override
	public List<ExcelRow> read(Object param) {
		InputStream in = null;
		try {
			in = new FileInputStream(mFileName);
			final HSSFSheet sh = getSheet(new HSSFWorkbook(new POIFSFileSystem(in)), param);
			final List<ExcelRow> rows = new ArrayList<ExcelRow>();
			final Iterator<Row> it = sh.rowIterator();

			for (; it.hasNext();) {
				rows.add(new ExcelRow(it.next()));
			}
			return rows;
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			StreamUtils.closeQuietly(in);
		}
	}
	
	@Override
	public ListVisitService<ExcelRow> readService(){
		return VisitServices.from(read());
	}
	
	@Override
	public ListVisitService<ExcelRow> readService(Object param){
		return VisitServices.from(read(param));
	}

	protected HSSFSheet getSheet(HSSFWorkbook book, Object param) {
		if (param == null) {
			throw new IllegalStateException();
		}
		
		if (param instanceof Integer) {
			return book.getSheetAt((Integer) param);
		} else if (param instanceof String) {
			return book.getSheet((String) param);
		}
		
		throw new IllegalStateException();
	}

}
