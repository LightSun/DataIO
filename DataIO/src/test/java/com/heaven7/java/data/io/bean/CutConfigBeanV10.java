package com.heaven7.java.data.io.bean;

import com.heaven7.java.visitor.PredicateVisitor;
import com.heaven7.java.visitor.collection.VisitServices;

import java.util.List;

/**
 * @author heaven7
 */
public class CutConfigBeanV10 {

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
        float cut;
        byte flags;

        public float getCut() {
            return cut;
        }
        public void setCut(float cut) {
            this.cut = cut;
        }
        public byte getFlags() {
            return flags;
        }

        public void setFlags(byte flags) {
            this.flags = flags;
        }

        public boolean hasFlag(byte typeSparse) {
            return false;
        }
    }

}
