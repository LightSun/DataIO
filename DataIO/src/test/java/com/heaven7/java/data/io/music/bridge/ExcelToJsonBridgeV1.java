package com.heaven7.java.data.io.music.bridge;

import com.heaven7.java.data.io.music.adapter.ExcelToJsonAdapterV1;
import com.heaven7.java.data.io.music.out.MusicOutDelegate1;
import com.heaven7.java.data.io.music.provider.MusicCutProviderV1;
import com.heaven7.java.data.io.poi.ExcelHelper;

/**
 * @author heaven7
 */
public class ExcelToJsonBridgeV1 extends ExcelToJsonBridge {

    public ExcelToJsonBridgeV1(String[] args) {
        super(args);
    }

    @Override
    protected void launchBridge(ExcelHelper helper, MusicOutDelegate1 delegate, Parameters param) {
        ExcelToJsonAdapterV1 adapter = new ExcelToJsonAdapterV1(param.outDir, param.filename, new MusicCutProviderV1(param.cutConfigFile));
      //  ExcelToJsonAdapter adapter = new ExcelToJsonAdapter(outDir, filename, new SimpleSpeedMusicCutProvider(cutConfigFile));
        adapter.setMusicOutDelegate(delegate);
        adapter.setInputMusicDir(param.inputMusicDir);
        helper.readAndWrite(adapter);
    }
}
