package com.heaven7.java.data.io.music.bridge;

import com.heaven7.java.data.io.music.out.MergedMusicOutDelegate;
import com.heaven7.java.data.io.music.out.MusicOutDelegate;
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
    }
    public MergedExcelToBridge(String outDir,String simpleFileName, ExcelToJsonBridge... bridges){
        this(outDir, simpleFileName, Arrays.asList(bridges));
    }

    @Override
    public void execute(MusicOutDelegate delegate) {
        Parameters param = getParameters();
        MergedMusicOutDelegate mmod = new MergedMusicOutDelegate(param.outDir, param.filename);
        for (ExcelToJsonBridge bridge : bridges){
            bridge.execute(mmod);
        }
        //final out
        mmod.mergeOut();
    }

    @Override
    protected void launchBridge(ExcelHelper helper, MusicOutDelegate delegate, Parameters param) {
        //empty impl
    }
}
