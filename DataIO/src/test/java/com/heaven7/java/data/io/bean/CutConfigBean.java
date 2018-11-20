package com.heaven7.java.data.io.bean;

import com.heaven7.java.visitor.PredicateVisitor;
import com.heaven7.java.visitor.collection.VisitServices;

import java.util.List;

/**
 * @author heaven7
 */
public class CutConfigBean {

    private List<CutItem> cutItems;

    public List<CutItem> getCutItems() {
        return cutItems;
    }
    public void setCutItems(List<CutItem> cutItems) {
        this.cutItems = cutItems;
    }

    public CutItem getCutItem(final String rowName) {
        if(cutItems == null){
            return null;
        }
        return VisitServices.from(cutItems).query(new PredicateVisitor<CutItem>() {
            @Override
            public Boolean visit(CutItem cutItem, Object param) {
                return rowName.equals(cutItem.getName());
            }
        });
    }

    public static class CutItem{
        private String name;
        private String cuts;

        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }

        public String getCuts() {
            return cuts;
        }
        public void setCuts(String cuts) {
            this.cuts = cuts;
        }
    }
}
