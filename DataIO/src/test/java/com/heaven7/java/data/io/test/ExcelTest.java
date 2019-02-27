package com.heaven7.java.data.io.test;

import com.heaven7.java.data.io.poi.*;
import com.heaven7.java.visitor.FireVisitor;
import com.heaven7.java.visitor.collection.VisitServices;
import junit.framework.TestCase;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class ExcelTest extends TestCase{

  // private final String mFilename = "E:/tmp/strings_zh.xls";
  private final String mFilename =
      "E:\\study\\github\\research-institute\\python\\Py_work\\Lib\\site-packages\\pandas\\tests\\io\\data\\test1.xls";

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
	public static void fire(List<ExcelRow> t){
		VisitServices.from(t).fire(new FireVisitor<ExcelRow>() {
			@Override
			public Boolean visit(ExcelRow t, Object param) {
				//System.out.println(t.getRowIndex());
				fireColumn(t.getColumns());
				return null;
			}
		});
	}

	public static void fireColumn(List<ExcelCol> columns) {
		System.out.println(columns);
	}
	@Test
	public void test4() {
		//poi 读取合并单元格的时候， 内容只会在第一行。
		String exl = "E:\\tmp\\bugfinds\\方案.xlsx";
		ExcelHelper helper = new ExcelHelper.Builder()
				.setUseXlsx(true)
				.setExcelPath(exl)
				// .setSheetName("切换音乐 - 表格 1-1-1-1")
				.setSheetName("工作表 1 - 表格 2-1")
				.setSkipToRowIndex(1)
				.build();
		List<ExcelRow> rows = helper.read();
        VisitServices.from(rows)
                .fire(
                        new FireVisitor<ExcelRow>() {
                            @Override
							public Boolean visit(ExcelRow row, Object param) {
                                System.out.println("============================ row index = " + row.getRowIndex());
                                VisitServices.from(row.getColumns())
                                        .fire(
                                                new FireVisitor<ExcelCol>() {
                                                    @Override
                                                    public Boolean visit(
                                                            ExcelCol excelCol, Object param) {
                                                        System.out.println(
                                                                "test : "
                                                                        + excelCol
                                                                                .getColumnString());
                                                        /*String[] strs =
                                                                excelCol.getColumnString()
                                                                        .split("\\n");
                                                        System.out.println(
                                                                "Arrays: " + Arrays.toString(strs));*/
                                                        return null;
                                                    }
                                                });
                                return null;
                            }
                        });
	}

}
