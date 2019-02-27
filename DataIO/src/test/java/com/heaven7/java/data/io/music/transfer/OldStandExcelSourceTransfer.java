package com.heaven7.java.data.io.music.transfer;

import com.heaven7.java.base.util.Logger;
import com.heaven7.java.base.util.TextUtils;
import com.heaven7.java.data.io.bean.MusicItem2;
import com.heaven7.java.data.io.music.UniformNameHelper;
import com.heaven7.java.data.io.music.adapter.IndexDelegate;
import com.heaven7.java.data.io.music.adapter.IndexDelegateV3;
import com.heaven7.java.data.io.music.in.MusicCutSource;
import com.heaven7.java.data.io.poi.ExcelCol;
import com.heaven7.java.data.io.poi.ExcelRow;

import java.util.List;

/**
 * @author heaven7
 */
public class OldStandExcelSourceTransfer extends SimpleExcelSourceTransfer{

    public OldStandExcelSourceTransfer(MusicCutSource musicCutSource, IndexDelegate indexDelegate, String outDir) {
        super(musicCutSource, indexDelegate, outDir);
    }

    @Override
    protected void parseBaseInfo(List<ExcelCol> columns, MusicItem2 item) {
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

    @Override
    protected boolean filter(ExcelRow row) {
        if(super.filter(row)){
            return true;
        }
        IndexDelegateV3 indexDelegate = (IndexDelegateV3) getIndexDelegate();
        /*String durationStr = row.getColumns().get(indexDelegate.getDurationIndex()).getColumnString();
        if(!"60s".equals(durationStr)){
            return true;
        }*/
        String category = row.getColumns().get(indexDelegate.getCategoryIndex()).getColumnString();
        if(TextUtils.isEmpty(category)){
            String name = row.getColumns().get(indexDelegate.getNameIndex()).getColumnString();
            Logger.w(TAG, "filter", "no category for music:  '+ " + name + " +'");
            return true;
        }
        return false;
    }
}
