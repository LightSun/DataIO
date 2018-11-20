package com.heaven7.java.data.io.music;

import com.heaven7.java.base.util.ResourceLoader;
import com.heaven7.java.base.util.TextReadHelper;

import java.util.List;

/**
 * @author heaven7
 */
public class MusicCutReader {

    public static void main(String[] args) {
        //
        String csvPath = "E:\\tmp\\bugfinds\\music_cuts\\chaoliu\\216_full_punch-and-slide_0187_preview.csv";
        String data = ResourceLoader.getDefault().loadFileAsString(null, csvPath);
        TextReadHelper<Float> readHelper = new TextReadHelper<>(new TextReadHelper.Callback<Float>() {
            @Override
            public Float parse(String line) {
                return Float.valueOf(line.split(",")[0]);
            }
        });
        List<Float> floats = readHelper.read(null, csvPath);
        System.out.println(floats);
    }
}
