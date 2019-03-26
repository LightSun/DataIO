package com.heaven7.java.data.io.os.sources;

import com.heaven7.java.base.util.ResourceLoader;
import com.heaven7.java.base.util.TextReadHelper;

import java.util.List;

/**
 * @author heaven7
 */
public class FileLinesTableSource<T> extends TableSource<T> {

    private final Object context;
    private final String file;
    private final TextReadHelper.Callback<List<T>> callback;

    public FileLinesTableSource(Object context, String file, TextReadHelper.Callback<List<T>> callback) {
        this.context = context;
        this.file = file;
        this.callback = callback;
    }


    @Override
    public List<List<T>> getTable() {
        return ResourceLoader.getDefault().readLines(context, file, callback);
    }
}
