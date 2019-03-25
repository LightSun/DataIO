package com.heaven7.java.data.io.os.sources;

import com.heaven7.java.base.util.IOUtils;
import com.heaven7.java.base.util.Platforms;
import com.heaven7.java.base.util.TextReadHelper;
import com.heaven7.java.data.io.poi.ExcelCol;
import com.heaven7.java.data.io.poi.ExcelHelper;
import com.heaven7.java.data.io.poi.ExcelRow;
import com.heaven7.java.data.io.poi.apply.Cell_StringApplier;
import com.heaven7.java.data.io.poi.apply.Sheet_WidthHeightApplier;
import com.heaven7.java.data.io.poi.apply.TitleRowApplier;
import com.heaven7.java.data.io.poi.write.DefaultExcelWriter;
import com.heaven7.java.data.io.poi.write.ExcelWriter;
import com.heaven7.java.visitor.FireIndexedVisitor;
import com.heaven7.java.visitor.ResultIndexedVisitor;
import com.heaven7.java.visitor.ResultVisitor;
import com.heaven7.java.visitor.StartEndVisitor;
import com.heaven7.java.visitor.collection.ListVisitService;
import com.heaven7.java.visitor.collection.VisitServices;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author heaven7
 */
public abstract class SourceIO {

    public static <T> ListSource<T> readAsListSource(InputStream in, final InTransformer<T> transformer){
        try{
            List<String> list = IOUtils.readStringLines(new InputStreamReader(in));
            return new DirectListSource<>(VisitServices.from(list).map(new ResultVisitor<String, T>() {
                @Override
                public T visit(String s, Object param) {
                    return transformer.transform(s);
                }
            }).getAsList());
        }catch (IOException e){
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(in);
        }
    }
    public static <T> TableSource<T> readAsTableSource(InputStream in, final int skipRowCount, final boolean hasTitle,
                                                       final String separator, final InTransformer<T> transformer){
        final ResultVisitor<String, T> inVisitor = new ResultVisitor<String, T>() {
            @Override
            public T visit(String s, Object param) {
                return transformer.transform(s);
            }
        };
        try{
            final List<String> titles = new ArrayList<>();
            List<String> list = IOUtils.readStringLines(new InputStreamReader(in));
            List<List<T>> tables = VisitServices.from(list).mapIndexed(null, new ResultIndexedVisitor<String, List<T>>() {
                boolean titleHandled = !hasTitle;
                @Override
                public List<T> visit(Object param, String s, int index, int size) {
                    if (index < skipRowCount) {
                        return null;
                    }
                    if (!titleHandled) {
                        titleHandled = true;
                        VisitServices.from(s.split(separator)).save(titles);
                        return null;
                    }
                    return VisitServices.from(s.split(separator)).map(inVisitor).getAsList();
                }
            }).getAsList();
            return new TitleTableSource<>(titles, tables);
        }catch (IOException e){
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(in);
        }
    }
    public static <T> ListSource<T> readTextFile(Object context,String file, final InTransformer<T> transformer){
         return new FileLinesListSource<>(context, file, new TextReadHelper.Callback<T>() {
             @Override
             public T parse(String line) {
                 return transformer.transform(line);
             }
         });
    }
    public static <T> TableSource<T> readTextFile(Object context, String file, final String separator, final InTransformer<T> transformer){
        final ResultVisitor<String, T> inVisitor = new ResultVisitor<String, T>() {
            @Override
            public T visit(String s, Object param) {
                return transformer.transform(s);
            }
        };
        return new FileLinesTableSource<>(context, file, new TextReadHelper.Callback<List<T>>() {
            @Override
            public List<T> parse(String line) {
                return VisitServices.from(line.split(separator)).map(inVisitor).getAsList();
            }
        });
    }
    public static <T> ListSource<T> readExcelFile(ExcelHelper helper, final int columnIndex, final InTransformer<T> transformer){
        return new DirectListSource<>(VisitServices.from(helper.read()).map(new ResultVisitor<ExcelRow, String>() {
            @Override
            public String visit(ExcelRow row, Object param) {
                return row.getColumns().get(columnIndex).getColumnString().trim();
            }
        }).map(new ResultVisitor<String, T>() {
            @Override
            public T visit(String s, Object param) {
                return transformer.transform(s);
            }
        }).getAsList());
    }
    public static <T> TableSource<T> readExcelFile(ExcelHelper helper, final int skipRowCount, final boolean hasTitle,final InTransformer<T> transformer){
        final ResultVisitor<ExcelCol, String> inVisitor = new ResultVisitor<ExcelCol, String>() {
            @Override
            public String visit(ExcelCol excelCol, Object param) {
                return excelCol.getColumnString().trim();
            }
        };
        final ResultVisitor<String, T> transVisitor = new ResultVisitor<String, T>() {
            @Override
            public T visit(String s, Object param) {
                return transformer.transform(s);
            }
        };
        final List<String> titles = new ArrayList<>();
        //VisitServices.from(row.getColumns()).map(inVisitor).getAsList();
        List<List<T>> tables = VisitServices.from(helper.read()).mapIndexed(null, new ResultIndexedVisitor<ExcelRow, List<T>>() {
            boolean titleHandled = !hasTitle;

            @Override
            public List<T> visit(Object param, ExcelRow row, int index, int size) {
                if (index < skipRowCount) {
                    return null;
                }
                if (!titleHandled) {
                    titleHandled = true;
                    VisitServices.from(row.getColumns()).map(inVisitor).save(titles);
                    return null;
                }
                return VisitServices.from(row.getColumns()).map(inVisitor).map(transVisitor).getAsList();
            }
        }).getAsList();
        return new TitleTableSource<>(titles, tables);
    }

    public static <T> void writeTextFile(ListSource<T> source, String file, final OutTransformer<? super T> transformer){
        FileWriter fos = null;
        try {
            fos = new FileWriter(file);
            writeTextFile(source, fos, transformer);
        }catch (IOException e){
            throw new RuntimeException(e);
        }finally {
            IOUtils.closeQuietly(fos);
        }
    }
    public static <T> void writeTextFile(ListSource<T> source, Writer writer, final OutTransformer<? super T> transformer){
        final StringBuilder sb = new StringBuilder();
        VisitServices.from(source.getList()).fireWithStartEnd(new StartEndVisitor<T>() {
            @Override
            public boolean visit(Object param, T t, boolean start, boolean end) {
                sb.append(transformer.transform(t));
                if(!end){
                    sb.append(Platforms.getNewLine());
                }
                return false;
            }
        });
        try {
            writer.write(sb.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> void writeTextFile(TableSource<T> source, String file, final String separator, final OutTransformer<? super T> transformer){
        FileWriter fos = null;
        try {
            fos = new FileWriter(file);
            writeTextFile(source, fos, separator, transformer);
        }catch (IOException e){
            throw new RuntimeException(e);
        }finally {
            IOUtils.closeQuietly(fos);
        }
    }
    public static <T> void writeTextFile(TableSource<T> source, Writer writer, final String separator, final OutTransformer<? super T> transformer){
        final StringBuilder sb = new StringBuilder();
        final ResultVisitor<T,String> rs = new ResultVisitor<T, String>() {
            @Override
            public String visit(T t, Object param) {
                return transformer.transform(t);
            }
        };
        final StartEndVisitor<String> lineVisitor = new StartEndVisitor<String>() {
            @Override
            public boolean visit(Object param, String s, boolean start, boolean end) {
                sb.append(s);
                if (!end) {
                    sb.append(separator);
                }
                return false;
            }
        };
        VisitServices.from(source.getList()).map(new ResultVisitor<List<T>, List<String>>() {
            @Override
            public List<String> visit(List<T> ts, Object param) {
                return VisitServices.from(ts).map(rs).getAsList();
            }
        }).asListService().fireWithStartEnd(new StartEndVisitor<List<String>>() {
            @Override
            public boolean visit(Object param, List<String> list, boolean start, boolean end) {
                VisitServices.from(list).fireWithStartEnd(lineVisitor);
                if(!end){
                    sb.append(Platforms.getNewLine());
                }
                return false;
            }
        });
        try {
            writer.write(sb.toString());
        } catch (IOException e) {
           throw new RuntimeException(e);
        }
    }

    public static <T> void writeExcelFile(ListSource<T> source, String excelFile, ExcelOutConfig config, OutTransformer<? super T> transformer){
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(excelFile);
            writeExcelFile(source, fos, config, transformer);
        }catch (IOException e){
            throw new RuntimeException(e);
        }finally {
            IOUtils.closeQuietly(fos);
        }
    }
    public static <T> void writeExcelFile(ListSource<T> source, OutputStream out, ExcelOutConfig config, OutTransformer<? super T> transformer){
        List<String> names = config.getColumnNames();
        ExcelWriter.SheetFactory sf = new DefaultExcelWriter().newWorkbook(ExcelWriter.TYPE_XSSF)
                .nesting()
                .newSheet(config.getSheetName())
                .apply(new Sheet_WidthHeightApplier(config.getWidth(), config.getHeight(), names.size()))
                .apply(new TitleRowApplier(names))
                .nesting();
        int rowIndex = 1;
        for (T t : source.getList()){
            sf.newRow(rowIndex)
                    .nesting()
                    .newCell(0)
                    .apply(new Cell_StringApplier(transformer.transform(t)))
                    .end();
            rowIndex ++;
        }
        sf.end().end().write(out);
    }

    public static <T> void writeExcelFile(TableSource<T> source, String excelFile, ExcelOutConfig config, final OutTransformer<? super T> transformer){
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(excelFile);
            writeExcelFile(source, fos, config, transformer);
        }catch (IOException e){
            throw new RuntimeException(e);
        }finally {
            IOUtils.closeQuietly(fos);
        }
    }

    public static <T> void writeExcelFile(TableSource<T> source, OutputStream out, ExcelOutConfig config, final OutTransformer<? super T> transformer){
        List<String> names = config.getColumnNames();
        final ExcelWriter.SheetFactory sf = new DefaultExcelWriter().newWorkbook(ExcelWriter.TYPE_XSSF)
                .nesting()
                .newSheet(config.getSheetName())
                .apply(new Sheet_WidthHeightApplier(config.getWidth(), config.getHeight(), names.size()))
                .apply(new TitleRowApplier(names))
                .nesting();
        final AtomicInteger excelIndex = new AtomicInteger(1);
        VisitServices.from(source.getList()).fireWithIndex(new FireIndexedVisitor<List<T>>() {
            @Override
            public Void visit(Object param, List<T> ts, int index, int size) {
                final ExcelWriter.RowFactory rowF = sf.newRow(excelIndex.getAndIncrement()).nesting();
                VisitServices.from(ts).fireWithIndex(new FireIndexedVisitor<T>() {
                    @Override
                    public Void visit(Object param, T t, int index, int size) {
                        rowF.newCell(index).apply(new Cell_StringApplier(transformer.transform(t)));
                        return null;
                    }
                });
                return null;
            }
        });
        sf.end().end().write(out);
    }
}
