package com.heaven7.java.data.io.os.sources;

import java.util.List;

/**
 * @author heaven7
 */
public class DirectTableSource<T> extends TableSource<T> {

    private List<List<T>> list;

    public DirectTableSource(List<List<T>> list) {
        this.list = list;
    }
    @Override
    public List<List<T>> getTable() {
        return list;
    }
}
