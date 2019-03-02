package com.heaven7.java.data.io.test;

import com.heaven7.java.data.io.music.scan.MusicCutterScannerV10;
import org.junit.Test;

import java.io.File;

/**
 * @author heaven7
 */
public class MusicCutterScannerV10Test {

    @Test
    public void test1(){
        // String dir = "E:\\tmp\\bugfinds\\music_cut3";
        String dir = "E:\\tmp\\bugfinds\\新版";
        new MusicCutterScannerV10(dir).serialize(dir + File.separator + "cut.txt");
    }
    //String dir = "E:\\tmp\\bugfinds\\music_cuts2\\1212\\60s";

    @Test
    public void test2(){
        String dir = "E:\\tmp\\bugfinds\\music_cut3";
        new MusicCutterScannerV10(dir).serialize(dir + File.separator + "cut.txt");
    }
}
