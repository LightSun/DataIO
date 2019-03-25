package com.heaven7.java.data.io.test.os;

import com.heaven7.java.data.io.os.sources.*;
import com.heaven7.java.data.io.utils.FileUtils;
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
        final List<List<String>> list = new ArrayList<>();
        for(int i = 0 ; i < 3 ; i ++){
            List<String> strs = new ArrayList<>();
            strs.add(i + "__text__0");
            strs.add(i + "__text__1");
            strs.add(i + "__text__2");
            list.add(strs);
        }
        String out = FileUtils.createFilePath(dir, "2.txt");
        SourceIO.writeTextFile(new TableSource<String>() {
            @Override
            public List<List<String>> getList() {
                return list;
            }
        }, out, " " ,OutTransformer.TO_STRING);
    }

    @Test
    public void testWriteExcel2(){
        final List<List<String>> list = new ArrayList<>();
        for(int i = 0 ; i < 3 ; i ++){
            List<String> strs = new ArrayList<>();
            strs.add(i + "__text__0");
            strs.add(i + "__text__1");
            strs.add(i + "__text__2");
            list.add(strs);
        }
        String out = FileUtils.createFilePath(dir, "2.xlsx");
        SourceIO.writeExcelFile(new TableSource<String>() {
            @Override
            public List<List<String>> getList() {
                return list;
            }
        }, out, new ExcelOutConfig.Builder()
                .setSheetName("excel2").setWidth(600).setHeight(200).setColumnNames(Arrays.asList("name", "age", "sax"))
                .build(), OutTransformer.DEFAUULT);
    }

    @Test
    public void testReadText1(){

    }
}
