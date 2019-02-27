package com.heaven7.java.data.io.music.out;

import com.heaven7.java.base.util.Logger;
import com.heaven7.java.data.io.bean.MusicItem;
import com.heaven7.java.visitor.collection.VisitServices;

import java.util.ArrayList;
import java.util.List;

/**
 * the merged music out delegate. which can out multi times until call {@linkplain #mergeOut()}.
 * @author heaven7
 */
public class MergedMusicOutDelegate implements MusicOutDelegate1 {

    private static final String TAG = "MergedMusicOutDelegate";
    private final List<MusicItem> mItems = new ArrayList<>();
    private final List<String> mOutDirs = new ArrayList<>();
    private final List<String> mWarnMessages = new ArrayList<>();

    private final MusicOutDelegate1 mInternalOut = new DefalutMusicOutDelegate();
    private final String mOutDir;
    private final String mSimpleFilename;

    public MergedMusicOutDelegate(String outDir,String simpleFileName) {
        this.mOutDir = outDir;
        this.mSimpleFilename = simpleFileName;
    }

    @Override
    public void writePart(String outDir, List<MusicItem> items) {
        mItems.addAll(items);
        mOutDirs.add(outDir);
    }

    @Override
    public void writeTotal(String outDir, String simpleFileName, List<MusicItem> items) {

    }
    @Override
    public void writeWarn(String outDir, String simpleFileName, String warnMessages) {
        mWarnMessages.add(warnMessages);
    }
    @Override
    public void writeItem(String outDir, List<MusicItem> items) {

    }
    @Override
    public void copyValidMusics(String outDir, List<MusicItem> items) {

    }
    /** export the all out delegate  */
    public void mergeOut(){
        Logger.d(TAG, "mergeOut" , "start merge out to "+ mOutDir + " >>> src out dirs: " + mOutDirs);
        String warnMsg = VisitServices.from(mWarnMessages).joinToString("\n");
        mInternalOut.writePart(mOutDir, mItems);
        mInternalOut.writeItem(mOutDir, mItems);
        mInternalOut.writeTotal(mOutDir, mSimpleFilename, mItems);
        mInternalOut.writeWarn(mOutDir, mSimpleFilename, warnMsg);
        mInternalOut.copyValidMusics(mOutDir, mItems);
        Logger.d(TAG, "mergeOut" ,"merge out success <<<");
        reset();
    }

    private void reset() {
        mItems.clear();
        mOutDirs.clear();
        mWarnMessages.clear();
    }

}
