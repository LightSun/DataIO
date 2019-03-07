package com.heaven7.java.data.io.music.scan;

import com.heaven7.java.data.io.bean.CutConfigBeanV10;
import com.heaven7.java.data.io.music.in.MusicNameSource;
import com.heaven7.java.data.io.utils.Debugger;

import java.io.File;
import java.util.List;

/**
 * @author heaven7
 */
public class MusicCutterScannerV11 extends MusicCutterScannerV10{

    private final MusicNameSource mMusicNameSource;

    public MusicCutterScannerV11(String dir,MusicNameSource mMusicNameSource) {
        super(dir);
        this.mMusicNameSource = mMusicNameSource;
    }
    @Override
    protected void writeCutConfig(String targetFilePath, List<CutConfigBeanV10.CutItem> cutItems) {
        //verify by MusicNameSource
        //add debug info
        String debugFile = new File(targetFilePath).getParent() + File.separator + "cuts_debug.txt";
        Debugger.debugPair(mMusicNameSource, cutItems, debugFile, "Cuts");
        super.writeCutConfig(targetFilePath, cutItems);
    }
}
