package com.heaven7.java.data.io.test;

import com.heaven7.java.data.io.music.in.ExcelMusicNameSource;
import com.heaven7.java.data.io.poi.ExcelHelper;
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
}
