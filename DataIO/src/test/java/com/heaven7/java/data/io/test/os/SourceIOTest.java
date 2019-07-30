package com.heaven7.java.data.io.test.os;

import com.google.gson.Gson;
import com.heaven7.java.base.util.ResourceLoader;
import com.heaven7.java.data.io.os.sources.*;
import com.heaven7.java.data.io.poi.ExcelHelper;
import com.heaven7.java.data.io.utils.FileUtils;
import com.heaven7.java.visitor.FireIndexedVisitor;
import com.heaven7.java.visitor.collection.VisitServices;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author heaven7
 */
public class SourceIOTest {

    final String dir = "E:\\tmp\\bug\\test1";

    @Test
    public void testWriteTxt1(){
        List<String> list = Arrays.asList("text1", "text2", "text3");
        String out = FileUtils.createFilePath(dir, "1.txt");
        SourceIO.writeTextFile(new DirectListSource<>(list), out, OutTransformer.TO_STRING);
        assetFileTextEquals(out, "text1\n" +
                "text2\n" +
                "text3");
    }
    @Test
    public void testWriteExcel1(){
        List<String> list = Arrays.asList("text1", "text2", "text3");
        String out = FileUtils.createFilePath(dir, "1.xlsx");
        SourceIO.writeExcelFile(new DirectListSource<>(list), out, new ExcelOutConfig.Builder()
                .setWidth(500).setHeight(200).setSheetName("excel1").setColumnNames(Arrays.asList("name"))
                .build(),OutTransformer.TO_STRING);
    }

    @Test
    public void testWriteTxt2(){
        final List<List<String>> list = createTableList();
        String out = FileUtils.createFilePath(dir, "2.txt");
        SourceIO.writeTextFile(new TableSource<String>() {
            @Override
            public List<List<String>> getTable() {
                return list;
            }
        }, out, " " ,OutTransformer.TO_STRING);
        assetFileTextEquals(out, "0__text__0 0__text__1 0__text__2\n" +
                "1__text__0 1__text__1 1__text__2\n" +
                "2__text__0 2__text__1 2__text__2");
    }

    @Test
    public void testWriteExcel2(){
        final List<List<String>> list = createTableList();
        String out = FileUtils.createFilePath(dir, "2.xlsx");
        SourceIO.writeExcelFile(new TableSource<String>() {
            @Override
            public List<List<String>> getTable() {
                return list;
            }
        }, out, new ExcelOutConfig.Builder()
                .setSheetName("excel2").setWidth(600).setHeight(200).setColumnNames(Arrays.asList("name", "age", "sax"))
                .build(), OutTransformer.STRING);
    }

    @Test
    public void testReadText1(){
        String out = FileUtils.createFilePath(dir, "1.txt");
        ListSource<String> source = SourceIO.readTextFile(out, false ,InTransformer.STRING);
        Assert.assertTrue(VisitServices.from(source.getList()).joinToString("\n").equals("text1\n" +
                "text2\n" +
                "text3"));
    }

    @Test
    public void testReadText2(){
        String out = FileUtils.createFilePath(dir, "2.txt");
        TableSource<String> source = SourceIO.readTextFile(out, " ", false, InTransformer.STRING);
        assetTableEquals(source.getTable(), createTableList());
    }
    @Test
    public void testReadExcel1(){
        String out = FileUtils.createFilePath(dir, "1.xlsx");
        ExcelHelper helper = new ExcelHelper.Builder()
                .setSheetName("excel1")
                .setUseXlsx(true)
                .setExcelPath(out)
                .build();
        ListSource<String> source = SourceIO.readExcelAsList(helper, 0,false ,InTransformer.STRING);
        List<String> list = source.getList();
        Assert.assertTrue(list.remove(0).equals("name"));//title

        String str = VisitServices.from(list).joinToString("\n");
        Assert.assertTrue(str.equals("text1\n" +
                "text2\n" +
                "text3"));
    }

    @Test
    public void testReadExcel2(){
        String out = FileUtils.createFilePath(dir, "2.xlsx");
        ExcelHelper helper = new ExcelHelper.Builder()
                .setSheetName("excel2")
                .setUseXlsx(true)
                .setExcelPath(out)
                .build();
        TableSource<String> source = SourceIO.readExcelAsTable(helper, 0, true, InTransformer.STRING);
        TitleTableSource<String> tts = (TitleTableSource<String>) source;

        Assert.assertTrue(tts.getTitles().equals(Arrays.asList("name", "age", "sax")));

        assetTableEquals(tts.getTable(), createTableList());
    }

    @Test
    public void testReadExcel3(){
        String out = "E:\\work\\ui4\\小蟹v6.5.xlsx";
        ExcelHelper helper = new ExcelHelper.Builder()
                .setSheetName("领域模型（旅行）")
                .setUseXlsx(true)
                .setExcelPath(out)
                .build();
        TableSource<String> source = SourceIO.readExcelAsTable(helper, 4, false, InTransformer.STRING);
        List<List<String>> table = source.getTable();
        System.out.println(table);
        // filter 0, 6
        List<TravelTag> outList = new ArrayList<>();
        for (List<String> list : table){
            list.remove(6);
            list.remove(0);
            outList.add(new TravelTag(list));
        }
        System.out.println(new Gson().toJson(outList));
    }

    private List<List<String>> createTableList() {
        final List<List<String>> list = new ArrayList<>();
        for(int i = 0 ; i < 3 ; i ++){
            List<String> strs = new ArrayList<>();
            strs.add(i + "__text__0");
            strs.add(i + "__text__1");
            strs.add(i + "__text__2");
            list.add(strs);
        }
        return list;
    }
    private void assetTableEquals(List<List<String>> strs,final List<List<String>> real) {
        Assert.assertTrue(strs.size() == real.size());
        VisitServices.from(strs).fireWithIndex(new FireIndexedVisitor<List<String>>() {
            @Override
            public Void visit(Object param, List<String> list, int index, int size) {
                Assert.assertTrue(list.equals(real.get(index)));
                return null;
            }
        });
    }
    private void assetListEquals(List<String> strs, String content) {
        String str = VisitServices.from(strs).joinToString("\n");
        Assert.assertTrue(str.equals(content));
    }
    private void assetFileTextEquals(String file, String content) {
        List<String> lines = ResourceLoader.getDefault().loadFileAsStringLines(null, file);
        String str = VisitServices.from(lines).joinToString("\n");
        Assert.assertTrue(str.equals(content));
    }

    public static class TravelTag{
        String tag;
        float evaluation;
        String shotType;
        int shotLevel;
        String desc;
        String desc2;
        public TravelTag(List<String> list){
            tag = list.get(0).trim();
            evaluation = Float.valueOf(list.get(1).trim());
            shotType = list.get(2).trim();
            shotLevel =  Integer.valueOf(list.get(3).trim());
            desc =  list.get(4);
            desc2 =  list.get(5);
        }
    }
}
