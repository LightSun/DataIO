package com.heaven7.java.data.io.music.transfer;

import com.heaven7.java.base.util.Predicates;
import com.heaven7.java.base.util.TextUtils;
import com.heaven7.java.data.io.bean.MusicItem2;
import com.heaven7.java.data.io.bean.WrappedSubItem;
import com.heaven7.java.data.io.poi.ExcelRow;
import com.heaven7.java.visitor.FireVisitor;
import com.heaven7.java.visitor.collection.VisitServices;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author heaven7
 */
public class FilterTransfer extends BaseAdditionalTransfer<List<String>> {

    public FilterTransfer() {
        this(new Indexer());
    }
    public FilterTransfer(Indexer indexer) {
        super("filter", indexer);
    }

    @Override
    protected List<String> parseItem(ExcelRow row) {
        Indexer mIndexer = (Indexer) this.indexer;
        String filters = row.getColumns().get(mIndexer.index_filter).getColumnString().trim();
        if(TextUtils.isEmpty(filters)){
            return new ArrayList<>();
        }
        String[] strs = filters.split("\\n");
        if(Predicates.isEmpty(strs)){
            return new ArrayList<>();
        }
        final List<String> allFilters = getEffectMappingSource().getFilters();
        VisitServices.from(strs).fire(new FireVisitor<String>() {
            @Override
            public Boolean visit(String s, Object param) {
                if(!allFilters.contains(s)){
                   getLogWriter().writeTransferEffect(getTransferName(), "wrong [Filter] = " + s);
                }
                return null;
            }
        });
        return Arrays.asList(strs);
    }
    @Override
    protected void applyAdditionInfo(MusicItem2 matchItem, WrappedSubItem<List<String>> wsb) {
        matchItem.setFilterNames(wsb.getSubItem());
    }

    public static class Indexer extends BaseAdditionalTransfer.Indexer{
        public int index_filter = 2;
    }
}
