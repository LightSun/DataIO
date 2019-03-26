package com.heaven7.java.data.io.poi;

import com.heaven7.java.base.anno.Nullable;
import com.heaven7.java.base.util.IOUtils;
import com.heaven7.java.base.util.ResourceLoader;
import com.heaven7.java.data.io.poi.adapter.ExcelVisitorAdapter;
import com.heaven7.java.visitor.PredicateVisitor;
import com.heaven7.java.visitor.collection.ListVisitService;
import com.heaven7.java.visitor.collection.VisitServices;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/** @author heaven7 */
public abstract class BaseExcelInput implements ExcelInput {

    private String filePath;
    private int startRowIndex;
    private RowInterceptor interceptor;
    private ExcelVisitor visitor = ExcelVisitorAdapter.EMPTY;
    private InputStream in;
    private Object mContext;

    @Override
    public ExcelInput visitor(@Nullable ExcelVisitor visitor) {
        this.visitor = visitor != null ? visitor : ExcelVisitorAdapter.EMPTY;
        return this;
    }

    @Override
    public ExcelInput context(Object context) {
        this.mContext = context;
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
    public ExcelInput input(InputStream in) {
        this.in = in;
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
        InputStream in = this.in;
        try {
            if(in == null){
                in = ResourceLoader.getDefault().loadFileAsStream(mContext, filePath);
            }
            workbook = onCreateWorkbook(in);
            final Sheet sheet = PoiUtils.getSheet(workbook, sheetParam);
            if(sheet == null){
                throw new RuntimeException("can't find sheet for " + sheetParam);
            }
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
            IOUtils.closeQuietly(workbook);
            IOUtils.closeQuietly(in);
        }
    }

  /**
   * called on create workbook
   * @param in the input
   * @return the excel Workbook
   */
    protected abstract Workbook onCreateWorkbook(InputStream in) throws IOException;
}
