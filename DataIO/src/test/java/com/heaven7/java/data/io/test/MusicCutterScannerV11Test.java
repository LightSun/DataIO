package com.heaven7.java.data.io.test;

import com.heaven7.java.data.io.music.in.ExcelMusicNameSource;
import com.heaven7.java.data.io.music.in.SimpleMusicNameSource;
import com.heaven7.java.data.io.music.scan.MusicCutterScannerV10;
import com.heaven7.java.data.io.music.scan.MusicCutterScannerV11;
import com.heaven7.java.data.io.poi.ExcelHelper;
import org.junit.Test;

import java.io.File;

/**
 * @author heaven7
 */
public class MusicCutterScannerV11Test {

    @Test
    public void test(){
        String musicNameTable = "E:\\tmp\\bugfinds\\music_name_table.xlsx";
        ExcelHelper musicNameTables = new ExcelHelper.Builder()
                .setUseXlsx(true)
                .setExcelPath(musicNameTable)
                .setSkipToRowIndex(1)
                .setSheetName("线上音乐文件")
                .build();
        String dir = "E:\\tmp\\bugfinds\\music_cut3";
        new MusicCutterScannerV11(dir, new ExcelMusicNameSource(musicNameTables, null))
                .serialize(dir + File.separator + "cut.txt");
    }
}
