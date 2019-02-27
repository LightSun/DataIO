package com.heaven7.java.data.io.music.bridge;

import com.heaven7.java.data.io.music.adapter.ExcelToJsonAdapterV3;
import com.heaven7.java.data.io.music.out.MusicOutDelegate1;
import com.heaven7.java.data.io.music.provider.SimpleSpeedMusicCutProvider;
import com.heaven7.java.data.io.poi.ExcelHelper;

/**
 * support speed type from cuts.
 * @author heaven7
 */
public class ExcelToJsonBridgeV3 extends ExcelToJsonBridge {

    public ExcelToJsonBridgeV3(String[] args) {
        super(args);
    }

    @Override
    protected void launchBridge(ExcelHelper helper, MusicOutDelegate1 delegate, Parameters param) {
        ExcelToJsonAdapterV3 adapter = new ExcelToJsonAdapterV3(param.outDir, param.filename, new SimpleSpeedMusicCutProvider(param.cutConfigFile));
        adapter.setMusicOutDelegate(delegate);
        adapter.setInputMusicDir(param.inputMusicDir);
        helper.readAndWrite(adapter);
    }
}
