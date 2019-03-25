package com.heaven7.java.data.io.poi.write;

import com.heaven7.java.data.io.apply.ApplyDelegate;
import com.heaven7.java.data.io.util.StreamUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/** @author heaven7 */
public class DefaultExcelWriter implements ExcelWriter {

    private final PoiContext mContext = new PoiContext();
    private Workbook mWorkbook;

    @Override
    public ExcelWriter newWorkbook(byte type) {
        mWorkbook = type == TYPE_XSSF ? new XSSFWorkbook() : new HSSFWorkbook();
        mContext.setWorkbook(mWorkbook);
        return this;
    }

    @Override
    public ExcelWriter apply(ApplyDelegate<PoiContext> delegate) {
        delegate.apply(mContext);
        return this;
    }

    @Override
    public WorkbookFactory nesting() {
        return new DefaultWorkbookFactory(this, mContext);
    }

    @Override
    public void write(OutputStream out) {
        try {
            writeIO(out);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void writeIO(OutputStream out) throws IOException {
        if (mWorkbook == null) {
            throw new NullPointerException();
        }
        try {
            mWorkbook.write(out);
            out.flush();
        } finally {
            StreamUtils.closeQuietly(out);
        }
    }

    @Override
    public void writeIO(String file) throws IOException {
        if (mWorkbook == null) {
            throw new NullPointerException();
        }
        File f = new File(file);
        if (f.exists()) f.delete();
        OutputStream out = null;
        try {
            out = new FileOutputStream(f);
            mWorkbook.write(out);
            out.flush();
        } finally {
            StreamUtils.closeQuietly(out);
        }
    }

    @Override
    public void write(String file) {
        try {
            writeIO(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /*public*/ static class DefaultWorkbookFactory implements WorkbookFactory {

        final ExcelWriter parent;
        final PoiContext context;
        Sheet sheet;

        public DefaultWorkbookFactory(ExcelWriter parent, PoiContext context) {
            this.parent = parent;
            this.context = context;
        }

        @Override
        public WorkbookFactory newSheet(String name) {
            sheet = context.getWorkbook().createSheet(name);
            context.setSheet(sheet);
            return this;
        }

        @Override
        public WorkbookFactory apply(ApplyDelegate<PoiContext> delegate) {
            delegate.apply(context);
            return this;
        }

        @Override
        public SheetFactory nesting() {
            return new DefaultSheetFactory(this, context);
        }

        @Override
        public ExcelWriter end() {
            return parent;
        }
    }

    /*public*/ static class DefaultSheetFactory implements SheetFactory {

        final WorkbookFactory parent;
        final PoiContext context;
        Row row;

        public DefaultSheetFactory(WorkbookFactory parent, PoiContext context) {
            this.parent = parent;
            this.context = context;
        }

        @Override
        public SheetFactory newRow(int rownum, CellStyle style) {
            row = context.getSheet().createRow(rownum);
            if(style != null){
                row.setRowStyle(style);
            }
            context.setRow(row);
            return this;
        }

        @Override
        public SheetFactory newRow(int rowNum) {
            return newRow(rowNum, null);
        }

        @Override
        public SheetFactory apply(ApplyDelegate<PoiContext> delegate) {
            delegate.apply(context);
            return this;
        }

        @Override
        public RowFactory nesting() {
            return new DefaultRowFactory(this, context);
        }

        @Override
        public WorkbookFactory end() {
            return parent;
        }
    }

    /*public*/ static class DefaultRowFactory implements RowFactory {

        final SheetFactory parent;
        final PoiContext context;
        Cell cell;

        public DefaultRowFactory(SheetFactory parent, PoiContext context) {
            this.parent = parent;
            this.context = context;
        }

        @Override
        public RowFactory newCell(int column, CellType type) {
            if (type == null) {
                cell = context.getRow().createCell(column);
            } else {
                cell = context.getRow().createCell(column, type);
            }
            context.setCell(cell);
            return this;
        }
        @Override
        public RowFactory newCell(int column) {
            return newCell(column, null);
        }

        @Override
        public RowFactory apply(ApplyDelegate<PoiContext> delegate) {
            delegate.apply(context);
            return this;
        }

        @Override
        public SheetFactory end() {
            return parent;
        }
    }
}
