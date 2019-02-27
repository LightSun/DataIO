package com.heaven7.java.data.io.music.out;

import com.heaven7.java.data.io.bean.MusicItem;
import com.heaven7.java.data.io.music.PartOutput;

import java.util.List;

/**
 * @author heaven7
 */
public interface MusicOutDelegate1 {

    /**
     * write music items as  parts output. see {@linkplain PartOutput}
     * @param items the music items
     * @param outDir the out dir
     */
    void writePart(String outDir, List<MusicItem> items);

    /**
     * called on write total music infos
     * @param items the music items
     * @param outDir the out dirs
     * @param simpleFileName the simple file names without extension
     */
    void writeTotal(String outDir, String simpleFileName, List<MusicItem> items);

    /**
     * call this to write warn info
     * @param outDir the out dirs
     * @param simpleFileName the simple file name
     * @param warnMessages the warn message
     */
    void writeWarn(String outDir, String simpleFileName, String warnMessages);


    /**
     * call this to write single music item info
     * @param outDir the out dir
     * @param items the music items
     */
    void writeItem(String outDir, List<MusicItem> items);

    /**
     * call this to copy valid music to one dir
     * @param outDir the base out dir
     * @param items the music items
     */
    void copyValidMusics(String outDir, List<MusicItem> items);
}
