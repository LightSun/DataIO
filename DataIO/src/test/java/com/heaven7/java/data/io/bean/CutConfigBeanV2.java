package com.heaven7.java.data.io.bean;

import com.heaven7.java.visitor.PredicateVisitor;
import com.heaven7.java.visitor.collection.VisitServices;

import java.util.List;

/**
 * @author heaven7
 */
public class CutConfigBeanV2 {

    public static final int SPEED_TYPE_UNKNOWN   = 0;
    public static final int SPEED_TYPE_SLOW   = 1;
    public static final int SPEED_TYPE_MIDDLE = 2;
    public static final int SPEED_TYPE_HIGH   = 3;

    private List<CutItem> cutItems;

    public List<CutItem> getCutItems() {
        return cutItems;
    }
    public void setCutItems(List<CutItem> cutItems) {
        this.cutItems = cutItems;
    }

    public CutItem getCutItem(final String rowName, final int duration) {
        if(cutItems == null){
            return null;
        }
        return VisitServices.from(cutItems).query(new PredicateVisitor<CutItem>() {
            @Override
            public Boolean visit(CutItem cutItem, Object param) {
                return rowName.equals(cutItem.getName()) && duration == cutItem.getDuration();
            }
        });
    }

    public static class CutItem{
        private String name;
        private int duration;
        private List<CutLine> cutLines;

        public int getDuration() {
            return duration;
        }
        public void setDuration(int duration) {
            this.duration = duration;
        }

        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }

        public List<CutLine> getCutLines() {
            return cutLines;
        }
        public void setCutLines(List<CutLine> cutLines) {
            this.cutLines = cutLines;
        }
    }

    public static class CutLine{
        private float cut;
        /** marked speed type. relative with {@linkplain MusicItem#getHigh_speed_areas()} and etc. */
        private int speedType = CutConfigBeanV2.SPEED_TYPE_UNKNOWN;

        public float getCut() {
            return cut;
        }
        public void setCut(float cut) {
            this.cut = cut;
        }

        public int getSpeedType() {
            return speedType;
        }
        public void setSpeedType(int speedType) {
            this.speedType = speedType;
        }
    }
}
