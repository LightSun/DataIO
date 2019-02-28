package com.heaven7.java.data.io.music.in;

import com.heaven7.java.data.io.bean.CutConfigBeanV10;
import com.heaven7.java.data.io.bean.CutInfo;
import com.heaven7.java.data.io.bean.MusicItemDelegate;
import com.heaven7.java.visitor.FireMultiVisitor2;
import com.heaven7.java.visitor.PredicateVisitor;
import com.heaven7.java.visitor.collection.VisitServices;
import com.heaven7.java.visitor.util.SparseArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * @author heaven7
 */
public class SimpleSpeedAreaSource implements SpeedAreaSource {

    private final CutConfigBeanV10 bean;
    private HashMap<MusicItemDelegate, SparseArray<List<List<Float>>>> areaMap = new HashMap<>();

    public SimpleSpeedAreaSource(CutConfigBeanV10 bean) {
        this.bean = bean;
    }

    @Override //变更： 高速区域h1, h2
    public List<List<Float>> getSpeedArea(MusicItemDelegate delegate, int areaType) {
        SparseArray<List<List<Float>>> map = areaMap.get(delegate);
        if(map == null){
            final SparseArray<List<List<Float>>> localMap = new SparseArray<>();
            CutConfigBeanV10.CutItem cutItem = bean.getCutItem(delegate.getName(), delegate.getDuration());
            VisitServices.from(cutItem.getCutLines()).filter(new PredicateVisitor<CutConfigBeanV10.CutLine>() {
                @Override
                public Boolean visit(CutConfigBeanV10.CutLine line, Object param) {
                    return line.hasFlag(CutInfo.FLAG_SPEED_AREA_MARKED);
                }
            }).asListService().fireMulti2(2, 1, null, new FireMultiVisitor2<CutConfigBeanV10.CutLine>() {
                @Override
                public boolean visit(Object param, int count, int step, List<CutConfigBeanV10.CutLine> cutLines) {
                    if(cutLines.size() == 1){
                        //the end
                        return false;
                    }
                    assert cutLines.size() == 2;
                    CutConfigBeanV10.CutLine line1 = cutLines.get(0);
                    CutConfigBeanV10.CutLine line2 = cutLines.get(1);
                    // speed type determined by first cut line
                    addSpeedArea(localMap, line1.getAreaType(), line1.getCut(), line2.getCut());
                    return false;
                }
            });
            areaMap.put(delegate, localMap);
        }
        return areaMap.get(delegate).get(areaType);
    }

    private void addSpeedArea(SparseArray<List<List<Float>>> map,int areaType, float cut1, float cut2) {
        List<List<Float>> lists = map.get(areaType);
        if(lists == null){
            lists = new ArrayList<>();
            map.put(areaType, lists);
        }
        lists.add(Arrays.asList(cut1, cut2));
    }
}
