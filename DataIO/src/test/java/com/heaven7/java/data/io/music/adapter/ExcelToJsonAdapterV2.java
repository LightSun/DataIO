package com.heaven7.java.data.io.music.adapter;

import com.heaven7.java.data.io.music.provider.MusicCutProvider;
import com.heaven7.java.data.io.poi.ExcelRow;

/**
 * @author heaven7
 */
public class ExcelToJsonAdapterV2 extends ExcelToJsonAdapterV1 {

    @Deprecated
    public ExcelToJsonAdapterV2(String outDir, String simpleFileName,final String cuts) {
        this(outDir, simpleFileName, new MusicCutProvider.DefaultMusicCutProvider(cuts));
    }

    public ExcelToJsonAdapterV2(String outDir, String simpleFileName, MusicCutProvider provider) {
        super(outDir, simpleFileName, provider, new IndexDelegateV2());
    }

    @Override
    protected boolean filter(ExcelRow row) {
         if(super.filter(row)){
             return true;
         }
        IndexDelegateV2 indexDelegate = (IndexDelegateV2) getIndexDelegate();
        String durationStr = row.getColumns().get(indexDelegate.getDurationIndex()).getColumnString();
        if(!"60s".equals(durationStr)){
            return true;
        }
        return false;
    }
}
