package com.heaven7.java.data.io.utils;

import com.heaven7.java.base.util.Predicates;
import com.heaven7.java.data.io.bean.EffectInfo;
import com.heaven7.java.data.io.bean.EffectOutItem;
import com.heaven7.java.visitor.MapFireVisitor;
import com.heaven7.java.visitor.ResultVisitor;
import com.heaven7.java.visitor.collection.KeyValuePair;
import com.heaven7.java.visitor.collection.VisitServices;

import java.util.List;

/**
 * @author heaven7
 */
public class MusicItem2Helper {

    public static EffectOutItem getEffectOutItem(List<EffectInfo> infos,
                                                 final EffectOutItem.Scope defaultImgScope,final boolean specialEffect){
        if(Predicates.isEmpty(infos)){
            return null;
        }
        final EffectOutItem item = new EffectOutItem();
        VisitServices.from(infos).groupService(new ResultVisitor<EffectInfo, Integer>() {
            @Override
            public Integer visit(EffectInfo effectInfo, Object param) {
                return effectInfo.getNameCategory();
            }
        }).fire(new MapFireVisitor<Integer, List<EffectInfo>>() {
            @Override
            public Boolean visit(KeyValuePair<Integer, List<EffectInfo>> pair, Object param) {
                final EffectOutItem.SpeedArea area;
                switch (pair.getKey()){
                    case EffectInfo.CATEGORY_LOW_AREA:
                        area = item.getSlow_speed_area();
                        break;
                    case EffectInfo.CATEGORY_MIDDLE_AREA:
                        area = item.getMiddle_speed_area();
                        break;
                    case EffectInfo.CATEGORY_HIGH_AREA:
                        area = item.getHigh_speed_area();
                        break;
                    default:
                        throw new RuntimeException("wrong effect category. = " + pair.getKey());
                }
                // before next travel . we need make name_type of 'NONE' to all
                VisitServices.from(pair.getValue()).groupService(new ResultVisitor<EffectInfo, Integer>() {
                    @Override
                    public Integer visit(EffectInfo effectInfo, Object param) {
                        return effectInfo.getNameType();
                    }
                }).fire(new MapFireVisitor<Integer, List<EffectInfo>>() {
                    @Override
                    public Boolean visit(KeyValuePair<Integer, List<EffectInfo>> pair, Object param) {
                        final EffectOutItem.ScoreArea scoreArea;
                        switch (pair.getKey()){
                            case EffectInfo.NAME_TYPE_LOW_SCORE:
                                scoreArea = area.getLow_score_area();
                                break;
                            case EffectInfo.NAME_TYPE_MIDDLE_SCORE:
                                scoreArea = area.getMiddle_score_area();
                                break;
                            case EffectInfo.NAME_TYPE_HIGH_SCORE:
                                scoreArea = area.getHigh_score_area();
                                break;
                            case EffectInfo.NONE:
                                //for none it can be used for low-middle-high score areas.
                                List<String> effects = inflateEffects(pair);
                                EffectOutItem.ScoreArea sa = area.ensureNoScoreAreaNotNull();
                                sa.getVideoScope().addEffects(effects);
                                sa.setImageScope(specialEffect ? defaultImgScope : sa.getVideoScope());
                              /*  area.getLow_score_area().getVideoScope().addEffects(effects);
                                area.getMiddle_score_area().getVideoScope().addEffects(effects);
                                area.getHigh_score_area().getVideoScope().addEffects(effects);*/
                                return true;
                            default:
                                throw new RuntimeException("wrong effect category. = " + pair.getKey());
                        }
                        scoreArea.setVideoScope(createScope(pair));
                        scoreArea.setImageScope(specialEffect ? defaultImgScope : scoreArea.getVideoScope());
                        return null;
                    }
                });
                return false;
            }
        });
        return item;
    }

    public static EffectOutItem.Scope createScope(KeyValuePair<Integer, List<EffectInfo>> pair) {
        EffectOutItem.Scope videoScope = new EffectOutItem.Scope();
        videoScope.addEffects(inflateEffects(pair));
        return videoScope;
    }

    private static List<String> inflateEffects(KeyValuePair<Integer, List<EffectInfo>> pair) {
        return VisitServices.from(pair.getValue()).map(new ResultVisitor<EffectInfo, String>() {
                @Override
                public String visit(EffectInfo effectInfo, Object param) {
                    return effectInfo.getEffect();
                }
            }).getAsList();
    }
}
