package com.heaven7.java.data.io.poi;

import com.heaven7.java.base.anno.Nullable;
import com.heaven7.java.data.io.poi.adapter.ExcelVisitorAdapter;
import com.heaven7.java.visitor.PredicateVisitor;
import com.heaven7.java.visitor.collection.ListVisitService;
import com.heaven7.java.visitor.collection.VisitServices;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/** @author heaven7 */
public abstract class BaseExcelInput implements ExcelInput {

    private String filePath;
    private int startRowIndex;
    private RowInterceptor interceptor;
    private ExcelVisitor visitor = ExcelVisitorAdapter.EMPTY;

    @Override
    public ExcelInput visitor(@Nullable ExcelVisitor visitor) {
        this.visitor = visitor != null ? visitor : ExcelVisitorAdapter.EMPTY;
        return this;
    }

    public int getStartRowIndex() {
        return startRowIndex;
    }

    public String getFilePath() {
        return filePath;
    }

    public RowInterceptor getRowInterceptor() {
        return interceptor;
    }

    @Override
    public ExcelInput filePath(String filePath) {
        this.filePath = filePath;
        return this;
    }

    @Override
    public ExcelInput rowInterceptor(RowInterceptor interceptor) {
        this.interceptor = interceptor;
        return this;
    }

    @Override
    public ExcelInput skipToRow(int rowIndex) {
        startRowIndex = rowIndex;
        return this;
    }

    @Override
    public List<ExcelRow> read() {
        return read(null);
    }

    @Override
    public ListVisitService<ExcelRow> readService() {
        return VisitServices.from(read());
    }

    @Override
    public ListVisitService<ExcelRow> readService(Object param) {
        return VisitServices.from(read(param));
    }

    @Override
    public List<ExcelRow> read(Object sheetParam) {
        final RowInterceptor rowInterceptor = getRowInterceptor();
        final ExcelVisitor visitor = this.visitor;

        final List<ExcelRow> rows = new ArrayList<ExcelRow>();
        Workbook workbook = null;
        try {
            workbook = onCreateWorkbook(getFilePath());
            final Sheet sheet = PoiUtils.getSheet(workbook, sheetParam);
            visitor.startVisitSheet(workbook, sheet);

            int count = sheet.getLastRowNum() + 1;
            int start = Math.max(0, getStartRowIndex());
            for (int i = start; i < count; i++) {
                Row row = sheet.getRow(i);
                if(row == null){
                    continue;
                }
                ExcelRow excelRow = new ExcelRow(row);
                visitor.visitRow(workbook, sheet, excelRow, rows);
                rows.add(excelRow);
            }
            visitor.endVisitSheet(workbook, sheet, rows);
            if (rowInterceptor == null) {
                return rows;
            }
            return VisitServices.from(rows).filter(new PredicateVisitor<ExcelRow>() {
                @Override
                public Boolean visit(ExcelRow row, Object param) {
                    return !rowInterceptor.intercept(row);
                }
            }).getAsList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (workbook != null) {
                try {
                    workbook.close();
                } catch (IOException e) {
                }
            }
        }
    }

  /**
   * called on create workbook
   * @param filePath the excel path
   * @return the excel Workbook
   */
    protected abstract Workbook onCreateWorkbook(String filePath) throws IOException;
}
