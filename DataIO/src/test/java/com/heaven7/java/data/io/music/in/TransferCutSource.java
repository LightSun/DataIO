package com.heaven7.java.data.io.music.in;

import com.heaven7.java.base.anno.NonNull;
import com.heaven7.java.base.util.Predicates;
import com.heaven7.java.data.io.bean.CutConfigBeanV10;
import com.heaven7.java.data.io.bean.CutInfo;
import com.heaven7.java.data.io.music.UniformNameHelper;
import com.heaven7.java.data.io.music.mock.MockExcelCol;
import com.heaven7.java.data.io.music.mock.MockExcelRow;
import com.heaven7.java.data.io.music.transfer.TransitionCutTransfer;
import com.heaven7.java.data.io.poi.ExcelRow;
import com.heaven7.java.visitor.ResultVisitor;
import com.heaven7.java.visitor.collection.VisitServices;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author heaven7
 */
public class TransferCutSource implements ExcelSource {

    private final CutConfigBeanV10 bean;
    private List<ExcelRow> mRows;

    public TransferCutSource(@NonNull CutConfigBeanV10 bean) {
        this.bean = bean;
    }

    @Override
    public List<ExcelRow> getRows() {
        if(mRows == null){
            List<CutConfigBeanV10.CutItem> cutItems = bean.getCutItems();
            if(Predicates.isEmpty(cutItems)){
                return Collections.emptyList();
            }
            List<ExcelRow> list = VisitServices.from(cutItems).map(new ResultVisitor<CutConfigBeanV10.CutItem, ExcelRow>() {
                @Override
                public ExcelRow visit(CutConfigBeanV10.CutItem cutItem, Object param) {
                    //trans cut
                    List<Float> transCuts = VisitServices.from(cutItem.getCutLines()).map(new ResultVisitor<CutConfigBeanV10.CutLine, Float>() {
                        @Override
                        public Float visit(CutConfigBeanV10.CutLine cutLine, Object param) {
                            if (cutLine.hasFlag(CutInfo.FLAG_TRANSITION_CUT)) {
                                return cutLine.getCut();
                            }
                            return null;
                        }
                    }).getAsList();
                    if (Predicates.isEmpty(transCuts)) {
                        return null;
                    }
                    String transCut = VisitServices.from(transCuts).joinToString(",");
                    List<MockExcelCol> cols = Arrays.asList(new MockExcelCol(UniformNameHelper.uniformSimpleMusicName(cutItem.getName())),
                            new MockExcelCol(cutItem.getDuration() + ""),
                            new MockExcelCol(transCut));
                    return new MockExcelRow(cols);
                }
            }).getAsList();
            //nop populate
            List<ExcelRow> nopRows = new ArrayList<>();
            for (int i = 0; i < TransitionCutTransfer.PERIOD - 1 ; i ++){
                nopRows.add(new MockExcelRow());
            }
            mRows = new ArrayList<>();
            for (int i =0, count = list.size() ; i < count ; i ++){
                mRows.addAll(nopRows);
                mRows.add(list.get(i));
            }
        }
        return mRows;
    }
}
