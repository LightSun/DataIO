package com.heaven7.java.data.io.music.transfer;

import com.heaven7.java.base.util.TextUtils;
import com.heaven7.java.data.io.bean.MusicItem2;
import com.heaven7.java.data.io.bean.WrappedSubItem;
import com.heaven7.java.data.io.poi.ExcelRow;
import com.heaven7.java.visitor.ResultVisitor;
import com.heaven7.java.visitor.collection.VisitServices;

import java.util.List;

/**
 * @author heaven7
 */
public class TransitionCutTransfer extends BaseAdditionalTransfer<List<Float>> {

    private int travelIndex = -1;

    public TransitionCutTransfer(){
        this(new TransitionCutTransfer.Indexer());
    }
    public TransitionCutTransfer(TransitionCutTransfer.Indexer indexer) {
        super("transition_cut", indexer);
    }

    @Override
    protected List<Float> parseItem(ExcelRow row) {
        //专场点目前只有每隔3行才有
        Indexer mIndexer = (Indexer) this.indexer;

        if(travelIndex < 0){
            travelIndex = 1;
        }else {
            travelIndex++;
        }
        if(travelIndex % 3 == 0){
            String str = row.getColumns().get(mIndexer.index_trans_cuts).getColumnString();
            if(TextUtils.isEmpty(str)){
                return null;
            }
            str = str.replace(" ", "");
            return VisitServices.from(str.split(",")).map(new ResultVisitor<String, Float>() {
                @Override
                public Float visit(String s, Object param) {
                    return Float.parseFloat(s);
                }
            }).getAsList();
        }
        return null;
    }

    @Override
    protected void applyAdditionInfo(MusicItem2 matchItem, WrappedSubItem<List<Float>> wsb) {
        if(matchItem.getTransitionCuts() != null && wsb.getSubItem() == null){
            System.err.println("TransitionCutTransfer >>> unexpect applyAdditionInfo. ");
        }
        matchItem.setTransitionCuts(wsb.getSubItem());
    }

    public static class Indexer extends BaseAdditionalTransfer.Indexer{
        public int index_trans_cuts = 3;
    }
}
