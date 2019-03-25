package com.heaven7.java.data.io.music.bridge;

import com.heaven7.java.data.io.music.out.MergedMusicOutDelegate;
import com.heaven7.java.data.io.music.out.MusicOutDelegate1;
import com.heaven7.java.data.io.poi.ExcelHelper;

import java.util.Arrays;
import java.util.List;

/**
 * @author heaven7
 */
public class MergedExcelToBridge extends ExcelToJsonBridge {

    private final List<ExcelToJsonBridge> bridges;

    public MergedExcelToBridge(String outDir,String simpleFileName, List<ExcelToJsonBridge> bridges) {
        super(new Parameters(outDir, simpleFileName));
        this.bridges = bridges;
        super.setMusicOutDelegate(new MergedMusicOutDelegate(outDir, simpleFileName));
    }
    public MergedExcelToBridge(String outDir,String simpleFileName, ExcelToJsonBridge... bridges){
        this(outDir, simpleFileName, Arrays.asList(bridges));
    }

    @Override
    public void setMusicOutDelegate(MusicOutDelegate1 outDelegate) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void execute() {
        MergedMusicOutDelegate delegate = (MergedMusicOutDelegate) getMusicOutDelegate();
        for (ExcelToJsonBridge bridge : bridges){
            bridge.setMusicOutDelegate(delegate);
            bridge.execute();
        }
        //final out
        delegate.mergeOut();
    }

    @Override
    protected void launchBridge(ExcelHelper helper, MusicOutDelegate1 delegate, Parameters param) {
        //empty pm
    }
}
