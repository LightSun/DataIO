package com.heaven7.java.data.io.music.adapter;

import com.heaven7.java.data.io.bean.MusicItem;
import com.heaven7.java.data.io.music.provider.MusicCutProvider;
import com.heaven7.java.data.io.music.provider.SpeedMusicCutProvider;
import com.heaven7.java.data.io.poi.ExcelCol;
import com.heaven7.java.data.io.poi.ExcelRow;

import java.util.List;

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

    protected void parseBaseInfo(List<ExcelCol> columns, MusicItem item){
        super.parseBaseInfo(columns, item);
        IndexDelegateV2 indexDelegate = (IndexDelegateV2) getIndexDelegate();
        String str = columns.get(indexDelegate.getDurationIndex()).getColumnString().trim();
        if(str.endsWith("s")){
            str = str.substring(0, str.length() -1);
        }
        item.setDuration(Integer.parseInt(str));
    }

    @Override
    protected String getKey(MusicItem item) {
        return item.getUniqueKey();
    }
    @Override
    protected String getCuts(MusicItem item, MusicCutProvider provider) {
        if(!(provider instanceof SpeedMusicCutProvider)){
            throw new IllegalStateException();
        }
        return ((SpeedMusicCutProvider) provider).getCuts(item);
    }
}
