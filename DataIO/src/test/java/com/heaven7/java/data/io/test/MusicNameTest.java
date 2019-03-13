package com.heaven7.java.data.io.test;

import com.heaven7.java.data.io.music.in.ExcelMusicNameSource;
import com.heaven7.java.data.io.poi.ExcelCol;
import com.heaven7.java.data.io.poi.ExcelHelper;
import com.heaven7.java.data.io.poi.ExcelRow;
import com.heaven7.java.visitor.FireVisitor;
import com.heaven7.java.visitor.ResultVisitor;
import com.heaven7.java.visitor.Visitors;
import com.heaven7.java.visitor.collection.VisitServices;
import org.junit.Test;

import java.util.List;

/**
 * @author heaven7
 */
public class MusicNameTest {

    @Test
    public void test1(){
        String musicNameTable = "E:\\tmp\\bugfinds\\music_name_table.xlsx";
        ExcelHelper musicNameTables = new ExcelHelper.Builder()
                .setUseXlsx(true)
                .setExcelPath(musicNameTable)
                .setSkipToRowIndex(1)
                .setSheetName("线上音乐文件")
                .build();
        List<String> musicNames = new ExcelMusicNameSource(musicNameTables).getMusicNames();
        System.out.println();
    }

    @Test
    public void test2(){
        String musicNameTable = "E:\\tmp\\bugfinds\\server_mapping.xlsx";
        ExcelHelper musicNameTables = new ExcelHelper.Builder()
                .setUseXlsx(true)
                .setExcelPath(musicNameTable)
                .setSkipToRowIndex(1)
                .setSheetName("工作表3")
                .build();
        List<ExcelRow> rows = musicNameTables.read();
        VisitServices.from(rows).fire(new FireVisitor<ExcelRow>() {
            @Override
            public Boolean visit(ExcelRow row, Object param) {
                String strs = VisitServices.from(row.getColumns()).map(new ResultVisitor<ExcelCol, String>() {
                    @Override
                    public String visit(ExcelCol excelCol, Object param) {
                        return excelCol.getColumnString();
                    }
                }).asListService().joinToString(", ");
                System.out.println(strs);
                return null;
            }
        });
        System.out.println();
    }
}
