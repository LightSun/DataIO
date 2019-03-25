package com.heaven7.java.data.io.os.sources;

import java.util.List;


/**
 * table source that means : every line have multi columns.
 * @author heaven7
 */
public interface TableSource<T> extends ListSource<List<T>> {

    @Override
    List<List<T>> getList();
}
