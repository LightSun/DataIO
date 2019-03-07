package com.heaven7.java.data.io.music.transfer;

import com.heaven7.java.base.util.Logger;
import com.heaven7.java.base.util.Predicates;
import com.heaven7.java.data.io.bean.CutConfigBeanV10;
import com.heaven7.java.data.io.bean.CutInfo;
import com.heaven7.java.data.io.bean.MusicItem2;
import com.heaven7.java.data.io.bean.TimeArea;
import com.heaven7.java.data.io.music.ErrorVerifier;
import com.heaven7.java.data.io.music.UniformNameHelper;
import com.heaven7.java.data.io.music.adapter.IndexDelegate;
import com.heaven7.java.data.io.music.in.*;
import com.heaven7.java.data.io.poi.ExcelCol;
import com.heaven7.java.data.io.poi.ExcelRow;
import com.heaven7.java.data.io.utils.FileUtils;
import com.heaven7.java.visitor.PredicateVisitor;
import com.heaven7.java.visitor.ResultVisitor;
import com.heaven7.java.visitor.collection.VisitServices;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.heaven7.java.data.io.music.Configs.parseDomain;
import static com.heaven7.java.data.io.music.Configs.parseMood;
import static com.heaven7.java.data.io.music.Configs.parseRhythm;
import static com.heaven7.java.data.io.music.helper.ParseHelper.parseTimeAreas;

/**
 * @author heaven7
 */
public abstract class SimpleExcelSourceTransfer implements StandTransfer {

    protected  final String TAG = getClass().getSimpleName();

    private final SpeedAreaSource speedAreaSource;
    private final MusicCutSource musicCutSource;
    private final IndexDelegate indexDelegate;
    private final String outDir;
    private final StringBuilder sb_warn = new StringBuilder();
    private boolean useOldSpeedArea;
    private LogWriter logWriter;
    private MusicNameSource musicNameSource;

    public SimpleExcelSourceTransfer(MusicCutSource musicCutSource, SpeedAreaSource speedAreaSource, IndexDelegate indexDelegate, String outDir) {
        this.speedAreaSource = speedAreaSource;
        this.musicCutSource = musicCutSource;
        this.indexDelegate = indexDelegate;
        this.outDir = outDir;
    }

    public void setMusicNameSource(MusicNameSource musicNameSource) {
        this.musicNameSource = musicNameSource;
    }

    public boolean isUseOldSpeedArea() {
        return useOldSpeedArea;
    }
    public void setUseOldSpeedArea(boolean useOldSpeedArea) {
        this.useOldSpeedArea = useOldSpeedArea;
    }

    public IndexDelegate getIndexDelegate() {
        return indexDelegate;
    }

    public StringBuilder getSb_warn(){
        return sb_warn;
    }

    public void setLogWriter(LogWriter logWriter) {
        this.logWriter = logWriter;
    }

    @Override
    public List<MusicItem2> transfer(ExcelSource source) {
        if(logWriter == null){
            logWriter = DefaultLogWriter.INSTANCE;
        }
        sb_warn.delete(0, sb_warn.length());
        List<MusicItem2> items = VisitServices.from(source.getRows()).map(new ResultVisitor<ExcelRow, MusicItem2>() {
            @Override
            public MusicItem2 visit(ExcelRow row, Object param) {
                List<ExcelCol> columns = row.getColumns();
                if (filter(row)) {
                    String msg =  "the row is filtered. row number = " + (row.getRowIndex() + 1);
                    log(columns, "filter(row)", msg);
                    logWriter.writeTransferItem(TAG, msg);
                    return null;
                }
                MusicItem2 item = new MusicItem2();
                parseBaseInfo(columns, item);
                item.setLineNumber(row.getRowIndex() + 1);
                item.setId(item.getName());
                if(!musicNameSource.getMusicNames().contains(item.getName())){
                    logWriter.writeMusicNameFilter("music name was filtered. name = " + item.getName());
                    return null;
                }

                List<CutInfo> infos = musicCutSource.getCutInfos(item);
                if (infos == null) {
                    String msg = "can't find cuts, line number = " + (row.getRowIndex() + 1) + " ,music key = " + item.getUniqueKey();
                    log(columns, "no cuts", msg);
                    logWriter.writeTransferItem(TAG, msg);
                    return null;
                }
                item.setCutInfos(infos);
                List<CutInfo> list = VisitServices.from(infos).filter(new PredicateVisitor<CutInfo>() {
                    @Override
                    public Boolean visit(CutInfo cutInfo, Object param) {
                        return cutInfo.getType() == CutInfo.TYPE_INTENSIVE;
                    }
                }).getAsList();
                if (Predicates.isEmpty(list)) {
                    throw new RuntimeException("must have CutInfo.TYPE_INTENSIVE. for music '" + item.getName() + "'");
                }
                CutInfo intensiveInfo = list.get(0);
                List<Float> cuts = intensiveInfo.getCuts();
                //speed areas
                handleSpeedAreas(columns, item, intensiveInfo);
                //old need check
                ErrorVerifier.check(item, cuts.get(cuts.size() - 1), sb_warn);

                if (item.isAllAreaEmpty()) {
                    String msg = "no speed area. line number = " + (row.getRowIndex() + 1) + " ,music key = " + item.getUniqueKey();
                    log(columns, "isAllAreaEmpty()", msg);
                    logWriter.writeTransferItem(TAG, msg);
                    return null;
                }
                return item;
            }
        }).getAsList();
        return items;
    }

    private void handleSpeedAreas(List<ExcelCol> columns, MusicItem2 item, CutInfo intensiveInfo) {
        if(!useOldSpeedArea && speedAreaSource != null){
            item.setSlow_speed_areas(speedAreaSource.getSpeedArea(item, CutConfigBeanV10.AREA_TYPE_LOW));
            item.setMiddle_speed_areas(speedAreaSource.getSpeedArea(item, CutConfigBeanV10.AREA_TYPE_MIDDLE));
            item.setHigh_speed_areas(speedAreaSource.getSpeedArea(item, CutConfigBeanV10.AREA_TYPE_HIGH));
        }else {
            List<TimeArea> areas = new ArrayList<>();
            item.setSlow_speed_areas(parseTimeAreas(intensiveInfo, item, columns.get(indexDelegate.getSlowSpeedIndex()).getColumnString(), areas));
            item.setMiddle_speed_areas(parseTimeAreas(intensiveInfo, item, columns.get(indexDelegate.getMiddleSpeedIndex()).getColumnString(), areas));
            item.setHigh_speed_areas(parseTimeAreas(intensiveInfo, item, columns.get(indexDelegate.getHighSpeedIndex()).getColumnString(), areas));
            //old need mapping
            writeMappingFile(item, areas);
        }
    }

    protected  void parseBaseInfo(List<ExcelCol> columns, MusicItem2 item){
        String name = columns.get(indexDelegate.getNameIndex()).getColumnString();
        String newName = UniformNameHelper.uniformSimpleMusicName(name);
        Logger.d(TAG, "parseBaseInfo", "ExcelCol -- music name changed : old = " +  name
                + " ,new = " + newName);
        item.setName(newName);
        //item.setName(columns.get(indexDelegate.getNameIndex()).getColumnString());
        item.setDomains(parseDomain(columns.get(indexDelegate.getDomainIndex()).getColumnString()));
        item.setProperty(parseMood(columns.get(indexDelegate.getMoodIndex()).getColumnString()));
        item.setRhythm(parseRhythm(columns.get(indexDelegate.getRhythmIndex()).getColumnString()));
    }

    protected boolean filter(ExcelRow row){
        List<ExcelCol> columns = row.getColumns();
        String name = columns.get(indexDelegate.getNameIndex()).getColumnString();
        String domain = columns.get(indexDelegate.getDomainIndex()).getColumnString();
        String mood = columns.get(indexDelegate.getMoodIndex()).getColumnString();
        String rhythm = columns.get(indexDelegate.getRhythmIndex()).getColumnString();
        if(name == null || name.trim().isEmpty()){
            return true;
        }
        if(domain == null || domain.trim().isEmpty()){
            return true;
        }
        if(mood == null || mood.trim().isEmpty()){
            return true;
        }
        if(rhythm == null || rhythm.trim().isEmpty()){
            return true;
        }
        return false;
    }

    //---------------------------------------------------------------------------- //

    private void writeMappingFile(MusicItem2 item, List<TimeArea> areas) {
        //write mapping
        String outFile = outDir + File.separator + "mapping" + File.separator + item.getName() + "___mapping.txt";
        StringBuilder sb_mapping = new StringBuilder();
        Collections.sort(areas);
        for (TimeArea ta : areas){
            sb_mapping.append(ta.toMappingText()).append("\r\n");
        }
        FileUtils.writeTo(outFile, sb_mapping.toString());
    }
    protected void log(List<ExcelCol> columns, String mTag, String msg){
        Logger.d(TAG, mTag, msg);
    }

}
