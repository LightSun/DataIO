package com.heaven7.java.data.io.music.transfer;

import com.heaven7.java.base.util.Logger;
import com.heaven7.java.data.io.bean.MusicItem2;
import com.heaven7.java.data.io.bean.WrappedSubItem;
import com.heaven7.java.data.io.music.UniformNameHelper;
import com.heaven7.java.data.io.music.in.ExcelSource;
import com.heaven7.java.data.io.poi.ExcelRow;
import com.heaven7.java.visitor.FireVisitor;
import com.heaven7.java.visitor.PredicateVisitor;
import com.heaven7.java.visitor.ResultVisitor;
import com.heaven7.java.visitor.collection.VisitServices;

import java.util.List;

/**
 * @author heaven7
 */
public abstract class BaseAdditionalTransfer<T> implements AdditionalTransfer {

    public static final int MAX_ROW_MERGE_COUNT = 3;
    protected final Indexer indexer;
    private final String transferName;

    public BaseAdditionalTransfer(String transferName, Indexer indexer) {
        this.indexer = indexer;
        this.transferName = transferName;
    }

    @Override
    public final void transfer(ExcelSource effect, final List<MusicItem2> toItems) {
        final List<ExcelRow> rows = effect.getRows();
        VisitServices.from(rows).map(new ResultVisitor<ExcelRow, WrappedSubItem<T>>() {
            @Override
            public WrappedSubItem<T> visit(ExcelRow row, Object param) {
                Logger.d(transferName, "transfer", "row index = " + row.getRowIndex());
                T t = parseItem(row);
                if(t == null){
                    //Logger.d(transferName, "parseItem is null.");
                    return null;
                }
                String name = row.getCellString(rows, indexer.nameIndex, MAX_ROW_MERGE_COUNT);
                String duration = row.getCellString(rows, indexer.durationIndex, MAX_ROW_MERGE_COUNT);
                if(duration.endsWith("s")){
                    duration = duration.substring(0, duration.length()-1);
                }
                String uniformName = UniformNameHelper.uniformSimpleMusicName(name);
                return new WrappedSubItem<T>(t, uniformName, Integer.parseInt(duration), name, row.getRowIndex() + 1);
            }
        }).fire(new FireVisitor<WrappedSubItem<T>>() {
            @Override
            public Boolean visit(final WrappedSubItem<T> wsb, Object param) {
                MusicItem2 matchItem = VisitServices.from(toItems).query(new PredicateVisitor<MusicItem2>() {
                    @Override
                    public Boolean visit(MusicItem2 mi, Object param) {
                        return wsb.getName().equals(mi.getName()) && wsb.getDuration() == mi.getDuration();
                    }
                });
                if(matchItem == null){
                    System.err.println("can't find " + transferName + " ,for lineNum = " + wsb.getLineNumber() + ",musicName = " + wsb.getName());
                }else {
                    applyAdditionInfo(matchItem, wsb);
                }
                return null;
            }
        });
    }

    /**
     * parse the item . if return null. means this row will be ignored.
     * @param row the excel row
     * @return the parsed item
     */
    protected abstract T parseItem(ExcelRow row);

    protected abstract void applyAdditionInfo(MusicItem2 matchItem, WrappedSubItem<T> wsb);

    public static class Indexer{
        public int nameIndex = 0;
        public int durationIndex = 1;
    }
}
