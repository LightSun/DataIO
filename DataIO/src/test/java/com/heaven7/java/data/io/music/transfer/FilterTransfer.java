package com.heaven7.java.data.io.music.transfer;

import com.heaven7.java.data.io.bean.MusicItem2;
import com.heaven7.java.data.io.bean.WrappedSubItem;
import com.heaven7.java.data.io.poi.ExcelRow;

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
        return Arrays.asList(row.getColumns().get(mIndexer.index_filter).getColumnString().trim());
    }
    @Override
    protected void applyAdditionInfo(MusicItem2 matchItem, WrappedSubItem<List<String>> wsb) {
        matchItem.setFilterNames(wsb.getSubItem());
    }

    public static class Indexer extends BaseAdditionalTransfer.Indexer{
        public int index_filter = 2;
    }
}
