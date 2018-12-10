package com.heaven7.java.data.io.music.bridge;

import com.heaven7.java.data.io.music.adapter.ExcelToJsonAdapterV2;
import com.heaven7.java.data.io.music.out.MusicOutDelegate;
import com.heaven7.java.data.io.music.provider.SimpleSpeedMusicCutProvider;
import com.heaven7.java.data.io.poi.ExcelHelper;

/**
 * support speed type from cuts.
 * @author heaven7
 */
public class ExcelToJsonBridgeV2 extends ExcelToJsonBridge {

    public ExcelToJsonBridgeV2(String[] args) {
        super(args);
    }

    @Override
    protected void launchBridge(ExcelHelper helper, MusicOutDelegate delegate, Parameters param) {
        ExcelToJsonAdapterV2 adapter = new ExcelToJsonAdapterV2(param.outDir, param.filename, new SimpleSpeedMusicCutProvider(param.cutConfigFile));
        adapter.setMusicOutDelegate(delegate);
        adapter.setInputMusicDir(param.inputMusicDir);
        helper.readAndWrite(adapter);
    }
}
