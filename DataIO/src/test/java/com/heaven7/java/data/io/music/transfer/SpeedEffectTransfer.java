package com.heaven7.java.data.io.music.transfer;

import com.heaven7.java.base.util.Predicates;
import com.heaven7.java.base.util.TextUtils;
import com.heaven7.java.data.io.bean.EffectInfo;
import com.heaven7.java.data.io.bean.MusicItem2;
import com.heaven7.java.data.io.bean.WrappedSubItem;
import com.heaven7.java.data.io.music.Configs;
import com.heaven7.java.data.io.poi.ExcelCol;
import com.heaven7.java.data.io.poi.ExcelRow;
import com.heaven7.java.visitor.ResultVisitor;
import com.heaven7.java.visitor.collection.VisitServices;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author heaven7
 */
public class SpeedEffectTransfer extends BaseAdditionalTransfer<List<EffectInfo>> {

    private static final List<IndexParseDelegate> sDelegates = new ArrayList<>();

    static {
        sDelegates.add(new IndexParseDelegate_LL());
        sDelegates.add(new IndexParseDelegate_LM());
        sDelegates.add(new IndexParseDelegate_LH());

        sDelegates.add(new IndexParseDelegate_ML());
        sDelegates.add(new IndexParseDelegate_MM());
        sDelegates.add(new IndexParseDelegate_MH());

        sDelegates.add(new IndexParseDelegate_HL());
        sDelegates.add(new IndexParseDelegate_HM());
        sDelegates.add(new IndexParseDelegate_HH());
    }

    public SpeedEffectTransfer(Indexer indexer) {
        super("speed_effect", indexer);
    }

    @Override
    protected List<EffectInfo> parseItem(ExcelRow row) {
        Indexer mIndexer = (Indexer) this.indexer;
        return mIndexer.parse(row.getColumns());
    }

    @Override
    protected void applyAdditionInfo(MusicItem2 matchItem, WrappedSubItem<List<EffectInfo>> wsb) {
        matchItem.addEffectInfos(wsb.getSubItem());
    }

    private static List<EffectInfo> parseSpeed(List<ExcelCol> columns, int index ,final IndexParseDelegate delegate){
        String columnString = columns.get(index).getColumnString().trim();
        if(TextUtils.isEmpty(columnString)){
            return null;
        }
        String[] effects = columnString.split("\\n");
        return VisitServices.from(Arrays.asList(effects)).map(new ResultVisitor<String, EffectInfo>() {
            @Override
            public EffectInfo visit(String s, Object param) {
                s = s.trim();
                EffectInfo info = delegate.create();
                String effectStr = Configs.getEffectStr(s);
                if (Predicates.isEmpty(effectStr)) {
                    System.err.println("RepeatBlackTransfer >>> wrong effect:  " + s);
                }
                info.setEffect(effectStr);
                return info;
            }
        }).getAsList();
    }

    public static class Indexer extends BaseAdditionalTransfer.Indexer{
        //category - type
        public int low_low_index = 2;
        public int low_middle_index = 3;
        public int low_high_index = 4;

        public int middle_low_index = 5;
        public int middle_middle_index = 6;
        public int middle_high_index = 7;

        public int high_low_index = 8;
        public int high_middle_index = 9;
        public int high_high_index = 10;

        public List<EffectInfo> parse(List<ExcelCol> columns){
            List<EffectInfo> infos = new ArrayList<>();
            for(IndexParseDelegate delegate :  sDelegates){
                delegate.parse(columns, this, infos);
            }
            return infos;
        }
    }


    /*public*/ abstract static class IndexParseDelegate{

        protected abstract int getIndex(Indexer indexer);

        protected abstract EffectInfo create();

        public void parse(List<ExcelCol> columns, Indexer indexer, List<EffectInfo> outInfos){
            List<EffectInfo> infos = parseSpeed(columns, getIndex(indexer), this);
            if(!Predicates.isEmpty(infos)){
                outInfos.addAll(infos);
            }
        }
    }

    /*public*/ static class IndexParseDelegate_HH extends IndexParseDelegate{
        @Override
        public EffectInfo create() {
            EffectInfo info = new EffectInfo();
            info.setNameCategory(EffectInfo.CATEGORY_HIGH_AREA);
            info.setNameType(EffectInfo.NAME_TYPE_HIGH_SCORE);
            return info;
        }
        @Override
        public int getIndex(Indexer indexer) {
            return indexer.high_high_index;
        }
    }
    /*public*/ static class IndexParseDelegate_HM extends IndexParseDelegate{
        @Override
        public EffectInfo create() {
            EffectInfo info = new EffectInfo();
            info.setNameCategory(EffectInfo.CATEGORY_HIGH_AREA);
            info.setNameType(EffectInfo.NAME_TYPE_MIDDLE_SCORE);
            return info;
        }
        @Override
        public int getIndex(Indexer indexer) {
            return indexer.high_middle_index;
        }
    }
    /*public*/ static class IndexParseDelegate_HL extends IndexParseDelegate{
        @Override
        public EffectInfo create() {
            EffectInfo info = new EffectInfo();
            info.setNameCategory(EffectInfo.CATEGORY_HIGH_AREA);
            info.setNameType(EffectInfo.NAME_TYPE_LOW_SCORE);
            return info;
        }
        @Override
        public int getIndex(Indexer indexer) {
            return indexer.high_low_index;
        }
    }
    /*public*/ static class IndexParseDelegate_MH extends IndexParseDelegate{
        @Override
        public EffectInfo create() {
            EffectInfo info = new EffectInfo();
            info.setNameCategory(EffectInfo.CATEGORY_MIDDLE_AREA);
            info.setNameType(EffectInfo.NAME_TYPE_HIGH_SCORE);
            return info;
        }
        @Override
        public int getIndex(Indexer indexer) {
            return indexer.middle_high_index;
        }
    }
    /*public*/ static class IndexParseDelegate_MM extends IndexParseDelegate{
        @Override
        public EffectInfo create() {
            EffectInfo info = new EffectInfo();
            info.setNameCategory(EffectInfo.CATEGORY_MIDDLE_AREA);
            info.setNameType(EffectInfo.NAME_TYPE_MIDDLE_SCORE);
            return info;
        }
        @Override
        public int getIndex(Indexer indexer) {
            return indexer.middle_middle_index;
        }
    }
    /*public*/ static class IndexParseDelegate_ML extends IndexParseDelegate{
        @Override
        public EffectInfo create() {
            EffectInfo info = new EffectInfo();
            info.setNameCategory(EffectInfo.CATEGORY_MIDDLE_AREA);
            info.setNameType(EffectInfo.NAME_TYPE_LOW_SCORE);
            return info;
        }
        @Override
        public int getIndex(Indexer indexer) {
            return indexer.middle_low_index;
        }
    }
    /*public*/ static class IndexParseDelegate_LH extends IndexParseDelegate{
        @Override
        public EffectInfo create() {
            EffectInfo info = new EffectInfo();
            info.setNameCategory(EffectInfo.CATEGORY_LOW_AREA);
            info.setNameType(EffectInfo.NAME_TYPE_HIGH_SCORE);
            return info;
        }
        @Override
        public int getIndex(Indexer indexer) {
            return indexer.low_high_index;
        }
    }
    /*public*/ static class IndexParseDelegate_LM extends IndexParseDelegate{
        @Override
        public EffectInfo create() {
            EffectInfo info = new EffectInfo();
            info.setNameCategory(EffectInfo.CATEGORY_LOW_AREA);
            info.setNameType(EffectInfo.NAME_TYPE_MIDDLE_SCORE);
            return info;
        }
        @Override
        public int getIndex(Indexer indexer) {
            return indexer.low_middle_index;
        }
    }
    /*public*/ static class IndexParseDelegate_LL extends IndexParseDelegate{
        @Override
        public EffectInfo create() {
            EffectInfo info = new EffectInfo();
            info.setNameCategory(EffectInfo.CATEGORY_LOW_AREA);
            info.setNameType(EffectInfo.NAME_TYPE_LOW_SCORE);
            return info;
        }
        @Override
        public int getIndex(Indexer indexer) {
            return indexer.low_low_index;
        }
    }
}
