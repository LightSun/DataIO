package com.heaven7.java.data.io.music.provider;

import com.heaven7.java.data.io.bean.CutConfigBeanV2;
import com.heaven7.java.data.io.bean.MusicItem;
import com.heaven7.java.visitor.FireMultiVisitor2;
import com.heaven7.java.visitor.MapFireVisitor;
import com.heaven7.java.visitor.collection.KeyValuePair;
import com.heaven7.java.visitor.collection.VisitServices;
import com.heaven7.java.visitor.util.SparseArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author heaven7
 */
public class SimpleSpeedMusicCutProvider extends MusicCutProviderV2 implements SpeedMusicCutProvider {

    public SimpleSpeedMusicCutProvider(String cutConfigFile) {
        super(cutConfigFile);
    }

    @Override
    public boolean fillSpeedAreasForMusicItem(final String rowName, final MusicItem item) {
        CutConfigBeanV2.CutItem cutItem = mBean.getCutItem(rowName);
        final SparseArray<List<List<Float>>> map = new SparseArray<>();
        VisitServices.from(cutItem.getCutLines()).fireMulti2(2, 1, null,
                new FireMultiVisitor2<CutConfigBeanV2.CutLine>() {
            @Override
            public boolean visit(Object param, int count, int step, List<CutConfigBeanV2.CutLine> cutLines) {
                if(cutLines.size() == 1){
                    //the end
                    return false;
                }
                assert cutLines.size() == 2;
                CutConfigBeanV2.CutLine line1 = cutLines.get(0);
                CutConfigBeanV2.CutLine line2 = cutLines.get(1);
                // speed type determined by first cut line
                addSpeedArea(map, line1.getSpeedType(), line1.getCut(), line2.getCut());
                return false;
            }
        });

        VisitServices.from(map).fire(new MapFireVisitor<Integer, List<List<Float>>>() {
            @Override
            public Boolean visit(KeyValuePair<Integer, List<List<Float>>> pair, Object param) {
                switch (pair.getKey()){
                    case CutConfigBeanV2.SPEED_TYPE_SLOW:
                        item.setSlow_speed_areas(pair.getValue());
                        break;

                    case CutConfigBeanV2.SPEED_TYPE_MIDDLE:
                        item.setMiddle_speed_areas(pair.getValue());
                        break;

                    case CutConfigBeanV2.SPEED_TYPE_HIGH:
                        item.setHigh_speed_areas(pair.getValue());
                        break;
                }
                return null;
            }
        });
        return true;
    }

    private void addSpeedArea(SparseArray<List<List<Float>>> map,int speedType, float cut1, float cut2) {
        List<List<Float>> lists = map.get(speedType);
        if(lists == null){
            lists = new ArrayList<>();
            map.put(speedType, lists);
        }
        lists.add(Arrays.asList(cut1, cut2));
    }

}
