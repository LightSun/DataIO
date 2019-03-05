package com.heaven7.java.data.io.music.transfer;

import com.heaven7.java.base.util.Predicates;
import com.heaven7.java.base.util.TextUtils;
import com.heaven7.java.data.io.bean.MusicItem2;
import com.heaven7.java.data.io.bean.WrappedSubItem;
import com.heaven7.java.data.io.poi.ExcelRow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
