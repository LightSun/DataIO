package com.heaven7.java.data.io.music.scan;

import com.heaven7.java.data.io.music.UniformNameHelper;

import java.io.File;

/**
 * 从csv文件中读出cuts同时读出speed type. often used as v2
 *
 * @author heaven7
 * @since 3
 */
public class SpeedTypeMusicCutScanner2 extends SpeedTypeMusicCutScanner {

    public SpeedTypeMusicCutScanner2(String dir) {
        super(dir);
    }

    @Override
    protected String formatMusicName(String simpleCsvFilename) {
        return UniformNameHelper.uniformSimpleMusicName(simpleCsvFilename);
    }

    public static void main(String[] args) {
        // String dir = "E:\\tmp\\bugfinds\\music_cuts2\\1208\\60s";
        String dir = "E:\\tmp\\bugfinds\\music_cuts2\\1212\\60s";
       // String dir = "E:\\tmp\\bugfinds\\music_cuts2\\1212_2\\60s";
        String configPath = new File(dir).getParent() + File.separator + "cuts_uniform.txt";
        new SpeedTypeMusicCutScanner2(dir).serialize(configPath);
    }
}
