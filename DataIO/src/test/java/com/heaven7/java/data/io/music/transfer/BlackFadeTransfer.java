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

/** @author heaven7 */
public class BlackFadeTransfer extends BaseAdditionalTransfer<List<EffectInfo>> {

    private static final List<ColumnDelegate> DELEGATES = new ArrayList<>();

    static {
        DELEGATES.add(new ColumnDelegate_L());
        DELEGATES.add(new ColumnDelegate_M());
        DELEGATES.add(new ColumnDelegate_H());
    }

    public BlackFadeTransfer(Indexer indexer) {
        super("repeat_black", indexer);
    }

    @Override
    protected List<EffectInfo> parseItem(ExcelRow row) {
        Indexer mIndexer = (Indexer) this.indexer;
        List<ExcelCol> columns = row.getColumns();
        List<EffectInfo> results = new ArrayList<>();

        for (ColumnDelegate delegate : DELEGATES) {
            delegate.parse(columns, mIndexer, results);
        }
        return results;
    }

    @Override
    protected void applyAdditionInfo(MusicItem2 matchItem, WrappedSubItem<List<EffectInfo>> wsb) {
        matchItem.addTransitionInfos(wsb.getSubItem());
    }

    private static List<EffectInfo> parseShotBlack(
            List<ExcelCol> columns, final int index, final ColumnDelegate delegate) {
        String colStr = columns.get(index).getColumnString().trim();
        if (TextUtils.isEmpty(colStr)) {
            return null;
        }
        String[] effects = colStr.split("\\n");
        return VisitServices.from(Arrays.asList(effects))
                .map(
                        new ResultVisitor<String, EffectInfo>() {
                            @Override
                            public EffectInfo visit(String s, Object param) {
                                s = s.trim();
                                EffectInfo info = delegate.create();
                                String effectStr = Configs.getTransitionStr(s);
                                if (Predicates.isEmpty(effectStr)) {
                                    System.err.println("RepeatBlackTransfer >>> wrong effect:  " + s);
                                }
                                info.setEffect(effectStr);
                                return info;
                            }
                        })
                .getAsList();
    }

    public static class Indexer extends BaseAdditionalTransfer.Indexer {
        public int index_low = 2;
        public int index_middle = 3;
        public int index_high = 4;
    }

    abstract static class ColumnDelegate {

        protected abstract int getIndex(Indexer indexer);

        public EffectInfo create() {
            EffectInfo info = new EffectInfo();
            info.setNameType(EffectInfo.NONE);
            onEffectCreated(info);
            return info;
        }

        protected abstract void onEffectCreated(EffectInfo info);

        public void parse(List<ExcelCol> columns, Indexer indexer, List<EffectInfo> outInfos) {
            List<EffectInfo> infos = parseShotBlack(columns, getIndex(indexer), this);
            if (!Predicates.isEmpty(infos)) {
                outInfos.addAll(infos);
            }
        }
    }

    static class ColumnDelegate_L extends ColumnDelegate{
        @Override
        protected int getIndex(Indexer indexer) {
            return indexer.index_low;
        }
        @Override
        protected void onEffectCreated(EffectInfo info) {
            info.setNameCategory(EffectInfo.CATEGORY_LOW_AREA);
        }
    }
    static class ColumnDelegate_M extends ColumnDelegate{
        @Override
        protected int getIndex(Indexer indexer) {
            return indexer.index_middle;
        }
        @Override
        protected void onEffectCreated(EffectInfo info) {
            info.setNameCategory(EffectInfo.CATEGORY_MIDDLE_AREA);
        }
    }
    static class ColumnDelegate_H extends ColumnDelegate{
        @Override
        protected int getIndex(Indexer indexer) {
            return indexer.index_high;
        }
        @Override
        protected void onEffectCreated(EffectInfo info) {
            info.setNameCategory(EffectInfo.CATEGORY_HIGH_AREA);
        }
    }
}
