package com.heaven7.java.data.io.test;

import java.util.List;
import java.util.TreeMap;

import com.heaven7.java.data.io.ExcelHelper;
import com.heaven7.java.data.io.poi.ExcelCol;
import com.heaven7.java.data.io.poi.ExcelDataServiceAdapter;
import com.heaven7.java.data.io.poi.ExcelRow;
import com.heaven7.java.data.io.poi.PoiUtils;
import com.heaven7.java.visitor.FireVisitor;
import com.heaven7.java.visitor.collection.VisitServices;

import junit.framework.TestCase;

public class ExcelTest extends TestCase{
	
	private final String mFilename = "E:/tmp/strings_zh.xls";
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}
	
	public void testReadExcelAndWrite(){
		PoiUtils.readExcelAndWrite(mFilename, new ExcelDataServiceAdapter() {
			@Override
			public int insertBatch(List<ExcelRow> t) {
				fire(t);
				return t.size();
			}
		});
	}
	
	public void testExcelReader(){
		PoiUtils.readExcel(mFilename).fire(new FireVisitor<ExcelRow>() {
			@Override
			public Boolean visit(ExcelRow t, Object param) {
				System.out.println(t.getRowIndex());
				fireColumn(t.getColumns());
				return null;
			}
		});
	}
	protected void fire(List<ExcelRow> t){
		VisitServices.from(t).fire(new FireVisitor<ExcelRow>() {
			@Override
			public Boolean visit(ExcelRow t, Object param) {
				//System.out.println(t.getRowIndex());
				fireColumn(t.getColumns());
				return null;
			}
		});
	}
	
	protected void fireColumn(List<ExcelCol> columns) {
		System.out.println(columns);
	}

	public void testOldExcelHelper(){
		TreeMap<String, String> map = ExcelHelper.readFromExcel(mFilename, new int[]{1});
		System.out.println(map);
	}
}
