package com.heaven7.java.data.io.music.adapter;

import com.heaven7.java.base.util.Logger;
import com.heaven7.java.data.io.bean.MusicItem;
import com.heaven7.java.data.io.music.UniformNameHelper;
import com.heaven7.java.data.io.music.provider.MusicCutProvider;
import com.heaven7.java.data.io.music.provider.SpeedMusicCutProvider;
import com.heaven7.java.data.io.poi.ExcelCol;
import com.heaven7.java.data.io.poi.ExcelRow;
import com.heaven7.java.data.io.utils.FileMd5Helper;

import java.util.List;

/**
 * V1, V2 综合
 * @author heaven7
 */
public class ExcelToJsonAdapterV3 extends ExcelToJsonAdapterV1 {

    private static final String TAG = "ExcelToJsonAdapterV3";

    @Deprecated
    public ExcelToJsonAdapterV3(String outDir, String simpleFileName, final String cuts) {
        this(outDir, simpleFileName, new MusicCutProvider.DefaultMusicCutProvider(cuts));
    }

    public ExcelToJsonAdapterV3(String outDir, String simpleFileName, MusicCutProvider provider) {
       // super(outDir, simpleFileName, provider, new IndexDelegateV2());
        super(outDir, simpleFileName, provider, new IndexDelegateV3());
    }

    @Override
    protected boolean filter(ExcelRow row) {
         if(super.filter(row)){
             return true;
         }
        IndexDelegateV3 indexDelegate = (IndexDelegateV3) getIndexDelegate();
        String durationStr = row.getColumns().get(indexDelegate.getDurationIndex()).getColumnString();
        if(!"60s".equals(durationStr)){
            return true;
        }
        return false;
    }

    protected void parseBaseInfo(List<ExcelCol> columns, MusicItem item){
        super.parseBaseInfo(columns, item);
        IndexDelegateV3 indexDelegate = (IndexDelegateV3) getIndexDelegate();
        String str = columns.get(indexDelegate.getDurationIndex()).getColumnString().trim();
        if(str.endsWith("s")){
            str = str.substring(0, str.length() -1);
        }
        item.setDuration(Integer.parseInt(str));
        //name changed
        String name = columns.get(indexDelegate.getNameIndex()).getColumnString();
        String newName = UniformNameHelper.uniformSimpleMusicName(name);
        Logger.d(TAG, "parseBaseInfo", "ExcelCol -- music name changed : old = " +  name
                + " ,new = " + newName);
        item.setName(newName);

        //columns.get(indexDelegate.getNameIndex()).setValue(newName);

        // category
        String category = columns.get(indexDelegate.getCategoryIndex()).getColumnString().trim();
        item.setCategoryStr(category);
    }

    protected boolean filterMusicItemByMp3(List<String> mp3s, MusicItem item){
        String name = item.getName();
        for(String mp3 : mp3s){
            String uniform_mp3_name = UniformNameHelper.uniformMusicFilename(mp3);
            if(uniform_mp3_name.equals(name)){
                item.setId(FileMd5Helper.getMD5Three(mp3));
                item.setRawFile(mp3);
                return true;
            }
        }
        return false;
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

    @Override
    protected boolean fillSpeedAreasByOld() {
        return true;
    }

    @Override
    protected void log(List<ExcelCol> columns, String mTag, String msg) {
        try{
            IndexDelegateV2 indexDelegate = (IndexDelegateV2) getIndexDelegate();
            String str = columns.get(indexDelegate.getDurationIndex()).getColumnString().trim();
            if(str.endsWith("s")){
                str = str.substring(0, str.length() -1);
            }
            if(Integer.parseInt(str) == 60){
                super.log(columns, mTag, msg);
            }
        }catch (Exception e){
            //ignore
        }
    }
}
