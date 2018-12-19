package com.heaven7.java.data.io.music.test;

import com.heaven7.java.data.io.music.bridge.ExcelToJsonBridgeV1;
import com.heaven7.java.data.io.music.bridge.ExcelToJsonBridgeV2;
import com.heaven7.java.data.io.music.bridge.ExcelToJsonBridgeV3;
import com.heaven7.java.data.io.music.bridge.MergedExcelToBridge;
import org.junit.Test;

import java.util.Arrays;

/** @author heaven7 */
public class TestExcelToJsonBridge {

    @Test
    public void testV1() {
        //  E:\tmp\bugfinds\music3.xlsx 0 E:\tmp\bugfinds\music_cuts2\cuts.txt
        // E:\tmp\bugfinds\out_music6 E:\tmp\bugfinds\music_cuts2 2
        String[] args = {
            "E:\\tmp\\bugfinds\\music3.xlsx",
            "0",
            "E:\\tmp\\bugfinds\\music_cuts2\\cuts.txt",
            "E:\\tmp\\bugfinds\\out_music7",
            "E:\\tmp\\bugfinds\\music_cuts2",
            "2",
        };
        new ExcelToJsonBridgeV1(args).execute();
    }

    @Test // $excelpath $sheet_name $cut_config_path $outDir $input_music_dir $ship_to_row_index
    public void testV2() {
        // E:\tmp\bugfinds\music_cuts2\1208\music4.xlsx
        // music1
        // E:\tmp\bugfinds\music_cuts2\1208\cuts.txt
        // E:\tmp\bugfinds\out_music7
        // E:\tmp\bugfinds\music_cuts2\1208\60s
        // 2
        /* String[] args = {
                "E:\\tmp\\bugfinds\\music_cuts2\\1208\\music4.xlsx",
                "music1",
                "E:\\tmp\\bugfinds\\music_cuts2\\1208\\cuts.txt",
                "E:\\tmp\\bugfinds\\out_music7",
                "E:\\tmp\\bugfinds\\music_cuts2\\1208\\60s",
                "2",
        };*/
        String[] args = {
            "E:\\tmp\\bugfinds\\music5_60s.xlsx",
            "music5",
            "E:\\tmp\\bugfinds\\music_cuts2\\1212\\cuts.txt",
            "E:\\tmp\\bugfinds\\out_music7",
            "E:\\tmp\\bugfinds\\music_cuts2\\1212\\60s",
            "2",
        };
        new ExcelToJsonBridgeV2(args).execute();
    }

    @Test // $excelpath $sheet_name $cut_config_path $outDir $input_music_dir $ship_to_row_index
    public void testV3() {
        String[] args = {
            "E:\\tmp\\bugfinds\\music8.xlsx",
            "sheet3",
            "E:\\tmp\\bugfinds\\music_cuts2\\1212\\cuts_uniform.txt",
            "E:\\tmp\\bugfinds\\out_music8",
            "E:\\tmp\\bugfinds\\right_musics\\60s",
            "1",
        };
        new ExcelToJsonBridgeV3(args).execute();
    }

    @Test
    public void testMerge() {
        String[] args = {
            "E:\\tmp\\bugfinds\\music3.xlsx",
            "0",
            "E:\\tmp\\bugfinds\\music_cuts2\\cuts.txt",
            "E:\\tmp\\bugfinds\\out_music7",
            "E:\\tmp\\bugfinds\\music_cuts2",
            "0",
        };
        ExcelToJsonBridgeV1 bridgeV1 = new ExcelToJsonBridgeV1(args);
        String[] args2 = {
            "E:\\tmp\\bugfinds\\music_cuts2\\1208\\music4.xlsx",
            "music1",
            "E:\\tmp\\bugfinds\\music_cuts2\\1208\\cuts.txt",
            "E:\\tmp\\bugfinds\\out_music7",
            "E:\\tmp\\bugfinds\\music_cuts2\\1208\\60s",
            "2",
        };
        ExcelToJsonBridgeV2 bridgeV2 = new ExcelToJsonBridgeV2(args2);

        String mergeOut = "E:\\tmp\\bugfinds\\out_merged";
        String simpleName = "music_merged";
        MergedExcelToBridge parentBridge =
                new MergedExcelToBridge(mergeOut, simpleName, bridgeV1, bridgeV2);
        parentBridge.execute();
    }
}
