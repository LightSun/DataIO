package com.heaven7.java.data.io.os.sources;

import com.heaven7.java.base.util.IOUtils;
import com.heaven7.java.base.util.Platforms;
import com.heaven7.java.base.util.Predicates;
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
import com.heaven7.java.visitor.collection.VisitServices;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author heaven7
 */
public abstract class SourceIO {

    private static final String TITLE_SEPARATOR = " ";

    public static <T> ListSource<T> readAsListSource(Reader in, boolean hasTitle, final InTransformer<T> transformer){
        try{
            List<String> list = IOUtils.readStringLines(in);
            final List<String> titles;
            if(hasTitle){
                String[] strs = list.remove(0).split(TITLE_SEPARATOR);
                titles = new ArrayList<>(Arrays.asList(strs));
            }else {
                titles = null;
            }
            DirectListSource<T> src = new DirectListSource<>(VisitServices.from(list).map(new ResultVisitor<String, T>() {
                @Override
                public T visit(String s, Object param) {
                    return transformer.transform(s);
                }
            }).getAsList());
            return hasTitle ? new TitleListSource<T>(titles, src) : src;
        }catch (IOException e){
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(in);
        }
    }
    public static <T> TableSource<T> readAsTableSource(Reader in, final int skipRowCount, final boolean hasTitle,
                                                       final String separator, final InTransformer<T> transformer){
        final ResultVisitor<String, T> inVisitor = new ResultVisitor<String, T>() {
            @Override
            public T visit(String s, Object param) {
                return transformer.transform(s);
            }
        };
        try{
            final List<String> titles = hasTitle ? new ArrayList<String>() : null;
            List<String> list = IOUtils.readStringLines(in);
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
            if(hasTitle){
                return new TitleTableSource<>(titles, tables);
            }
            return new DirectTableSource<>(tables);
        }catch (IOException e){
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(in);
        }
    }
    public static <T> ListSource<T> readTextFile(String file, boolean hasTitle, final InTransformer<T> transformer){
        return readTextFile((Object)null, file, hasTitle, transformer);
    }
    public static <T> ListSource<T> readTextFile(Object context, String file, final boolean hasTitle, final InTransformer<T> transformer){
        final List<String> titles = hasTitle ? new ArrayList<String>() : null;
        List<T> list = new FileLinesListSource<>(context, file, new TextReadHelper.Callback<T>() {
            boolean titleHandled = !hasTitle;
            @Override
            public T parse(String line) {
                if (!titleHandled) {
                    titleHandled = true;
                    VisitServices.from(line.split(TITLE_SEPARATOR)).save(titles);
                }
                return transformer.transform(line);
            }
        }).getList();
        return hasTitle ? new TitleListSource<T>(titles, new DirectListSource<T>(list)) : new DirectListSource<T>(list);
    }
    public static <T> TableSource<T> readTextFile(String file, final String separator,
                                                  final boolean hasTitle, final InTransformer<T> transformer){
        return readTextFile(null, file, separator, hasTitle, transformer);
    }
    public static <T> TableSource<T> readTextFile(Object context, String file, final String separator,
                                                  final boolean hasTitle, final InTransformer<T> transformer){
        final ResultVisitor<String, T> inVisitor = new ResultVisitor<String, T>() {
            @Override
            public T visit(String s, Object param) {
                return transformer.transform(s);
            }
        };
        final List<String> titles = hasTitle ? new ArrayList<String>() : null;
        List<List<T>> tables = new FileLinesTableSource<>(context, file, new TextReadHelper.Callback<List<T>>() {
            boolean titleHandled = !hasTitle;
            @Override
            public List<T> parse(String line) {
                if (!titleHandled) {
                    titleHandled = true;
                    VisitServices.from(line.split(separator)).save(titles);
                    return null;
                }
                return VisitServices.from(line.split(separator)).map(inVisitor).getAsList();
            }
        }).getTable();
        if(hasTitle){
            return new TitleTableSource<>(titles, tables);
        }
        return new DirectTableSource<>(tables);
    }
    public static <T> ListSource<T> readExcelAsList(ExcelHelper helper, final int columnIndex, final boolean hasTitle,
                                                  final InTransformer<T> transformer){
        final List<String> titles = hasTitle ? new ArrayList<String>() : null;
        List<T> list = VisitServices.from(helper.read()).map(new ResultVisitor<ExcelRow, String>() {
            @Override
            public String visit(ExcelRow row, Object param) {
                return row.getColumns().get(columnIndex).getColumnString().trim();
            }
        }).map(new ResultVisitor<String, T>() {
            boolean titleHandled = !hasTitle;
            @Override
            public T visit(String s, Object param) {
                if(!titleHandled){
                    titleHandled = true;
                    titles.add(s);
                }
                return transformer.transform(s);
            }
        }).getAsList();
        return hasTitle ? new TitleListSource<T>(titles, new DirectListSource<T>(list)) : new DirectListSource<T>(list);
    }
    public static <T> TableSource<T> readExcelAsTable(ExcelHelper helper, final int skipRowCount, final boolean hasTitle,final InTransformer<T> transformer){
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
        final List<String> titles = hasTitle ? new ArrayList<String>() : null;
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
        if(hasTitle){
            return new TitleTableSource<>(titles, tables);
        }
        return new DirectTableSource<>(tables);
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
        final boolean hasTitle = source instanceof TitleSource;
        if(hasTitle){
            VisitServices.from(((TitleSource) source).getTitles()).fireWithStartEnd(new StartEndVisitor<String>() {
                @Override
                public boolean visit(Object param, String s, boolean start, boolean end) {
                    sb.append(s);
                    if(!end){
                        sb.append(TITLE_SEPARATOR);
                    }
                    return false;
                }
            });
        }
        VisitServices.from(source.getList()).fireWithStartEnd(new StartEndVisitor<T>() {
            @Override
            public boolean visit(Object param, T t, boolean start, boolean end) {
                if(start && hasTitle){
                    sb.append(Platforms.getNewLine());
                }
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
        //title
        final boolean hasTitle = source instanceof TitleSource;
        if(hasTitle){
            VisitServices.from(((TitleSource) source).getTitles()).fireWithStartEnd(new StartEndVisitor<String>() {
                @Override
                public boolean visit(Object param, String s, boolean start, boolean end) {
                    sb.append(s);
                    if (!end) {
                        sb.append(separator);
                    }
                    return false;
                }
            });
        }

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
        VisitServices.from(source.getTable()).map(new ResultVisitor<List<T>, List<String>>() {
            @Override
            public List<String> visit(List<T> ts, Object param) {
                return VisitServices.from(ts).map(rs).getAsList();
            }
        }).asListService().fireWithStartEnd(new StartEndVisitor<List<String>>() {
            @Override
            public boolean visit(Object param, List<String> list, boolean start, boolean end) {
                if(start && hasTitle){
                    sb.append(Platforms.getNewLine());
                }
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
        List<String> names = getTitles(source, config);
        ExcelWriter.SheetFactory sf = new DefaultExcelWriter().newWorkbook(ExcelWriter.TYPE_XSSF)
                .nesting()
                .newSheet(config.getSheetName())
                .apply(new Sheet_WidthHeightApplier(config.getWidth(), config.getHeight(),names.size()))
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

    @SuppressWarnings("unchecked")
    public static <T> void writeExcelFile(TableSource<T> source, OutputStream out, ExcelOutConfig config, final OutTransformer<? super T> transformer){
        List<String> names = getTitles(source, config);
        final ExcelWriter.SheetFactory sf = new DefaultExcelWriter().newWorkbook(ExcelWriter.TYPE_XSSF)
                .nesting()
                .newSheet(config.getSheetName())
                .apply(new Sheet_WidthHeightApplier(config.getWidth(), config.getHeight(), names.size()))
                .apply(new TitleRowApplier(names))
                .nesting();
        final AtomicInteger excelIndex = new AtomicInteger(1);
        VisitServices.from(source.getTable()).fireWithIndex(new FireIndexedVisitor<List<T>>() {
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

    private static List<String> getTitles(Object source, ExcelOutConfig config) {
        List<String> names = config.getColumnNames();
        if(Predicates.isEmpty(names) && source instanceof TitleSource){
            names = ((TitleSource) source).getTitles();
        }
        return names;
    }
}
