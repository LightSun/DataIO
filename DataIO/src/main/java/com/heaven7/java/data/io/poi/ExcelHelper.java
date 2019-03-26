package com.heaven7.java.data.io.poi;

import com.heaven7.java.visitor.FireBatchVisitor;
import com.heaven7.java.visitor.collection.ListVisitService;
import com.heaven7.java.visitor.collection.VisitServices;
import com.heaven7.java.visitor.util.Collections2;

import java.io.InputStream;
import java.util.Collection;
import java.util.List;

/**
 * @author heaven7
 */
public class ExcelHelper {

    private boolean useXlsx;
    private int sheetIndex = -1;
    private String sheetName;
    private String excelPath;
    private int skipToRowIndex = -1;
    private Object context;
    private InputStream inputStream;
    private ExcelVisitor visitor;

    protected ExcelHelper(ExcelHelper.Builder builder) {
        this.useXlsx = builder.useXlsx;
        this.sheetIndex = builder.sheetIndex;
        this.sheetName = builder.sheetName;
        this.excelPath = builder.excelPath;
        this.skipToRowIndex = builder.skipToRowIndex;
        this.context = builder.context;
        this.inputStream = builder.inputStream;
        this.visitor = builder.visitor;
    }

    public List<ExcelRow> read(){
        ExcelInput input = (useXlsx ? new XSSFExcelInput(): new HSSFExcelInput())
                .filePath(excelPath)
                .input(inputStream)
                .context(context)
                .visitor(visitor);
        if(skipToRowIndex >= 0){
            input.skipToRow(skipToRowIndex);
        }
        if(sheetName != null){
            return input.read(sheetName);
        }else {
            if(sheetIndex < 0){
                throw new IllegalStateException();
            }
            return input.read(sheetIndex);
        }
    }
    public ListVisitService<ExcelRow> readAsService(){
        return VisitServices.from(read());
    }


    public void readAndWrite(final ExcelDataService service){
        readAsService().fireBatch(new FireBatchVisitor<ExcelRow>() {
            @Override
            public Void visit(Collection<ExcelRow> coll, Object param) {
                new ExcelOutputImpl(service).writeBatch(Collections2.asList(coll));
                return null;
            }
        });
    }

    public boolean isUseXlsx() {
        return this.useXlsx;
    }

    public int getSheetIndex() {
        return this.sheetIndex;
    }

    public String getSheetName() {
        return this.sheetName;
    }

    public String getExcelPath() {
        return this.excelPath;
    }

    public int getSkipToRowIndex() {
        return this.skipToRowIndex;
    }

    public Object getContext() {
        return this.context;
    }

    public InputStream getInputStream() {
        return this.inputStream;
    }

    public ExcelVisitor getVisitor() {
        return this.visitor;
    }

    public static class Builder {
        private boolean useXlsx;
        private int sheetIndex = -1;
        private String sheetName;
        private String excelPath;
        private int skipToRowIndex = -1;
        private Object context;
        private InputStream inputStream;
        private ExcelVisitor visitor;

        public Builder setUseXlsx(boolean useXlsx) {
            this.useXlsx = useXlsx;
            return this;
        }

        public Builder setSheetIndex(int sheetIndex) {
            this.sheetIndex = sheetIndex;
            return this;
        }

        public Builder setSheetName(String sheetName) {
            this.sheetName = sheetName;
            return this;
        }

        public Builder setExcelPath(String excelPath) {
            this.excelPath = excelPath;
            return this;
        }

        public Builder setSkipToRowIndex(int skipToRowIndex) {
            this.skipToRowIndex = skipToRowIndex;
            return this;
        }

        public Builder setContext(Object context) {
            this.context = context;
            return this;
        }

        public Builder setInputStream(InputStream inputStream) {
            this.inputStream = inputStream;
            return this;
        }

        public Builder setVisitor(ExcelVisitor visitor) {
            this.visitor = visitor;
            return this;
        }

        public ExcelHelper build() {
            return new ExcelHelper(this);
        }
    }
}
